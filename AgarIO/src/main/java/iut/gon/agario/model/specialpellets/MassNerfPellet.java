package iut.gon.agario.model.specialpellets;

import iut.gon.agario.model.Cell;
import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

public class MassNerfPellet extends SpecialPellet {


    public MassNerfPellet(double startX, double startY, double startRadius, Color color, Player player) {
        super(startX, startY, startRadius, color, player);
    }

    @Override
    public void applyEffect() {
        for(Cell cell : player.getCells()) {
            cell.setMass(player.getMass() * 0.66);
        }
    }
}
