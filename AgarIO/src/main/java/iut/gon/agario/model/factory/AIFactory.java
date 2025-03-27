package iut.gon.agario.model.factory;

import iut.gon.agario.model.AI.AIPlayer;
import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Random;

public class AIFactory implements Factory{

    public static Entity factory(Double maxX, Double maxY) {
        Random rand = new Random();
        double X = rand.nextDouble(maxX);
        double Y = rand.nextDouble(maxY);
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        Color color = Color.rgb(r,g,b);
        return new AIPlayer(X,Y,10, color);
    }
}
