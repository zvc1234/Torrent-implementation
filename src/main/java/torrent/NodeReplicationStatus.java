package torrent;

public class NodeReplicationStatus {

    Torrent.NodeId node;
    int chunkIndex;
    Torrent.Status status;
    String errorMessage;

    public NodeReplicationStatus() {
    }

    public Torrent.NodeId getNode() {
        return node;
    }

    public void setNode(Torrent.NodeId node) {
        this.node = node;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
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

    public NodeReplicationStatus(Torrent.NodeId node, int chunkIndex, Torrent.Status status, String errorMessage) {
        this.node = node;
        this.chunkIndex = chunkIndex;
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
