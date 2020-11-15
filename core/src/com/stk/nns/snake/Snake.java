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

    double[] output;
    Vector2 direction;

    private final int startingLength = 8;
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
    float distanceToFoodAtDeath = Float.MAX_VALUE;

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
            distanceToFoodAtDeath = snakeLevel.getDistanceToFood(newHead);
/*
            for (int i = 0; i < 1000; i++) {
                double[] target = snakeLevel.getTarget(nextMove, body.get(0), newHead, input, output);
                network.train(input, target, 0.3);
            }
*/

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
        System.out.println("\n----------------------------------------------------------------------------------------------------");
        double[] input = snakeLevel.getNeighborTiles(body.get(0));
        if (input.length != 8) {
            throw new IllegalStateException("Input length must be 8");
        }
        input = getCartesianDirection(body.get(0), snakeLevel.getFoodPosition(), input);



        output = network.calculate(input);

        System.out.println("input[0]: "+ input[0] + " = up value");
        System.out.println("input[1]: "+ input[1] + " = down value");
        System.out.println("input[2]: "+ input[2] + " = left value");
        System.out.println("input[3]: "+ input[3] + " = right value");
        System.out.println("input[4]: "+ input[4] + " = up food?");
        System.out.println("input[5]: "+ input[5] + " = down food?");
        System.out.println("input[6]: "+ input[6] + " = left food?");
        System.out.println("input[7]: "+ input[7] + " = right food?");


/*        System.out.println("input: " + Arrays.toString(input));
        System.out.println("output: " + Arrays.toString(output));*/


        if (output.length != 4) {
            throw new IllegalStateException("Output must be equal to 4");
        }

        double up = output[0];
        double down = output[1];
        double left = output[2];
        double right = output[3];

        System.out.println("\toutput[0]: "+ output[0] + " = network UP recommendation");
        System.out.println("\toutput[1]: "+ output[1] + " = network DOWN recommendation");
        System.out.println("\toutput[2]: "+ output[2] + " = network LEFT recommendation");
        System.out.println("\toutput[3]: "+ output[3] + " = network RIGHT recommendation");

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
        if (move == Input.Keys.LEFT) {
            System.out.println("\t\tSnake chooses LEFT");
        } else if (move == Input.Keys.RIGHT) {
            System.out.println("\t\tSnake chooses RIGHT");
        } else if (move == Input.Keys.UP) {
            System.out.println("\t\tSnake chooses UP");
        } else if (move == Input.Keys.DOWN) {
            System.out.println("\t\tSnake chooses DOWN");
        }
        setNextMove(move);
    }


    private boolean isMoveLegal(int move) {
        boolean isLegal = !(
                (lastMove == Input.Keys.UP && move == Input.Keys.DOWN) ||
                        (lastMove == Input.Keys.DOWN && move == Input.Keys.UP) ||
                        (lastMove == Input.Keys.LEFT && move == Input.Keys.RIGHT) ||
                        (lastMove == Input.Keys.RIGHT && move == Input.Keys.LEFT)
        );

        if (!isLegal) {
            System.out.println("-------------->>>>>>>>>>>>>>> ILLEGAL MOVE!!!!");
        }
        return isLegal;
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

    public double[] getCartesianDirection(Vector2 snakeHead, Vector2 foodPosition, double[] input) {

        Vector2 direction = new Vector2(foodPosition.x - snakeHead.x, foodPosition.y-snakeHead.y);

        System.out.println("Food direction: " + direction.x + "," + direction.y);

        input[4] = 0f;
        input[5] = 0f;
        input[6] = 0f;
        input[7] = 0f;


        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            // Left or right
            if (direction.x > 0) {
                /*return Snake.RIGHT;*/
                System.out.println("food is RIGHT");
                input[7] = 1.0f;
            } else {
                System.out.println("food is LEFT");
/*                return Snake.LEFT;*/
                input[6] = 1.0f;
            }
        } else {
            // Up or down
            if (direction.y > 0) {
                System.out.println("food is UP");
/*                return Snake.UP;*/
                input[4] = 1.0f;
            } else {
                System.out.println("food is DOWN");
/*                return Snake.DOWN;*/
                input[5] = 1.0f;
            }
        }
        return input;
    }

    public int getLastMove() {
        return lastMove;
    }

    public Float getDistanceToFoodAtDeath() {
        return distanceToFoodAtDeath;
    }
}
