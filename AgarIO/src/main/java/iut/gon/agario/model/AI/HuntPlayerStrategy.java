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

        for(Player player : players) {
            if(player != aiPlayer && aiPlayer.getMass() >= player.getMass() * 1.33) {
                double distance = Math.sqrt(Math.pow(aiPlayer.getX() - player.getX(),2) + Math.pow(aiPlayer.getY() - player.getY(), 2));

                if(distance < minDistance) {
                    minDistance = distance;
                    target = player;
                }
            }
        }

        if (target != null) {
            aiPlayer.move(target.getX(), target.getY());
        }
    }
}
