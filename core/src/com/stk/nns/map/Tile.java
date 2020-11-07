package com.stk.nns.map;

import com.badlogic.gdx.math.Vector2;

public class Tile {

    private int value;
    private Vector2 position;


    public Tile(int value, Vector2 pos) {
        this.value = value;
        this.position = pos;
    }

    public int getValue() {
        return value;
    }

    public Vector2 getPosition() {
        return position;
    }
}
