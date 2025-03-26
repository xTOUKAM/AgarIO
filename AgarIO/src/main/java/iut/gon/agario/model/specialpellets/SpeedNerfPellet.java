package iut.gon.agario.model.specialpellets;

import iut.gon.agario.model.Cell;
import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

public class SpeedNerfPellet extends SpecialPellet {


    public SpeedNerfPellet(double startX, double startY, double startRadius, Color color, Player player) {
        super(startX, startY, startRadius, color, player);
    }

    @Override
    public void applyEffect() {
        for(Cell cell : player.getCells()) {
            //cell.setMaxSpeed(player.currentMaxSpeed() * 0.66);
        }
    }
}
