package iut.gon.agario.model.AI;

import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Player;

import java.util.List;

public class HuntPlayerStrategy implements AIDecisionStrategy {
    @Override
    public void makeDecision(AIPlayer aiPlayer, GameWorld gameWorld) {
        List<Player> players = gameWorld.getPlayers();
        Player target = null;

        double minDistance = Double.MAX_VALUE;

        for(Player other : players) {
            if(aiPlayer.canAbsorb(other)) {
                double distance = Math.sqrt(Math.pow(aiPlayer.getX() - other.getX(),2) + Math.pow(aiPlayer.getY() - other.getY(), 2));

                if(distance < minDistance) {
                    minDistance = distance;
                    target = other;
                }
            }
        }
        if (target != null) {
            aiPlayer.move(target.getX(), target.getY());
        }
    }
}
