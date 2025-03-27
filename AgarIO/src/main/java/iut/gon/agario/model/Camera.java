package iut.gon.agario.model;

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
        this.x = player.getX() - 2000 / (2 * zoom);
        this.y = player.getY() - 2000 / (2 * zoom);
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
        return viewX + viewWidth >= 0 && viewX <= 2000 && viewY + viewHeight >= 0 && viewY <= 2000;
    }

    public Rectangle2D getViewBounds() {
        double width = 2000 / zoom;
        double height = 2000 / zoom;
        return new Rectangle2D(x, y, width, height);
    }

    private double calculateZoom(double mass) {
        return BASE_ZOOM / Math.sqrt(mass);
    }
}