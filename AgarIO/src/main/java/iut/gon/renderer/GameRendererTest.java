package iut.gon.renderer;

import iut.gon.renderer.GameRenderer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

public class GameRendererTest extends Application {

    private GameRenderer gameRenderer;
    private Random random = new Random();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Game Renderer Test");

        Canvas canvas = new Canvas(800, 600);
        gameRenderer = new GameRenderer(canvas);

        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // Simulate server sending information at 30 FPS
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000 / 30), e -> updateGameState()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateGameState() {
        //gameRenderer.decodeJSON(json);
        gameRenderer.update();
    }

    public static void main(String[] args) {
        launch(args);
    }
}