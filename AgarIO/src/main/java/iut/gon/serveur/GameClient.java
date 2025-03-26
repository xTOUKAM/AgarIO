package iut.gon.serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class GameClient {

    private int ID;
    private final BufferedReader serverOutput;
    private final PrintWriter serverInput;


    public GameClient(String serverAddress, int serverPort){
        //Try connecting to server
        try{
            Socket socket = new Socket(InetAddress.getByName(serverAddress), serverPort);
            serverOutput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverInput = new PrintWriter(socket.getOutputStream());
            System.out.println("CLIENT | Connection to server successful");
        } catch (IOException e) {
            System.out.println("CLIENT | error while trying to connect to server");
            throw new RuntimeException(e);
        }
    }

    private void serverMessageHandler(){
        Thread ConnectionToServer = new Thread(() -> {
            boolean keepListening = true;
            while(keepListening) {
                try {
                    switch (MessageType.values()[Integer.parseInt(serverOutput.readLine())]){

                        case SERVER_ID:
                            this.ID = Integer.parseInt(serverOutput.readLine());
                            System.out.println("CLIENT | ID received => " + this.ID);
                            break;

                        case SERVER_INITIAL_GAME_STATE:
                            //TODO initialize  client renderer
                            System.out.println(serverOutput.readLine());
                            Communication.send(serverInput, MessageType.CLIENT_STATUS, "true");
                            break;

                        case SERVER_GAME_STATE:
                            //TODO send data to client renderer
                            System.out.println(serverOutput.readLine());
                            break;

                        case SERVER_STOP:
                            System.out.println("CLIENT | server stopped");
                            keepListening = false;
                            break;

                        default:
                            keepListening = false;
                            System.out.println("CLIENT | server stopped unexpectedly");
                    }
                } catch (Exception e) {
                    keepListening = false;
                    System.out.println("CLIENT | server stopped unexpectedly");
                }
            }
        });
        ConnectionToServer.start();
    }

    public static void main(String[] args) {
        GameClient.launch("127.0.0.1", 1234);
    }


    public static void launch(String serverAddress, int serverPort){
        GameClient gameClient = new GameClient(serverAddress, serverPort);

        //Listen to server output
        gameClient.serverMessageHandler();

        //TODO run client game renderer
    }


}
