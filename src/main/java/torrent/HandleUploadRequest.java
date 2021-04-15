package torrent;

import com.google.protobuf.ByteString;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class HandleUploadRequest {

    SaveFile saveFile;

    public HandleUploadRequest(SaveFile saveFile){
        this.saveFile = saveFile;
    }

    public static ByteString getMd5(String input)
    {
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] messageDigest = md.digest();
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < messageDigest.length; i++){
                sb.append(Integer.toString((messageDigest[i] & 0xff) + 0x100, 16).substring(1));
            }

            String hashtext = sb.toString();

            ByteString str = ByteString.copyFrom(hashtext.getBytes());
            return str;
        }

        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public Torrent.Message handleUploadRequest(Torrent.UploadRequest request){

        String name = request.getFilename();
        ByteString data = request.getData();
        Torrent.Status status = null;
        String errorMessage = "";
        Torrent.FileInfo.Builder file = Torrent.FileInfo.newBuilder();

        try {
            if (data.isEmpty()) {
                status = Torrent.Status.MESSAGE_ERROR;
                errorMessage = "file is empty";
            } else if (!data.isEmpty()) {
                status = Torrent.Status.SUCCESS;
                errorMessage = "All is ok";

                ArrayList<Chunk> chunks = new ArrayList<>();
                ByteString d;

                int index = 0;
                for (int i = 0; i < data.size(); i += 1024) {
                    if (index == data.size() / 1024)
                        d = data.substring(i, data.size());
                    else {
                        d = data.substring(i, 1024);
                    }
                    chunks.add(new Chunk(index, d.size(), getMd5(d.toString())));
                    Torrent.ChunkInfo.Builder ch = Torrent.ChunkInfo.newBuilder().setHash(getMd5(d.toString())).
                            setIndex(index).setSize(d.size());
                    file.addChunks(ch);
                    index++;
                }

                File f = new File(getMd5(data.toString()), data.size(), name, chunks);
                saveFile.saveFile(f);

                file.setHash(f.getHash());
                file.setSize(f.getSize());
                file.setFilename(f.getName());
                file.build();

            }
        } catch (Exception e){
            status = Torrent.Status.PROCESSING_ERROR;
            errorMessage = "Other errors";
        }

        Torrent.Message m = Torrent.Message.newBuilder().setType(Torrent.Message.Type.UPLOAD_RESPONSE).
                setUploadResponse(Torrent.UploadResponse.newBuilder().setStatus(status).
                setErrorMessage(errorMessage).setFileInfo(file).build()).build();

        return m;
    }
}
