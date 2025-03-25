package iut.gon.agario.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Launcher extends Application {
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        root.setSpacing(10);

        // Bouton pour jouer en local
        Button localGameButton = new Button("Jouer en local");
        localGameButton.setOnAction(e -> startLocalGame(primaryStage));

        // Bouton pour jouer en ligne
        Button networkGameButton = new Button("Jouer en ligne");
        networkGameButton.setOnAction(e -> startNetworkGame(primaryStage));

        root.getChildren().addAll(localGameButton, networkGameButton);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Game Launcher");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startLocalGame(Stage primaryStage) {
        Main mainApp = new Main();
        mainApp.start(primaryStage);
    }

    private void startNetworkGame(Stage primaryStage) {
        // TODO
    }

    public static void main(String[] args) {
        launch(args);
    }
}
