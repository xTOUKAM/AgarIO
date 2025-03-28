package iut.gon.serveur;

import iut.gon.agario.gameEngine.GameEngine;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class GameServer {

    private final LinkedList<ClientInput> clientInputs = new LinkedList<>();
    private final ServerSocket server;
    private int lastID = 1;
    private boolean running = true;
    private GameEngine gameEngine = null;
    public static int WIDTH_MAP = 200;
    public static int HEIGHT_MAP = 200;


    public GameServer(int port){
        try {
            server = new ServerSocket(port);
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void sendToAllClient(MessageType messageType, String data, boolean onlineCheck){
        for(ClientInput clientInput : clientInputs){
            if(onlineCheck){
                if(clientInput.online){
                    Communication.send(clientInput.printWriter, messageType, data);
                }
            } else {
                Communication.send(clientInput.printWriter, messageType, data);
            }
        }
    }

    public void sendToClientByID(MessageType messageType, String data, int ID, boolean onlineCheck){
        for(ClientInput clientInput : clientInputs){
            if(clientInput.ID == ID){
                if(onlineCheck){
                    if(clientInput.online){
                        Communication.send(clientInput.printWriter, messageType, data);
                    }
                } else {
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
        System.out.println("SERVER | Started successfully");
        try{
            while(running){
                Socket newClientSocket = server.accept();
                System.out.println("SERVER | New connection ID => " + this.lastID + " SOCKET => " + newClientSocket.toString());
                PrintWriter clientOutput = new PrintWriter(newClientSocket.getOutputStream(), false);
                synchronized (this.clientInputs){
                    this.clientInputs.add(new ClientInput(lastID, clientOutput));
                }

                // Send id to client
                sendToClientByID(MessageType.SERVER_ID, "" + lastID, lastID, false);

                // Send first game state to client for rendering
                String data = "Initial game state"; //TODO get game state from game engine instead
                sendToClientByID(MessageType.SERVER_INITIAL_GAME_STATE, data, lastID, false);

                // Set up socket for client input
                ClientHandler clientHandler = new ClientHandler(newClientSocket, this, lastID++, gameEngine);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println("SERVER | Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GameServer.launch(1234);
    }

    public static void launch(int port){
        GameServer gameServer = new GameServer(port);
        GameEngine gameEngine = new GameEngine(gameServer, WIDTH_MAP, HEIGHT_MAP, 10);


        gameServer.setGameEngine(gameEngine);
        Thread listeningThread = new Thread(gameServer::startServer);

        gameEngine.start();
        listeningThread.start();


        // SERVER STOP
        Scanner inputReader = new Scanner(System.in);
        while (gameServer.running){
            if(inputReader.hasNext()){
                String text = inputReader.next();
                if("stop".equals(text) || "STOP".equals(text)){
                    gameServer.running = false;
                    // Send stop message to all clients
                    gameServer.sendToAllClient(MessageType.SERVER_STOP, "stop", false);
                    System.out.println("SERVER | Stopped successfully");
                    System.exit(0);
                }
            }
        }
    }
}