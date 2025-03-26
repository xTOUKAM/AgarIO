package iut.gon.agario.model.AI;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Pellet;
import iut.gon.agario.model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomMovementStrategy {
    private static final Random random = new Random();
    private static double directionX = random.nextDouble() * 2 - 1;
    private static double directionY = random.nextDouble() * 2 - 1;
    private static long lastDirectionChangeTime = System.currentTimeMillis();
    private static final long DIRECTION_CHANGE_INTERVAL = 2000 + random.nextInt(1001); // 2-3 seconds


    public static void makeDecision(AIPlayer aiPlayer, GameWorld gameWorld) {
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
            if (entity instanceof Pellet) {
                // Move towards the pastille
                System.out.println("X : "+directionX+"  | Y :"+directionY);
                HashMap<String, Double> coordinates = EatFoodStrategy.eat(aiPlayer, entity);
                directionX = coordinates.get("X");
                directionY = coordinates.get("Y");
                System.out.println("comparé à : ");
                System.out.println("X : "+directionX+"  | Y :"+directionY);
                break;
            }
        }

        // Normalize direction
        double distance = Math.sqrt(directionX * directionX + directionY * directionY);
        if (distance != 0) {
            directionX /= distance;
            directionY /= distance;
            System.out.println("newX : "+directionX+"  newY : "+directionY
            );
        }

        gameWorld.move(directionX, directionY, aiPlayer);
        System.out.println("on a bougé");
    }
}
