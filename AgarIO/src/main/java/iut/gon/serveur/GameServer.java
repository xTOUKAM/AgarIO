package iut.gon.serveur;

import iut.gon.client.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class GameServer{

    private final LinkedList<Client> clients = new LinkedList<>();
    private final ServerSocket server;
    private int lastID = 1;
    private boolean running = true;


    public GameServer(int port){
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {throw new RuntimeException(e);}
    }


    public void sendToAllClient(MessageType messageType, String data){
        for(Client client: clients){
            if(client.online){
                client.printWriter.write(messageType.ordinal()+"\n");
                client.printWriter.write(data +"\n");
                client.printWriter.flush();
            }
        }
    }

    public void sendToClientByID(MessageType messageType, String data, int ID){
        for(Client client: clients){
            if(client.online && client.ID == ID){
                client.printWriter.write(messageType.ordinal()+"\n");
                client.printWriter.write(data +"\n");
                client.printWriter.flush();
            }
        }
    }

    public void updateClientStatus(int ID, boolean status){
        for(Client client: clients){
            if(client.ID == ID){
                client.online = status;
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
                synchronized (this.clients){
                    this.clients.add(new Client(lastID, clientOutput));
                }

                //send id to client
                clientOutput.write("0\n");
                clientOutput.write(lastID + "\n");
                clientOutput.flush(); // Send off the data

                //set up socket for client input
                ClientHandler clientHandler = new ClientHandler(newClientSocket, this, lastID++);
                clientHandler.start();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static void main(String[] args) {
        GameServer gameServer = new GameServer(Integer.parseInt(args[0]));
        Thread lisenningThread = new Thread(gameServer::startServer);
        lisenningThread.start();


        //TODO LAUNCH GAME ENGINE THREAD


        Thread gameClock = new Thread(()->{
            try {
                while (true) {
                    //TODO use game state from game engine
                    gameServer.sendToAllClient(MessageType.SERVER_GAME_STATE,"Game data");
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
                if("stop".equals(inputReader.next())){
                    gameServer.running = false;
                    //TODO send error message to all client
                    gameServer.sendToAllClient(MessageType.SERVER_STOP, "stop");
                    System.out.println("SERVER | stopped successfully");
                    System.exit(0);
                }
            }
        }
    }
}
