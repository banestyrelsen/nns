package com.stk.nns.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.Main;
import com.stk.nns.game.Game;
import com.stk.nns.snake.Snake;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class SnakeLevel {

    public final static float SOLID = -1.0f;
    public final static float SNAKE = -0.9f;
    public final static float EMPTY = 0.5f;
    public final static float SNAKE_HEAD = 0.0f;
    public final static float FOOD = 1.0f;

    private List<Vector2> obstacles = new ArrayList<>();
    private List<Vector2> emptyPositions = new ArrayList<>();
    private Vector2 foodPosition;

    Random rnd = new Random();

    LinkedHashMap<TileIndex, Tile> tiles;

    public static int WIDTH;
    public static int HEIGHT;

    public SnakeLevel(String fileName) {
        tiles = new LinkedHashMap<>();

        List<String> lines = readFile(fileName);

        HEIGHT = lines.size();
        WIDTH = lines.get(0).length();


        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            /*          System.out.println(line);
             */
            for (int x = 0; x < line.length(); x++) {
                float value = Float.parseFloat(String.valueOf(line.charAt(x))) == 1.0f ? SOLID : EMPTY;
                Tile tile = new Tile(value,
                        new Vector2(x * Main.TILESIZE, lines.size() - y * Main.TILESIZE + Main.TILESIZE * 32 - 64));
                tiles.put(new TileIndex(tile.getPosition()), tile);

                sb.append((int) tile.getPosition().x + "," + (int) tile.getPosition().y + " ");
                if (tile.getValue() == 1) {
                    obstacles.add(tile.getPosition());
                } else {
                    emptyPositions.add(tile.getPosition());
                }
            }
            sb.append("\n");
        }


        /*        System.out.println("Done");*/

        /*                System.out.println(sb);*/

/*        for (Entry<TileIndex, Tile> entry : tiles.entrySet()) {
            Tile tile = entry.getValue();
            System.out.print(tile.getPosition().x + ","+ tile.getPosition().y + " ") ;
            if (tile.getPosition().x == 1024) {
                System.out.println("\n");
            }
        }*/
/*        System.out.println("ORDERED KEYS");
        for (Entry<TileIndex,Tile> entry:  tiles.entrySet()) {
            System.out.println(entry.getKey().position);
        }
        System.out.println("<<<<<<<<<<<<<<<--------------------->");*/
    }

    private List<String> readFile(String fileName) {
        String mapAsString = Gdx.files.internal(fileName).readString();    //creates a new file instance
        return Arrays.asList(mapAsString.split(System.lineSeparator()));
    }

    public void render(SpriteBatch batch, Texture tileWall, Texture foodTile, Texture tileHead, Texture textureSnakeBody) {

        for (Entry<TileIndex, Tile> entry : tiles.entrySet()) {
            Tile tile = entry.getValue();
            if (tile.getValue() == SOLID) {
                batch.draw(tileWall, tile.getPosition().x, tile.getPosition().y);
            } else if (tile.getValue() == FOOD) {
                batch.draw(foodTile, tile.getPosition().x, tile.getPosition().y);
            } else if (tile.getValue() == SNAKE) {
                batch.draw(textureSnakeBody, tile.getPosition().x, tile.getPosition().y);
            } else if (tile.getValue() == SNAKE_HEAD) {
                batch.draw(tileHead, tile.getPosition().x, tile.getPosition().y);
            }
        }
    }

    public void placeFood() {
        boolean positionFound = false;
        Vector2 candidate = null;
        while (!positionFound) {
            candidate = emptyPositions.get(rnd.nextInt(emptyPositions.size()));

            Tile tile = tiles.get(new TileIndex(candidate));
            if (tile.getValue() == EMPTY) {
                positionFound = true;
                tile.setValue(FOOD);
                foodPosition = candidate;
            }
        }

    }

    public void placeFood(Vector2 newPosition) {
        Tile tile = tiles.get(new TileIndex(newPosition));
        tile.setValue(FOOD);
        foodPosition = newPosition;
    }

    public Vector2 getFoodPosition() {
        return foodPosition;
    }

    public void updateSnakePosition(Vector2 newHeadPosition, Vector2 prevHeadPosition, Vector2 finalSegment) {
        tiles.get(new TileIndex(newHeadPosition)).setValue(SNAKE_HEAD);
        tiles.get(new TileIndex(prevHeadPosition)).setValue(SNAKE);
        tiles.get(new TileIndex(finalSegment)).setValue(EMPTY);
    }

    public List<Vector2> getSnakePositions() {
        List<Entry<TileIndex, Tile>> entries = tiles.entrySet().stream().filter(
                e -> e.getValue().getValue() == SNAKE).collect(Collectors.toList());

        return entries.stream().map(e -> e.getValue().getPosition()).collect(Collectors.toList());
    }

    public Tile getTile(Vector2 position) {
        return tiles.get(new TileIndex(position));
    }

    public LinkedHashMap<TileIndex, Tile> getTiles() {
        return tiles;
    }

    public List<Tile> getTileValues() {

        /*Entry<TileIndex,Tile> entry:  */
        return tiles.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());

    }

    public double[] getTileOptions(Vector2 snakeHead, int left, int forward, int right) {

        double[] tileOptions = new double[4];

        tileOptions[0] = getDirectionValue(left, snakeHead);
        tileOptions[1] = getDirectionValue(forward, snakeHead);
        tileOptions[2] = getDirectionValue(right, snakeHead);

        return tileOptions;
    }

    private float getDirectionValue(int direction, Vector2 headPosition) {
        switch (direction) {
            case Input.Keys.UP:
                return tiles.get(new TileIndex(new Vector2(headPosition.x, headPosition.y + Game.TILESIZE))).getValue();
            case Input.Keys.DOWN:
                return tiles.get(new TileIndex(new Vector2(headPosition.x, headPosition.y - Game.TILESIZE))).getValue();
            case Input.Keys.LEFT:
                return tiles.get(new TileIndex(new Vector2(headPosition.x - Game.TILESIZE, headPosition.y))).getValue();
            case Input.Keys.RIGHT:
                return tiles.get(new TileIndex(new Vector2(headPosition.x + Game.TILESIZE, headPosition.y))).getValue();
            default:
                throw new IllegalStateException("Invalid direction " + direction);
        }
    }

    public int getFoodDirection(Vector2 snakeHead, int lastMove, int forward) {

        Vector2 foodDirection = getDirection(snakeHead, foodPosition);

        int direction;

        if (Math.abs(foodDirection.x) > Math.abs(foodDirection.y)) {
            if (foodDirection.x > 0) {
                direction = Input.Keys.RIGHT; // 22
            } else {
                direction = Input.Keys.LEFT; // 21
            }
        } else {
            if (foodDirection.y > 0) {
                direction = Input.Keys.UP; // 19
            } else {
                direction = Input.Keys.DOWN; // 20
            }
        }

/*        if (direction == lastMove) {
            if (lastMove == Input.Keys.UP || lastMove == Input.Keys.DOWN) {
                direction = foodDirection.x > 0 ?  Input.Keys.RIGHT : Input.Keys.LEFT;
            } else if (lastMove == Input.Keys.LEFT || lastMove == Input.Keys.RIGHT) {
                direction = foodDirection.y > 0 ?  Input.Keys.UP : Input.Keys.DOWN;
            } else {
                throw new IllegalStateException("Direction should be a valid Input.Keys integer value but was " + direction);
            }
        }*/
        return direction;
    }

    public int getDirectionKeyInteger(Vector2 a, Vector2 b) {

        Vector2 direction = getDirection(b, a);

        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            if (direction.x > 0) {
                return Input.Keys.RIGHT;
            } else {
                return Input.Keys.LEFT;
            }
        } else {
            if (direction.y > 0) {
                return Input.Keys.UP;
            } else {
                return Input.Keys.DOWN;
            }
        }
    }


    public double[] getNeighborTiles(Vector2 headPosition) {

        double[] array = new double[8];

        array[0] = tiles.get(new TileIndex(new Vector2(headPosition.x, headPosition.y + Game.TILESIZE))).getValue(); // up
        array[1] = tiles.get(new TileIndex(new Vector2(headPosition.x, headPosition.y - Game.TILESIZE))).getValue(); // down
        array[2] = tiles.get(new TileIndex(new Vector2(headPosition.x - Game.TILESIZE, headPosition.y))).getValue(); // left
        array[3] = tiles.get(new TileIndex(new Vector2(headPosition.x + Game.TILESIZE, headPosition.y))).getValue(); // right

        return array;
    }

    public Vector2 getSize() {
        return new Vector2(WIDTH, HEIGHT);
    }

    public List<Vector2> getObstacles() {
        return obstacles;  }


