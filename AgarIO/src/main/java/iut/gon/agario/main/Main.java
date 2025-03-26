package iut.gon.agario.main;

import iut.gon.agario.controller.LocalGameController;
import iut.gon.agario.model.*;
import iut.gon.agario.model.AI.AIPlayer;
import iut.gon.agario.model.AI.EatFoodStrategy;
import iut.gon.agario.view.LocalGameView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static javafx.application.Application.launch;

public class Main extends Application {

    public static final int CANVAS_WIDTH = 200;
    public static final int CANVAS_HEIGHT = 200;
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    private static final int NUM_PASTILLES = 100;
    private static final int NUM_BOTS = 5;
    private List<Pastille> pastilles;
    private List<AIPlayer> bots;
    private Player player;
    private GameWorld gameWorld;
    private Camera camera;
    private Canvas gameCanvas;
    private double x;
    private double y;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("Agario Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize game world
        gameWorld = new GameWorld(WIDTH, HEIGHT);

        // Initialize game elements
        pastilles = new ArrayList<>();
        bots = new ArrayList<>();

        // Create game canvas
        gameCanvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(gameCanvas);

        // Spawn pastilles
        spawnPastilles(root);

        // Spawn bots
        spawnBots(root);

        // Spawn player
        spawnPlayer(root);

        // Create camera
        camera = new Camera(player);

        // Create mini-map
        Canvas miniMap = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        miniMap.setLayoutX(WIDTH - 210);
        miniMap.setLayoutY(HEIGHT - 210);
        root.getChildren().add(miniMap);

        // Score box
        VBox scoreBox = new VBox();
        scoreBox.setLayoutX(WIDTH - 210);
        scoreBox.setLayoutY(10);
        root.getChildren().add(scoreBox);

        // Chat box
        /*TextArea chatHistory = new TextArea();
        chatHistory.setPrefHeight(100);
        chatHistory.setDisable(true);
        chatHistory.setLayoutX(10);
        chatHistory.setLayoutY(HEIGHT - 120);
        root.getChildren().add(chatHistory);

        TextField chatInput = new TextField();
        chatInput.setLayoutX(10);
        chatInput.setLayoutY(HEIGHT - 30);
        root.getChildren().add(chatInput);*/

        if(player != null) {

            scene.setOnMouseMoved(e -> {
                x = e.getX();
                y = e.getY();
            });

            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.SPACE) {
                    player.split();
                }
            });
        }

        // Game loop
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(33), e -> {
            update(miniMap, scoreBox);
            if(player != null)gameWorld.move(x,y,player);}));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void spawnPastilles(Pane root) {
        Random rand = new Random();
        for (int i = 0; i < NUM_PASTILLES; i++) {
            double x = rand.nextDouble() * WIDTH;
            double y = rand.nextDouble() * HEIGHT;
            Pastille pastille = new Pastille(x, y, 5, Color.GREEN);
            pastilles.add(pastille);
            root.getChildren().add(pastille.getRepresentation());
            gameWorld.addPastille(pastille);
        }
    }

    private void spawnBots(Pane root) {
        Random rand = new Random();
        for (int i = 0; i < NUM_BOTS; i++) {
            double x = rand.nextDouble() * WIDTH;
            double y = rand.nextDouble() * HEIGHT;
            AIPlayer bot = new AIPlayer(x, y, 20, Color.RED);
            bot.setStrategy(new EatFoodStrategy());
            bots.add(bot);
            for (Cell cell : bot.getCells()) {
                root.getChildren().add(cell.getRepresentation());
            }
        }
    }

    private void spawnPlayer(Pane root) {
        player = new Player(WIDTH / 2, HEIGHT / 2, 10, Color.BLUE);
        for (Cell cell : player.getCells()) {
            root.getChildren().add(cell.getRepresentation());
        }
    }

    private void update(Canvas miniMap, VBox scoreBox) {
        // Update bots movements
        for (AIPlayer bot : bots) {
            bot.makeDecision(gameWorld);
        }

        // Check for collisions between player and pastilles
        checkCollisions(player);

        // Check for collisions between bots and pastilles
        for (AIPlayer bot : bots) {
            checkCollisions(bot);
            checkCollisionsAI(player);
        }

        camera.update();
        render(miniMap, scoreBox);
    }

    private void render(Canvas miniMap, VBox scoreBox) {
        // Clear the mini-map
        GraphicsContext miniMapGC = miniMap.getGraphicsContext2D();
        miniMapGC.clearRect(0, 0, miniMap.getWidth(), miniMap.getHeight());

        // Draw players on mini-map
        for (Player player : gameWorld.getPlayers()) {
            miniMapGC.fillOval(player.getX() / 4, player.getY() / 4, 5, 5);
        }

        // Draw game world (centered on player)
        GraphicsContext gameGC = gameCanvas.getGraphicsContext2D();
        gameGC.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        gameWorld.draw(gameGC, camera);

        // Update score box
        scoreBox.getChildren().clear();
        Leaderboard leaderboard = new Leaderboard(gameWorld);
        List<Player> topPlayers = leaderboard.getLeaderboard(10);
    }

    private void checkCollisionsAI(Player currentPlayer) {
        List<AIPlayer> eatenAI = new ArrayList<>();
        for (AIPlayer aiPlayer : bots) {
            for (Cell aiCell : aiPlayer.getCells()) {
                for (Cell playerCell : currentPlayer.getCells()) {
                    if (playerCell.getRepresentation().getBoundsInParent().intersects(aiCell.getRepresentation().getBoundsInParent())) {
                        if (gameWorld.canAbsorb(aiCell, playerCell)) {
                            gameWorld.absorb(aiCell, playerCell);
                            eatenAI.add(aiPlayer);
                        } else if (gameWorld.canAbsorb(playerCell, aiCell)) {
                            gameWorld.absorb(playerCell, aiCell);
                            Pane pane = (Pane) playerCell.getRepresentation().getParent();
                            if (pane != null) {
                                pane.getChildren().remove(playerCell.getRepresentation());
                            }
                        }
                    }
                }
            }
        }

        bots.removeAll(eatenAI);
        for (AIPlayer aiPlayer : eatenAI) {
            for (Cell aiCell : aiPlayer.getCells()) {
                if (currentPlayer.getCells().get(0).getRepresentation().getParent() instanceof Pane parent) {
                    parent.getChildren().remove(aiCell.getRepresentation());
                }
            }
        }
    }

    private void checkCollisions(Player player) {
        List<Pastille> eatenPastilles = new ArrayList<>();
        for (Pastille pastille : pastilles) {
            for (Cell playerCell : player.getCells()) {  // Parcours des cellules du player
                if (playerCell.getRepresentation().getBoundsInParent().intersects(pastille.getRepresentation().getBoundsInParent())) {
                    eatenPastilles.add(pastille);
                    playerCell.setMass(player.getMass() + 1);  // Augmente la masse lorsqu'une pastille est mangée
                }
            }
        }
        pastilles.removeAll(eatenPastilles);
        for (Pastille pastille : eatenPastilles) {
            for (Cell playerCell : player.getCells()) {  // Supprime uniquement les pastilles liées à une cellule spécifique
                if (playerCell.getRepresentation().getParent() instanceof Pane parent) {
                    parent.getChildren().remove(pastille.getRepresentation());
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}