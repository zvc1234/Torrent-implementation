package torrent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Node2 {

    static public void main(String args[]) {
        Torrent.NodeId node = Torrent.NodeId.newBuilder().setIndex(2).setPort(6005).setHost("127.0.0.1")
                .setOwner("zasd").build();

        Runnable runnable = new RunnableClass(node);
        Socket socket = null;
        ServerSocket serverSocket = null;

        OutputStream out = null;
        InputStream in = null;

        try{
            socket = new Socket("127.0.0.1", 6000);
            out = socket.getOutputStream();
            Torrent.Message message = Torrent.Message.newBuilder().setType(Torrent.Message.Type.REGISTRATION_REQUEST).
                    setRegistrationRequest(Torrent.RegistrationRequest.newBuilder().setIndex(2).setPort(6005).setOwner("zasd")
                            .build()).build();
            byte[] buffer = message.toByteArray();
            int size = buffer.length;
            byte[] b = ByteBuffer.allocate(50).putInt(0, size).put(4, buffer).array();
            out.write(b);


        } catch (UnknownHostException u){
            System.out.println(u);
        } catch(IOException i) {
            java.lang.System.out.println(i);
        }
        try {
            out.close();
            socket.close();
        }
        catch(IOException i) {
            java.lang.System.out.println(i);
        }

        Thread thread1 = new Thread(runnable);
        thread1.start();
    }

}
