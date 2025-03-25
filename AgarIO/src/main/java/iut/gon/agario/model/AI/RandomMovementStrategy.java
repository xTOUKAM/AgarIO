package iut.gon.agario.model.AI;

import iut.gon.agario.model.GameWorld;

import java.util.Random;

public class RandomMovementStrategy implements AIDecisionStrategy {
    private static final Random random = new Random();

    @Override
    public void makeDecision(AIPlayer aiPlayer, GameWorld gameWorld) {
        double directionX = random.nextDouble() * 2 - 1;
        double directionY = random.nextDouble() * 2 - 1;
        aiPlayer.move(aiPlayer.getX() + directionX, aiPlayer.getY() + directionY);
    }
}
