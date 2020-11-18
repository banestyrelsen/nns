package com.stk.nns.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.game.Pathing;
import com.stk.nns.map.GameBoard;
import com.stk.nns.map.Tile;
import com.stk.nns.util.VectorUtils;

import java.time.Instant;

public class PathingTestInputProcessor implements InputProcessor {

    OrthographicCamera camera;
    Vector2 mousePosition;
    Vector2 mouseDragPosition;
    Vector2 mouse2TouchDownPosition;
    Vector2 mouse2TouchUpPosition;
    GameBoard board;
    Pathing pathing;
    int button = -1;
    Instant lastPathChange;


    public PathingTestInputProcessor(OrthographicCamera camera) {
        this.camera = camera;
        this.mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        lastPathChange = Instant.now();
    }

    public void setGameBoard(GameBoard board) {
        this.board = board;
    }

    public void setPathing(Pathing pathing) {
        this.pathing = pathing;
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
        mouseButtonTouchDownAction(button, getMousePosition());
        this.button = button;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        mouseButtonTouchUpAction(button, getMousePosition());
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        this.mouseDragPosition =  VectorUtils.unproject(camera, new Vector2(screenX, screenY));
        System.out.println("DRAAAAG " + mouseDragPosition.x + " , " + mouseDragPosition.y);
        mouseButtonTouchDownAction(button, mouseDragPosition);
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

    private void mouseButtonTouchDownAction(int button, Vector2 position) {

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
                    if (Instant.now().toEpochMilli() - lastPathChange.toEpochMilli() > 50) {
                        this.pathing.setPathPosition(tile);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void mouseButtonTouchUpAction(int button, Vector2 position) {

        Tile tile = board.getTileAtPosition(position);
        if ( tile != null) {
            switch (button) {
                case 0: // left mouse button
                    this.button = -1;
                    break;
                case 1: // right mouse button

                    break;
                case 2: // middle mouse button

                    break;
                default:
                    break;
            }
        }
    }

    public Vector2 getMouse2TouchDownPosition() {
        return mouse2TouchDownPosition;
    }

    public void setMouse2TouchDownPosition(Vector2 mouse2TouchDownPosition) {
        this.mouse2TouchDownPosition = mouse2TouchDownPosition;
    }

    public Vector2 getMouse2TouchUpPosition() {
        return mouse2TouchUpPosition;
    }

    public void setMouse2TouchUpPosition(Vector2 mouse2TouchUpPosition) {
        this.mouse2TouchUpPosition = mouse2TouchUpPosition;
    }
}
