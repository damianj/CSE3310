package com.dodgydive.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dodgydive.DodgyDiveGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		// Set window width and height
		config.width = 1920;
		config.height = 1080;
		new LwjglApplication(new DodgyDiveGame(), config);
	}
}