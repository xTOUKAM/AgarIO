package iut.gon.agario.model;

public class Boundary {
    private double x, y, width, height;

    public Boundary(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean contains(Entity entity) {
        return (entity.getX() >= x &&
                entity.getX() + entity.getWidth() <= x + width &&
                entity.getY() >= y &&
                entity.getY() + entity.getHeight() <= y + height
                );
    }

    public boolean intersects(Boundary other) {
        return (x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y);
    }

    public boolean intersectsWithEntity(Entity other){
        return (x < other.getX() + other.getWidth() &&
                x + width > other.getX() &&
                y < other.getY() + other.getHeight() &&
                y + height > other.getY());
    }
}
