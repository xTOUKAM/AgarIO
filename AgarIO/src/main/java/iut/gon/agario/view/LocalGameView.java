package iut.gon.agario.view;

import iut.gon.agario.controller.LocalGameController;
import iut.gon.agario.model.Cell;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Pellet;
import iut.gon.agario.model.Player;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class LocalGameView extends GameView {
    private final BorderPane rootPane;
    private final StackPane miniMapPane;
    private final Text scoreBoard;
    private  final  LocalGameController gameController;

    public LocalGameView(GameWorld gameWorld, LocalGameController gameController) {
        super(gameWorld);
        this.rootPane = new BorderPane();
        this.miniMapPane = new StackPane();
        this.scoreBoard = new Text();
        this.gameController = gameController;
        initializeLocalGameView();
    }

    private void initializeLocalGameView() {
        rootPane.setCenter(gamePane);

        miniMapPane.setPrefSize(200,200);
        miniMapPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        rootPane.setBottom(miniMapPane);

        scoreBoard.setStyle("-fx-font-size: 16px;");
        rootPane.setTop(scoreBoard);

        updateScoreboard();
    }

    private void updateScoreboard() {
        StringBuilder scoreText = new StringBuilder("Top joueurs: \n");
        gameWorld.getPlayers().stream()
                .sorted((p1, p2) -> Double.compare(p2.getMass(), p1.getMass()))
                .limit(10)
                .forEach(player -> scoreText.append(player.getId()).append(": ").append(player.getMass()).append("\n"));
        scoreBoard.setText(scoreText.toString());
    }

    @Override
    public void updateView() {
        super.updateView();
        updateScoreboard();
        for (Player player : gameController.getPlayesInRenderDistance()) {
            for(Cell cell : player.getCells()){
                rootPane.getChildren().add(cell.getRepresentation());
            }
        }
        for (Pellet pellet : gameController.getPastillesInRenderDistance()){
            rootPane.getChildren().add((pellet.getRepresentation()));
        }
    }

    public BorderPane getRootPane() {
        return rootPane;
    }
}
