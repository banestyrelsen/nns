package com.stk.nns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.food.Food;
import com.stk.nns.input.BoardInputProcessor;
import com.stk.nns.map.Map;
import com.stk.nns.snake.Snake;

import java.time.Instant;

public class Board {
    SpriteBatch batch;
    Texture tileWall;
    Texture tileHead;
    Texture tileFood;
    Map map;
    Food food;
    private OrthographicCamera camera;
    BoardInputProcessor boardInputProcessor;

    public static int WIDTH;
    public static int HEIGHT;

    Snake snake;
    Instant timeStarted;
    Instant prevSnakeUpdate;

    public static final int TILESIZE = 32;

    private boolean GAME_OVER = false;
    BitmapFont mainFont;
    BitmapFont mainFontRed;
    PlaySound playSound;

    public Board(PlaySound playSound) {
        this.playSound = playSound;
    }

    public BoardInputProcessor getBoardInputProcessor() {
        return boardInputProcessor;
    }

    public void create(BitmapFont mainFont, BitmapFont mainFontRed) {
        this.mainFont = mainFont;
        this.mainFontRed = mainFontRed;
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        map = new Map("maps/map1.map");

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.5f;
        camera.update();

        batch = new SpriteBatch();
        /*		img = new Texture("badlogic.jpg");*/
        tileWall = new Texture("tile_wall.png");
        tileHead = new Texture("tile_head_up.png");
        tileFood = new Texture("tile_food.png");


        boardInputProcessor = new BoardInputProcessor(camera, snake);

        Gdx.input.setInputProcessor(boardInputProcessor);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newGame();

    }

    private void newGame() {

        GAME_OVER = false;


        /*        basicScreen = new BasicScreen(map, camera, batch, tileWall);*/


        snake = new Snake(new Vector2(TILESIZE * 16, TILESIZE * 16), map.getObstacles(), playSound);
        timeStarted = Instant.now();
        prevSnakeUpdate = timeStarted;

        food = new Food();
        map.placeFood(food, snake);

        boardInputProcessor.setSnake(snake);

    }

    private void update() {
        if (!GAME_OVER) {
            if (snake.eat(food)) {
                map.placeFood(food, snake);
            }

            if (Instant.now().toEpochMilli() - prevSnakeUpdate.toEpochMilli() > 80) {

                if (!snake.move()) {
                    GAME_OVER = true;
                    playSound.gameOver();
                }
                prevSnakeUpdate = Instant.now();
            }
        }

    }

    public void render() {
        camera.update();
        update();

        if (GAME_OVER) {
            // Draw game over screen
            batch.begin();
            mainFontRed.draw(batch, "GAME OVER", TILESIZE * 8, TILESIZE * 16);
            batch.end();
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                newGame();
            }

        } else {
            Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.setProjectionMatrix(camera.combined);

            // Draw score
            batch.begin();
            mainFont.draw(batch, "" + snake.getnFeedings(), TILESIZE * 30, TILESIZE * 35);
            batch.end();

            batch.begin();

            // Draw map
            map.render(batch, tileWall);

            // Draw food
            batch.draw(tileFood, food.getPosition().x, food.getPosition().y);

            // Draw snake
            snake.render(batch, tileWall, tileHead);

            batch.end();

        }

    }

    public void dispose() {
        batch.dispose();
        tileWall.dispose();
    }
}
