package iut.gon.agario.controller;

import iut.gon.agario.model.AI.AIPlayer;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Player;

public class LocalGameController extends GameController {
    public LocalGameController(GameWorld gameWorld) {
        super(gameWorld);
    }

    @Override
    protected void updateGame() {
        gameWorld.update();

        for(Player player : gameWorld.getPlayers()) {
            if(player instanceof AIPlayer aiPlayer) {
                aiPlayer.makeDecision(gameWorld);
            }
        }
    }
}
