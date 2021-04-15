package torrent;

import java.util.ArrayList;

public class NodeSearchStatus {

    Torrent.NodeId node;
    Torrent.Status status;
    String errorMessage;
    ArrayList<Torrent.FileInfo> files;

    public NodeSearchStatus() {
    }

    public NodeSearchStatus(Torrent.NodeId node, Torrent.Status status, String errorMessage, ArrayList<Torrent.FileInfo> files) {
        this.node = node;
        this.status = status;
        this.errorMessage = errorMessage;
        this.files = files;
    }

    public Torrent.NodeId getNode() {
        return node;
    }

    public void setNode(Torrent.NodeId node) {
        this.node = node;
    }

    public Torrent.Status getStatus() {
        return status;
    }

    public void setStatus(Torrent.Status status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ArrayList<Torrent.FileInfo> getFiles() {
        return files;
    }

    public void setFiles(Torrent.FileInfo file) {
        this.files.add(file);
    }
}
