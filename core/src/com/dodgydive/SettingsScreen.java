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
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/******************************************************************
 * Class that constructs the settings screen where the player can
 * adjust various aspects of the game. They can set the background,
 * character costume, music volume, and game difficulty.
 *
 * @author      Damian Jimenez, jimenez.dmn@gmail.com
 *              <br>Maurice Harris, maurice.harris@mavs.uta.edu
 *              <br>Neunzo Vincent, neunzo.thomas@mavs.uta.edu
 *              <br>Craig Lautenslager, craig.lautenslager@mavs.uta.edu
 ******************************************************************/
public class SettingsScreen extends ScreenAdapter {
	private static final int WORLD_WIDTH = Gdx.graphics.getWidth();
	private static final int WORLD_HEIGHT = Gdx.graphics.getHeight();
	private static final float SCALING = 1440f / Gdx.graphics.getHeight();
	private static final Preferences PREFS = Gdx.app.getPreferences("Game_Settings");
	private final DodgyDiveGame dodgyDiveGame;
	private Stage stage;

	/******************************************************************
	 * Constructor method for the class. Set's up a DodgyDiveGame instance
	 ******************************************************************/
	public SettingsScreen(DodgyDiveGame dodgyDiveGame) {
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
		Table settingsTable = new Table();
		settingsTable.setFillParent(true);

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("score_font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 58;
		parameter.kerning = true;
		parameter.borderStraight = true;
		parameter.borderWidth = 1;
		BitmapFont customFont = generator.generateFont(parameter);
		customFont.setColor(0, 0, 0, 0.70f);
		generator.dispose();

		TextureAtlas textureAtlas = dodgyDiveGame.getAssetManager().get("dodgy_dive_assets.atlas");

		TextureRegion backgroundTexture = textureAtlas.findRegion("settings_background");
		Image background = new Image(backgroundTexture);
		background.setWidth(WORLD_WIDTH);
		background.setHeight(WORLD_HEIGHT);

		TextureRegionDrawable sliderBackground = new TextureRegionDrawable(textureAtlas.findRegion("slider"));
		TextureRegionDrawable sliderKnob = new TextureRegionDrawable(textureAtlas.findRegion("slider_knob"));
		TextureRegionDrawable checkbox = new TextureRegionDrawable(textureAtlas.findRegion("checkbox"));
		TextureRegionDrawable checkboxChecked = new TextureRegionDrawable(textureAtlas.findRegion("checkbox_checked"));

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
		stage.addActor(settingsTable);

		table.left();
		settingsTable.center().top();

		scoreboardButton.padLeft(12);
		table.add(scoreboardButton).size(75, 75).expand().pad(16).align(Align.topRight);
		table.add(homeButton).size(75, 75).pad(16).align(Align.topRight);
		table.add(creditsButton).size(75, 75).pad(16).align(Align.topRight);

		settingsTable.padLeft(0.475f * WORLD_WIDTH);
		settingsTable.padTop((0.270f / (SCALING > 1 ? 1.1f : SCALING)) * WORLD_HEIGHT); // Hack to make it look okay on both the Nexus 5X and 6P...

		final CheckBox backgroundCheckbox = new CheckBox(null, new CheckBox.CheckBoxStyle(checkbox, checkboxChecked, customFont, null));
		settingsTable.add(backgroundCheckbox).pad(0).padBottom((0.0455f / SCALING) * WORLD_HEIGHT).row();
		backgroundCheckbox.setChecked(PREFS.contains("gameBackground") && PREFS.getString("gameBackground").equalsIgnoreCase("background_radioactive"));
		backgroundCheckbox.addListener(
				new ActorGestureListener() {
					@Override
					public void tap(InputEvent event, float x, float y, int count, int button) {
						super.tap(event, x, y, count, button);
						if(backgroundCheckbox.isChecked()) {
							updateGameBackground("background_radioactive");
						} else {
							updateGameBackground("background");
						}
					}
				}
		);

		final CheckBox costumeCheckbox = new CheckBox(null, new CheckBox.CheckBoxStyle(checkbox, checkboxChecked, customFont, null));
		settingsTable.add(costumeCheckbox).size(147, 147).pad((0.0455f / SCALING) * WORLD_HEIGHT).padBottom((0.043f / SCALING) * WORLD_HEIGHT).row();
		costumeCheckbox.setChecked(PREFS.contains("diverCostume") && PREFS.getString("diverCostume").equalsIgnoreCase("diver_alt"));
		costumeCheckbox.addListener(
				new ActorGestureListener() {
					@Override
					public void tap(InputEvent event, float x, float y, int count, int button) {
						super.tap(event, x, y, count, button);
						if(costumeCheckbox.isChecked()) {
							updateDiverCostume("diver_alt");
						} else {
							updateDiverCostume("diver");
						}
					}
				}
		);

		final Slider musicSlider = new Slider(0, 1, 0.1f, false, new Slider.SliderStyle(sliderBackground, sliderKnob));
		musicSlider.setValue(PREFS.contains("musicVolume") ? PREFS.getFloat("musicVolume") : 0.5f);
		settingsTable.add(musicSlider).width((WORLD_WIDTH / 2) - 100).pad((0.0455f / SCALING) * WORLD_HEIGHT).padBottom((0.05f / SCALING) * WORLD_HEIGHT).row();
		musicSlider.addListener(
				new DragListener() {
					@Override
					public void dragStop(InputEvent event, float x, float y, int pointer) {
						super.dragStop(event, x, y, pointer);
						updateMusicVolume(musicSlider.getValue());
					}
				}
		);
		musicSlider.addListener(
				new ActorGestureListener() {
					@Override
					public void tap(InputEvent event, float x, float y, int count, int button) {
						super.tap(event, x, y, count, button);
						updateMusicVolume(musicSlider.getValue());
					}
				}
		);

		final Slider diffSlider = new Slider(100, 250, 5f, false, new Slider.SliderStyle(sliderBackground, sliderKnob));
		diffSlider.setValue(PREFS.contains("difficulty") ? PREFS.getFloat("difficulty") : 175f);
		settingsTable.add(diffSlider).width((WORLD_WIDTH / 2) - 100).pad((0.0455f / SCALING) * WORLD_HEIGHT).row();
		diffSlider.addListener(
				new DragListener() {
					@Override
					public void dragStop(InputEvent event, float x, float y, int pointer) {
						super.dragStop(event, x, y, pointer);
						updateDifficulty(diffSlider.getValue());
					}
				}
		);
		diffSlider.addListener(
				new ActorGestureListener() {
					@Override
					public void tap(InputEvent event, float x, float y, int count, int button) {
						super.tap(event, x, y, count, button);
						updateDifficulty(diffSlider.getValue());
					}
				}
		);
	}

	/******************************************************************
	 * Switches between the normal character costume and an alternate
	 * character costume that is purple in color.
	 ******************************************************************/
	public void updateDiverCostume(String costumeName) {
		PREFS.putString("diverCostume", costumeName);
		PREFS.flush();
	}

	/******************************************************************
	 * Switches between the normal background and an alternate background
	 * that features a radioactive setting.
	 ******************************************************************/
	public void updateGameBackground(String backgroundName) {
		PREFS.putString("gameBackground", backgroundName);
		PREFS.flush();
	}

	/******************************************************************
	 * Updates the volume of the game music whenever the volume slider
	 * is dragged or tapped on.
	 ******************************************************************/
	public void updateMusicVolume(float volume) {
		PREFS.putFloat("musicVolume", volume);
		PREFS.flush();
	}

	/******************************************************************
	 * Updates the difficulty of the game whenever the difficulty slider
	 * is dragged or tapped on.
	 ******************************************************************/
	public void updateDifficulty(float difficulty) {
		PREFS.putFloat("difficulty", difficulty);
		PREFS.flush();
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

