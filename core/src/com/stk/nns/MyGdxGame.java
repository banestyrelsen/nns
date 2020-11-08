package com.stk.nns;

import com.badlogic.gdx.ApplicationAdapter;

public class MyGdxGame extends ApplicationAdapter {
    public static final int TILESIZE = 32;

/*    SpriteBatch batch;
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

    int nFeedings = 0;
    int nLength = 0;

    public static final int TILESIZE = 32;

    private boolean GAME_OVER = false;

    Random rnd = new Random();

    boolean hasBurped = false;
    boolean shouldBurp = false;*/

    Board board;
    PlaySound playSound;
    @Override
    public void create() {
        playSound = new PlaySound();
        board = new Board(playSound);
        board.create();
/*        board.create();*/
/*        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        map = new Map("maps/map1.map");

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.5f;
        camera.update();

        batch = new SpriteBatch();
        *//*		img = new Texture("badlogic.jpg");*//*
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
        newGame();*/

    }

/*    private void newGame() {
        nFeedings = 0;
        GAME_OVER = false;


        *//*        basicScreen = new BasicScreen(map, camera, batch, tileWall);*//*

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

    }*/

    @Override
    public void render() {
        board.render();
/*        camera.update();
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
            scoreFont.draw(batch, "" + nFeedings, TILESIZE * 30, TILESIZE * 35);
            batch.end();

            batch.begin();

            // Draw map
            map.render(batch, tileWall);

            // Draw food
            batch.draw(tileFood, food.getPosition().x, food.getPosition().y);

            // Draw snake
            snake.render(batch, tileWall, tileHead);

            batch.end();

        }*/

    }

    @Override
    public void dispose() {
        board.dispose();
/*        batch.dispose();
        tileWall.dispose();
        tileHead.dispose();
        tileFood.dispose();*/
    }
}
