package iut.gon.agario.controller;

import iut.gon.agario.model.GameWorld;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public abstract class GameController {
    protected final GameWorld gameWorld;
    protected Timeline gameLoop;

    public GameController(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        intializeGameLoop();
    }

    private void intializeGameLoop() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(33), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateGame();
            }
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
    }

    public void startGame() {
        gameLoop.play();
    }

    public void stopGame() {
        gameLoop.stop();
    }

    protected abstract void updateGame();
}
