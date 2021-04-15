package torrent;

import java.util.ArrayList;

public class HandleReplicateRequest {

    SaveFile saveFile;
    Torrent.NodeId node;

    public HandleReplicateRequest(SaveFile saveFile, Torrent.NodeId node, Torrent.Status status){
        this.saveFile = saveFile;
        this.node = node;
        this.status = status;
    }

    Torrent.Status status;
    String errorMessage = "";
    Torrent.ReplicateResponse.Builder rr = Torrent.ReplicateResponse.newBuilder();

    public Torrent.Message handleReplicateRequest(Torrent.ReplicateRequest request){

        if(request.getFileInfo().getFilename().isEmpty()) {
            status = Torrent.Status.MESSAGE_ERROR;
            errorMessage = "filename is empty";
        }
        else {
            ArrayList<Chunk> chunks = new ArrayList<>();
            for(Torrent.ChunkInfo c : request.getFileInfo().getChunksList()){
                chunks.add(new Chunk(c.getIndex(), c.getSize(), c.getHash()));
            }

            File file = new File(request.getFileInfo().getHash(), request.getFileInfo().getSize(),
                    request.getFileInfo().getFilename(), chunks);
            boolean found = false;
            for (File f : saveFile.getFiles()) {
                if (f.getName().equals(file.getName()) && f.getSize() == file.getSize() &&
                        f.getHash().equals(file.getHash()) && f.getChunks().equals(file.getChunks())) {
                    found = true;
                }
            }
            if (!found) {
                for (int i = 0; i < file.getChunks().size(); i++) {
                    Torrent.Message m = Torrent.Message.newBuilder().setType(Torrent.Message.Type.CHUNK_REQUEST).
                            setChunkRequest(Torrent.ChunkRequest.newBuilder().setFileHash(file.getHash()).
                                    setChunkIndex(i).build()).build();

                    for(int j = 1; j <= 6; j++){
                        if((6000+j) != node.getPort()) {
                            SendToNodes sendToNodes = new SendToNodes(m, j);
                            Thread t = new Thread(sendToNodes);
                            t.start();
                        }
                    }

                }

            }
            else {
                status = Torrent.Status.SUCCESS;
                errorMessage = "succes";
            }
        }

        return hasChunkResponse();
    }

    public Torrent.Message hasChunkResponse(){
        ArrayList<Torrent.NodeId> nodeList = new ArrayList<>();
        boolean c = true;
        int wait = 1;
        while (c && wait <= 15) {
            for (NodeReplicationStatus n : saveFile.getNodeReplicationStatuses()) {
                if (n.getNode() != node && !nodeList.contains(n.getNode()))
                    nodeList.add(n.getNode());
            }
            if (nodeList.size() == 5)
                c = false;
            wait++;
        }

        for(NodeReplicationStatus n : saveFile.getNodeReplicationStatuses()){
            Torrent.NodeReplicationStatus.Builder nd = Torrent.NodeReplicationStatus.newBuilder().
                    setNode(n.getNode()).setChunkIndex(n.getChunkIndex()).setErrorMessage(n.getErrorMessage()).
                    setStatus(n.getStatus());
            rr.addNodeStatusList(nd.build());
        }
        rr.setErrorMessage(errorMessage);
        rr.setStatus(status);
        rr.build();

        Torrent.Message m = Torrent.Message.newBuilder().setType(Torrent.Message.Type.REPLICATE_RESPONSE).
                setReplicateResponse(rr).build();

        return m;


    }

}
