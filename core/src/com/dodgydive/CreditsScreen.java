package com.dodgydive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/******************************************************************
 * Class that constructs the credits screen for the game. This screen
 * simply displays the names of the developers.
 *
 * @author      Damian Jimenez, jimenez.dmn@gmail.com
 *              <br>Maurice Harris, maurice.harris@mavs.uta.edu
 *              <br>Neunzo Vincent, neunzo.thomas@mavs.uta.edu
 *              <br>Craig Lautenslager, craig.lautenslager@mavs.uta.edu
 ******************************************************************/
public class CreditsScreen extends ScreenAdapter {
	private static final int WORLD_WIDTH = Gdx.graphics.getWidth();
	private static final int WORLD_HEIGHT = Gdx.graphics.getHeight();
	private final DodgyDiveGame dodgyDiveGame;
	private Stage stage;

	/******************************************************************
	 * Constructor method for the class. Set's up a DodgyDiveGame instance
	 ******************************************************************/
	public CreditsScreen(DodgyDiveGame dodgyDiveGame) {
		this.dodgyDiveGame = dodgyDiveGame;
	}

	/******************************************************************
	 * Draws all of the relevant assets on the screen. A table is used
	 * to make sure the alignment of the assets is consistent.
	 ******************************************************************/
	@Override
	public void show() {
		super.show();

		stage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
		Gdx.input.setInputProcessor(stage);

		Table table = new Table();
		table.setFillParent(true);

		TextureAtlas textureAtlas = dodgyDiveGame.getAssetManager().get("dodgy_dive_assets.atlas");

		TextureRegion backgroundTexture = textureAtlas.findRegion("credits_screen");
		Image background = new Image(backgroundTexture);
		background.setWidth(WORLD_WIDTH);
		background.setHeight(WORLD_HEIGHT);

		TextureRegion settingsTexture = textureAtlas.findRegion("settings");
		TextureRegion settingsPressedTexture = textureAtlas.findRegion("settings_pressed");
		ImageButton settingsButton = new ImageButton(new TextureRegionDrawable(settingsTexture),
				new TextureRegionDrawable(settingsPressedTexture));

		settingsButton.addListener(new ActorGestureListener() {
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {
				super.tap(event, x, y, count, button);
				dodgyDiveGame.setScreen(new SettingsScreen(dodgyDiveGame));
				dispose();
			}
		});


		TextureRegion homeTexture = textureAtlas.findRegion("home");
		TextureRegion homePressedTexture = textureAtlas.findRegion("home_pressed");
		ImageButton homeButton = new ImageButton(new TextureRegionDrawable(homeTexture),
				new TextureRegionDrawable(homePressedTexture));

		homeButton.addListener(new ActorGestureListener() {
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {
				super.tap(event, x, y, count, button);
				dodgyDiveGame.setScreen(new StartScreen(dodgyDiveGame));
				dispose();
			}
		});

		TextureRegion scoreboardTexture = textureAtlas.findRegion("scoreboard");
		TextureRegion scoreboardPressedTexture = textureAtlas.findRegion("scoreboard_pressed");
		ImageButton scoreboardButton = new ImageButton(new TextureRegionDrawable(scoreboardTexture),
				new TextureRegionDrawable(scoreboardPressedTexture));

		scoreboardButton.addListener(new ActorGestureListener() {
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {
				super.tap(event, x, y, count, button);
				dodgyDiveGame.setScreen(new ScoreboardScreen(dodgyDiveGame));
				dispose();
			}
		});

		stage.addActor(background);
		stage.addActor(table);

		scoreboardButton.padLeft(12);
		table.add(settingsButton).size(75, 75).expand().pad(16).align(Align.topRight);
		table.add(scoreboardButton).size(75, 75).pad(16).align(Align.topRight);
		table.add(homeButton).size(75, 75).pad(16).align(Align.topRight);
	}

	/******************************************************************
	 * Keeps track of whenever the screen is resized and makes the
	 * viewport (viewable screen area) resize to the given width and height.
	 ******************************************************************/
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.getViewport().update(width, height, true);
	}

	/******************************************************************
	 * Clears the screen and then draws what needs to be rendered on
	 * the screen.
	 ******************************************************************/
	@Override
	public void render(float delta) {
		super.render(delta);
		clearScreen();
		stage.act(delta);
		stage.draw();
	}

	/******************************************************************
	 * Cleans up all of the disposable resources when the screen is no
	 * longer in use.
	 ******************************************************************/
	@Override
	public void dispose() {
		super.dispose();
		stage.dispose();
	}

	/******************************************************************
	 * Clears the screen to black, to prepare it to draw the next frame.
	 ******************************************************************/
	private void clearScreen() {
		Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}

