package com.dodgydive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.Locale;

/******************************************************************
 * Class that constructs the score-board screen where the player
 * can see the top 5 scores.
 *
 * @author      Damian Jimenez, jimenez.dmn@gmail.com
 *              <br>Maurice Harris, maurice.harris@mavs.uta.edu
 *              <br>Neunzo Vincent, neunzo.thomas@mavs.uta.edu
 *              <br>Craig Lautenslager, craig.lautenslager@mavs.uta.edu
 ******************************************************************/
public class ScoreboardScreen extends ScreenAdapter {
	private static final int WORLD_WIDTH = Gdx.graphics.getWidth();
	private static final int WORLD_HEIGHT = Gdx.graphics.getHeight();
	private static final float SCALING = 1440f / Gdx.graphics.getHeight();
	private static final Preferences PREFS = Gdx.app.getPreferences("Game_Settings");
	private final DodgyDiveGame dodgyDiveGame;
	private Stage stage;

	/******************************************************************
	 * Constructor method for the class. Set's up a DodgyDiveGame instance
	 ******************************************************************/
	public ScoreboardScreen(DodgyDiveGame dodgyDiveGame) {
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

		loadScores();

		Table table = new Table();
		table.setFillParent(true);
		Table scoresTable = new Table();
		scoresTable.setFillParent(true);

		TextureAtlas textureAtlas = dodgyDiveGame.getAssetManager().get("dodgy_dive_assets.atlas");

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("score_font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 80;
		parameter.kerning = true;
		parameter.borderStraight = true;
		parameter.borderWidth = 1;
		BitmapFont scoreFont = generator.generateFont(parameter);
		scoreFont.setColor(0, 0, 0, 0.70f);
		generator.dispose();

		TextureRegion backgroundTexture = textureAtlas.findRegion("scores_background");
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

		TextureRegion creditsTexture = textureAtlas.findRegion("credits");
		TextureRegion creditsPressedTexture = textureAtlas.findRegion("credits_pressed");
		ImageButton creditsButton = new ImageButton(new TextureRegionDrawable(creditsTexture),
				new TextureRegionDrawable(creditsPressedTexture));

		creditsButton.addListener(new ActorGestureListener() {
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {
				super.tap(event, x, y, count, button);
				dodgyDiveGame.setScreen(new CreditsScreen(dodgyDiveGame));
				dispose();
			}
		});

		stage.addActor(background);
		stage.addActor(table);
		stage.addActor(scoresTable);

		table.add(settingsButton).size(75, 75).expand().pad(16).align(Align.topRight);
		table.add(homeButton).size(75, 75).pad(16).align(Align.topRight);
		table.add(creditsButton).size(75, 75).pad(16).align(Align.topRight);

		scoresTable.center().top().padTop((0.273f / (SCALING > 1 ? 1.05f : SCALING)) * WORLD_HEIGHT);

		Array<TextButton> scoreButtons = new Array<TextButton>();
		for(int i = 0; i < 5; i++) {
			scoreButtons.add(new TextButton(String.format(Locale.US, "%010d", PREFS.getInteger("hS" + (i + 1))), new TextButton.TextButtonStyle(null, null, null, scoreFont)));
			scoresTable.add(scoreButtons.get(i)).pad(i > 0 ? (.046f / (SCALING > 1 ? 1.29f : SCALING)) * WORLD_HEIGHT : 0).padBottom((.046f / (SCALING > 1 ? 1.29f : SCALING)) * WORLD_HEIGHT).row();
		}
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

	/******************************************************************
	 * Makes sure scores are actually stored in the preferences file.
	 * If there are no scores stored, then create a preferences file
	 * and store 0's for the top 5 scores.
	 ******************************************************************/
	public void loadScores() {
		if(!PREFS.contains("hiScoresSet") && !PREFS.getBoolean("hiScoresSet")) {
			PREFS.putBoolean("hiScoresSet", true);
			PREFS.putInteger("hS1", 0);
			PREFS.putInteger("hS2", 0);
			PREFS.putInteger("hS3", 0);
			PREFS.putInteger("hS4", 0);
			PREFS.putInteger("hS5", 0);
			PREFS.flush();
		}
	}
}
