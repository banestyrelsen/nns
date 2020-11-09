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
import com.stk.nns.input.GameInputProcessor;
import com.stk.nns.map.Level;
import com.stk.nns.snake.Snake;

import java.time.Instant;

public class Game {
    SpriteBatch batch;
    Texture tileWall;
    Texture tileHead;
    Texture tileFood;
    Level level;
    private OrthographicCamera camera;
    GameInputProcessor gameInputProcessor;

    public static int WIDTH;
    public static int HEIGHT;

    Snake snake;
    Instant timeStarted;
    Instant prevSnakeUpdate;
    Instant prevSpeedUpdate;

    int slowest = 200;
    int fastest = 10;

    public static final int TILESIZE = 32;

    private boolean GAME_OVER = false;
    BitmapFont mainFont;
    BitmapFont mainFontRed;
    PlaySound playSound;

    public Game(PlaySound playSound) {
        this.playSound = playSound;
    }

    public GameInputProcessor getGameInputProcessor() {
        return gameInputProcessor;
    }

    private int snakeUpdateInterval = 80;



    public void create(BitmapFont mainFont, BitmapFont mainFontRed) {
        this.mainFont = mainFont;
        this.mainFontRed = mainFontRed;
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();


        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.5f;
        camera.update();


        batch = new SpriteBatch();
        tileWall = new Texture("tile_wall.png");
        tileHead = new Texture("tile_head_up.png");
        tileFood = new Texture("tile_food.png");


        gameInputProcessor = new GameInputProcessor(camera, snake, this);

        Gdx.input.setInputProcessor(gameInputProcessor);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newGame();

    }

    private void newGame() {
        GAME_OVER = false;
        level = new Level("maps/map1.map");
        snake = new Snake(new Vector2(480f, 576), level, playSound);
        timeStarted = Instant.now();
        prevSnakeUpdate = timeStarted;
        prevSpeedUpdate = timeStarted;

        level.placeFood();

        gameInputProcessor.setSnake(snake);

    }

    private void update() {
        if (!GAME_OVER) {
            if (snake.eat()) {
                level.placeFood();
            }

            if (Instant.now().toEpochMilli() - prevSnakeUpdate.toEpochMilli() > snakeUpdateInterval) {

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
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);



            // Draw score
            batch.begin();
            drawSpeed();
            mainFont.draw(batch, "" + snake.getnFeedings(), TILESIZE * 30, TILESIZE * 37);
            batch.end();


            batch.begin();
            // Draw level
            level.render(batch, tileWall, tileFood);

            // Draw snake
            snake.render(batch, tileWall, tileHead);
            batch.end();

        if (GAME_OVER) {
            // Draw game over screen
            batch.begin();
            mainFontRed.draw(batch, "GAME OVER", TILESIZE * 8, TILESIZE * 16);

            batch.end();
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                newGame();
            }

        }


    }

    public void dispose() {
        batch.dispose();
        tileWall.dispose();
        tileFood.dispose();
        mainFont.dispose();
        mainFontRed.dispose();
    }

    private void drawSpeed() {
        mainFont.draw(batch, "" + getSpeed(), TILESIZE * 0, TILESIZE * 37);
    }

    public void changeSnakeUpdateInterval(int delta) {
        if (Instant.now().toEpochMilli() - prevSpeedUpdate.toEpochMilli() > 10) {
            if (!(snakeUpdateInterval + delta <= 10) && snakeUpdateInterval + delta < 200) {
                snakeUpdateInterval += snakeUpdateInterval > 50 ? delta * 2 : delta * 2;
            }
            System.out.println("SPEED: " + snakeUpdateInterval);
        }

    }

    private int getSpeed() {
        return ((slowest - (snakeUpdateInterval - fastest)) / 2);
    }
}
