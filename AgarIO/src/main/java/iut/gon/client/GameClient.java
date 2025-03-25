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

        DataInputStream dIn = null;
        try {
            dIn = new DataInputStream(gameClient.socket.getInputStream());
            dIn.readByte();
            System.out.println("Message A: " + dIn.readUTF());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
