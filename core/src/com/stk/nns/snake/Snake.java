package com.stk.nns.snake;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.Main;
import com.stk.nns.nn.Network;
import com.stk.nns.sound.PlaySound;
import com.stk.nns.map.SnakeLevel;
import com.stk.nns.nn.Brain;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Snake {

    public static float UP = 1f;
    public static float DOWN = -1f;
    public static float LEFT = 1f;
    public static float RIGHT = -1f;


    LinkedList<Vector2> body;

    Vector2 direction;

    private final int startingLength = 2;
    private int lastMove = Input.Keys.UP;
    private int nextMove = Input.Keys.UP;
    int nFeedings = 0;
    Instant lastAte = Instant.now();
    Instant born = Instant.now();
    long finalLifeSpan = 0;
    boolean shouldBurp = false;
    boolean isAlive = true;

    PlaySound playSound;

    SnakeLevel snakeLevel;
    Brain brain;

    public final Control control;

    Network network;

    // Constructor for player controlled snake
    public Snake(Vector2 startPos, SnakeLevel snakeLevel, PlaySound playSound) {
        this.control = Control.PLAYER_CONTROLLED;
        body = new LinkedList<>();
        body.add(new Vector2(startPos.x, startPos.y));
        for (int i = 1; i < startingLength; i++) {
            body.add(new Vector2(startPos.x, startPos.y - Main.TILESIZE));
        }
        this.snakeLevel = snakeLevel;
        this.playSound = playSound;
        this.direction = new Vector2(0, -1);
    }

    public Snake(Vector2 startPos, SnakeLevel snakeLevel, PlaySound playSound, Network network) {
        this.control = Control.AI_CONTROLLED;
        body = new LinkedList<>();
        body.add(new Vector2(startPos.x, startPos.y));
        for (int i = 1; i < startingLength; i++) {
            body.add(new Vector2(startPos.x, startPos.y - Main.TILESIZE));
        }
        this.snakeLevel = snakeLevel;
        this.playSound = playSound;
        this.direction = new Vector2(0, -1);

        if (control == Control.AI_CONTROLLED) {
            this.network = network;
        }
    }

    public void setNextMove(int nextMove) {
        this.nextMove = nextMove;
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

        if (control == Control.AI_CONTROLLED) {
            aiSetNextMove();
        }

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
                body.get(0).x + direction.x * Main.TILESIZE,
                body.get(0).y + direction.y * Main.TILESIZE);

        if (collide(newHead)) {
            isAlive = false;
            finalLifeSpan = Instant.now().toEpochMilli() - born.toEpochMilli();
            return false;
        }

        // Drag tail
        Vector2 prevHead = body.getFirst();
        body.addFirst(newHead);
        Vector2 removedSegment = body.removeLast();
        snakeLevel.updateSnakePosition(newHead, prevHead, removedSegment);


        lastMove = move;
        return true;

    }


    public void aiSetNextMove() {
        double[] input = snakeLevel.getAllFourDirectionValues(body.get(0));
        if (input.length != 8) {
            throw new IllegalStateException("Input length must be 8");
        }
        getCartesianDirection(body.get(0), snakeLevel.getFoodPosition(), input);

        double[] output = network.calculate(input);
/*        System.out.println("input: " + Arrays.toString(input));
        System.out.println("output: " + Arrays.toString(output));*/

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
/*        if (move == Input.Keys.LEFT) {
            System.out.println("LEFT");
        } else if (move == Input.Keys.RIGHT) {
            System.out.println("RIGHT");
        } else if (move == Input.Keys.UP) {
            System.out.println("UP");
        } else if (move == Input.Keys.DOWN) {
            System.out.println("DOWN");
        }*/
        setNextMove(move);
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

        return snakeLevel.collide(head);

    }

    public boolean eat() {
        if (snakeLevel.getFoodPosition().equals(body.get(0))) {
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

    public Integer getNumberOfFeedings() {
        return nFeedings;
    }

    public Instant getLastAte() {
        return lastAte;
    }

    public Long getFinalLifeSpan() {
        return finalLifeSpan;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public Network getNetwork() {
        return network;
    }

    public double[] getCartesianDirection(Vector2 a, Vector2 b, double[] array) {

        Vector2 direction = new Vector2(b.x - a.x, b.y-a.y);

        array[4] = 0f;
        array[5] = 0f;
        array[6] = 0f;
        array[7] = 0f;

        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            // Left or right
            if (direction.x > 0) {
                /*return Snake.RIGHT;*/
                array[6] = 1.0f;
            } else {
/*                return Snake.LEFT;*/
                array[7] = 1.0f;
            }
        } else {
            // Up or down
            if (direction.y > 0) {
/*                return Snake.UP;*/
                array[4] = 1.0f;
            } else {
/*                return Snake.DOWN;*/
                array[5] = 1.0f;
            }
        }
        return array;
    }
}
