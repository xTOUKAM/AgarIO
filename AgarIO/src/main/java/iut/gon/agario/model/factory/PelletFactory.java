package iut.gon.agario.model.factory;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Pellet;
import iut.gon.agario.model.specialpellets.*;
import javafx.scene.paint.Color;

import java.util.Random;

public class PelletFactory extends Factory {

    private GameWorld gameWorld;

    public PelletFactory(GameWorld gameWorld){
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
        int sp = rand.nextInt(6);
        if (sp == 6){
            sp = rand.nextInt(6);
            switch (sp){
                case 1:
                    return new InvisibilityPellet(X,Y,5,color,gameWorld.getPlayers().get(0));
                case 2:
                    return new MassBuffPellet(X,Y,5,color,gameWorld.getPlayers().get(0));
                case 3:
                    return new MassNerfPellet(X,Y,5,color,gameWorld.getPlayers().get(0));
                case 4:
                    return new SpeedBuffPellet(X,Y,5,color,gameWorld.getPlayers().get(0));
                case 5:
                    return new SpeedNerfPellet(X,Y,5,color,gameWorld.getPlayers().get(0));
                case 6:
                    return new SplitPellet(X,Y,5,color,gameWorld.getPlayers().get(0));
            }
        }
        return new Pellet(X,Y,5, color);
    }
}
