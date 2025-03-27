package iut.gon.renderer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

public class GameRendererTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Game Renderer Test");

        Canvas canvas = new Canvas(800, 600);
        GameRenderer gameRenderer = new GameRenderer(canvas);

        JSONObject jsonObject = new JSONObject();
        JSONArray playersArray = new JSONArray();
        JSONArray pelletsArray = new JSONArray();
        JSONObject gameWorldObject = new JSONObject();
        JSONObject cameraObject = new JSONObject();

        Random random = new Random();

        // Generate 10 players with random positions and properties
        for (int i = 1; i <= 10; i++) {
            JSONObject playerObject = new JSONObject();
            playerObject.put("name", "Player " + i);
            playerObject.put("x", random.nextDouble() * 800);
            playerObject.put("y", random.nextDouble() * 600);
            playerObject.put("mass", random.nextDouble() * 50 + 10);
            playerObject.put("color", String.format("#%06X", (random.nextInt(0xFFFFFF + 1))));
            playersArray.put(playerObject);
        }

        // Generate 20 pellets with random positions and properties
        for (int i = 1; i <= 20; i++) {
            JSONObject pelletObject = new JSONObject();
            pelletObject.put("x", random.nextDouble() * 800);
            pelletObject.put("y", random.nextDouble() * 600);
            pelletObject.put("radius", random.nextDouble() * 15 + 5);
            pelletObject.put("color", String.format("#%06X", (random.nextInt(0xFFFFFF + 1))));
            pelletsArray.put(pelletObject);
        }

        // Set game world properties
        gameWorldObject.put("width", 800.0);
        gameWorldObject.put("height", 600.0);

        // Set camera properties
        cameraObject.put("startX", 0.0);
        cameraObject.put("startY", 0.0);
        cameraObject.put("width", 800.0);
        cameraObject.put("height", 600.0);
        cameraObject.put("scale", 1.0);

        jsonObject.put("players", playersArray);
        jsonObject.put("pellets", pelletsArray);
        jsonObject.put("gameWorld", gameWorldObject);
        jsonObject.put("camera", cameraObject);

        gameRenderer.decodeJSON(jsonObject.toString());
        gameRenderer.update();

        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}