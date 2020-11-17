package com.stk.nns.snake;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.Main;
import com.stk.nns.nn.Network;
import com.stk.nns.sound.PlaySound;
import com.stk.nns.map.GameBoard;
import com.stk.nns.nn.Brain;

import java.time.Instant;
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

    private final int startingLength = 6;
    private int lastMove = Input.Keys.UP;
    private int nextMove = Input.Keys.UP;
    int nFeedings = 0;
    Instant lastAte = Instant.now();
    Instant born = Instant.now();
    long finalLifeSpan = 0;
    boolean shouldBurp = false;
    boolean isAlive = true;

    PlaySound playSound;

    GameBoard gameBoard;
    Brain brain;

    public final Control control;
    int MOVES_LEFT_MAX = 80;
    int movesLeft = MOVES_LEFT_MAX;

    Network network;
    float distanceToFoodAtDeath = Float.MAX_VALUE;

    // Constructor for player controlled snake
    public Snake(Vector2 startPos, GameBoard gameBoard, PlaySound playSound) {
        this.control = Control.PLAYER_CONTROLLED;
        body = new LinkedList<>();
        body.add(new Vector2(startPos.x, startPos.y));
        for (int i = 1; i < startingLength; i++) {
            body.add(new Vector2(startPos.x, startPos.y - Main.TILESIZE));
        }
        this.gameBoard = gameBoard;
        this.playSound = playSound;
        this.direction = new Vector2(0, -1);
    }

    public Snake(Vector2 startPos, GameBoard gameBoard, PlaySound playSound, Network network) {
        this.control = Control.AI_CONTROLLED;
        body = new LinkedList<>();
        body.add(new Vector2(startPos.x, startPos.y));
        for (int i = 1; i < startingLength; i++) {
            body.add(new Vector2(startPos.x, startPos.y - Main.TILESIZE));
        }
        this.gameBoard = gameBoard;
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
            distanceToFoodAtDeath = gameBoard.getDistanceToFood(newHead);

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
        gameBoard.updateSnakePosition(newHead, prevHead, removedSegment);


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
        int forwardKey = gameBoard.getDirectionKeyInteger(body.get(0), body.get(1));
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

        double[] input = gameBoard.getTileOptions(body.get(0), leftKey, forwardKey, rightKey);

        int foodDirectionKey = gameBoard.getFoodDirection(body.get(0), lastMove, forwardKey);

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

        double[] output = network.calculate(input);

  /*              System.out.println("----------------------------------------");
        System.out.println(printDirection(leftKey, forwardKey, rightKey));
        System.out.println("Food is " +  getDirection(foodDirectionKey));
        System.out.println(Arrays.toString(input));
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
/*        System.out.println("Recommendation " + getDirection(nextMove));*/
/*        System.out.println("Snake chose: "  + getDirection(nextMove) + " (index "+largestIndex+")");*/

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

        return gameBoard.collide(head);

    }

    public boolean eat() {
        if (gameBoard.getFoodPosition().equals(body.get(0))) {
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

    public Network getNetwork() {
        return network;
    }

    public Float getDistanceToFoodAtDeath() {
        return distanceToFoodAtDeath;
    }

    private int getLargestIndex(double[] array, int start, int end) {
        double largest = Double.MIN_VALUE;
        int largestIndex = -1;
        for (int i = start; i <= end; i++) {
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
