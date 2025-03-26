package iut.gon.agario.model.factory;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

import java.util.Random;

public class PlayerFactory extends Factory {


    private GameWorld gameWorld;

    public PlayerFactory(GameWorld gameWorld) {
        super(gameWorld);
        this.gameWorld = gameWorld;
    }

    @Override
    public Entity factory() {
        Random rand = new Random();
        double X = rand.nextDouble(gameWorld.getWidth());
        double Y = rand.nextDouble(gameWorld.getWidth());
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        Color color = Color.rgb(r,g,b);
        return (Entity) new Player(X,Y,10, color);
    }
}
