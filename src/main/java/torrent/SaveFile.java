package torrent;

import java.util.ArrayList;

public class SaveFile {

    private ArrayList<File> files;
    private ArrayList<NodeSearchStatus> nodeSearchStatuses;
    private ArrayList<NodeReplicationStatus> nodeReplicationStatuses;

    public SaveFile(){

        files = new ArrayList<>();
        nodeSearchStatuses = new ArrayList<>();
        nodeReplicationStatuses = new ArrayList<>();
    }

    public void saveFile(File file){
        files.add(file);
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public ArrayList<NodeSearchStatus> getNodeSearchStatuses() {
        return nodeSearchStatuses;
    }

    public ArrayList<NodeReplicationStatus> getNodeReplicationStatuses() {
        return nodeReplicationStatuses;
    }

    public void saveReplicationStatus(NodeReplicationStatus n){
        nodeReplicationStatuses.add(n);
    }

    public void saveSearchStatus(NodeSearchStatus n){
        nodeSearchStatuses.add(n);
    }

}
