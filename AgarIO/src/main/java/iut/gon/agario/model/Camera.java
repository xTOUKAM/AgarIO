package iut.gon.agario.model;

import iut.gon.agario.main.Main;
import javafx.geometry.Rectangle2D;

public class Camera extends Boundary{
    private double zoom;
    private final Player player;
    private static final double BASE_ZOOM = 100;

    public Camera(double x, double y, double width, double height, Player player) {
        super(x,y,width,height);
        this.player = player;
        update();
    }

    public void update() {
        this.zoom = calculateZoom();
        centerOnPlayer();
    }

    private void centerOnPlayer() {
        setX(player.getX() -  getWidth()/ (2 * zoom));
        setY(player.getY() - getHeight() / (2 * zoom));
    }

    public double getZoom() {
        return zoom;
    }

    public double getViewX(double worldX) {
        return (worldX - getX()) * zoom;
    }

    public double getViewY(double worldY) {
        return (worldY - getY()) * zoom;
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
        return new Rectangle2D(getX(), getY(), width, height);
    }

    private double calculateZoom() {
        return BASE_ZOOM * Math.sqrt(player.calculateRadius());
    }
}