package com.stk.nns.pathfinding.astar;

import com.stk.nns.map.Tile;

import java.util.List;

public class RouteNode implements Comparable<RouteNode> {

    public Tile tile;
    private int h; // Heuristic, the "Manhattan distance" to target node
    private int g; // Movement cost from start grid
    public final String id;
    List<RouteNode> successors;
    RouteNode predecessor;

    public RouteNode(Tile tile, int h, int g) {
        this.tile = tile;
        this.h = h;
        this.g = g;
        this.id = tile.x + "," + tile.y;
        predecessor = null;
    }

    public int getF() {
        return h + g;
    }

    public int getH() {
        return h;
    }

    public String getId() {
        return id;
    }

    public RouteNode getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(RouteNode predecessor) {
        this.predecessor = predecessor;
    }

    public List<RouteNode> getSuccessors() {
        return successors;
    }

    public void setSuccessors(List<RouteNode> successors) {
        this.successors = successors;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public Tile getTile() {
        return tile;
    }

    @Override
    public int compareTo(RouteNode other) {
        if (this.getF() > other.getF()) {
            return 1;
        } else if (this.getF() < other.getF()) {
            return -1;
        } else {
            return 0;
        }
    }

}
