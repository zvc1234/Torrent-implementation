package torrent;

import com.google.protobuf.ByteString;

import java.util.ArrayList;

public class HandleChunkRequest {

    SaveFile saveFile;
    Torrent.NodeId node;

    public HandleChunkRequest(SaveFile saveFile, Torrent.NodeId node){
        this.saveFile = saveFile;
        this.node = node;
    }

    public Torrent.Message handleChunkRequest(Torrent.ChunkRequest request){

        Chunk chunk = new Chunk();
        boolean found = false;
        Torrent.Status status;
        String errorMessage;
        NodeReplicationStatus nodeReplicationStatus = new NodeReplicationStatus();

        Torrent.Message.Builder m = Torrent.Message.newBuilder().setType(Torrent.Message.Type.CHUNK_RESPONSE);

        try{
            ByteString fileHash = request.getFileHash();
            int chuckIndex = request.getChunkIndex();

            if(fileHash.size() != 16 || chuckIndex < 0){
                status = Torrent.Status.MESSAGE_ERROR;
                errorMessage = "Filehash is not 16 bytes long or the index is less than 0";
            }
            else {
                ArrayList<File> files = saveFile.getFiles();
                for (File f : files) {
                    if (f.getHash().equals(fileHash)) {
                        for (Chunk c : f.getChunks()) {
                            if (c.getIndex() == chuckIndex) {
                                chunk = c;
                                found = true;
                            }
                        }
                    }
                }
                nodeReplicationStatus.setChunkIndex(chunk.getIndex());

                if (!found) {
                    status = Torrent.Status.UNABLE_TO_COMPLETE;
                    errorMessage = "I don't have the chunk";
                } else {
                    status = Torrent.Status.SUCCESS;
                    errorMessage = "I have the chunk";
                    m.setChunkResponse(Torrent.ChunkResponse.newBuilder().setData(chunk.getHash()).build());
                }
            }
        } catch(Exception e){
                status = Torrent.Status.PROCESSING_ERROR;
                errorMessage = "Other";
        }

        nodeReplicationStatus.setStatus(status);
        nodeReplicationStatus.setErrorMessage(errorMessage);
        nodeReplicationStatus.setNode(node);

        saveFile.saveReplicationStatus(nodeReplicationStatus);

        m.setChunkResponse(Torrent.ChunkResponse.newBuilder().setStatus(status).
                setErrorMessage(errorMessage).build()).build();
        Torrent.Message mm = m.build();

        return mm;

    }

}
