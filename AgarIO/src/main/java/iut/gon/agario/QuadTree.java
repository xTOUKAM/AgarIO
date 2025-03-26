package iut.gon.agario;

import iut.gon.agario.model.Entity;

import java.awt.desktop.AboutEvent;
import java.util.ArrayList;

public class QuadTree {

    final int maxDepth;
    final int depth;
    final ArrayList<Coords> boundary;
    ArrayList<Entity> entities = null;
    ArrayList<QuadTree> children = null;


    public QuadTree(int depth, ArrayList<Coords> boundary, int maxDepth){

        this.depth = depth;
        this.maxDepth = maxDepth;
        this.boundary = boundary;
        if(depth != maxDepth){
            this.children = new ArrayList<>();

            Coords top = new Coords(this.boundary.get(3).x/2, this.boundary.get(0).y);
            Coords left = new Coords(this.boundary.get(0).x, this.boundary.get(3).y/2);
            Coords middle = new Coords(this.boundary.get(3).x/2, this.boundary.get(3).y/2);
            Coords right = new Coords(this.boundary.get(1).x, this.boundary.get(3).y/2);
            Coords bottom = new Coords(this.boundary.get(3).x/2, this.boundary.get(3).y);

            //TOP lEFT CORNER
            ArrayList<Coords> newBoundary0 = new ArrayList<>();
            newBoundary0.add(this.boundary.get(0));
            newBoundary0.add(top);
            newBoundary0.add(left);
            newBoundary0.add(middle);
            this.children.add(new QuadTree(depth+1, newBoundary0, maxDepth));

            //TOP RIGHT CORNER
            ArrayList<Coords> newBoundary1 = new ArrayList<>();
            newBoundary1.add(top);
            newBoundary1.add(this.boundary.get(1));
            newBoundary1.add(middle);
            newBoundary1.add(right);
            this.children.add(new QuadTree(depth+1, newBoundary1, maxDepth));

            //BOTTOM lEFT CORNER
            ArrayList<Coords> newBoundary2 = new ArrayList<>();
            newBoundary2.add(left);
            newBoundary2.add(middle);
            newBoundary2.add(this.boundary.get(2));
            newBoundary2.add(bottom);
            this.children.add(new QuadTree(depth+1, newBoundary2, maxDepth));

            //BOTTOM lEFT CORNER
            ArrayList<Coords> newBoundary3 = new ArrayList<>();
            newBoundary3.add(middle);
            newBoundary3.add(right);
            newBoundary3.add(bottom);
            newBoundary3.add(this.boundary.get(3));
            this.children.add(new QuadTree(depth+1, newBoundary3, maxDepth));
        }else{
            this.entities = new ArrayList<>();
        }

    }

    public boolean isInBoundery(double x, double y){
        return (x >= this.boundary.get(0).x && y >= this.boundary.get(0).y) && (x <= this.boundary.get(3).x && y <= this.boundary.get(3).y);
    }

    public void addEntity(Entity entity){
        if(depth == maxDepth){
            this.entities.add(entity);
        }else{
            QuadTree deeperTree = this.children.get(0);
            for(QuadTree quadTree : this.children){
                if(quadTree.isInBoundery(entity.getX(), entity.getY())){
                    deeperTree = quadTree;
                }
            }
            deeperTree.addEntity(entity);
        }
    }


    public static QuadTree buildEmptyTree(double maxHeight, double maxWidth, int maxDepth){
        ArrayList<Coords> statBoundary = new ArrayList<>();
        statBoundary.add(new Coords(0, 0));
        statBoundary.add(new Coords(maxWidth, 0));
        statBoundary.add(new Coords(0, maxHeight));
        statBoundary.add(new Coords(maxWidth, maxHeight));
        return new QuadTree(1, statBoundary, maxDepth);
    }

    public static void main(String[] args) {
        QuadTree quadTree = QuadTree.buildEmptyTree(1000, 1000, 5);
        System.out.println(quadTree.toString());
    }
}