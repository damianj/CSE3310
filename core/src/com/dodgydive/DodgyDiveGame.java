package com.dodgydive;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class DodgyDiveGame extends Game {

    private final AssetManager assetManager = new AssetManager();

    /*
    *
    *   create() - This function is used to start the game and setup which screen to start
    *   the game on. We also pass this game as the owner of the start screen.
    *
    */
	@Override
	public void create () {
        // Load up the texture atlas along with several fonts using the asset manager.
        assetManager.load("dodgy_dive_assets.atlas", TextureAtlas.class);
        assetManager.load("score_font.fnt", BitmapFont.class);
        assetManager.load("debug_font.fnt", BitmapFont.class);

        // Make sure the asset manager finishes loading all of the resources and then set the screen
        // to the start screen.
        assetManager.finishLoading();
		setScreen(new StartScreen(this));
	}

    /*
    *
    *   getAssetManager() - This function is used to allow access to the asset manager to
    *   whichever class may need it.
    *
     */
    public AssetManager getAssetManager() {
        return this.assetManager;
    }
}
