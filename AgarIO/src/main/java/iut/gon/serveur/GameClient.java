package iut.gon.serveur;

import iut.gon.renderer.GameRenderer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class GameClient extends Application {

    private int ID;
    private BufferedReader serverOutput;
    private PrintWriter serverInput;
    private GameRenderer gameRenderer;
    private Canvas canvas;

    public GameClient() {
    }

    public GameClient(String serverAddress, int serverPort) {
        //Try connecting to server
        try {
            Socket socket = new Socket(InetAddress.getByName(serverAddress), serverPort);
            serverOutput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverInput = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("CLIENT | Connection to server successful");
        } catch (IOException e) {
            System.out.println("CLIENT | error while trying to connect to server");
            throw new RuntimeException(e);
        }
    }

    private void serverMessageHandler() {
        Thread ConnectionToServer = new Thread(() -> {
            boolean keepListening = true;
            while (keepListening) {
                try {
                    switch (MessageType.values()[Integer.parseInt(serverOutput.readLine())]) {
                        case SERVER_ID -> {
                            this.ID = Integer.parseInt(serverOutput.readLine());
                            System.out.println("CLIENT | ID received => " + this.ID);
                        }
                        case SERVER_INITIAL_GAME_STATE -> {
                            String initialState = serverOutput.readLine();
                            System.out.println("CLIENT | Initial game state: " + initialState);
                            try {
                                gameRenderer.decodeJSON(initialState);
                            } catch (Exception e) {
                                System.err.println("CLIENT | Error decoding JSON: " + e.getMessage());
                                e.printStackTrace();
                            }
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

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Game Client");

        canvas = new Canvas(1280, 720); // Changer la taille du Canvas ici
        gameRenderer = new GameRenderer(canvas);

        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root, 1280, 720)); // Changer la taille de la scène ici
        primaryStage.show();

        GameClient gameClient = new GameClient("127.0.0.1", 1234);
        gameClient.serverMessageHandler();
        gameClient.gameRenderer = gameRenderer;

        canvas.setOnMouseMoved(event -> {
            double x = event.getX();
            double y = event.getY();
            Communication.send(gameClient.serverInput, MessageType.CLIENT_MOVEMENT, x + "," + y );
        });

        primaryStage.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                Communication.send(gameClient.serverInput, MessageType.CLIENT_SPLIT, "split" );
            }
        });


        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(33), e -> gameRenderer.update())); // Ajuster pour 30 FPS
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

