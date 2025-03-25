package iut.gon.agario.model.AI;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

public class AIPlayer extends Player implements Entity {
    private AIDecisionStrategy strategy;

    public AIPlayer(double startX, double startY, double startMass, Color color) {
        super(startX, startY, startMass, color);
        this.strategy = new RandomMovementStrategy();
    }

    public void setStrategy(AIDecisionStrategy strategy) {
        this.strategy = strategy;
    }

    public void makeDecision(GameWorld gameWorld) {
        if(strategy != null) {
            strategy.makeDecision(this, gameWorld);
        }
    }
}