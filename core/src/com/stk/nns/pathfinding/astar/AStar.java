package com.stk.nns.pathfinding.astar;

import com.stk.nns.map.GameBoard;
import com.stk.nns.map.Tile;

import java.util.*;
import java.util.stream.Collectors;

public class AStar {

    Tile[][] grid;
    RouteNode start;
    RouteNode current;
    Tile targetTile;
    Map<String, RouteNode> allNodes;
    Map<String, RouteNode> closed;
    Queue<RouteNode> open;
    boolean done = false;

    /*    Stack<RouteNode> currentPath;*/

    public static void main(String[] args) {
        GameBoard board = new GameBoard("maps/map0.map");
        AStar aStar = new AStar();


        aStar.getPath(aStar.grid[10][10], aStar.grid[20][20], board);
    }
    /*    Comparator<RouteNode> routeNodeComparator = Comparator.comparingInt((RouteNode a) -> a.f);*/


    public List<Tile> getPath(Tile startTile, Tile targetTile, GameBoard gameBoard) {
        /*        currentPath = new Stack<>();*/
        done = false;
        this.targetTile = targetTile;
        grid = gameBoard.getGrid();
        open = new PriorityQueue<>();
        this.start = new RouteNode(startTile, getDistance(startTile.x, startTile.y), 0);
        allNodes = new HashMap<>();
        closed = new HashMap<>();
        /*        currentPath.push(start);*/

        initNodes();

        open.add(start);

/*        while(!open.isEmpty()) {
            RouteNode node = open.poll();
            System.out.println(node.id + ": \t" + node.getF() );
        }*/
        /*    traverse();*/

/*        System.out.println("PATH");
        currentPath.stream().forEach(node -> System.out.printf(node.id + ":\t" + node.getF()  + "\t" + node.getH() +"\n") );*/
        List<RouteNode> path = getRoute();
/*        if (path == null) {
            System.out.println("No path");
        } else {
            System.out.println("PATH");
            for (RouteNode r : path) {
                System.out.println(r.id);
            }
        }*/
        if (path == null) {
            return null;
        }
        return path.stream().map(RouteNode::getTile).collect(Collectors.toList());
    }

    private List<RouteNode> getRoute() {
        while (!done && !open.isEmpty()) {
            done = expand(open.poll());
        }

        if (current == null) {
            return null;
        }
        List<RouteNode> route = new ArrayList<>();
        route.add(current);
        do {
            current = current.predecessor;
            route.add(0, current);
        } while (current != null);
        route.remove(0);
        return route;
    }

    private boolean expand(RouteNode node) {
        current = node;

        if (node.getTile().x == targetTile.x && node.getTile().y == targetTile.y) {
            return true;
        }
        RouteNode previouslyClosed = closed.get(node.id);
        closed.put(node.id, node);
        if (previouslyClosed == null || node.getF() < previouslyClosed.getF()) {

            if (node.getSuccessors() == null) {
                node.setSuccessors(getConnections(node));
            }
            for (RouteNode successor : node.getSuccessors()) {

                if (!betterOpenRouteThroughNodeExists(node) && !betterClosedRouteThroughNodeExists(node)) {
                    open.add(successor);
                    successor.setPredecessor(node);
                }
            }
        }
        if (open.isEmpty()) {
            return true;
        }
        return expand(open.poll());

    }

    public List<RouteNode> getConnections(RouteNode node) {
        List<RouteNode> connections = new ArrayList<>();
        Tile parentTile = node.getTile();
        if (parentTile.x != 0) {
            RouteNode successor = allNodes.get((parentTile.x - 1) + "," + parentTile.y);
            if (successor != null && !isPredecessor(node, successor)) addConnection(connections, node, successor);
        }
        if (parentTile.x != grid.length - 1) {
            RouteNode successor = allNodes.get((parentTile.x + 1) + "," + parentTile.y);
            if (successor != null && !isPredecessor(node, successor)) addConnection(connections, node, successor);
        }
        if (parentTile.y != 0) {
            RouteNode successor = allNodes.get(parentTile.x + "," + (parentTile.y - 1));
            if (successor != null && !isPredecessor(node, successor)) addConnection(connections, node, successor);
        }
        if (parentTile.y != grid[0].length - 1) {
            RouteNode successor = allNodes.get(parentTile.x + "," + (parentTile.y + 1));
            if (successor != null && !isPredecessor(node, successor)) addConnection(connections, node, successor);

        }


        for (RouteNode connection : connections) {
            connection.setG(node.getG() + 1);
            connection.setPredecessor(node);
        }
        return connections;
    }

    private void addConnection(List<RouteNode> connections, RouteNode node, RouteNode potentialConnection) {
        RouteNode previouslyClosed = closed.get(potentialConnection.id);
        if (previouslyClosed == null || previouslyClosed.getF() > potentialConnection.getF()) {
            connections.add(potentialConnection);
        }
    }

    private boolean isPredecessor(RouteNode current, RouteNode next) {
        if (current.id == start.id) {
            return false;
        }
        if (next.id == current.predecessor.id) {
            return true;
        }
        return false;
    }

    private boolean betterOpenRouteThroughNodeExists(RouteNode node) {
        return open.stream().anyMatch(r -> r.id.equals(node.id) && r.getF() < node.getF());
    }

    private boolean betterClosedRouteThroughNodeExists(RouteNode node) {
        return closed.entrySet().stream().anyMatch(r -> r.getValue().id.equals(node.id) && r.getValue().getF() < node.getF());
    }

    private void initNodes() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid.length; y++) {
                Tile tile = grid[x][y];
                if (tile.getValue() != GameBoard.SOLID && tile.getValue() != GameBoard.SNAKE) {
                    RouteNode node = new RouteNode(tile, getDistance(tile.x, tile.y), 0);
                    allNodes.put(node.id, node);

                }

            }
        }
    }


    private void addConnection(RouteNode parent, Tile tile, List<RouteNode> connections, int g) {
        if (tile.getValue() != GameBoard.SOLID) {
            int distance = getDistance(tile.x, tile.y);
            connections.add(new RouteNode(tile, distance, g + 1));
        }
    }

    private void initializeWeights(Tile startTile, Tile targetTile) {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                Tile tile = grid[x][y];
                if (GameBoard.SOLID != tile.getValue()) {

                }

            }
        }
    }

    private int getDistance(int x, int y) {
        // Manhattan distance since we can only move in four directions.
        return Math.abs(targetTile.x - x) + Math.abs(targetTile.y - y);
    }
}
