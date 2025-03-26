package iut.gon.agario.main;

import iut.gon.agario.model.*;
import iut.gon.agario.model.AI.AIPlayer;
import iut.gon.agario.model.AI.EatFoodStrategy;
import iut.gon.agario.model.factory.AIFactory;
import iut.gon.agario.model.factory.PelletFactory;
import iut.gon.agario.model.factory.PlayerFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main extends Application {

    public static final int CANVAS_WIDTH = 200;
    public static final int CANVAS_HEIGHT = 200;
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    private static final int NUM_PELLETS = 100;
    private static final int NUM_BOTS = 5;
    private List<Pellet> pellets;
    private CopyOnWriteArrayList<AIPlayer> bots;
    private Player player;
    private GameWorld gameWorld;
    private Camera camera;
    private Canvas gameCanvas;

    @Override
    public void start(Stage primaryStage) {
        //will be used with quadtree
        /*GameWorld gameWorld = new GameWorld(800, 600);
        LocalGameController gameController = new LocalGameController(gameWorld);
        LocalGameView gameView = new LocalGameView(gameWorld, gameController);
        Scene scene = new Scene(gameView.getRootPane(), 800, 600);*/

        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("Agario Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize game world
        gameWorld = new GameWorld(WIDTH, HEIGHT);

        // Initialize game elements
        pellets = new ArrayList<>();
        bots = new CopyOnWriteArrayList<>();

        // Create game canvas
        gameCanvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(gameCanvas);

        // Spawn pellets
        spawnPellets(root);

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
        TextArea chatHistory = new TextArea();
        chatHistory.setPrefHeight(100);
        chatHistory.setDisable(true);
        chatHistory.setLayoutX(10);
        chatHistory.setLayoutY(HEIGHT - 120);
        root.getChildren().add(chatHistory);

        TextField chatInput = new TextField();
        chatInput.setLayoutX(10);
        chatInput.setLayoutY(HEIGHT - 30);
        root.getChildren().add(chatInput);

        scene.setOnMouseMoved(e -> {
            gameWorld.move(e.getX(), e.getY(), player);
        });

        // Game loop
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), e -> update(miniMap, scoreBox)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void spawnPellets(Pane root) {
        Random rand = new Random();
        for (int i = 0; i < NUM_PELLETS; i++) {
            PelletFactory pelletFactory = new PelletFactory(gameWorld);
            Pellet pellet = (Pellet) pelletFactory.factory();
            pellets.add(pellet);
            root.getChildren().add(pellet.getRepresentation());
            gameWorld.addPellet(pellet);
        }
    }

    private void spawnBots(Pane root) {
        Random rand = new Random();
        for (int i = 0; i < NUM_BOTS; i++) {
            AIFactory aiFactory = new AIFactory(gameWorld);
            AIPlayer bot = (AIPlayer) aiFactory.factory();
            //bot.setStrategy(new EatFoodStrategy());
            bot.name = Names.getRandomName().name();
            bots.add(bot);
            root.getChildren().add(bot.getRepresentation());
            gameWorld.addPlayer(bot);
        }
    }

    private void spawnPlayer(Pane root) {
        PlayerFactory playerFactory = new PlayerFactory(gameWorld);
        Player p = (Player) playerFactory.factory();
        player = p;
        p.name = "C MOI WSH";
        root.getChildren().add(player.getRepresentation());
        gameWorld.addPlayer(player);
    }

    private void update(Canvas miniMap, VBox scoreBox) {
        // Update bots movements
        for (AIPlayer bot : bots) {
            bot.makeDecision(gameWorld);
        }

        // Check for collisions between player and pellets
        checkCollisions(player);

        // Check for collisions between bots and pellets
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

        ObservableList<Label> observableList = FXCollections.observableArrayList();
        for (Player p : topPlayers) {
            Label label = new Label(p.getName()+"     |     "+(int)p.getMass());
            observableList.add(label);
        }
        Bindings.bindContent(scoreBox.getChildren(), observableList);

    }

    private void checkCollisionsAI(Player currentPlayer) {
        List<AIPlayer> eatenAI = new ArrayList<>();
        for(AIPlayer aiPlayer : bots) {
            if(currentPlayer.getRepresentation().getBoundsInParent().intersects(aiPlayer.getRepresentation().getBoundsInParent())) {
                eatenAI.add(aiPlayer);
                currentPlayer.setMass(currentPlayer.getMass() + aiPlayer.getMass());
            }
        }

        bots.removeAll(eatenAI);
        for(AIPlayer aiPlayer : eatenAI) {
            if(currentPlayer.getRepresentation().getParent() instanceof Pane parent) {
                parent.getChildren().remove(aiPlayer.getRepresentation());
            }
        }
    }

    private void checkCollisions(Player player) {
        List<Pellet> eatenPellets = new ArrayList<>();
        for (Pellet pellet : pellets) {
            if (player.getRepresentation().getBoundsInParent().intersects(pellet.getRepresentation().getBoundsInParent())) {
                eatenPellets.add(pellet);
                player.setMass(player.getMass() + 1); // Increase player mass when eating a pellet
            }
        }
        pellets.removeAll(eatenPellets);
        for (Pellet pellet : eatenPellets) {
            if (player.getRepresentation().getParent() instanceof Pane parent) {
                parent.getChildren().remove(pellet.getRepresentation());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}