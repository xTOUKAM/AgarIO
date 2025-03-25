package iut.gon.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        BufferedReader serverData = null;
        try {
            serverData = new BufferedReader(new InputStreamReader(gameClient.socket.getInputStream()));
            while(true) {

                switch (Integer.parseInt(serverData.readLine())){
                    case 1:
                        System.out.println("Mon id : " + serverData.readLine());
                        break;
                    case 2:
                        //TODO send data to client renderer
                        System.out.println(serverData.readLine());
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
