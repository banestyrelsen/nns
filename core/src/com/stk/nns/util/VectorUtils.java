package com.stk.nns.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Camera;

public class VectorUtils {

    public static Vector2 unproject(Camera camera, Vector2 screenPos) {
        Vector3 vec3 = camera.unproject(new Vector3(screenPos.x,screenPos.y,  0f));

        return new Vector2(vec3.x, vec3.y);
    }
}
