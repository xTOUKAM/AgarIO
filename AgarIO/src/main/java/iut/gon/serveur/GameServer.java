package iut.gon.serveur;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class GameServer{

    private final LinkedList<ClientInput> clientInputs = new LinkedList<>();
    private final ServerSocket server;
    private int lastID = 1;
    private boolean running = true;


    public GameServer(int port){
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {throw new RuntimeException(e);}
    }


    public void sendToAllClient(MessageType messageType, String data, boolean OnlineCheck){
        for(ClientInput clientInput : clientInputs){
            if(OnlineCheck){
                if(clientInput.online){
                    Communication.send(clientInput.printWriter, messageType, data);
                }
            }else {
                Communication.send(clientInput.printWriter, messageType, data);
            }
        }
    }

    public void sendToClientByID(MessageType messageType, String data, int ID, boolean OnlineCheck){
        for(ClientInput clientInput : clientInputs){
            if(clientInput.ID == ID){
                if(OnlineCheck){
                    if(clientInput.online){
                        Communication.send(clientInput.printWriter, messageType, data);
                    }
                }else {
                    Communication.send(clientInput.printWriter, messageType, data);
                }
            }
        }
    }

    public void updateClientStatus(int ID, boolean status){
        for(ClientInput clientInput : clientInputs){
            if(clientInput.ID == ID){
                clientInput.online = status;
            }
        }
    }


    public void startServer(){

        System.out.println("SERVER | started successfully");
        try{
            while(running){

                Socket newClientSocket = server.accept();
                System.out.println("SERVER | new connection ID => "+this.lastID+ " SOCKET => " +newClientSocket.toString());
                PrintWriter clientOutput = new PrintWriter(newClientSocket.getOutputStream(), false);
                synchronized (this.clientInputs){
                    this.clientInputs.add(new ClientInput(lastID, clientOutput));
                }

                //send id to client
                sendToClientByID(MessageType.SERVER_ID, ""+lastID, lastID, false);

                //send first game state to client for rendering
                String data = "Initial game state";//TODO get game state from game engine instead
                sendToClientByID(MessageType.SERVER_INITIAL_GAME_STATE, data, lastID, false);

                //set up socket for client input
                ClientHandler clientHandler = new ClientHandler(newClientSocket, this, lastID++);
                clientHandler.start();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        GameServer.launch(1234);
    }

    public static void launch(int port){
        GameServer gameServer = new GameServer(port);
        Thread lisenningThread = new Thread(gameServer::startServer);
        lisenningThread.start();


        //TODO LAUNCH GAME ENGINE THREAD


        Thread gameClock = new Thread(()->{
            try {
                while (true) {
                    //TODO use game state from game engine
                    gameServer.sendToAllClient(MessageType.SERVER_GAME_STATE,"Game data", true);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println(" ");
            }
        });
        gameClock.start();


        //SERVER STOP
        Scanner inputReader = new Scanner(System.in);
        while (gameServer.running){
            if(inputReader.hasNext()){
                String text = inputReader.next();
                if("stop".equals(text) || "STOP".equals(text)){
                    gameServer.running = false;
                    //TODO send error message to all client
                    gameServer.sendToAllClient(MessageType.SERVER_STOP, "stop", false);
                    System.out.println("SERVER | stopped successfully");
                    System.exit(0);
                }
            }
        }
    }



}
