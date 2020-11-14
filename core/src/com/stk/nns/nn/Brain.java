package com.stk.nns.nn;

import com.badlogic.gdx.Input;
import com.stk.nns.map.SnakeLevel;
import com.stk.nns.map.Tile;

import java.util.List;

public class Brain {

    Network network;
    SnakeLevel snakeLevel;
    List<Tile> tiles;

    public Brain(SnakeLevel snakeLevel) {
        this.snakeLevel = snakeLevel;
        this.tiles = snakeLevel.getTileValues();
        this.network = new Network(tiles.size());
    }

    public int move() {
        double[] input = tiles.stream().mapToDouble(e -> e.getValue()).toArray();
        double[] output = network.calculate(input);

        if (output.length != 4) {
            throw new IllegalStateException("Output must be equal to 4");
        }

        double up = output[0];
        double down = output[1];
        double left = output[2];
        double right = output[3];

        int move = Input.Keys.UP;
        if (up < down) {
            move = Input.Keys.DOWN;
        }
        if (down < left) {
            move = Input.Keys.LEFT;
        }
        if (left < right) {
            move = Input.Keys.RIGHT;
        }

        return move;
    }
}
