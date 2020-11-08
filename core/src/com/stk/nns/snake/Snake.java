package com.stk.nns.snake;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.MyGdxGame;
import com.stk.nns.PlaySound;
import com.stk.nns.food.Food;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Snake {

    LinkedList<Vector2> body;

    Vector2 newDirection = new Vector2(0, -1);
    Vector2 direction = new Vector2(0, -1);
    List<Vector2> obstaclePositions;

    private int startingLength = 2;
    private int lastMove = Input.Keys.UP;
    private int nextMove = Input.Keys.UP;
    int nFeedings = 0;
    Instant lastAte = Instant.now();
    boolean shouldBurp = false;

    PlaySound playSound;

    public Snake(Vector2 startPos, List<Vector2> obstaclePositions, PlaySound playSound) {
        body = new LinkedList<>();
        body.add(new Vector2(startPos.x, startPos.y));
        for (int i = 1; i < startingLength; i++) {
            body.add(new Vector2(startPos.x, startPos.y + i));
        }
        this.obstaclePositions = obstaclePositions;
        this.playSound = playSound;

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

    private void update() {
        if (shouldBurp && Instant.now().toEpochMilli() - lastAte.toEpochMilli() > 800) {
            playSound.burp();
            shouldBurp = false;

        }
    }

    /**
     * Returns false if there is a collision
     * @return
     */
    public boolean move() {
        update();

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

        lastMove = move;

        return true;

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
            grow();
            nFeedings++;
            lastAte = Instant.now();
            playSound.eat();
            if (nFeedings == 0) {
                shouldBurp = false;
            } else if (nFeedings % 5 == 0) {
                shouldBurp = true;
            }
            return true;
        }
        return false;
    }

    private void grow() {
        Vector2 lastSegment = body.get(body.size() - 1);
        body.add(new Vector2(lastSegment.x, lastSegment.y));

    }

    public void render(SpriteBatch batch, Texture tileWall, Texture tileHead) {
        for (int i = 0; i < body.size(); i++) {
            Vector2 segment = body.get(i);
            if (i == 0) {
                batch.draw(tileHead, segment.x, segment.y);
            } else {
                batch.draw(tileWall, segment.x, segment.y);
            }

        }
    }

    public int getnFeedings() {
        return nFeedings;
    }
}
