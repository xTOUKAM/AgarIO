package iut.gon.serveur;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class GameServer{

    private final LinkedList<PrintWriter> clientInputs;
    public ServerSocket server;
    private int lastID = 1;


    public GameServer(int port){
        clientInputs = new LinkedList<>();
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    //TODO use game state from game engine
    public void sendGameState(){
        for(PrintWriter clientInput: clientInputs){
            clientInput.println(2);
            clientInput.println("Game state");
        }
    }


    public void startServer(){

        System.out.println("SERVER | started successfully");
        try{
            while(true){

                Socket newClientSocket = server.accept();
                System.out.println("SERVER | new connection "+ newClientSocket.toString());
                DataOutputStream clientOutputStream = new DataOutputStream(newClientSocket.getOutputStream());
                synchronized (this.clientInputs){
                    this.clientInputs.add(new PrintWriter(clientOutputStream,true));
                }
                //send id to client
                clientOutputStream.writeByte(1);
                clientOutputStream.writeUTF(String.valueOf(lastID++));
                clientOutputStream.flush(); // Send off the data

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
        try {
            while (true) {
                gameServer.sendGameState();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("error with server clock");
        }



        //SERVER STOP
        Scanner inputReader = new Scanner(System.in);
        boolean running = true;
        //test for server stop
        while (running){
            while (inputReader.hasNext()){
                if("stop".equals(inputReader.next())){
                    running = false;
                    //TODO send error message to all client
                }
            }
        }
        System.out.println("SERVER | stopped successfully");
    }
}
