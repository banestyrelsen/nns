package com.stk.nns.menu;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.stk.nns.Main;
import com.stk.nns.sound.PlaySound;

public class Menu {

    PlaySound playSound;
    SpriteBatch batch;
    BitmapFont mainFont;

    enum MenuOptions{
        PLAY_GAME
    }

    public Menu(PlaySound playSound, BitmapFont mainFont) {
        this.playSound = playSound;
        this.batch = new SpriteBatch();
        this.mainFont = mainFont;
    }


    public void render() {
        batch.begin();
        mainFont.draw(batch, getMenuString(), 16*32, 16*32);
        batch.end();

    }

    private String getMenuString() {
        StringBuilder sb = new StringBuilder();

        return "1. Play\n"
                +"2. AI Play\n"
                + "Q. Quit";

    }
}
