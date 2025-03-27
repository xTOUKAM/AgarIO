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
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
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
    public static final int WIDTH = 1580;
    public static final int HEIGHT = 720;
    private static final int NUM_PASTILLES = 200;
    private static final int NUM_BOTS = 20;
    private List<Pellet> pellets;
    private CopyOnWriteArrayList<AIPlayer> bots;
    public Player player;
    private static GameWorld gameWorld;
    private Camera camera;
    private Canvas gameCanvas;
    private double x;
    private double y;

    public static GameWorld getGameWorld() {
        // Assurez-vous que le GameWorld a bien été initialisé avant de le renvoyer
        if (gameWorld == null) {
            initializeGameWorld();  // Initialiser s'il n'est pas encore créé
        }
        return gameWorld;
    }

    public static void initializeGameWorld() {
        if (gameWorld == null) {
            // Créer une instance du GameWorld avec la largeur et la hauteur du jeu
            gameWorld = new GameWorld(WIDTH, HEIGHT);

            // Ajouter des joueurs
            PlayerFactory playerFactory = new PlayerFactory(gameWorld);
            Player player = (Player) playerFactory.factory();
            gameWorld.addPlayer(player);

            // Ajouter des bots AI
            for (int i = 0; i < NUM_BOTS; i++) {
                AIFactory aiFactory = new AIFactory(gameWorld);
                AIPlayer bot = (AIPlayer) aiFactory.factory();
                bot.name = Names.getRandomName().name();  // Vous pouvez donner un nom aléatoire aux bots
                bot.setStrategy(new EatFoodStrategy());
                gameWorld.addPlayer(bot);
            }

            // Ajouter des pastilles
            for (int i = 0; i < NUM_PASTILLES; i++) {
                PelletFactory pelletFactory = new PelletFactory(gameWorld);
                Pellet pellet = (Pellet) pelletFactory.factory();
                gameWorld.addPellet(pellet);
            }
        }
    }

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
            if(player != null)gameWorld.move(x,y,player);
        }));
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
            for (Cell cell : bot.getCells()) {
                root.getChildren().add(cell.getRepresentation());
                root.getChildren().add(cell.getRepresentationPerimeter());
            }
        }
    }

    private void spawnPlayer(Pane root) {
        player = new Player(WIDTH / 2, HEIGHT / 2, 10, Color.BLUE);
        for (Cell cell : player.getCells()) {
            root.getChildren().add(cell.getRepresentation());
            root.getChildren().add(cell.getRepresentationPerimeter());
        }
    }

    private void update(Canvas miniMap, VBox scoreBox) {
        // Update bots movements - chaque bot prend une nouvelle décision
        for (AIPlayer bot : bots) {
            //bot.makeDecision(gameWorld);  // Appel à la logique de déplacement des bots
        }

        // Mettre à jour le monde du jeu
        gameWorld.update();  // Met à jour les collisions et la logique de jeu
            //bot.makeDecision(gameWorld);

        // Check for collisions between player and pastilles
        checkCollisions();

        // Redessiner la scène (y compris les positions des bots et du joueur)
        render(miniMap, scoreBox);  // Redessine le jeu et met à jour les positions à chaque tick de la boucle
    }

    public void render(Canvas miniMap, VBox scoreBox) {
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
            for(Cell cell : player.getCells())
            gameGC.setFill(cell.getColor());
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

    private void checkCollisions() {
        for (Cell cell : player.getCells()){
            List<Pellet> eatenPastilles = new ArrayList<>();
            for (Pellet pastille : pellets) {
                if (gameWorld.canAbsorbPellet(pastille, cell)) {
                    gameWorld.absorb(pastille, cell);
                    eatenPastilles.add(pastille);
                    // Lancer l'animation d'ouverture de la bouche lorsque la pastille est mangée
                    Animation animation = new Animation(cell.getRepresentation());
                    animation.startAnimation();

                    // Optionnel: fermer la bouche après un délai de 300ms pour simuler la fermeture
                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            animation.resetAnimation(); // Réinitialiser l'animation pour simuler la fermeture
                        }
                    }, 300);
                }
            }
            pellets.removeAll(eatenPastilles);
            for (Pellet pastille : eatenPastilles) {
                if (cell.getRepresentation().getParent() instanceof Pane parent) {
                    parent.getChildren().remove(pastille.getRepresentation());
                }
            }
        }


        try{
            for (Cell cell : player.getCells()){
                for(Cell cell2 : player.getCells()) {
                    if(cell != cell2){
                        gameWorld.absorb(cell,cell2);
                    }
                }
            }
        }
        catch (Exception ignore){

        }

        // Check for collisions between bots and pastilles
        for (AIPlayer bot : bots) {
            List<Cell> botCells = new ArrayList<>(bot.getCells());
            for(Cell cell : botCells) {
                List<Pellet> eatenPastilles = new ArrayList<>();
                for (Pellet pastille : pellets) {
                    if (gameWorld.canAbsorbPellet(pastille, cell)) {
                        gameWorld.absorb(pastille, cell);
                        eatenPastilles.add(pastille);

                        // Lancer l'animation d'ouverture de la bouche pour les bots aussi
                        Animation animation = new Animation(cell.getRepresentation());
                        animation.startAnimation();

                        // Optionnel: fermer la bouche après un délai de 300ms pour simuler la fermeture
                        new java.util.Timer().schedule(new java.util.TimerTask() {
                            @Override
                            public void run() {
                                animation.resetAnimation(); // Réinitialiser l'animation pour simuler la fermeture
                            }
                        }, 300);
                    }
                }
                pellets.removeAll(eatenPastilles);
                for (Pellet pastille : eatenPastilles) {
                    if (cell.getRepresentation().getParent() instanceof Pane parent) {
                        parent.getChildren().remove(pastille.getRepresentation());
                    }
                }
                // Vérification des collisions avec d'autres cellules (bots ou joueurs)
                List<Cell> playerCells = new ArrayList<>(player.getCells());
                for (Cell playerCell : playerCells) {
                    // Si le joueur mange une cellule du bot (ou vice-versa)
                    if (gameWorld.canAbsorb(cell, playerCell)) {
                        gameWorld.absorb(cell, playerCell);

                        // Lancer l'animation d'ouverture de la bouche pour le joueur qui mange un bot
                        Animation animation = new Animation(playerCell.getRepresentation());
                        animation.startAnimation();

                        // Optionnel: fermer la bouche après un délai de 300ms pour simuler la fermeture
                        new java.util.Timer().schedule(new java.util.TimerTask() {
                            @Override
                            public void run() {
                                animation.resetAnimation(); // Réinitialiser l'animation pour simuler la fermeture
                            }
                        }, 300);  // Temps de fermeture
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}