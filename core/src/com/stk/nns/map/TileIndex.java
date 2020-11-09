package com.stk.nns.map;

import com.badlogic.gdx.math.Vector2;

public class TileIndex {

    public final Vector2 position;

    public TileIndex(Vector2 position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileIndex tileIndex = (TileIndex) o;
        return position.equals(tileIndex.position);
    }

    @Override
    public int hashCode() {
        return  (int)( ((int) this.position.x << 16) + this.position.y);
    }
}
