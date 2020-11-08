package com.stk.nns;

import com.badlogic.gdx.ApplicationAdapter;
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
import com.stk.nns.map.Tile;
import com.stk.nns.screen.BasicScreen;
import com.stk.nns.snake.Snake;

import java.time.Instant;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture tileWall;
    Texture tileHead;
    Texture tileFood;
    Map map;
    Food food;
    BasicScreen basicScreen;
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

    int nFeedings = 0;
    int nLength = 0;

    public static final int TILESIZE = 32;

    private boolean GAME_OVER = false;

    Random rnd = new Random();

    boolean hasBurped = false;
    boolean shouldBurp = false;

    @Override
    public void create() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        System.out.println("---------------->>>>>>>>>>>>>>>>> " + Gdx.graphics.getWidth() + ", " + Gdx.graphics.getHeight());
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 2f;
        camera.update();


        batch = new SpriteBatch();
        /*		img = new Texture("badlogic.jpg");*/
        tileWall = new Texture("tile_wall.png");
        tileHead = new Texture("tile_head_up.png");
        tileFood = new Texture("tile_food.png");
        eatSound = Gdx.audio.newSound(Gdx.files.internal("sound/apple-crunch.wav"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sound/fart2.wav"));
        burpSound = Gdx.audio.newSound(Gdx.files.internal("sound/burp1.wav"));

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
        nFeedings = 0;
        GAME_OVER = false;
        map = new Map("maps/map1.map");

        basicScreen = new BasicScreen(map, camera, batch, tileWall);

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

        snake = new Snake(new Vector2(TILESIZE * 16, TILESIZE * 16), map.getObstacles());
        timeStarted = Instant.now();
        prevSnakeUpdate = timeStarted;

        food = new Food();
        map.placeFood(food, snake);

        nLength = snake.getBody().size();

        inputHandler.setSnake(snake);

    }

    private void update() {
        if (!GAME_OVER) {
            if (snake.eat(food)) {
                nFeedings++;
                lastAte = Instant.now();
                eatSound.play();
                if (rnd.nextFloat() <= 0.20f) {
                    shouldBurp = true;
                    hasBurped = false;
                }
                map.placeFood(food, snake);
            }

            if (Instant.now().toEpochMilli() - prevSnakeUpdate.toEpochMilli() > 80) {
                if (shouldBurp && !hasBurped && Instant.now().toEpochMilli() - lastAte.toEpochMilli() > 800) {
                    burpSound.play();
                    shouldBurp = false;
                    hasBurped = false;

                }
                if (!snake.move()) {
                    GAME_OVER = true;
                    gameOverSound.play();
                }
                prevSnakeUpdate = Instant.now();
            }
        }

    }

    @Override
    public void render() {
        camera.update();
        update();

        if (GAME_OVER) {
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
            scoreFont.draw(batch, "" + nFeedings, TILESIZE * 30, TILESIZE * 35);
            batch.end();

            batch.begin();

            Tile[][] tile = map.getTile();
            Vector2 size = map.getSize();


            // Draw wall
            for (int x = 0; x < size.x; x++) {
                for (int y = 0; y < size.y; y++) {
                    if (tile[x][y].getValue() == 1) {
                        batch.draw(tileWall, tile[x][y].getPosition().x, tile[x][y].getPosition().y);
                    }
                }
            }

            // Draw food
            batch.draw(tileFood, food.getPosition().x, food.getPosition().y);

            drawSnake();

/*            for (Vector2 segment : snake.getBody()) {
                batch.draw(tileWall, segment.x, segment.y);
            }*/

            batch.end();

        }
        /*        inputHandler.handleInput();*/

    }

    private void drawSnake() {
        // Draw snake
        for (int i = 0; i < snake.getBody().size(); i++) {
            Vector2 segment = snake.getBody().get(i);
            if (i == 0) {
                batch.draw(tileHead, segment.x, segment.y);
            } else {
                batch.draw(tileWall, segment.x, segment.y);
            }

        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        tileWall.dispose();
    }
}
