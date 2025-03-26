package iut.gon.agario.model.specialpellets;

import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

public class SplitPellet extends SpecialPellet {

    public SplitPellet(double startX, double startY, double startRadius, Color color, Player player) {
        super(startX, startY, startRadius, color, player);
    }

    @Override
    public void applyEffect() {
        //player.split();
    }
}
