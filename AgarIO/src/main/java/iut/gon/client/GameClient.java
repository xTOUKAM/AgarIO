package iut.gon.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class GameClient {

    private final Socket socket;

    public GameClient(InetSocketAddress serverAddress){
        try {
            this.socket = new Socket(serverAddress.getAddress(), serverAddress.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        GameClient gameClient = new GameClient(address);
        try {
            while(true) {
                DataInputStream serverData = null;

                serverData = new DataInputStream(gameClient.socket.getInputStream());
                switch (serverData.readByte()){
                    case 1:
                        System.out.println("Mon id : " + serverData.readUTF());
                        serverData.readUTF();
                        break;
                    case 2:
                        //TODO send data to client renderer
                        System.out.println(serverData.readUTF());
                        break;
                    default:
                        throw new IOException("error in server data");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
