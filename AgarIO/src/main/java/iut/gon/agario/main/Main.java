package iut.gon.agario.main;

import iut.gon.agario.model.*;
import iut.gon.agario.model.AI.AIPlayer;
import iut.gon.agario.model.AI.EatFoodStrategy;
import iut.gon.agario.model.AI.RandomMovementStrategy;
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
    private static final int NUM_PASTILLES = 100;
    private static final int NUM_BOTS = 5;
    private List<Pellet> pellets;
    private List<AIPlayer> bots;
    private Player player;
    private GameWorld gameWorld;
    private Camera camera;
    private Canvas gameCanvas;

    @Override
    public void start(Stage primaryStage) {
        System.out.println("Application started!");
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("Agario Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize game world
        gameWorld = new GameWorld(WIDTH, HEIGHT);

        // Initialize game elements
        pellets = new ArrayList<>();
        bots = new ArrayList<>();

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
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(33), e -> update(miniMap, scoreBox)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();  // Si cette ligne n'est pas présente, le jeu ne commence pas
    }

    private void spawnPellets(Pane root) {
        Random rand = new Random();
        for (int i = 0; i < NUM_PASTILLES; i++) {
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
            bot.name = Names.getRandomName().name();
            bot.setStrategy(new EatFoodStrategy());
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
        root.getChildren().add(p.getRepresentation());
        gameWorld.addPlayer(p);
    }

    private void update(Canvas miniMap, VBox scoreBox) {
        // Update bots movements - chaque bot prend une nouvelle décision
        for (AIPlayer bot : bots) {
            bot.makeDecision(gameWorld);  // Appel à la logique de déplacement des bots
        }

        // Mettre à jour le monde du jeu
        gameWorld.update();  // Met à jour les collisions et la logique de jeu

        checkCollisions(player); // Vérifie les collisions entre le joueur et les pastilles
        checkCollisionsAI(player); // Vérifie les collisions entre le joueur et les bots
        gameWorld.checkBotCollisions(bots);  // Vérifier les collisions entre bots

        // Redessiner la scène (y compris les positions des bots et du joueur)
        render(miniMap, scoreBox);  // Redessine le jeu et met à jour les positions à chaque tick de la boucle
    }

    private void render(Canvas miniMap, VBox scoreBox) {
        // Clear the mini-map
        GraphicsContext miniMapGC = miniMap.getGraphicsContext2D();
        miniMapGC.clearRect(0, 0, miniMap.getWidth(), miniMap.getHeight());

        // Draw players on mini-map
        for (Player player : gameWorld.getPlayers()) {
            miniMapGC.fillOval(player.getX() / 4, player.getY() / 4, 5, 5);
        }

        // Clear the game canvas and redraw the game world
        GraphicsContext gameGC = gameCanvas.getGraphicsContext2D();
        gameGC.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Test simple : dessiner un cercle rouge pour vérifier que le rendu fonctionne
        gameGC.setFill(Color.RED);
        gameGC.fillOval(100, 100, 50, 50);  // Test : dessine un cercle rouge à la position (100, 100)

        // Redraw all players and bots (on the main game canvas)
        for (Player player : gameWorld.getPlayers()) {
            gameGC.setFill(player.getColor());
            gameGC.fillOval(player.getX() - player.getMass() / 2, player.getY() - player.getMass() / 2, player.getMass(), player.getMass());
        }

        // Update score box
        scoreBox.getChildren().clear();
        Leaderboard leaderboard = new Leaderboard(gameWorld);
        List<Player> topPlayers = leaderboard.getLeaderboard(10);
        for (Player p : topPlayers) {
            Label label = new Label(p.getName()+"     |     "+(int)p.getMass());
            scoreBox.getChildren().add(label);
        }
    }

    private void checkCollisionsAI(Player currentPlayer) {
        List<AIPlayer> eatenAI = new ArrayList<>();
        for (AIPlayer aiPlayer : bots) {
            if (currentPlayer.getRepresentation().getBoundsInParent().intersects(aiPlayer.getRepresentation().getBoundsInParent())) {
                eatenAI.add(aiPlayer);
                currentPlayer.setMass(currentPlayer.getMass() + aiPlayer.getMass()); // Augmenter la masse du joueur
            }
        }

        // Enlever les bots mangés
        bots.removeAll(eatenAI);
        for (AIPlayer aiPlayer : eatenAI) {
            if (currentPlayer.getRepresentation().getParent() instanceof Pane parent) {
                parent.getChildren().remove(aiPlayer.getRepresentation());  // Supprimer le bot de la scène
            }
        }
    }

    private void checkCollisions(Player player) {
        List<Pellet> eatenPellets = new ArrayList<>();
        for (Pellet pellet : pellets) {
            if (player.getRepresentation().getBoundsInParent().intersects(pellet.getRepresentation().getBoundsInParent())) {
                eatenPellets.add(pellet);
                player.setMass(player.getMass() + 1); // Augmente la masse du joueur lorsqu'il mange une pastille
            }
        }
        // Enlever les pastilles mangées
        pellets.removeAll(eatenPellets);
        for (Pellet pellet : eatenPellets) {
            if (player.getRepresentation().getParent() instanceof Pane parent) {
                parent.getChildren().remove(pellet.getRepresentation());  // Supprimer la pastille de la scène
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}