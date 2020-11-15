package com.stk.nns.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.input.GameInputProcessor;
import com.stk.nns.map.SnakeLevel;
import com.stk.nns.nn.Network;
import com.stk.nns.nn.Recombinator;
import com.stk.nns.snake.Snake;
import com.stk.nns.sound.PlaySound;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AiGame extends Game {

    List<Snake> snakes;
    int generationSize = 50;
    int generation;
    List<Network> childNetworks;
    int currentSnakeIndex = 0;

    public AiGame(PlaySound playSound) {
        super(playSound);
    }


    public void create(BitmapFont mainFont, BitmapFont mainFontRed) {
        super.create(mainFont, mainFontRed);

        snakes = new ArrayList<>();
        snakeUpdateInterval = slowest;
        inputProcessor = new GameInputProcessor(camera, snake, this);
        firstGeneration();
    }

    private void firstGeneration() {
        generation = 1;
        newGame();
    }

    private void nextGeneration() {
        generation++;
        currentSnakeIndex = 0;

        System.out.println("%%%%%%%%%%%%%%%%% GENERATION " + generation +" %%%%%%%%%%%%%%%%%");
        childNetworks = Recombinator.recombine(snakes, generation);
        snakes = new ArrayList<>();
        newGame();
    }


    protected void newGame() {
        GAME_OVER = false;
        snakeLevel = new SnakeLevel("maps/map0.map");
        Network network;
        if (generation == 1) {
            network = new Network(8, 5, 4);
        } else {
             network = childNetworks.get(currentSnakeIndex);
        }
        snakes.add(new Snake(new Vector2(512f, 512), snakeLevel, playSound, network));
        snake = snakes.get(snakes.size()-1);
        inputProcessor.setSnake(snake);

        timeStarted = Instant.now();
        prevSnakeUpdate = timeStarted;
        prevSpeedUpdate = timeStarted;

        prevDuration = "";
        prevTimeleft = "";

        timeUntilStarvation = 10200;
        timeLeft = timeUntilStarvation;

        snakeLevel.placeFood(new Vector2(256f, 768f));


    }

    protected void update() {
        super.update();

        if (GAME_OVER) {

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentSnakeIndex++;
            if (currentSnakeIndex < generationSize) {
                newGame();
            } else {
                nextGeneration();
            }
        }
    }

    public void render() {
        super.render();
        batch.begin();
        mainFont.draw(batch, "" + generation + "." + (currentSnakeIndex + 1), TILESIZE * 14, TILESIZE * 37);
        batch.end();
    }
}
