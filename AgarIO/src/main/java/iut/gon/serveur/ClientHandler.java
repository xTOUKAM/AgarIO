package iut.gon.serveur;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler extends Thread{

    private final OutputStream fromClientStream;
    private final InputStream toClientStream;

    public ClientHandler(Socket socket){
        try {
            fromClientStream = socket.getOutputStream();
            toClientStream = socket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void run(){

    }

}
