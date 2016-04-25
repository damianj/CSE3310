package com.dodgydive.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.dodgydive.DodgyDiveGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		// Set window width and height
		config.width = 1920;
		config.height = 1080;

		// Tell the texture packer tool to create a texture atlas of all of our image files.
		// A texture atlas is just one big image file with various images in it used to save
		// space and processing time.
		TexturePacker.process("../assets", "../assets", "dodgy_dive_assets");
		new LwjglApplication(new DodgyDiveGame(), config);
	}
}