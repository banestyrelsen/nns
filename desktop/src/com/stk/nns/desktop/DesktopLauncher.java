package com.stk.nns.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.stk.nns.Main;

public class DesktopLauncher {
	public static final int VIRTUAL_WIDTH = 1920;
	public static final int VIRTUAL_HEIGHT = 1080;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = VIRTUAL_WIDTH;
		config.height = VIRTUAL_HEIGHT;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		config.vSyncEnabled = false;

		// Window position
		config.x = -2000;
		config.y = 200;
		new LwjglApplication(new Main(), config);
	}
}
