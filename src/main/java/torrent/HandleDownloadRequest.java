package torrent;

import java.util.ArrayList;

public class HandleDownloadRequest {

    SaveFile saveFile;

    public Torrent.Message handleDownloadRequest(Torrent.DownloadRequest request){
        ArrayList<File> files = saveFile.getFiles();
        Torrent.Status status = null;
        String errorMassage = "";
        File file = null;

        try {
            boolean found = false;

            for (File f : files) {
                if (f.getHash().equals(request.getFileHash())) {
                    found = true;
                    file = f;
                }
            }

            if (request.getFileHash().size() != 16) {
                status = Torrent.Status.MESSAGE_ERROR;
                errorMassage = "filehash is not 16 bytes long";
            } else if (found) {
                status = Torrent.Status.SUCCESS;
                errorMassage = "ok";
            } else if (!found) {
                status = Torrent.Status.UNABLE_TO_COMPLETE;
                errorMassage = "File not found";
            }
        }catch (Exception e) {
            status = Torrent.Status.PROCESSING_ERROR;
            errorMassage = "Other";
        }

        Torrent.Message m = Torrent.Message.newBuilder().setType(Torrent.Message.Type.DOWNLOAD_RESPONSE).
                setDownloadResponse(Torrent.DownloadResponse.newBuilder().setStatus(status).
                setErrorMessage(errorMassage).setData(file.getHash()).build()).build();

        return m;
    }
}
