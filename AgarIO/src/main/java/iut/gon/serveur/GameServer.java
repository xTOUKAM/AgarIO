package iut.gon.serveur;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class GameServer{

    LinkedList<PrintWriter> clientInput;
    public ServerSocket server;
    private boolean running = true;
    private int lastID = 1;


    public GameServer(int port){
        clientInput = new LinkedList<PrintWriter>();
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {throw new RuntimeException(e);}
    }


    public void startServer(){

        System.out.println("SERVER | started successfully");

        Thread stopThread = new Thread(){
            public void run(){
                Scanner inputReader = new Scanner(System.in);
                //test for server stop
                while (inputReader.hasNext()){
                    if("stop".equals(inputReader.next())){
                        running = false;
                    }
                }
            }
        };
        stopThread.start();

        while(running){

            try{
                Socket newClientSocket = server.accept();
                System.out.println("SERVER | new connection "+ newClientSocket.toString());
                DataOutputStream clientOutputStream = new DataOutputStream(newClientSocket.getOutputStream());
                synchronized (this.clientInput){
                    this.clientInput.add(new PrintWriter(clientOutputStream,true));
                }
                clientOutputStream.writeByte(1);
                clientOutputStream.writeUTF(String.valueOf(lastID++));
                clientOutputStream.flush(); // Send off the data
                clientOutputStream.close();

                ClientHandler clientHandler = new ClientHandler(newClientSocket);
                clientHandler.start();

            }catch (IOException e) {
                throw new RuntimeException(e);
            }



        }
        System.out.println("SERVER | stopped successfully");
    }


    public static void main(String[] args) {
        GameServer gameServer = new GameServer(Integer.parseInt(args[0]));
        Thread lisenningThread = new Thread(){
            public void run(){
                gameServer.startServer();
            }
        };
        lisenningThread.start();

        //TODO LAUNCH GAME ENGINE THREAD
    }
}
