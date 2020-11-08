package com.stk.nns.snake;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.MyGdxGame;
import com.stk.nns.food.Food;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Snake {

    LinkedList<Vector2> body;

    Vector2 newDirection = new Vector2(0, -1);
    Vector2 direction = new Vector2(0, -1);
    List<Vector2> obstaclePositions;

    private int startingLength = 2;
    private int lastMove = Input.Keys.UP;
    private int nextMove = Input.Keys.UP;

    public Snake(Vector2 startPos, List<Vector2> obstaclePositions) {
        body = new LinkedList<>();
        body.add(new Vector2(startPos.x, startPos.y));
        for (int i = 1; i < startingLength; i++) {
            body.add(new Vector2(startPos.x, startPos.y + i));
        }
        this.obstaclePositions = obstaclePositions;
    }

    public void setNextMove(int nextMove) {
        this.nextMove = nextMove;
    }

    public void setNewDirection(Vector2 newDirection) {

        this.newDirection.x = newDirection.x;
        this.newDirection.y = newDirection.y;
    }

    public Vector2 getDirection() {
        return new Vector2(direction.x, direction.y);
    }

    private String directionString(Vector2 direction) {
        if (direction.x == -1 && direction.y == 0) {
            return "LEFT";
        } else if (direction.x == 1 && direction.y == 0) {
            return "RIGHT";
        } else if (direction.x == 0 && direction.y == 1) {
            return "UP";
        } else if (direction.x == 0 && direction.y == -1) {
            return "DOWN";
        }
        return "?";
    }

    public boolean move() {
        // If next move is last move in reverse, repeat last move
        int move = isMoveLegal(nextMove) ? nextMove : lastMove;

        if (move == Input.Keys.LEFT) {
            direction.x = -1;
            direction.y = 0;
        } else if (move == Input.Keys.RIGHT) {
            direction.x = 1;
            direction.y = 0;
        } else if (move == Input.Keys.UP) {
            direction.x = 0;
            direction.y = 1;
        } else if (move == Input.Keys.DOWN) {
            direction.x = 0;
            direction.y = -1;
        }


        Vector2 newHead = new Vector2(
                body.get(0).x + direction.x * MyGdxGame.TILESIZE,
                body.get(0).y + direction.y * MyGdxGame.TILESIZE);

        if (collide(newHead)) {
            return false;
        }

        // Drag tail
        body.addFirst(newHead);
        body.removeLast();

/*        // Drag tail
        for (int i = body.size() - 1; i > 0; i--) {
            Vector2 segment = body.get(i);
            Vector2 earlierSegment = body.get(i - 1);
            segment.x = earlierSegment.x;
            segment.y = earlierSegment.y;
        }*/

        lastMove = move;

        return true;

/*        System.out.println("MOVING " + directionString(direction) + " --->>> " + directionString(newDirection));

        System.out.println("direction " + (direction == newDirection));


*//*        if (    // Ignore orders to reverse direction
                !(newDirection.x == -direction.x && newDirection.y == 0)
                && !(newDirection.y == -direction.y && newDirection.x == 0)) {
            direction = newDirection;
        } else {
            System.out.println("OOOOOOOOPS!");
        }*//*
*//*        if (!body.stream().anyMatch(segment ->
            segment != body.get(0)
                    && segment.x == body.get(0).x + newDirection.x * MyGdxGame.TILESIZE
                    && segment.y == body.get(0).y + newDirection.x * MyGdxGame.TILESIZE
        )) *//*
        if (
                body.get(1).x == body.get(0).x + newDirection.x * MyGdxGame.TILESIZE
                        && body.get(1).y == body.get(0).y + newDirection.x * MyGdxGame.TILESIZE
        ) {
*//*                direction.x = newDirection.x;
                direction.y = newDirection.y;*//*
        }*/
    }

    private boolean isMoveLegal(int move) {
        return !(
                (lastMove == Input.Keys.UP && move == Input.Keys.DOWN) ||
                        (lastMove == Input.Keys.DOWN && move == Input.Keys.UP) ||
                        (lastMove == Input.Keys.LEFT && move == Input.Keys.RIGHT) ||
                        (lastMove == Input.Keys.RIGHT && move == Input.Keys.LEFT)
        );
    }

    public List<Vector2> getBody() {
        return body;
    }

    public boolean collide(Vector2 head) {
        for (Vector2 segment : body) {
            if (head != segment && head.x == segment.x && head.y == segment.y) {
                return true;
            }
        }

        for (Vector2 obstaclePosition : obstaclePositions) {
            if (head.equals(obstaclePosition)) {
                return true;
            }
        }
        return false;
    }

    public boolean eat(Food food) {
        if (food.getPosition().equals(body.get(0))) {
            Vector2 lastSegment = body.get(body.size() - 1);
            body.add(new Vector2(lastSegment.x, lastSegment.y));
            return true;
        }
        return false;
    }


}
