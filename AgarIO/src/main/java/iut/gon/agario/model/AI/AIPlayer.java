package iut.gon.agario.model.AI;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

public class AIPlayer extends Player implements Entity {

    public AIPlayer(double startX, double startY, double startMass, Color color) {
        super(startX, startY, startMass, color);
    }


    public void makeDecision(GameWorld gameWorld) {
        System.out.println("avant décision");
        RandomMovementStrategy.makeDecision(this, gameWorld);
    }
}