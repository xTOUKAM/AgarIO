package iut.gon.agario.model.specialpellets;

import iut.gon.agario.model.CompositePlayer;
import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

public class SpeedNerfPellet extends SpecialPellet {


    public SpeedNerfPellet(double startX, double startY, double startRadius, Color color, Player player) {
        super(startX, startY, startRadius, color, player);
    }

    @Override
    public void applyEffect() {
        for(CompositePlayer cell : player.getCells()) {
            //cell.setMaxSpeed(player.currentMaxSpeed() * 0.66);
        }
    }
}
