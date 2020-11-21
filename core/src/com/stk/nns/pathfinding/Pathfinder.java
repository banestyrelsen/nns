package com.stk.nns.pathfinding;

import com.stk.nns.map.GameBoard;
import com.stk.nns.map.Tile;
import com.stk.nns.pathfinding.astar.AStar;

import java.util.List;

public class Pathfinder {

    public List<Tile> getPath(Tile start, Tile end, GameBoard gameBoard) {
        AStar aStar = new AStar();
        return aStar.getPath(start, end, gameBoard);
    }
}
