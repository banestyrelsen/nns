package com.stk.nns.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputHandler {

    public static void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
/*        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.RIGHT)
            || Gdx.input.isKeyPressed(Input.Keys.UP)
            || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {

        }


        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            System.out.println("LEFT");
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            System.out.println("RIGHT");
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            System.out.println("UP");
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            System.out.println("DOWN");
        }*/
    }
}
