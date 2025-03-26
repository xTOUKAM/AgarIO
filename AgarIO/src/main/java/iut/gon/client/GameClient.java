package iut.gon.client;

import iut.gon.serveur.Client;

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

    private void ConnectToServer(){
        Thread ConnectionToServer = new Thread(() -> {
            boolean keepListening = true;
            while(keepListening) {
                try {
                    switch (MessageType.values()[Integer.parseInt(serverOutput.readLine())]){

                        case SERVER_ID:
                            this.ID = Integer.parseInt(serverOutput.readLine());
                            System.out.println("CLIENT | ID received => " + this.ID);
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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ConnectionToServer.start();
    }

    public void sendToServer(MessageType messageType, String data){
        serverInput.write(messageType.ordinal()+"\n");
        serverInput.write(data +"\n");
        serverInput.flush();
    }

    public static void main(String[] args) {
        GameClient.launch(args[0], Integer.parseInt(args[1]));
    }


    public static void launch(String serverAddress, int serverPort){
        GameClient gameClient = new GameClient(serverAddress, serverPort);

        //Listen to server output
        gameClient.ConnectToServer();

        //TODO run client game renderer
    }


}
