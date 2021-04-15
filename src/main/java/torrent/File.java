package torrent;

import com.google.protobuf.ByteString;

import java.util.ArrayList;

public class File {

    int size;
    ByteString hash;
    String name;
    ArrayList<Chunk> chunks;

    public File(){}

    public File(ByteString hash, int size, String name, ArrayList<Chunk> chunks) {
        this.size = size;
        this.hash = hash;
        this.name = name;
        this.chunks = chunks;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Chunk> getChunks() {
        return chunks;
    }

    public void setChunks(ArrayList<Chunk> chunks) {
        this.chunks = chunks;
    }

}
