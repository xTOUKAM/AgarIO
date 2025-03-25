package iut.gon.agario.main;

import iut.gon.agario.controller.LocalGameController;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.view.LocalGameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameWorld gameWorld = new GameWorld(800, 600);
        LocalGameController gameController = new LocalGameController(gameWorld);
        LocalGameView gameView = new LocalGameView(gameWorld, gameController);
        Scene scene = new Scene(gameView.getRootPane(), 800, 600);
        primaryStage.setTitle("Agario Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        gameController.startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
