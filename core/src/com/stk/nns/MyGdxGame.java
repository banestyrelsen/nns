package com.stk.nns;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.map.Map;
import com.stk.nns.screen.BasicScreen;

import java.util.ArrayList;
import java.util.List;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture tileWhite;
	Map map;
	BasicScreen basicScreen;
	private OrthographicCamera camera;

	public static int WIDTH;
	public static int HEIGHT;



	@Override
	public void create () {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		System.out.println("---------------->>>>>>>>>>>>>>>>> " + Gdx.graphics.getWidth() + ", " + Gdx.graphics.getHeight());
		camera  = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.zoom = -2f;
		camera.update();

		batch = new SpriteBatch();
/*		img = new Texture("badlogic.jpg");*/
		tileWhite = new Texture("tile_white.png");


		map = new Map("maps/map1.map");

		basicScreen = new BasicScreen(map, camera, batch, tileWhite);
	}

	@Override
	public void render () {

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(tileWhite, 200, 200);
		batch.end();

		basicScreen.render(1f);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		tileWhite.dispose();
	}
}
