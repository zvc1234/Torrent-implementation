package torrent;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HandleSearchRequest {

    Torrent.NodeId node;
    SaveFile saveFile;

    HandleSearchRequest(Torrent.NodeId node, SaveFile saveFile){
        this.node = node;
        this.saveFile = saveFile;
    }


    public Torrent.Message handleSearchRequest(Torrent.SearchRequest request){

        String regex;
        Torrent.Status status;
        String errorMessage = "";
        Torrent.SearchResponse.Builder sr = Torrent.SearchResponse.newBuilder();

        if(!request.getRegex().getClass().equals(String.class)){
            status = Torrent.Status.MESSAGE_ERROR;
            errorMessage = "Regex not ok";
        }
        else {

            status = Torrent.Status.SUCCESS;
            errorMessage = "ok";

            regex = request.getRegex();
            Torrent.Message message = Torrent.Message.newBuilder().setType(Torrent.Message.Type.LOCAL_SEARCH_REQUEST).
                    setLocalSearchRequest(Torrent.LocalSearchRequest.newBuilder().setRegex(regex).build()).build();


            for (int i = 1; i <= 6; i++) {
                if ((6000 + i) != node.getPort()) {
                    SendToNodes sendToNodes = new SendToNodes(message, i);
                    Thread t = new Thread(sendToNodes);
                    t.start();
                }

            }

            ArrayList<Torrent.NodeId> nodeList = new ArrayList<>();
            boolean c = true;
            int wait = 1;
            while (c && wait <= 15) {
                for (NodeSearchStatus n : saveFile.getNodeSearchStatuses()) {
                    if (n.getNode() != node && !nodeList.contains(n.getNode()))
                        nodeList.add(n.getNode());
                }
                if (nodeList.size() == 5)
                    c = false;
                wait++;
            }

            for (NodeSearchStatus n : saveFile.getNodeSearchStatuses()) {
                Torrent.NodeSearchResult.Builder nd = Torrent.NodeSearchResult.newBuilder();
                for (Torrent.FileInfo f : n.getFiles()) {
                    nd.addFiles(f);
                }
                nd.setStatus(n.getStatus()).setNode(n.getNode()).setErrorMessage(n.getErrorMessage());
                sr.addResults(nd);
            }
        }

        sr.setStatus(status);
        sr.setErrorMessage(errorMessage);
        Torrent.Message m = Torrent.Message.newBuilder().setType(Torrent.Message.Type.SEARCH_RESPONSE).
                setSearchResponse(sr.build()).build();

        return m;
    }
}
