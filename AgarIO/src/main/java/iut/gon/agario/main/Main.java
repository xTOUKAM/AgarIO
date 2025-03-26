package iut.gon.agario.main;

import iut.gon.agario.controller.LocalGameController;
import iut.gon.agario.model.*;
import iut.gon.agario.model.AI.AIPlayer;
import iut.gon.agario.model.AI.EatFoodStrategy;
import iut.gon.agario.view.LocalGameView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
    private List<Pellet> pastilles;
    private List<AIPlayer> bots;
    private CompositePlayer compositePlayer;
    private GameWorld gameWorld;
    private Camera camera;
    private Canvas gameCanvas;
    private double x, y;

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
        camera = new Camera(compositePlayer);


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
/*
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
        root.getChildren().add(chatInput);*/

        scene.setOnMouseMoved(e -> {
            x = e.getX();
            y = e.getY();
        });

        scene.setOnKeyPressed(e ->{
            if (e.getCode() == KeyCode.SPACE) {
                compositePlayer.split(x,y);
                List<Player> newPlayers = compositePlayer.getPlayers();
                for (Player player : newPlayers) {
                    root.getChildren().add(player.getRepresentation());
                }
            }
        });

        // Game loop
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(33), e -> {
            update(miniMap, scoreBox);
            compositePlayer.update();
            gameWorld.move(x,y,compositePlayer);}));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void spawnPastilles(Pane root) {
        Random rand = new Random();
        for (int i = 0; i < NUM_PASTILLES; i++) {
            double x = rand.nextDouble() * WIDTH;
            double y = rand.nextDouble() * HEIGHT;
            Pellet pastille = new Pellet(x, y, 5, Color.GREEN);
            pastilles.add(pastille);
            root.getChildren().add(pastille.getRepresentation());
            gameWorld.addPellet(pastille);
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
            root.getChildren().add(bot.getRepresentation());
            gameWorld.addPlayer(bot);
        }
    }

    private void spawnPlayer(Pane root) {
        Player player = new Player(WIDTH / 2, HEIGHT / 2, 30, Color.BLUE);
        compositePlayer = new CompositePlayer(player, gameWorld);
        root.getChildren().add(player.getRepresentation());
    }

    private synchronized void update(Canvas miniMap, VBox scoreBox) {
        // Crée une copie de la liste des bots pour éviter les problèmes de modification concurrente
        List<AIPlayer> botsCopy = new ArrayList<>(bots);

        // Met à jour la logique des bots
        for (AIPlayer bot : botsCopy) {
            bot.makeDecision(gameWorld);
        }

        // Met à jour l'état du monde de jeu
        gameWorld.update();

        // Vérifie les collisions entre le joueur et les pastilles
        checkCollisions(compositePlayer);

        // Utilisation d'une copie de la liste pour itérer et éviter les erreurs de modification concurrente
        synchronized (this) {
            for (AIPlayer bot : botsCopy) {
                // Crée un composite pour chaque bot
                CompositePlayer compositeBot = new CompositePlayer(bot, gameWorld);

                // Vérifie les collisions entre les bots et le joueur composite
                checkCollisions(compositeBot);
                checkCollisionsAI(compositePlayer);

                // Vérifie les collisions entre les bots
                gameWorld.checkBotCollisions(bots);
            }
        }

        // Mise à jour de la caméra
        camera.update();

        // Déplace le joueur composite en fonction des coordonnées de la souris
        compositePlayer.move(x, y);

        // Rend la scène et met à jour le mini-map et le score
        render(miniMap, scoreBox);
    }
/*


        checkCollisions(compositePlayer); // Vérifie les collisions entre le joueur et les pastilles
        checkCollisionsAI(compositePlayer); // Vérifie les collisions entre le joueur et les bots
        gameWorld.checkBotCollisions(bots);  // Vérifier les collisions entre bots

        // Redessiner la scène (y compris les positions des bots et du joueur)
        compositePlayer.update();
        render(miniMap, scoreBox);  // Redessine le jeu et met à jour les positions à chaque tick de la boucle
    }
*/


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

    private void checkCollisionsAI(CompositePlayer compositePlayer) {
        List<AIPlayer> eatenAI = new ArrayList<>();
        for (Player player : compositePlayer.getPlayers()) {
            for (AIPlayer aiPlayer : bots) {
                if (player.getRepresentation().getBoundsInParent().intersects(aiPlayer.getRepresentation().getBoundsInParent())) {
                    eatenAI.add(aiPlayer);
                    player.setMass(player.getMass() + aiPlayer.getMass());
                }
            }
        }

        bots.removeAll(eatenAI);
        for(AIPlayer aiPlayer : eatenAI) {
            for (Player player : compositePlayer.getPlayers()) {
                if (player.getRepresentation().getParent() instanceof Pane parent) {
                    parent.getChildren().remove(aiPlayer.getRepresentation());
                }
            }
        }
    }

    private void checkCollisions(CompositePlayer compositePlayer) {
        List<Pellet> eatenPastilles = new ArrayList<>();
        for (Player player : compositePlayer.getPlayers()){
            for (Pellet pastille : pastilles) {
                if (player.getRepresentation().getBoundsInParent().intersects(pastille.getRepresentation().getBoundsInParent())) {
                    eatenPastilles.add(pastille);
                    player.setMass(player.getMass() + 1);
                }
            }
        }

        pastilles.removeAll(eatenPastilles);
        for (Pellet pastille : eatenPastilles) {
            for (Player player : compositePlayer.getPlayers()) {
                if (player.getRepresentation().getParent() instanceof Pane parent) {
                    parent.getChildren().remove(pastille.getRepresentation());
                }
            }
        }

        List<Player> players = compositePlayer.getPlayers();
        for (Player player1 : players) {
            for (Player player2 : players) {
                if (player1.getMass() > player2.getMass()) {
                    gameWorld.absorb(player2, player1);
                } else {
                    gameWorld.absorb(player1, player2);
                }
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}