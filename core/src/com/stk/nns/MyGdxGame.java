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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture tileWhite;
    Texture tileGreen;
    Map map;
    Food food;
    BasicScreen basicScreen;
    private OrthographicCamera camera;

    public static int WIDTH;
    public static int HEIGHT;
    BitmapFont font;
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

    @Override
    public void create() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        System.out.println("---------------->>>>>>>>>>>>>>>>> " + Gdx.graphics.getWidth() + ", " + Gdx.graphics.getHeight());
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = -2f;
        camera.update();

        batch = new SpriteBatch();
        /*		img = new Texture("badlogic.jpg");*/
        tileWhite = new Texture("tile_white.png");
        tileGreen= new Texture("tile_green.png");
        eatSound = Gdx.audio.newSound(Gdx.files.internal("sound/apple-crunch.wav"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sound/fart2.wav"));
        burpSound = Gdx.audio.newSound(Gdx.files.internal("sound/burp1.wav"));
        newGame();
    }

    private void newGame() {
        GAME_OVER = false;
        map = new Map("maps/map1.map");

        basicScreen = new BasicScreen(map, camera, batch, tileWhite);

        font = new BitmapFont(Gdx.files.internal("fonts/square-deal.fnt"), true);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.setColor(Color.RED);
        font.getData().setScale(-1,1);
        font.getData().markupEnabled = true;

        snake = new Snake(new Vector2(TILESIZE * 16, TILESIZE * 16));
        timeStarted = Instant.now();
        prevSnakeUpdate = timeStarted;

        food = new Food();
        map.placeFood(food, snake);
    }

    @Override
    public void render() {
        if (GAME_OVER) {
            batch.begin();
            font.draw(batch, "GAME OVER", TILESIZE * 24, TILESIZE * 16);
            batch.end();
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                newGame();
            }

        } else {

            Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.setProjectionMatrix(camera.combined);

            batch.begin();
            if (snake.collide(map.getObstacles())) {
                GAME_OVER = true;
                gameOverSound.play();
                /*			Gdx.app.exit();*/
            }

            if (snake.eat(food)) {
                lastAte = Instant.now();
                eatSound.play();
                if (rnd.nextFloat() <= 0.33f) {
                    shouldBurp = true;
                    hasBurped = false;
                }
                map.placeFood(food, snake);
            }

            if (Instant.now().toEpochMilli() - prevSnakeUpdate.toEpochMilli() > 80) {
                if (shouldBurp && !hasBurped && Instant.now().toEpochMilli() - lastAte.toEpochMilli() > 800 ) {
                    burpSound.play();
                    shouldBurp = false;
                    hasBurped = false;

                }
                snake.update();
                prevSnakeUpdate = Instant.now();
            }


            /*		basicScreen.render(1f);*/


            Tile[][] tile = map.getTile();
            Vector2 size = map.getSize();

            batch.setProjectionMatrix(camera.combined);
            /*		batch.begin();*/


            for (int x = 0; x < size.x; x++) {

                for (int y = 0; y < size.y; y++) {
                    if (tile[x][y].getValue() == 1) {
                        batch.draw(tileWhite, tile[x][y].getPosition().x, tile[x][y].getPosition().y);
                    }
                }
            }

            batch.draw(tileGreen, food.getPosition().x, food.getPosition().y);

            for (Vector2 segment : snake.getBody()) {
                batch.draw(tileWhite, segment.x, segment.y);
            }

            batch.end();

        }
        InputHandler.handleInput();

    }

    @Override
    public void dispose() {
        batch.dispose();
        tileWhite.dispose();
    }
}
