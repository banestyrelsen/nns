package com.stk.nns.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.input.GameInputProcessor;
import com.stk.nns.map.SnakeLevel;
import com.stk.nns.snake.Control;
import com.stk.nns.snake.Snake;
import com.stk.nns.sound.PlaySound;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public abstract class Game {

    SpriteBatch batch;
    static Texture tileWall;
    static Texture textureSnakeHead;
    static Texture textureSnakeBody;
    static Texture tileFood;
    SnakeLevel snakeLevel;
    protected OrthographicCamera camera;
    GameInputProcessor inputProcessor;

    public static int WIDTH;
    public static int HEIGHT;

    Snake snake;
    Instant timeStarted;
    Instant prevSnakeUpdate;
    protected Instant prevSpeedUpdate;

    int slowest = 200;
    int fastest = 10;

    public static final int TILESIZE = 32;

    protected boolean GAME_OVER = false;
    BitmapFont mainFont;
    BitmapFont mainFontRed;
    PlaySound playSound;
    String prevDuration;
    String prevTimeleft;

    long timeUntilStarvation = 10200;
    long timeLeft = timeUntilStarvation;

    public InputProcessor getGameInputProcessor() {
        return inputProcessor;
    }
    protected int snakeUpdateInterval = 80; // Can be changed during the game

    public Game(PlaySound playSound) {
        this.playSound = playSound;
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.5f;
        camera.update();


        batch = new SpriteBatch();
        tileWall = tileWall == null ? new Texture("tile_wall.png") : tileWall;
        textureSnakeHead = textureSnakeHead == null ? new Texture("tile_head.png") : textureSnakeHead;
        textureSnakeBody  = textureSnakeBody == null ? new Texture("tile_snake_body.png") : textureSnakeBody;
        tileFood = tileFood == null ? new Texture("tile_food.png") : tileFood;


    }




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
        tileWall = tileWall == null ? new Texture("tile_wall.png") : tileWall;
        textureSnakeHead = textureSnakeHead == null ? new Texture("tile_head.png") : textureSnakeHead;
        textureSnakeBody  = textureSnakeBody == null ? new Texture("tile_snake_body.png") : textureSnakeBody;
        tileFood = tileFood == null ? new Texture("tile_food.png") : tileFood;

        inputProcessor = new GameInputProcessor(camera, snake, this);
        Gdx.input.setInputProcessor(inputProcessor);



    }

    protected void newGame() {
/*        GAME_OVER = false;
        level = new Level("maps/map0.map");
        snake = new Snake(Control.AI_CONTROLLED, new Vector2(480f, 576), level, playSound);
        timeStarted = Instant.now();
        prevSnakeUpdate = timeStarted;
        prevSpeedUpdate = timeStarted;

        prevDuration = "";
        prevTimeleft = "";

        timeUntilStarvation = 10200;
        timeLeft = timeUntilStarvation;

        level.placeFood();

        playerGameInputProcessor.setSnake(snake);*/

    }

    protected void update() {
        if (!GAME_OVER) {
            if (snake.eat()) {
                if (snake.control == Control.AI_CONTROLLED) {
                    if (snake.getNumberOfFeedings() == 1) {
                        snakeLevel.placeFood(new Vector2(256f, 256f));
                    } else if (snake.getNumberOfFeedings() == 2) {
                        snakeLevel.placeFood(new Vector2(768f, 768f));
                    }else if (snake.getNumberOfFeedings() == 3) {
                        snakeLevel.placeFood(new Vector2(768f, 256f));
                    } else {
                        snakeLevel.placeFood();
                    }

                } else {
                    snakeLevel.placeFood();
                }
            }

            if (Instant.now().toEpochMilli() - prevSnakeUpdate.toEpochMilli() > snakeUpdateInterval) {

                if (!snake.move()) {
                    GAME_OVER = true;
                    if (snake.control == Control.PLAYER_CONTROLLED) {
                        playSound.gameOver();
                    }
                }
                prevSnakeUpdate = Instant.now();
            }

            if (timeLeft <= 1) {
                GAME_OVER = true;
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
        mainFont.draw(batch, "" + snake.getNumberOfFeedings(), TILESIZE * 30, TILESIZE * 37);
        mainFont.draw(batch, "" + (GAME_OVER ? prevDuration : getTimeString()), TILESIZE * 0, TILESIZE * -2);
        drawTimeLeft();

        batch.end();


        batch.begin();
        // Draw level
        snakeLevel.render(batch, tileWall, tileFood, textureSnakeHead, textureSnakeBody);

        // Draw snake
/*        snake.render(batch, tileWall, tileHead);*/
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

    protected void drawSpeed() {
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

    protected int getSpeed() {
        return ((slowest - (snakeUpdateInterval - fastest)) / 2);
    }

    protected String getTimeString() {
        long duration = Instant.now().toEpochMilli() - timeStarted.toEpochMilli();

        prevDuration = String.format("%01d.%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        return prevDuration;
    }

    protected String getTimeRemaining() {
        long sinceAte = Instant.now().toEpochMilli() - snake.getLastAte().toEpochMilli();
        timeLeft = timeUntilStarvation - sinceAte;

        prevTimeleft = String.format("%d", TimeUnit.MILLISECONDS.toSeconds(timeLeft));
        return prevTimeleft;
    }

    protected void drawTimeLeft() {
        BitmapFont font = mainFont;
        if (timeLeft < 4000) {
            font = mainFontRed;
        }
        font.draw(batch, "" + (GAME_OVER ? prevTimeleft : getTimeRemaining()), TILESIZE * 30, TILESIZE * -2);
    }
}
