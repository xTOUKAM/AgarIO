package iut.gon.agario.model.AI;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Pastille;
import iut.gon.agario.model.Player;

import java.util.List;
import java.util.Random;

public class RandomMovementStrategy implements AIDecisionStrategy {
    private static final Random random = new Random();
    private double directionX = random.nextDouble() * 2 - 1;
    private double directionY = random.nextDouble() * 2 - 1;
    private long lastDirectionChangeTime = System.currentTimeMillis();
    private static final long DIRECTION_CHANGE_INTERVAL = 2000 + random.nextInt(1001); // 2-3 seconds

    @Override
    public void makeDecision(AIPlayer aiPlayer, GameWorld gameWorld) {
        long currentTime = System.currentTimeMillis();

        // Change direction periodically
        if (currentTime - lastDirectionChangeTime >= DIRECTION_CHANGE_INTERVAL) {
            directionX = random.nextDouble() * 2 - 1;
            directionY = random.nextDouble() * 2 - 1;
            lastDirectionChangeTime = currentTime;
        }

        // Determine targets and adjust direction
        List<Entity> nearbyEntities = gameWorld.getQuadTree().retrieve(aiPlayer);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Pastille) {
                // Move towards the pastille
                directionX = entity.getX() - aiPlayer.getX();
                directionY = entity.getY() - aiPlayer.getY();
                break;
            } else if (entity instanceof Player otherPlayer) {
                if (otherPlayer.getMass() > aiPlayer.getMass() * 1.33) {
                    // Avoid larger players
                    directionX = aiPlayer.getX() - otherPlayer.getX();
                    directionY = aiPlayer.getY() - otherPlayer.getY();
                    break;
                }
            }
        }

        // Normalize direction
        double distance = Math.sqrt(directionX * directionX + directionY * directionY);
        if (distance != 0) {
            directionX /= distance;
            directionY /= distance;
        }

        double newX = aiPlayer.getX() + directionX;
        double newY = aiPlayer.getY() + directionY;
        gameWorld.move(newX, newY, aiPlayer);
    }
}
