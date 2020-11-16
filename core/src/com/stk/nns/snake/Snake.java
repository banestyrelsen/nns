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
import java.util.Random;

public class Snake {

    public static final float LEFT_INPUT = -1f;
    public static final float FORWARD_INPUT = 0;
    public static final float RIGHT_INPUT = 1f;


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
    int MOVES_LEFT_MAX = 100;
    int movesLeft = MOVES_LEFT_MAX;

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
     *
     * @return
     */
    public boolean move() {
        update();
        double[] input = null;
        if (control == Control.AI_CONTROLLED) {
            input = aiMove();
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
        movesLeft--;
        if (collide(newHead)) {
/*            System.out.println("\t----> COLLISION! <-----");*/

            isAlive = false;
            finalLifeSpan = Instant.now().toEpochMilli() - born.toEpochMilli();
            distanceToFoodAtDeath = snakeLevel.getDistanceToFood(newHead);

/*            for (int i = 0; i < 1000; i++) {

                network.train(input, getTarget(input), 0.3);
            }
            network.calculate(input);
            network.print();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
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

    private String printDirection(int left, int forward, int right) {
        return String.format("forward is %s, left is %s, right is %s", getDirection(forward), getDirection(left), getDirection(right));
    }

    private String getDirection(int d) {
        switch (d) {
            case Input.Keys.UP:
                return "UP";
            case Input.Keys.DOWN:
                return "DOWN";
            case Input.Keys.LEFT:
                return "LEFT";
            case Input.Keys.RIGHT:
                return "RIGHT";
            default:
                throw new IllegalStateException("Illegal key");
        }
    }

    public double[] aiMove() {
        int forwardKey = snakeLevel.getDirectionKeyInteger(body.get(0), body.get(1));
        int leftKey = -1;
        int rightKey = -1;

        // Translate food direction up/down/left/right to forward/left/right
        switch (forwardKey) {
            case Input.Keys.UP:
                leftKey = Input.Keys.LEFT;
                rightKey = Input.Keys.RIGHT;
                break;
            case Input.Keys.DOWN:
                leftKey = Input.Keys.RIGHT;
                rightKey = Input.Keys.LEFT;
                break;
            case Input.Keys.LEFT:
                leftKey = Input.Keys.DOWN;
                rightKey = Input.Keys.UP;
                break;
            case Input.Keys.RIGHT:
                leftKey = Input.Keys.UP;
                rightKey = Input.Keys.DOWN;
                break;
            default:
                break;
        }

        if (leftKey < 0 || rightKey < 0 || forwardKey < 0) {
            throw new IllegalStateException("Invalid value(s) for left/forward/right");
        }

        double[] input = snakeLevel.getTileOptions(body.get(0), leftKey, forwardKey, rightKey);

        int foodDirectionKey = snakeLevel.getFoodDirection(body.get(0), lastMove, forwardKey);

        float foodDirectionInput = Float.MIN_VALUE;
        if (foodDirectionKey == forwardKey) {
            foodDirectionInput = FORWARD_INPUT;
        } else if (foodDirectionKey == leftKey) {
            foodDirectionInput = LEFT_INPUT;
        } else if (foodDirectionKey == rightKey) {
            foodDirectionInput = RIGHT_INPUT;
        } else {
            // Food is behind us, pick random direction
            float r = new Random().nextFloat();
            if (r <= 0.33f) {
                foodDirectionInput = LEFT_INPUT;
            } else if (r > 0.33f && r < 0.67f) {
                foodDirectionInput = FORWARD_INPUT;
            } else if (r <= 1.0f) {
                foodDirectionInput = RIGHT_INPUT;
            }
        }

        if (foodDirectionInput != FORWARD_INPUT && foodDirectionInput != LEFT_INPUT && foodDirectionInput != RIGHT_INPUT) {
            throw new IllegalStateException("Invalid value for foodDirection, was " + foodDirectionInput);
        }


        input[3] = foodDirectionInput;
/*        System.out.println("----------------------------------------");
        System.out.println(printDirection(leftKey, forwardKey, rightKey));
        System.out.println("Food is " +  getDirection(foodDirectionKey));*/

        double[] output = network.calculate(input);

/*        System.out.println(Arrays.toString(input));
        System.out.println(Arrays.toString(output));*/

        if (output.length != 3) throw new IllegalStateException("Invalid output length");


        int largestIndex = getLargestIndex(output, 0, output.length - 1);

        if (largestIndex < 0 || largestIndex > output.length)
            throw new IllegalStateException("Invalid largestIndex size: " + largestIndex);


        if (largestIndex == 0) {
            nextMove = leftKey;
        } else if (largestIndex == 1) {
            nextMove = forwardKey;
        } else if (largestIndex == 2) {
            nextMove = rightKey;
        }
/*        System.out.println("Recommendation " + getDirection(nextMove));
        System.out.println("Snake chose: "  + getDirection(nextMove) + " (index "+largestIndex+")");*/
        return input;
    }

    public double[] aiSetNextMove() {
        System.out.println("\n----------------------------------------------------------------------------------------------------");
        /*        network.print();*/
        double[] input = snakeLevel.getNeighborTiles(body.get(0));
        if (input.length != 8) {
            throw new IllegalStateException("Input length must be 8");
        }
        input = getCartesianDirection(body.get(0), snakeLevel.getFoodPosition(), input);


        output = network.calculate(input);

        System.out.println("input[0]: " + input[0] + " = up value");
        System.out.println("input[1]: " + input[1] + " = down value");
        System.out.println("input[2]: " + input[2] + " = left value");
        System.out.println("input[3]: " + input[3] + " = right value");
        System.out.println("input[4]: " + input[4] + " = up food?");
        System.out.println("input[5]: " + input[5] + " = down food?");
        System.out.println("input[6]: " + input[6] + " = left food?");
        System.out.println("input[7]: " + input[7] + " = right food?");


/*        System.out.println("input: " + Arrays.toString(input));
        System.out.println("output: " + Arrays.toString(output));*/


        if (output.length != 4) {
            throw new IllegalStateException("Output must be equal to 4");
        }

        double up = output[0];
        double down = output[1];
        double left = output[2];
        double right = output[3];

        System.out.println("\toutput[0]: " + output[0] + " = network UP recommendation");
        System.out.println("\toutput[1]: " + output[1] + " = network DOWN recommendation");
        System.out.println("\toutput[2]: " + output[2] + " = network LEFT recommendation");
        System.out.println("\toutput[3]: " + output[3] + " = network RIGHT recommendation");


        double largest = Integer.MIN_VALUE;
        int largestIndex = -1;
        for (int i = 0; i < output.length; i++) {
            if (output[i] > largest) {
                largest = output[i];
                largestIndex = i;
            }
        }
        int move = Input.Keys.UP;
        switch (largestIndex) {
            case 0:
                move = Input.Keys.UP;
                break;
            case 1:
                move = Input.Keys.DOWN;
                break;
            case 2:
                move = Input.Keys.LEFT;
                break;
            case 3:
                move = Input.Keys.RIGHT;
                break;
        }


/*
        if (move < down) {
            move = Input.Keys.DOWN;
        }
        if (move < left) {
            move = Input.Keys.LEFT;
        }
        if (move < right) {
            move = Input.Keys.RIGHT;
        }*/
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

        return input;
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
/*        for (Vector2 segment : body) {
            if (head != segment && head.x == segment.x && head.y == segment.y) {
                return true;
            }
        }*/

        return snakeLevel.collide(head);

    }

    public boolean eat() {
        if (snakeLevel.getFoodPosition().equals(body.get(0))) {
            grow();
            MOVES_LEFT_MAX++;
            movesLeft = MOVES_LEFT_MAX;
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

        Vector2 direction = new Vector2(foodPosition.x - snakeHead.x, foodPosition.y - snakeHead.y);

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

    private double[] getTarget(double[] input) {
        System.out.println("input[0]: " + input[0] + " = up value");
        System.out.println("input[1]: " + input[1] + " = down value");
        System.out.println("input[2]: " + input[2] + " = left value");
        System.out.println("input[3]: " + input[3] + " = right value");
        System.out.println("input[4]: " + input[4] + " = up food?");
        System.out.println("input[5]: " + input[5] + " = down food?");
        System.out.println("input[6]: " + input[6] + " = left food?");
        System.out.println("input[7]: " + input[7] + " = right food?");

        double[] target = new double[4];

        for (int i = 0; i < 4; i++) {
            target[i] = input[i];
        }

        int foodIndex = getLargestIndex(input, 4, 7) - 4;

        if (target[foodIndex] > SnakeLevel.SNAKE_HEAD) {
            target[foodIndex] = SnakeLevel.FOOD;
        }

        System.out.println("input: " + Arrays.toString(input));
        System.out.println("target: " + Arrays.toString(target));
/*        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return target;
    }

    private int getLargestIndex(double[] array, int start, int end) {
        double largest = Double.MIN_VALUE;
        int largestIndex = -1;
        for (int i = start; i < end; i++) {
            if (array[i] > largest) {
                largest = array[i];
                largestIndex = i;
            }
        }
        return largestIndex;
    }

    public int getMovesLeft() {
        return movesLeft;
    }
}
