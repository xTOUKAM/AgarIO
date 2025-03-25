package iut.gon.agario.main;

import iut.gon.agario.controller.LocalGameController;
import iut.gon.agario.model.AI.AIPlayer;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Pastille;
import iut.gon.agario.model.Player;
import iut.gon.agario.view.LocalGameView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static javafx.application.Application.launch;

public class Main extends Application {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int NUM_PASTILLES = 50;
    private static final int NUM_BOTS = 5;
    private List<Pastille> pastilles;
    private List<Player> bots;
    private Player player;
    private AIPlayer aiPlayer;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("Agario");
        primaryStage.setScene(scene);
        primaryStage.show();

        pastilles = new ArrayList<>();
        bots = new ArrayList<>();

        spawnPastilles(root);
        spawnBots(root);
        spawnPlayer(root);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(33), e-> update()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void spawnPastilles(Pane root) {
        Random rand = new Random();
        for(int i = 0; i < NUM_PASTILLES; i++) {
            double x = rand.nextDouble() * WIDTH;
            double y = rand.nextDouble() * HEIGHT;
            Pastille pastille = new Pastille(x,y,5, Color.GREEN);
            pastilles.add(pastille);
            root.getChildren().add(pastille.getRepresentation());
        }
    }

    private void spawnBots(Pane root) {
        Random rand = new Random();

        for(int i = 0; i < NUM_BOTS; i++) {
            double x = rand.nextDouble() * WIDTH;
            double y = rand.nextDouble() * HEIGHT;
            Player bot = new Player(x, y, 20, Color.RED);
            bots.add(bot);
            root.getChildren().add(bot.getRepresentation());
        }
    }

    private void spawnPlayer(Pane root) {
        player = new Player((double) WIDTH / 2, (double) HEIGHT / 2, 30, Color.BLUE);
        root.getChildren().add(player.getRepresentation());
    }

    private void update() {
        // Update player position based on input (to be implemented)
        // updatePlayerPosition();

        // Update bots movements
        for (Player bot : bots) {
            moveBot(bot);
        }

        // Check for collisions between player and pastilles
        checkCollisions(player);

        // Check for collisions between bots and pastilles
        for (Player bot : bots) {
            checkCollisions(bot);
        }
    }

    private void moveBot(Player bot) {
        Random rand = new Random();
        double deltaX = rand.nextDouble() * 2 - 1; // Random value between -1 and 1
        double deltaY = rand.nextDouble() * 2 - 1; // Random value between -1 and 1
        bot.setX(bot.getX() + deltaX * bot.getMass());
        bot.setY(bot.getY() + deltaY * bot.getMass());
    }

    private void checkCollisions(Player player) {
        List<Pastille> eatenPastilles = new ArrayList<>();
        for (Pastille pastille : pastilles) {
            if (player.getRepresentation().getBoundsInParent().intersects(pastille.getRepresentation().getBoundsInParent())) {
                eatenPastilles.add(pastille);
                player.setMass(player.getMass() + 1); // Increase player mass when eating a pastille
            }
        }
        pastilles.removeAll(eatenPastilles);
        for (Pastille pastille : eatenPastilles) {
            if (player.getRepresentation().getParent() instanceof Pane parent) {
                parent.getChildren().remove(pastille.getRepresentation());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
