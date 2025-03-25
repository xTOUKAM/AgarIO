package iut.gon.serveur;

import iut.gon.client.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class GameServer{

    private final LinkedList<PrintWriter> clientInputs;
    public ServerSocket server;
    private int lastID = 1;
    boolean running = true;


    public GameServer(int port){
        clientInputs = new LinkedList<>();
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {throw new RuntimeException(e);}
    }


    public void sendToAllClient(MessageType messageType, String data){
        for(PrintWriter clientInput: clientInputs){
            clientInput.write(messageType.ordinal()+"\n");
            clientInput.write(data +"\n");
            clientInput.flush();
        }
    }


    public void startServer(){

        System.out.println("SERVER | started successfully");
        try{
            while(running){

                Socket newClientSocket = server.accept();
                System.out.println("SERVER | new connection "+ newClientSocket.toString());
                PrintWriter clientOutput = new PrintWriter(newClientSocket.getOutputStream(), false);
                synchronized (this.clientInputs){
                    this.clientInputs.add(clientOutput);
                }
                //send id to client
                clientOutput.write("0\n");
                clientOutput.write(lastID++ + "\n");
                clientOutput.flush(); // Send off the data

                //set up socket for the game
                ClientHandler clientHandler = new ClientHandler(newClientSocket);
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
                    gameServer.sendToAllClient(MessageType.GAME_STATE,"Game data");
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
