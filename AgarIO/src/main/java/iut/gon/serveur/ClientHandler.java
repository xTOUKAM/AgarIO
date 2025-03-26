package iut.gon.serveur;

import iut.gon.client.MessageType;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread{

    private final BufferedReader clientOutput;
    private final int ID;
    private final GameServer gameServer;

    public ClientHandler(Socket socket, GameServer gameServer, int ID){
        this.gameServer = gameServer;
        this.ID = ID;
        try {
            clientOutput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void run(){
        boolean keepListening = true;
        while(keepListening) {
            try {
                switch (MessageType.values()[Integer.parseInt(clientOutput.readLine())]){

                    case CLIENT_STATUS:
                        this.gameServer.updateClientStatus(this.ID, Boolean.parseBoolean(clientOutput.readLine()));
                        break;

                    case CLIENT_MOVEMENT:
                        //TODO send data to game engine
                        clientOutput.readLine();
                        break;

                    case CLIENT_CHAT_MESSAGE:
                        //TODO update chat with new message
                        clientOutput.readLine();
                        break;

                    default:
                        keepListening = false;
                        System.out.println("SERVER | Connection to client "+ this.ID +" stopped unexpectedly");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
