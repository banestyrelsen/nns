package com.stk.nns.map;

import com.badlogic.gdx.math.Vector2;

public class Tile {

    private float value;
    private final Vector2 position;


    public Tile(float value, Vector2 pos) {
        this.value = value;
        this.position = pos;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Vector2 getPosition() {
        return position;
    }
}
