package iut.gon.agario.controller;

import iut.gon.agario.model.*;
import iut.gon.agario.model.AI.AIPlayer;

import java.util.List;

public class LocalGameController extends GameController {
    private List<Player> playersInRenderDistance;
    private  List<Pellet> pelletsInRenderDistance;
    public LocalGameController(GameWorld gameWorld) {
        super(gameWorld);
    }

    @Override
    protected void updateGame() {
        //gameWorld.update();

        for(Player player : gameWorld.getPlayers()) {
            if(player instanceof AIPlayer aiPlayer) {
                aiPlayer.makeDecision(gameWorld);
            }
        }
        //TODO : get a chunk and display it with all its entities with zoom
        //get a chunk
        //temp
        double x = 0;
        double y = 0;
        double wd = 0;
        double ht = 100;
        //take camera zoom into account
        Boundary queryBoundary = new Boundary(x,y,wd,ht);
        //playersInRenderDistance = gameWorld.getQuadTree().retrieveAllPlayersInBoundary(queryBoundary);
        //pelletsInRenderDistance = gameWorld.getQuadTree().retrieveAllPelletsInBoundary(queryBoundary);
    }

    public List<Player> getPlayesInRenderDistance(){
        return playersInRenderDistance;
    }
    public List<Pellet> getPastillesInRenderDistance(){
        return pelletsInRenderDistance;
    }
}
