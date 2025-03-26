package iut.gon.agario.model.AI;

import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Pellet;

import java.util.List;

public class EatFoodStrategy implements AIDecisionStrategy{

    @Override
    public void makeDecision(AIPlayer aiPlayer, GameWorld gameWorld) {
        List<Pellet> pellets = gameWorld.getPellets();
        Pellet target = null;

        double minDistance = Double.MAX_VALUE;

        for(Pellet food : pellets) {
            double distance = Math.sqrt(Math.pow(aiPlayer.getX() - food.getX(),2) + Math.pow(aiPlayer.getY() - food.getY(), 2));
            if(distance < minDistance) {
                minDistance = distance;
                target = food;
            }
        }
        if (target != null) {
            gameWorld.move(target.getX(), target.getY(), aiPlayer);
        }
    }
}
