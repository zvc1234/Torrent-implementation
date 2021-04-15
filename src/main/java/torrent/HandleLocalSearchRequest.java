package torrent;

import java.util.ArrayList;

public class HandleLocalSearchRequest {

    SaveFile saveFile;
    Torrent.NodeId node;

    public HandleLocalSearchRequest(SaveFile saveFile, Torrent.NodeId node){
        this.saveFile = saveFile;
        this.node = node;
    }

    public Torrent.Message handleLocalSearchRequest(Torrent.LocalSearchRequest request){

        String regex = "";
        ArrayList<File> files = saveFile.getFiles();
        Torrent.Status status = null;
        String errorMessage = "";
        NodeSearchStatus nodeSearchStatus = new NodeSearchStatus();
        Torrent.LocalSearchResponse.Builder localSearch = Torrent.LocalSearchResponse.newBuilder();

        if(!request.getRegex().getClass().equals(String.class)){
            status = Torrent.Status.PROCESSING_ERROR;
            errorMessage = "Other";
        }
        else {

            regex = request.getRegex();

            for (File f : files) {
                if (f.getName().contains(regex)) {
                    Torrent.FileInfo.Builder fileInfo = Torrent.FileInfo.newBuilder().setHash(f.getHash()).
                            setSize(f.getSize()).setFilename(f.getName());
                    for (Chunk c : f.getChunks()) {
                        Torrent.ChunkInfo.Builder chunkInfo = Torrent.ChunkInfo.newBuilder().setHash(c.getHash()).
                                setIndex(c.getIndex()).setSize(c.getSize());
                        fileInfo.addChunks(chunkInfo);
                    }
                    localSearch.addFileInfo(fileInfo);
                    nodeSearchStatus.setFiles(fileInfo.build());
                }
            }

            status = Torrent.Status.SUCCESS;
            errorMessage = "ok";

            localSearch.setStatus(status);
            localSearch.setErrorMessage(errorMessage);
            localSearch.build();
        }
        nodeSearchStatus.setStatus(status);
        nodeSearchStatus.setErrorMessage(errorMessage);
        nodeSearchStatus.setNode(node);

        saveFile.saveSearchStatus(nodeSearchStatus);

        Torrent.Message m = Torrent.Message.newBuilder().setType(Torrent.Message.Type.LOCAL_SEARCH_RESPONSE).
                setLocalSearchResponse(localSearch).build();

        return m;

    }

}
