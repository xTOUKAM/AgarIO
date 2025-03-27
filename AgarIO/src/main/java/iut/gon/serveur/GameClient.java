package iut.gon.serveur;

import iut.gon.renderer.GameRenderer;
import iut.gon.renderer.GameRendererTest;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private GameRenderer gameRenderer;
    private GameRendererTest gameRendererTest;


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
                    switch (MessageType.values()[Integer.parseInt(serverOutput.readLine())]) {
                        case SERVER_ID -> {
                            this.ID = Integer.parseInt(serverOutput.readLine());
                            System.out.println("CLIENT | ID received => " + this.ID);
                        }
                        case SERVER_INITIAL_GAME_STATE -> {
                            //TODO initialize  client renderer
                            System.out.println("CLIENT | Initial game state: " + serverOutput.readLine());
                            Communication.send(serverInput, MessageType.CLIENT_STATUS, "true");
                        }
                        case SERVER_GAME_STATE -> {
                            String gameState = serverOutput.readLine();
                            System.out.println("CLIENT | Game state received: " + gameState);
                            gameRenderer.decodeJSON(gameState);
                        }
                        case SERVER_STOP -> {
                            System.out.println("CLIENT | server stopped");
                            keepListening = false;
                        }
                        default -> {
                            keepListening = false;
                            System.out.println("CLIENT | server stopped unexpectedly");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        Canvas clientGameDisplay = new Canvas();
        GameRenderer clientGameRender = new GameRenderer(clientGameDisplay);
        //Listen to server output
        gameClient.serverMessageHandler();

        gameClient.gameRenderer = new GameRenderer(clientGameDisplay);
        gameClient.gameRenderer.start();
    }

}