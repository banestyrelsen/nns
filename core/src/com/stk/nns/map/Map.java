package com.stk.nns.map;

import com.badlogic.gdx.math.Vector2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Map {

    private Tile[][] tile;

    public Map(String fileName) {
        File file=new File(fileName);    //creates a new file instance
        List<String> lines = new ArrayList<>();
        try {

            FileReader fr=new FileReader(file);   //reads the file
            BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while((line=br.readLine())!=null) {
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
                tile[x][y] = new Tile(Integer.parseInt(String.valueOf(line.charAt(x))));
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
}
