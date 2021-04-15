package torrent;

import com.google.protobuf.ByteString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


public class RunnableClass implements java.lang.Runnable {

    Torrent.NodeId node;
    SaveFile saveFile;
    HandleUploadRequest handleUploadRequest ;
    HandleSearchRequest handleSearchRequest;
    HandleLocalSearchRequest handleLocalSearchRequest;
    HandleDownloadRequest handleDownloadRequest;
    HandleChunkRequest handleChunkRequest;
    HandleReplicateRequest handleReplicateRequest;

    RunnableClass(Torrent.NodeId node){
        this.node = node;
        saveFile = new SaveFile();
        handleUploadRequest = new HandleUploadRequest(saveFile);
        handleSearchRequest = new HandleSearchRequest(node, saveFile);
        handleLocalSearchRequest = new HandleLocalSearchRequest(saveFile, node);
        handleDownloadRequest = new HandleDownloadRequest();
        handleChunkRequest = new HandleChunkRequest(saveFile, node);
        handleReplicateRequest = new HandleReplicateRequest(saveFile, node, Torrent.Status.PROCESSING_ERROR);
    }

    public byte[] decorateMessage(Torrent.Message m)
    {
        byte[] buffer = m.toByteArray();
        int size = buffer.length;
        byte[] out = ByteBuffer.allocate(32000).putInt(0, size).put(4, buffer).array();
        return out;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        InputStream input = null;
        OutputStream out = null;
        java.lang.System.out.println("Network thread");
        System.out.println("Port: " + node.getPort());

        try {
            serverSocket = new ServerSocket(node.getPort());
           while(true) {

                Socket socket = serverSocket.accept();
                input = socket.getInputStream();
                out = socket.getOutputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[32000];
                baos.write(buffer, 0, input.read(buffer));
                byte result[] = baos.toByteArray();

                ByteBuffer bBuf = ByteBuffer.wrap(result);
                byte[] b = new byte[4];
                bBuf.get(b, 0, b.length);
                ByteBuffer newBBuf = ByteBuffer.wrap(b);
                int size = newBBuf.getInt();

                byte[] buf = ByteBuffer.allocate(size).put(buffer, 4, size).array();

                bBuf.clear();
                newBBuf.clear();

                Torrent.Message message = Torrent.Message.parseFrom(buf);

                if(message.hasSearchRequest()){
                    System.out.println("Search request");
                    Torrent.Message m = handleSearchRequest.handleSearchRequest(message.getSearchRequest());
                   // System.out.println("Search response");
                    out.write(decorateMessage(m));
                }
                if(message.hasLocalSearchRequest()){
                    System.out.println("Search local request");
                    Torrent.Message m = handleLocalSearchRequest.handleLocalSearchRequest(message.getLocalSearchRequest());
                    //System.out.println("Search local response");
                    out.write(decorateMessage(m));
                }
               if(message.hasUploadRequest()){
                   System.out.println("Upload request");
                   Torrent.Message m = handleUploadRequest.handleUploadRequest(message.getUploadRequest());
                 //  System.out.println("upload response");
                   out.write(decorateMessage(m));
               }
               if(message.hasDownloadRequest()){
                   System.out.println("Download request");
                   Torrent.Message m = handleDownloadRequest.handleDownloadRequest(message.getDownloadRequest());
                 //  System.out.println("Download response");
                   out.write(decorateMessage(m));
               }
               if(message.hasChunkRequest()){
                   System.out.println("Chunk request");
                   Torrent.Message m = handleChunkRequest.handleChunkRequest(message.getChunkRequest());
                  // System.out.println("Chunk response");
                   out.write(decorateMessage(m));
               }
               if(message.hasReplicateRequest()){
                   System.out.println("Replicate request");
                   Torrent.Message m = handleReplicateRequest.handleReplicateRequest(message.getReplicateRequest());
                //   System.out.println("Replicate response");
                   out.write(decorateMessage(m));
               }
               if(message.hasChunkResponse()){
                   System.out.println("Chunk response");
                   Torrent.Message m = handleReplicateRequest.hasChunkResponse();
                   out.write(decorateMessage(m));
               }

            }
        }
        catch (UnknownHostException u) {
            java.lang.System.out.println("Hub " + u);
        } catch (IOException i) {
            java.lang.System.out.println("Hub " + i);
        }
        try {
            input.close();
            serverSocket.close();
        } catch (IOException i) {
            java.lang.System.out.println(i);
        }
    }
}

