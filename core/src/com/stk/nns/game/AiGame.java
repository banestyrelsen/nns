package com.stk.nns.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.input.GameInputProcessor;
import com.stk.nns.map.GameBoard;
import com.stk.nns.nn.Network;
import com.stk.nns.nn.Recombinator;
import com.stk.nns.snake.Snake;
import com.stk.nns.sound.PlaySound;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AiGame extends Game {

    List<Snake> snakes;
    int generationSize = 100;
    int generation;
    List<Network> childNetworks;
    int currentSnakeIndex = 0;

    public AiGame(PlaySound playSound) {
        super(playSound);
    }


    public void create(BitmapFont mainFont, BitmapFont mainFontRed) {
        super.create(mainFont, mainFontRed);
        snakes = new ArrayList<>();
        childNetworks = new ArrayList<>();
        snakeUpdateInterval = fastest;
        inputProcessor = new GameInputProcessor(camera, snake, this);
        firstGeneration();
    }

    private void firstGeneration() {
        generation = 1;
        newGame();
    }

    private void nextGeneration() {
        System.out.println("%%%%%%%%%%%%%%%%% GENERATION " + generation +" %%%%%%%%%%%%%%%%%");
        generation++;
        currentSnakeIndex = 0;
        childNetworks = Recombinator.recombine(snakes, generation);


            snakes = new ArrayList<>();
            newGame();
  /*      }*/
    }




    protected void newGame() {
/*        if (generation < 10) {
            snakeUpdateInterval = 0;
        }*/
        GAME_OVER = false;
        gameBoard = new GameBoard("maps/map0.map");
        Network network;
        if (generation == 1) {
            network = new Network(4, 4, 3);
        } else {
             network = childNetworks.get(currentSnakeIndex);
        }
        snakes.add(new Snake(new Vector2(512f, 512), gameBoard, playSound, network));
        snake = snakes.get(snakes.size()-1);
        inputProcessor.setSnake(snake);

        timeStarted = Instant.now();
        prevSnakeUpdate = timeStarted;
        prevSpeedUpdate = timeStarted;

        prevDuration = "";
        prevTimeleft = "";

/*        timeUntilStarvation = 4200;*/
        timeLeft = timeUntilStarvation;

        gameBoard.placeFood(new Vector2(128f, 512f));
        // gameBoard.placeFood(new Vector2(1024f - 96f, 128f));


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
