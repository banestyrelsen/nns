package com.stk.nns.map;

import com.badlogic.gdx.math.Vector2;
import com.stk.nns.MyGdxGame;
import com.stk.nns.snake.Snake;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Map {

    private Tile[][] tile;

    private List<Vector2> obstacles = new ArrayList<>();

    private Snake snake;

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
            for (int x = 0; x < line.length(); x++) {
                tile[x][y] = new Tile(Integer.parseInt(String.valueOf(line.charAt(x))), new Vector2(x * MyGdxGame.TILESIZE, y * MyGdxGame.TILESIZE));
                if (tile[x][y].getValue() == 1) {
                    obstacles.add(tile[x][y].getPosition());
                }
            }
        }

        System.out.println("Done");
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
}
