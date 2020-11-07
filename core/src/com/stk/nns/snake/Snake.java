package com.stk.nns.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.MyGdxGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Snake {

    List<Vector2> body;

    Vector2 direction = new Vector2(0,-1);

    public Snake(Vector2 startPos) {
        body = new ArrayList<>();
        body.add(new Vector2(startPos.x, startPos.y));
    }

    public void update() {
        handleInput();
    }

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            System.out.println("LEFT");
            direction.x = 1;
            direction.y = 0;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            System.out.println("RIGHT");
            direction.x = -1;
            direction.y = 0;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            System.out.println("UP");
            direction.x = 0;
            direction.y = -1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            System.out.println("DOWN");
            direction.x = 0;
            direction.y = 1;
        }
        move();
    }

    public void move() {
        body.get(0).x += direction.x * MyGdxGame.TILESIZE;
        body.get(0).y += direction.y * MyGdxGame.TILESIZE;
    }

    public List<Vector2> getBody() {
        return body;
    }
}
