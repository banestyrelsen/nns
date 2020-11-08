package com.stk.nns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.food.Food;
import com.stk.nns.input.InputHandler;
import com.stk.nns.map.Map;
import com.stk.nns.snake.Snake;

import java.time.Instant;
import java.util.Random;

public class Board {
    SpriteBatch batch;
    Texture tileWall;
    Texture tileHead;
    Texture tileFood;
    Map map;
    Food food;
    private OrthographicCamera camera;
    InputHandler inputHandler;


    public static int WIDTH;
    public static int HEIGHT;
    BitmapFont gameOverFont;
    BitmapFont scoreFont;
    Snake snake;
    Instant timeStarted;
    Instant prevSnakeUpdate;
    Instant lastAte;
    Sound eatSound;
    Sound burpSound;
    Sound gameOverSound;



    public static final int TILESIZE = 32;

    private boolean GAME_OVER = false;

    Random rnd = new Random();

    boolean hasBurped = false;
    boolean shouldBurp = false;

    PlaySound playSound;

    public Board(PlaySound playSound) {
        this.playSound = playSound;
    }

    public void create() {
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


        inputHandler = new InputHandler(camera, snake);

        Gdx.input.setInputProcessor(inputHandler);

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

        gameOverFont = new BitmapFont(Gdx.files.internal("fonts/square-deal.fnt"), false);
        gameOverFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        gameOverFont.setColor(Color.RED);
        gameOverFont.getData().setScale(1, 1);
        gameOverFont.getData().markupEnabled = true;

        scoreFont = new BitmapFont(Gdx.files.internal("fonts/square-deal.fnt"), false);
        scoreFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        scoreFont.setColor(Color.WHITE);
        scoreFont.getData().setScale(1, 1);
        scoreFont.getData().markupEnabled = true;

        snake = new Snake(new Vector2(TILESIZE * 16, TILESIZE * 16), map.getObstacles(), playSound);
        timeStarted = Instant.now();
        prevSnakeUpdate = timeStarted;

        food = new Food();
        map.placeFood(food, snake);

        inputHandler.setSnake(snake);

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
            gameOverFont.draw(batch, "GAME OVER", TILESIZE * 8, TILESIZE * 16);
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
            scoreFont.draw(batch, "" + snake.getnFeedings(), TILESIZE * 30, TILESIZE * 35);
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
