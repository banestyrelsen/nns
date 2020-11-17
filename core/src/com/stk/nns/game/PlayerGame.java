package com.stk.nns.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.input.GameInputProcessor;
import com.stk.nns.map.GameBoard;
import com.stk.nns.snake.Snake;
import com.stk.nns.sound.PlaySound;

import java.time.Instant;

public class PlayerGame extends Game {

    public PlayerGame(PlaySound playSound) {
        super(playSound);
        this.playSound = playSound;
    }

    public void create(BitmapFont mainFont, BitmapFont mainFontRed) {
        super.create(mainFont, mainFontRed);
        inputProcessor = new GameInputProcessor(camera, snake, this);
        newGame();
    }


    protected void newGame() {

        GAME_OVER = false;
        gameBoard = new GameBoard("maps/map0.map");
        snake = new Snake(new Vector2(480f, 576), gameBoard, playSound);

        inputProcessor.setSnake(snake);

        timeStarted = Instant.now();
        prevSnakeUpdate = timeStarted;
        prevSpeedUpdate = timeStarted;

        prevDuration = "";
        prevTimeleft = "";

        timeUntilStarvation = 10200;
        timeLeft = timeUntilStarvation;

        gameBoard.placeFood();
        inputProcessor.setSnake(snake);

    }
}
