package com.stk.nns.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.snake.Snake;

public class BoardInputProcessor implements InputProcessor {


    private Snake snake;
    private OrthographicCamera camera;
    private Vector2 direction = new Vector2(0,1);

    public BoardInputProcessor(OrthographicCamera camera, Snake snake) {
        this.camera = camera;
        this.snake = snake;
    }

    public void setSnake(Snake snake) {
        this.snake = snake;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
        }

        if (keycode == Input.Keys.LEFT ||
                keycode ==  Input.Keys.RIGHT ||
                keycode ==  Input.Keys.UP ||
                keycode ==  Input.Keys.DOWN ) {
            snake.setNextMove(keycode);
            return true;
        }


        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        System.out.println("NOICE x: " + amountX);
        System.out.println("NOICE y: " + amountY);
        if (amountY == -1) {
            // Zoom in
            if (camera.zoom > 1) {
                // Zoom faster if far away
                camera.zoom += 0.15;
            }
            camera.zoom += 0.05;
            if (camera.zoom < 0.05f) {
                camera.zoom = 0.05f;
            }
        } else if (amountY == 1) {
            // Zoom out
            if (camera.zoom > 1) {
                // Zoom faster if far away
                camera.zoom -= 0.15;
            }

            camera.zoom -= 0.05;
            if (camera.zoom > 5.0f) {
                camera.zoom = 5.0f;
            }
        }
        return false;
    }
}
