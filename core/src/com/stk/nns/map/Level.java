package com.stk.nns.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.Main;
import com.stk.nns.snake.Snake;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Level {

    private final float SOLID = 1.0f;
    private final float SNAKE = 0.5f;
    private final float EMPTY = 0.25f;
    private final float FOOD = 0.0f;

    private List<Vector2> obstacles = new ArrayList<>();
    private List<Vector2> emptyPositions = new ArrayList<>();
    private Vector2 foodPosition;

    private Snake snake;

    Random rnd = new Random();

    HashMap<TileIndex, Tile> tiles;

    public static int WIDTH;
    public static int HEIGHT;

    public Level(String fileName) {
        tiles = new HashMap<>();

        List<String> lines = readFile(fileName);

        HEIGHT = lines.size();
        WIDTH = lines.get(0).length();


        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            System.out.println(line);
            for (int x = 0; x < line.length(); x++) {
                float value = Float.parseFloat(String.valueOf(line.charAt(x))) == 1.0f ? SOLID : EMPTY;
                Tile tile = new Tile(value,
                        new Vector2(x * Main.TILESIZE, lines.size()-y * Main.TILESIZE + Main.TILESIZE * 32 - 64));
                tiles.put(new TileIndex(tile.getPosition()), tile);

                sb.append((int)tile.getPosition().x +","+ (int)tile.getPosition().y + " ");
                if (tile.getValue() == 1) {
                    obstacles.add(tile.getPosition());
                } else {
                    emptyPositions.add(tile.getPosition());
                }
            }
            sb.append("\n");
        }


        System.out.println("Done");

        System.out.println(sb);

/*        for (Entry<TileIndex, Tile> entry : tiles.entrySet()) {
            Tile tile = entry.getValue();
            System.out.print(tile.getPosition().x + ","+ tile.getPosition().y + " ") ;
            if (tile.getPosition().x == 1024) {
                System.out.println("\n");
            }
        }*/
    }

    private List<String> readFile(String fileName) {
        List<String> lines = new ArrayList<>();
        File file = new File(fileName);    //creates a new file instance
        try {

            FileReader fr = new FileReader(file);   //reads the file
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public void render(SpriteBatch batch, Texture tileWall, Texture foodTile) {

        for (Entry<TileIndex, Tile> entry : tiles.entrySet()) {
            Tile tile = entry.getValue();
            if (tile.getValue() == SOLID) {
                batch.draw(tileWall, tile.getPosition().x, tile.getPosition().y);
            } else if (tile.getValue() == FOOD) {
                batch.draw(foodTile, tile.getPosition().x, tile.getPosition().y);
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

    public Vector2 getFoodPosition() {
        return foodPosition;
    }

    public void updateSnakePosition(Vector2 newHeadPosition, Vector2 lastPosition) {
        tiles.get(new TileIndex(newHeadPosition)).setValue(SNAKE);
        tiles.get(new TileIndex(lastPosition)).setValue(EMPTY);
    }

    public List<Vector2> getSnakePositions() {
        List<Entry<TileIndex, Tile>> entries = tiles.entrySet().stream().filter(
                e -> e.getValue().getValue() == SNAKE).collect(Collectors.toList());

        return entries.stream().map(e -> e.getValue().getPosition()).collect(Collectors.toList());
    }

    public Tile getTile(Vector2 position) {
        return tiles.get(new TileIndex(position));
    }

    public HashMap<TileIndex, Tile> getTiles() {
        return tiles;
    }


    public Vector2 getSize() {
        return new Vector2(WIDTH, HEIGHT);
    }

    public List<Vector2> getObstacles() {
        return obstacles;
    }

    public List<Vector2> getEmptyPositions() {
        return emptyPositions;
    }

    public boolean collide(Vector2 position) {
        return tiles.get(new TileIndex(position)).getValue() == SOLID;
    }
}
