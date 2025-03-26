package iut.gon.agario.model;

import iut.gon.agario.main.Main;
import javafx.geometry.Rectangle2D;

public class Camera {
    private double x;
    private double y;
    private double zoom;
    private final Player player;
    private static final double BASE_ZOOM = 100;

    public Camera(Player player) {
        this.player = player;
        update();
    }

    public void update() {
        this.zoom = calculateZoom(player.getMass());
        centerOnPlayer();
    }

    private void centerOnPlayer() {
        this.x = player.getX() - Main.WIDTH / (2 * zoom);
        this.y = player.getY() - Main.HEIGHT / (2 * zoom);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZoom() {
        return zoom;
    }

    public double getViewX(double worldX) {
        return (worldX - x) * zoom;
    }

    public double getViewY(double worldY) {
        return (worldY - y) * zoom;
    }

    public boolean isInView(double worldX, double worldY, double width, double height) {
        double viewX = getViewX(worldX);
        double viewY = getViewY(worldY);
        double viewWidth = width * zoom;
        double viewHeight = height * zoom;
        return viewX + viewWidth >= 0 && viewX <= Main.WIDTH && viewY + viewHeight >= 0 && viewY <= Main.HEIGHT;
    }

    public Rectangle2D getViewBounds() {
        double width = Main.WIDTH / zoom;
        double height = Main.HEIGHT / zoom;
        return new Rectangle2D(x, y, width, height);
    }

    private double calculateZoom(double mass) {
        return BASE_ZOOM / Math.sqrt(mass);
    }
}