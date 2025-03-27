package iut.gon.agario.model.factory;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Pellet;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Random;

public class PelletFactory extends Factory {

    public PelletFactory(Double maxX, Double maxY){
        super(maxX, maxY);
    }

    @Override
    public Entity factory() {
        Random rand = new Random();
        double X = rand.nextDouble(maxX);
        double Y = rand.nextDouble(maxY);
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        Color color = Color.rgb(r,g,b);
        return new Pellet(X,Y,5, color);
    }
}
