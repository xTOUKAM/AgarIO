package iut.gon.agario.model.AI;

import iut.gon.agario.model.Cell;
import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Player;

import java.util.HashMap;
import java.util.List;

public class HuntPlayerStrategy implements AIDecisionStrategy {
    public static HashMap<String, Double> eat(AIPlayer aiPlayer, Player player, GameWorld gameWorld) {
        HashMap<String, Double> coordinates = new HashMap<>();
        for(Cell cell : aiPlayer.getCells()){
            for(Cell playerCell : player.getCells()) {
                if (gameWorld.canAbsorb(playerCell, cell)) {
                    double directionX = aiPlayer.getX() - playerCell.getX();
                    double directionY = aiPlayer.getY() - playerCell.getY();
                    coordinates.put("X", directionX);
                    coordinates.put("Y", directionY);
                }
            }
        }

        return coordinates;
    }

    @Override
    public void makeDecision(AIPlayer aiPlayer, GameWorld gameWorld) {
        List<Player> players = gameWorld.getPlayers();
        Player target = null;

        double minDistance = Double.MAX_VALUE;

        for(Player other : players) {
            /*if(gameWorld.canAbsorb(other, aiPlayer)) {
                double distance = Math.sqrt(Math.pow(aiPlayer.getX() - other.getX(),2) + Math.pow(aiPlayer.getY() - other.getY(), 2));

                if(distance < minDistance) {
                    minDistance = distance;
                    target = other;
                }
            }*/
        }
        if (target != null) {
            gameWorld.move(target.getX(), target.getY(), aiPlayer);
        }
    }
}
