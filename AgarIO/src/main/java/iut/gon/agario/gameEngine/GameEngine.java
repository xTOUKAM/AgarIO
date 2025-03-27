package iut.gon.agario.gameEngine;

public class GameEngine extends Thread {

    private final QuadTree gameMap;
    private final int nbPelle;

    public GameEngine(double mapWidth, double mapHeight, int nbPelle){
        gameMap = QuadTree.buildEmptyTree(mapWidth, mapHeight, 6);
        this.nbPelle = nbPelle;



    }



    @Override
    public void run(){

    }
}
