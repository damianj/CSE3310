package com.dodgydive;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/******************************************************************
 * Wrapper class for our game, it loads the assets and starts the app
 *
 * @author      Damian Jimenez, jimenez.dmn@gmail.com
 *              <br>Maurice Harris, maurice.harris@mavs.uta.edu
 *              <br>Neunzo Vincent, neunzo.thomas@mavs.uta.edu
 *              <br>Craig Lautenslager, craig.lautenslager@mavs.uta.edu
 ******************************************************************/
public class DodgyDiveGame extends Game {
	private final AssetManager assetManager = new AssetManager();

	/******************************************************************
	 * Load the assets and create a new instance of our game.
	 ******************************************************************/
	@Override
	public void create() {
		// Load up the texture atlas along with several fonts using the asset manager.
		assetManager.load("dodgy_dive_assets.atlas", TextureAtlas.class);
		assetManager.load("score_font.fnt", BitmapFont.class);
		assetManager.load("debug_font.fnt", BitmapFont.class);

		// Make sure the asset manager finishes loading all of the resources and then set the screen
		// to the start screen.
		assetManager.finishLoading();
		setScreen(new StartScreen(this));
	}

	/******************************************************************
	 * Getter method for the asset manager so other classes can access
	 * the assets.
	 *
	 * @return    AssetManager containing the games loaded assets
	 *            (i.e., images, sprites, etc.)
	 ******************************************************************/
	public AssetManager getAssetManager() {
		return this.assetManager;
	}
}
