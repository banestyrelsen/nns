package com.stk.nns.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.map.Map;
import com.stk.nns.map.Tile;

public class BasicScreen implements com.badlogic.gdx.Screen {

    Map map;
    SpriteBatch batch;
    Texture tileWhite;
    OrthographicCamera camera;

    public BasicScreen(Map map, OrthographicCamera camera, SpriteBatch batch, Texture tileWhite) {
        this.map = map;
        this.camera = camera;
        this.batch = batch;
        this.tileWhite = tileWhite;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Tile[][] tile = map.getTile();
        Vector2 size = map.getSize();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        for (int x = 0; x < size.x; x++) {

            for (int y = 0; y < size.y; y++) {
                if (tile[x][y].getValue() == 1) {
                    batch.draw(tileWhite, x * 32, y * 32);
                }
            }
        }


        batch.end();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