public List<Vector2> getEmptyPositions() {
        return emptyPositions;
    }

    public boolean collide(Vector2 position) {
        float value = tiles.get(new TileIndex(position)).getValue();
/*        if (value < 0f)
        System.out.println("COLLIDE VALUE: " + value);*/
        return value == SOLID || value == SNAKE;
    }

    public double[] getTarget(int moveAttempted, Vector2 posBeforeMove, Vector2 move, double[] input, double[] output) {

/*        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("input: " + Arrays.toString(input));
        System.out.println("output: " + Arrays.toString(output));*/


        // What direction did it move?

        float actual = tiles.get(new TileIndex(move)).getValue();

        double[] target = new double[4];

        float up = tiles.get(new TileIndex(new Vector2(posBeforeMove.x, posBeforeMove.y + Game.TILESIZE))).getValue();
        float down = tiles.get(new TileIndex(new Vector2(posBeforeMove.x, posBeforeMove.y - Game.TILESIZE))).getValue();

        float left = tiles.get(new TileIndex(new Vector2(posBeforeMove.x - Game.TILESIZE, posBeforeMove.y))).getValue();
        float right = tiles.get(new TileIndex(new Vector2(posBeforeMove.x + Game.TILESIZE, posBeforeMove.y))).getValue();
        /*      System.out.println("up: " + up + ", down: " + down + ", left" + left + ", right: "+ right);*/


        target[0] = up;
        target[1] = down;
        target[2] = left;
        target[3] = right;

        if (moveAttempted == Input.Keys.UP) {
            target[0] = actual;
        }
        if (moveAttempted == Input.Keys.DOWN) {
            target[1] = actual;
        }
        if (moveAttempted == Input.Keys.LEFT) {
            target[2] = actual;
        }
        if (moveAttempted == Input.Keys.RIGHT) {
            target[3] = actual;
        }

        target[getDirectionInteger(move, getFoodPosition())] = SnakeLevel.FOOD;

/*        System.out.println("target: " + Arrays.toString(target));
        Vector2 foodDirection = getDirection(move, getFoodPosition());
        System.out.println("foodDirection: " + (foodDirection.x/Game.TILESIZE) + "," +(foodDirection.y/Game.TILESIZE));*/


        // Avoid wall/body


        // Move toward fruit


/*        input[4] = 0f; // UP
        input[5] = 0f; // DOWN
        input[6] = 0f; // LEFT
        input[7] = 0f; // RIGHT*/

/*        double up = output[0];
        double down = output[1];
        double left = output[2];
        double right = output[3];*/

        /*        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            // Left or right
            if (direction.x > 0) {
                *//*return Snake.RIGHT;*//*
                array[6] = 1.0f;
            } else {
                *//*                return Snake.LEFT;*//*
                array[7] = 1.0f;
            }
        } else {
            // Up or down
            if (direction.y > 0) {
                *//*                return Snake.UP;*//*
                array[4] = 1.0f;
            } else {
                *//*                return Snake.DOWN;*//*
                array[5] = 1.0f;
            }
        }*/

        return target;
    }

    public Vector2 getDirection(Vector2 a, Vector2 b) {
        return new Vector2(b.x - a.x, b.y - a.y);
    }

    public int getDirectionInteger(Vector2 a, Vector2 b) {

        Vector2 direction = getDirection(a, b);

        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            if (direction.x > 0) {
                return 3;
            } else {
                return 2;
            }
        } else {
            if (direction.y > 0) {
                return 0;
            } else {
                return 1;
            }
        }
    }


    public float getDistanceToFood(Vector2 snakeHead) {
        return getDirection(this.foodPosition, snakeHead).len();
    }
}
