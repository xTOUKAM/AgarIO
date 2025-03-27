package iut.gon.agario.gameEngine;

public class GameEngine extends Thread {

    private final QuadTree gameMap;

    public GameEngine(double mapWidth, double mapHeight){
        gameMap = QuadTree.buildEmptyTree(mapWidth, mapHeight, 6);
    }

    @Override
    public void run(){

    }
}
