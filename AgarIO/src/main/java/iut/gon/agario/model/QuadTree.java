package iut.gon.agario.model;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private static final int MAX_OBJECTS = 10;
    private static final int MAX_LEVELS = 5;

    private int level;
    private List<Entity> objects;
    private Boundary bounds;
    private QuadTree[] nodes;

    public QuadTree(int level, Boundary bounds) {
        this.level = level;
        this.objects = new ArrayList<>();
        this.bounds = bounds;
        this.nodes = new QuadTree[4];
    }

    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    public void split() {
        double subWidth = bounds.getWidth() / 2;
        double subHeight = bounds.getHeight() / 2;
        double x = bounds.getX();
        double y = bounds.getY();

        nodes[0] = new QuadTree(level + 1, new Boundary(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new QuadTree(level + 1, new Boundary(x, y, subWidth, subHeight));
        nodes[2] = new QuadTree(level + 1, new Boundary(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new QuadTree(level + 1, new Boundary(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    private int getIndex(Entity entity) {
        int index = -1;
        double verticalMidpoint = bounds.getX() + bounds.getWidth() / 2;
        double horizontalMidpoint = bounds.getY() + bounds.getHeight() / 2;

        boolean topQuadrant = (entity.getY() < horizontalMidpoint && entity.getY() + entity.getHeight() < horizontalMidpoint);
        boolean bottomQuadrant = (entity.getY() > horizontalMidpoint);

        if (entity.getX() < verticalMidpoint && entity.getX() + entity.getWidth() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (entity.getX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    public void insert(Entity entity) {
        if (nodes[0] != null) {
            int index = getIndex(entity);

            if (index != -1) {
                nodes[index].insert(entity);
                return;
            }
        }

        objects.add(entity);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public List<Entity> retrieve(Entity entity) {
        int index = getIndex(entity);
        List<Entity> returnObjects = new ArrayList<>(objects);

        if (nodes[0] != null) {
            if (index != -1) {
                returnObjects.addAll(nodes[index].retrieve(entity));
            } else {
                for (int i = 0; i < nodes.length; i++) {
                    returnObjects.addAll(nodes[i].retrieve(entity));
                }
            }
        }

        return returnObjects;
    }

    public void remove(Entity entity) {
        int index = getIndex(entity);

        if(index != -1 && nodes[0] != null) {
            nodes[index].remove(entity);
        } else {
            objects.remove(entity);
        }
    }

    public List<Player> retrieveAllPlayersInBoundary(Boundary queryBoundary) {
        List<Player> returnObjects = new ArrayList<>();
        if (!bounds.intersects(queryBoundary)) {
            return returnObjects;
        }

        for (Entity entity : objects) {
            if (queryBoundary.intersectsWithEntity(entity)) {
                if(entity instanceof Player){
                    returnObjects.add((Player) entity);
                }
            }
        }

        if (nodes[0] != null) {
            for (QuadTree node : nodes) {
                returnObjects.addAll(node.retrieveAllPlayersInBoundary(queryBoundary));
            }
        }

        return returnObjects;
    }

    public List<Pellet> retrieveAllPelletsInBoundary(Boundary queryBoundary) {
        List<Pellet> returnObjects = new ArrayList<>();
        if (!bounds.intersects(queryBoundary)) {
            return returnObjects;
        }

        for (Entity entity : objects) {
            if (queryBoundary.intersectsWithEntity(entity)) {
                if(entity instanceof Pellet){
                    returnObjects.add((Pellet) entity);
                }
            }
        }

        if (nodes[0] != null) {
            for (QuadTree node : nodes) {
                returnObjects.addAll(node.retrieveAllPelletsInBoundary(queryBoundary));
            }
        }
        return returnObjects;
    }
}
