package iut.gon.agario.gameEngine;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.Pellet;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

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

    public boolean pointIsInBoundary(double x, double y){
        return (x >= this.boundary.get(0).x && y >= this.boundary.get(0).y) && (x <= this.boundary.get(3).x && y <= this.boundary.get(3).y);
    }

    public boolean isInScope(Coords topLeftCorne, Coords bottomRight){
        for(Coords corner: this.boundary){
            if((corner.x > topLeftCorne.x && corner.y > topLeftCorne.y) && (corner.x < bottomRight.x && corner.y < bottomRight.y))return true;
        }
        return false;
    }

    public void addEntity(Entity entity){
        if(depth == maxDepth){
            this.entities.add(entity);
        }else{
            QuadTree deeperTree = this.children.get(0);
            for(QuadTree quadTree : this.children){
                if(quadTree.pointIsInBoundary(entity.getX(), entity.getY())){
                    deeperTree = quadTree;
                }
            }
            deeperTree.addEntity(entity);
        }
    }

    public void removeEntity(Entity entity){
        if(depth == maxDepth){
            this.entities.remove(entity);
        }
        else{
            for(QuadTree quadTree : children){{
                if(quadTree.pointIsInBoundary(entity.getX(), entity.getY())){
                    quadTree.removeEntity(entity);
                }
            }}
        }
    }

    public ArrayList<Entity> getEntitiesFromPoint(double x, double y, double scopeX, double scopeY){

        Coords topLeft = new Coords(x - scopeX/2, y - scopeY/2);
        Coords bottomRight = new Coords(x + scopeX/2, y + scopeY/2);
        if(depth == maxDepth){
            return this.entities;
        }else{
            ArrayList<Entity> allEntity = new ArrayList<>();
            for(QuadTree quadTree : this.children){
                if(quadTree.isInScope(topLeft, bottomRight)){
                    allEntity.addAll(quadTree.getEntitiesFromPoint(x, y, scopeX, scopeY));
                }
            }
            return allEntity;
        }
    }

    public static QuadTree buildEmptyTree(double maxWidth, double maxHeight, int maxDepth){
        ArrayList<Coords> statBoundary = new ArrayList<>();
        statBoundary.add(new Coords(0, 0));
        statBoundary.add(new Coords(maxWidth, 0));
        statBoundary.add(new Coords(0, maxHeight));
        statBoundary.add(new Coords(maxWidth, maxHeight));
        return new QuadTree(1, statBoundary, maxDepth);
    }

}