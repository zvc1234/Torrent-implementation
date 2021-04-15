package torrent;

import com.google.protobuf.ByteString;

public class Chunk {

    int index;
    int size;
    ByteString hash;

    public Chunk(){};

    public Chunk(int index, int size, ByteString hash) {
        this.index = index;
        this.size = size;
        this.hash = hash;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ByteString getHash() {
        return hash;
    }

    public void setHash(ByteString hash) {
        this.hash = hash;
    }
}
