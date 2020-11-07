package com.stk.nns.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.MyGdxGame;
import com.stk.nns.map.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Snake {

    List<Vector2> body;

    Vector2 direction = new Vector2(0,-1);
    private int startingLength = 20;

    public Snake(Vector2 startPos) {
        body = new ArrayList<>();
        body.add(new Vector2(startPos.x, startPos.y));
        for (int i = 1; i < startingLength; i++) {
            body.add(new Vector2(startPos.x, startPos.y+i));
        }
    }

    public void update() {
        handleInput();
    }

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
/*            System.out.println("LEFT");*/
            if (direction.x != -1 && direction.y != 0) {
                direction.x = 1;
                direction.y = 0;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            /*System.out.println("RIGHT");*/
            if (direction.x != 1 && direction.y != 0) {
                direction.x = -1;
                direction.y = 0;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
/*            System.out.println("UP");*/
            if (direction.x != 0 && direction.y != 1) {
                direction.x = 0;
                direction.y = -1;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            /*            System.out.println("DOWN");*/
            if (direction.x != 0 && direction.y != -1) {
                direction.x = 0;
                direction.y = 1;
            }
        }
        move();
    }

    public void move() {
        for (int i = body.size()-1; i>0; i--) {
            Vector2 segment = body.get(i);
            Vector2 earlierSegment = body.get(i-1);
            segment.x = earlierSegment.x;
            segment.y = earlierSegment.y;
        }
        body.get(0).x += direction.x * MyGdxGame.TILESIZE;
        body.get(0).y += direction.y * MyGdxGame.TILESIZE;
    }

    public List<Vector2> getBody() {
        return body;
    }

    public boolean collide(List<Vector2> obstaclePositions) {
        for (Vector2 segment : body) {
            if (body.get(0) != segment && body.get(0).x == segment.x && body.get(0).y == segment.y) {
                return true;
            }
        }

        for (Vector2 obstaclePosition : obstaclePositions) {
            if (body.get(0).equals(obstaclePosition)) {
                return true;
            }
        }
        return false;
    }
}
