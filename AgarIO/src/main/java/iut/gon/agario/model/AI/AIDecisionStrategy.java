package iut.gon.agario.model.AI;

import iut.gon.agario.model.GameWorld;

public interface AIDecisionStrategy {
    void makeDecision(AIPlayer aiPlayer, GameWorld gameWorld);
}
