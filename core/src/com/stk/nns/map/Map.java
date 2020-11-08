package com.stk.nns.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.Main;
import com.stk.nns.food.Food;
import com.stk.nns.snake.Snake;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {

    private Tile[][] tile;

    private List<Vector2> obstacles = new ArrayList<>();
    private List<Vector2> emptyPositions = new ArrayList<>();

    private Snake snake;

    Random rnd = new Random();

    public Map(String fileName) {
        File file = new File(fileName);    //creates a new file instance
        List<String> lines = new ArrayList<>();
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

        tile = new Tile[lines.get(0).length()][lines.size()];

        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            System.out.println(line);
            for (int x = 0; x < line.length(); x++) {
                tile[x][y] = new Tile(Integer.parseInt(String.valueOf(line.charAt(x))), new Vector2(x * Main.TILESIZE, y * Main.TILESIZE));
                if (tile[x][y].getValue() == 1) {
                    System.out.println("obstacle at: " + x + "," + y);
                    obstacles.add(tile[x][y].getPosition());
                } else {
                    emptyPositions.add(tile[x][y].getPosition());
                }
            }
        }

        System.out.println("Done");
    }

    public void render(SpriteBatch batch, Texture tileWall) {
        Vector2 size = getSize();
        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
                if (tile[x][y].getValue() == 1) {
                    batch.draw(tileWall, tile[x][y].getPosition().x, tile[x][y].getPosition().y);
                }
            }
        }
    }

    public void placeFood(Food food, Snake snake) {
        boolean positionFound = false;
        Vector2 candidate = null;
        while (!positionFound) {
            candidate = emptyPositions.get(rnd.nextInt(emptyPositions.size()));

            Vector2 finalCandidate = candidate;
            if (!snake.getBody().stream().anyMatch(segment -> segment.x == finalCandidate.x && segment.y == finalCandidate.y)) {
                positionFound = true;
            }
        }
        food.setPosition(new Vector2(candidate.x, candidate.y));
    }

    public Tile[][] getTile() {
        return tile;
    }


    public Vector2 getSize() {
        return new Vector2(this.tile[0].length, this.tile.length);
    }

    public List<Vector2> getObstacles() {
        return obstacles;
    }

    public List<Vector2> getEmptyPositions() {
        return emptyPositions;
    }
}
