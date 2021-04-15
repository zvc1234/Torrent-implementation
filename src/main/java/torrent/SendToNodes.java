package torrent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class SendToNodes implements Runnable{

    private Torrent.Message message;
    private int i;
    private HandleLocalSearchRequest handleLocalSearchRequest;
    private HandleChunkRequest handleChunkRequest;
    private HandleReplicateRequest handleReplicateRequest;
    private HandleDownloadRequest handleDownloadRequest;

    public SendToNodes(Torrent.Message message, int i){
        this.message = message;
        this.i = i;
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
        Socket socket = null;
        InputStream input = null;
        OutputStream out = null;
        java.lang.System.out.println("Nodes thread");

        try {
            int port = 6000 + i;
            socket = new Socket("127.0.0.1", port);
            out = socket.getOutputStream();

            out.write(decorateMessage(message));
        }
        catch (UnknownHostException u) {
            java.lang.System.out.println("Nodes " + i + " " + u);
        } catch (IOException e) {
            java.lang.System.out.println("Nodes " + i + " " + e);
        }
        try {
            if(out != null)
                out.close();
            if(socket != null)
                socket.close();
            System.out.println("Connection closed");
        } catch (IOException e) {
            java.lang.System.out.println(e);
        }
    }
}


