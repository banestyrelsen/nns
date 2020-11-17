package com.stk.nns.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.stk.nns.Main;

public class MenuInputProcessor implements InputProcessor  {
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.NUM_1 || keycode == Input.Keys.NUMPAD_1) {
            Main.newMode = Main.Mode.PLAY;
        }
        if (keycode == Input.Keys.NUM_2 || keycode == Input.Keys.NUMPAD_2) {
            Main.newMode = Main.Mode.AI;
        }
        if (keycode == Input.Keys.NUM_3 || keycode == Input.Keys.NUMPAD_3) {
            Main.newMode = Main.Mode.PATHING;
        }
        if (keycode == Input.Keys.Q || keycode == Input.Keys.ESCAPE) {
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
        return false;
    }
}
