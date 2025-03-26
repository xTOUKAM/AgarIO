package iut.gon.agario.main;

import iut.gon.agario.Config;
import iut.gon.agario.model.*;
import iut.gon.agario.model.AI.AIPlayer;
import iut.gon.agario.model.AI.EatFoodStrategy;
import iut.gon.agario.view.SettingsDialog;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
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
    private List<Pellet> pellets;
    private List<AIPlayer> bots;
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
        bots = new ArrayList<>();

        // Create game canvas
        gameCanvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(gameCanvas);

        // Spawn pellets
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
        TextArea chatHistory = new TextArea();
        chatHistory.setPrefHeight(100);
        chatHistory.setDisable(true);
        chatHistory.setLayoutX(10);
        chatHistory.setLayoutY(HEIGHT - 120);
        root.getChildren().add(chatHistory);

        TextField chatInput = new TextField();
        chatInput.setDisable(true);
        StackPane inputPane = new StackPane();
        inputPane.getChildren().add(chatInput);
        inputPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (chatInput.isDisabled()) {
                chatInput.setDisable(false);
                chatInput.requestFocus();
                event.consume();
            }
        });

        scene.setOnMouseClicked(e ->{
            if(!inputPane.isFocused() && !chatInput.isDisabled()){
                chatInput.setDisable(true);
            }
        });
        inputPane.setLayoutX(10);
        inputPane.setLayoutY(HEIGHT - 30);
        root.getChildren().add(inputPane);

        scene.setOnMouseMoved(e -> {
            gameWorld.move(e.getX(), e.getY(), player);
        });

        //game settings
        Button settingsButton = new Button("Settings");

        settingsButton.setLayoutX(0);
        settingsButton.setLayoutY(0);
        root.getChildren().add(settingsButton);
        settingsButton.setOnAction(e ->{
            SettingsDialog dialog = new SettingsDialog();
            dialog.showAndWait().ifPresent(result->{
                Config.ABSORPTION_RATIO = result.getrAbsorptionRatio();
                Config.MERGE_OVERLAP = result.getrMergeOverlap();
                Config.DECAY_FACTOR = result.getrDecayFactor();
                Config.SPEED_DECAY_DURATION =result.getrSpeedDecayDuration();
                Config.CONTROL_RADIUS = result.getrControlRadius();
                Config.MIN_SPEED = result.getrMinSpeed();
                Config.PELLET_MASS = result.getrPelletMass();
                Config.INITIAL_MAX_SPEED = result.getrInitialMaxSpeed();
                Config.COEFFICIENT_ATTENUATION = result.getrCoefficientAttenuation();
                Config.MINIMUM_SPLIT = result.getrMinimumSplit();
                Config.MASS_BUFF_MULTIPLIER = result.getrMassBuffMultiplier();
                Config.MASS_NERF_MULTIPLIER = result.getrMassNerfMultiplier();
                Config.SPEED_BUFF_MULTIPLIER = result.getrSpeedBuffMultiplier();
                Config.SPEED_NERF_MULTIPLIER = result.getrSpeedNerfMultiplier();
                Config.BASE_ZOOM = result.getrBaseZoom();
            });
        });


        // Game loop
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), e -> update(miniMap, scoreBox)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void spawnPastilles(Pane root) {
        Random rand = new Random();
        for (int i = 0; i < NUM_PASTILLES; i++) {
            double x = rand.nextDouble() * WIDTH;
            double y = rand.nextDouble() * HEIGHT;
            Pellet pellet = new Pellet(x, y, 5, Color.GREEN);
            pellets.add(pellet);
            root.getChildren().add(pellet.getRepresentation());
            gameWorld.addPastille(pellet);
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
        player = new Player(WIDTH / 2, HEIGHT / 2, 30, Color.BLUE);
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
        for (Player p : topPlayers) {
            Label label = new Label(p.getName()+"     |     "+(int)p.getMass());
            scoreBox.getChildren().add(label);
        }
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