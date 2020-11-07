package com.stk.nns.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.stk.nns.MyGdxGame;

public class DesktopLauncher {
	public static final int VIRTUAL_WIDTH = 1080;
	public static final int VIRTUAL_HEIGHT = 1080;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = VIRTUAL_WIDTH;
		config.height = VIRTUAL_HEIGHT;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		config.vSyncEnabled = false;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
