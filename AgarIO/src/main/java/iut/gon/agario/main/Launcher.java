package iut.gon.agario.main;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static javafx.geometry.Pos.CENTER;

public class Launcher extends Application {
    private String userName = "Default Username";
    @Override
    public void start(Stage primaryStage) {


        //Textbox pour le pseudo
        TextField usernameField = new TextField();

        // Bouton pour jouer en local
        Button localGameButton = new Button("Jouer en local");
        localGameButton.setOnAction(e -> startLocalGame(primaryStage));
        userName = usernameField.getText();

        // Bouton pour jouer en ligne
        Button networkGameButton = new Button("Jouer en ligne");
        networkGameButton.setOnAction(e -> startNetworkGame(primaryStage));
        userName = usernameField.getText();
        //idk how network game will handle

        //vbox pour afficher les élements
        VBox root = new VBox(localGameButton,networkGameButton,usernameField);
        root.setSpacing(10);
        root.setAlignment(Pos.CENTER);

        //root.getChildren().add(root);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Game Launcher");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startLocalGame(Stage primaryStage) {
        Main mainApp = new Main();
        mainApp.start(primaryStage);
        mainApp.player.name = userName;
    }

    private void startNetworkGame(Stage primaryStage) {
        // TODO
    }

    public static void main(String[] args) {
        launch(args);
    }
}
