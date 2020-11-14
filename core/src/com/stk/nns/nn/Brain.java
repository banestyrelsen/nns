package com.stk.nns.nn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.map.Level;
import com.stk.nns.map.Tile;

import java.util.List;
import java.util.stream.Collectors;

public class Brain {

    Network network;
    Level level;
    List<Tile> tiles;

    public Brain(Level level) {
        this.level = level;
        this.tiles = level.getTileValues();
        this.network = new Network(tiles.size() + 1);
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
