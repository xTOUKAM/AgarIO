package iut.gon.agario.view;

import iut.gon.agario.model.CompositePlayer;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Player;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class GameView {
    protected final GameWorld gameWorld;
    protected final Pane gamePane;

    public GameView(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.gamePane = new Pane();
    }

    private void initializeGameView() {
        gamePane.setPrefSize(gameWorld.getWidth(), gameWorld.getHeight());
        gamePane.setStyle("-fx-background-color: lightgreen;");

        for(Player player : gameWorld.getPlayers()) {
            for(CompositePlayer cell : player.getCells()) {
                Circle playerCircle = cell.getRepresentation();
                gamePane.getChildren().add(playerCircle);
            }
        }
    }

    public Pane getGamePane() {
        return gamePane;
    }

    public void updateView() {
        gamePane.getChildren().clear();

        for(Player player : gameWorld.getPlayers()) {
            for(CompositePlayer cell : player.getCells()) {
                Circle playerCircle = cell.getRepresentation();
                gamePane.getChildren().add(playerCircle);
            }
        }
    }
}
