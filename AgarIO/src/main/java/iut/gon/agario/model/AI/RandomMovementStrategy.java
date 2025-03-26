package iut.gon.agario.model.AI;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Pellet;
import iut.gon.agario.model.Player;

import java.util.List;
import java.util.Random;

public class RandomMovementStrategy implements AIDecisionStrategy {
    private static final Random random = new Random();
    private static double directionX = random.nextDouble() * 2 - 1;
    private static double directionY = random.nextDouble() * 2 - 1;
    private static long lastDirectionChangeTime = System.currentTimeMillis();
    private static final long DIRECTION_CHANGE_INTERVAL = 2000 + random.nextInt(1001); // 2-3 seconds

    @Override
    public void makeDecision(AIPlayer aiPlayer, GameWorld gameWorld) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastDirectionChangeTime;

        if (elapsedTime >= DIRECTION_CHANGE_INTERVAL) {
            directionX = random.nextDouble() * 2 - 1;
            directionY = random.nextDouble() * 2 - 1;
            lastDirectionChangeTime = currentTime;
        }

        // Normalisation de la direction
        double distance = Math.sqrt(directionX * directionX + directionY * directionY);
        if (distance == 0) {
            directionX = random.nextDouble() * 2 - 1;
            directionY = random.nextDouble() * 2 - 1;
        } else {
            directionX /= distance;
            directionY /= distance;
        }

        double newX = aiPlayer.getX() + directionX;
        double newY = aiPlayer.getY() + directionY;

        // Limiter le mouvement du bot pour qu'il ne dépasse pas les bords du plateau
        newX = Math.max(aiPlayer.getMass() / 2, Math.min(newX, gameWorld.getWidth() - aiPlayer.getMass() / 2));
        newY = Math.max(aiPlayer.getMass() / 2, Math.min(newY, gameWorld.getHeight() - aiPlayer.getMass() / 2));

        gameWorld.move(newX, newY, aiPlayer);
    }
}
