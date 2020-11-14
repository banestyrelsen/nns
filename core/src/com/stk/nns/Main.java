package com.stk.nns;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.stk.nns.input.GameInputProcessor;
import com.stk.nns.input.MenuInputProcessor;
import com.stk.nns.menu.Menu;

public class Main extends ApplicationAdapter {
    public static final int TILESIZE = 32;

    Game game;
    PlaySound playSound;
    Menu menu;
    BitmapFont mainFont;
    BitmapFont mainFontRed;
    MenuInputProcessor menuInputProcessor;
    GameInputProcessor gameInputProcessor;

    private Mode mode = Mode.MENU;
    public static Mode newMode = Mode.MENU;
    public enum Mode {
        PLAY,
        MENU
    }

    public void setMode(Mode newMode) {
        if (newMode != mode) {
            mode = newMode;
            switch (mode) {
                case MENU:
                    Gdx.input.setInputProcessor(menuInputProcessor);
                case PLAY:
                    Gdx.input.setInputProcessor(game.getGameInputProcessor());
            }
        }
    }

    @Override
    public void create() {
        playSound = new PlaySound();

        mainFont = new BitmapFont(Gdx.files.internal("fonts/square-deal.fnt"), false);
        mainFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        mainFont.setColor(Color.WHITE);
        mainFont.getData().setScale(1, 1);
        mainFont.getData().markupEnabled = true;

        mainFontRed = new BitmapFont(Gdx.files.internal("fonts/square-deal.fnt"), false);
        mainFontRed.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        mainFontRed.setColor(Color.RED);
        mainFontRed.getData().setScale(1, 1);
        mainFontRed.getData().markupEnabled = true;

        menu = new Menu(playSound, mainFont);


        game = new Game(playSound);
        game.create(mainFont,mainFontRed);


        menuInputProcessor = new MenuInputProcessor();
        Gdx.input.setInputProcessor(menuInputProcessor);



    }

    private void update() {
        if (newMode != mode) {
            setMode(newMode);
        }
    }

    @Override
    public void render() {
        update();
        switch (mode) {
            case MENU:
                menu.render();
                break;
            case PLAY:
                game.render();
                break;
            default:
                break;
        }
    }

    @Override
    public void dispose() {
        game.dispose();
    }

}
