package iut.gon.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class GameClient {


    public static void startConnection(BufferedReader serverOutput){
        try{
            boolean keepListening = true;

            while(keepListening) {
                switch (MessageType.values()[Integer.parseInt(serverOutput.readLine())]){

                    case ID:
                        System.out.println("Mon id : " + serverOutput.readLine());
                        break;

                    case GAME_STATE:
                        //TODO send data to client renderer
                        System.out.println(serverOutput.readLine());
                        break;

                    case SERVER_STOP:
                        System.out.println("Server stopped");
                        keepListening = false;
                        break;

                    default:
                        keepListening = false;
                        System.out.println("Server stopped unexpectedly");

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void launch(String serverAddress, int serverPort) {

        //Connection to server
        final Socket socket;
        try {
            socket = new Socket(InetAddress.getByName(serverAddress), serverPort);
        } catch (IOException e) {
            System.out.println("CLIENT | error while trying to connect to server");
            throw new RuntimeException(e);
        }


        try{
            BufferedReader serverOutput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread ConnectionToServer = new Thread(() -> startConnection(serverOutput));
            ConnectionToServer.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //TODO run client game renderer
    }




    public static void main(String[] args) {
        GameClient.launch(args[0], Integer.parseInt(args[1]));
    }
}
