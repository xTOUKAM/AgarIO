package iut.gon.agario.model;

import iut.gon.agario.main.Main;
import javafx.geometry.Rectangle2D;

public class Camera {
    private Player player;
    private double zoom;

    public Camera(Player player) {
        this.player = player;
        this.zoom = calculateZoom(player.getMass());
    }

    public void update() {
        this.zoom = calculateZoom(player.getMass());
    }

    private double calculateZoom(double mass) {
        return 100 / Math.sqrt(mass);
    }

    public Rectangle2D getViewBounds() {
        double width = Main.WIDTH / zoom;
        double height = Main.HEIGHT / zoom;
        double x = player.getX() - width / 2;
        double y = player.getY() - height / 2;
        return new Rectangle2D(x, y, width, height);
    }
}
