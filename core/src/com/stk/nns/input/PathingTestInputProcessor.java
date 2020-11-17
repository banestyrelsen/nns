package com.stk.nns.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.stk.nns.map.GameBoard;
import com.stk.nns.map.Tile;
import com.stk.nns.util.VectorUtils;

public class PathingTestInputProcessor implements InputProcessor {

    OrthographicCamera camera;
    Vector2 mousePosition;
    Vector2 mouseDragPosition;
    GameBoard board;
    int button = -1;

    public PathingTestInputProcessor(OrthographicCamera camera) {
        this.camera = camera;
        this.mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    public void setGameBoard(GameBoard board) {
        this.board = board;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
        }
        return false;
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
        draw(button, getMousePosition());
        this.button = button;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        this.button = -1;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        this.mouseDragPosition =  VectorUtils.unproject(camera, new Vector2(screenX, screenY));
        System.out.println("DRAAAAG " + mouseDragPosition.x + " , " + mouseDragPosition.y);
        draw(button, mouseDragPosition);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        this.mousePosition =  VectorUtils.unproject(camera, new Vector2(screenX, screenY));
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
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

    public Vector2 getMousePosition() {
        return mousePosition;
    }

    private void draw(int button, Vector2 position) {

        Tile tile = board.getTileAtPosition(position);
        if ( tile != null) {
            switch (button) {
                case 0: // left mouse button
                    tile.setValue(GameBoard.SOLID);
                    break;
                case 1: // right mouse button
                    tile.setValue(GameBoard.EMPTY);
                    break;
                case 2: // middle mouse button
                    break;
                default:
                    break;
            }
        }
    }
}
