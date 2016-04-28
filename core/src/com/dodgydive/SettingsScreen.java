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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import com.badlogic.gdx.utils.viewport.StretchViewport;


/**
 * Created by mwhar on 4/24/2016.
 */
public class SettingsScreen extends ScreenAdapter {

	private static final int WORLD_WIDTH = Gdx.graphics.getWidth();
	private static final int WORLD_HEIGHT = Gdx.graphics.getHeight();
	private static final float SCALING = 1440f/Gdx.graphics.getHeight();

	// Were going to use the built-in preferences class from LibGDX and store our preferences there
	// methods to call are updateDiverCostume(), updateGameBackground(), musicVolume(), updateDifficulty()
	private static final Preferences PREFS = Gdx.app.getPreferences("Game_Settings");

	private final DodgyDiveGame dodgyDiveGame;

	private Stage stage;
	private Table table, settingsTable;

	private TextureRegion backgroundTexture;
	private TextureRegion creditsTexture;
	private TextureRegion creditsPressedTexture;
	private TextureRegion homeTexture;
	private TextureRegion homePressedTexture;
	private TextureRegion scoreboardTexture;
	private TextureRegion scoreboardPressedTexture;
	private TextureRegionDrawable sliderBackground;
	private TextureRegionDrawable sliderKnob;
	private TextureRegionDrawable checkbox;
	private TextureRegionDrawable checkboxChecked;
	private BitmapFont customFont;


	public SettingsScreen(DodgyDiveGame dodgyDiveGame) {
		this.dodgyDiveGame = dodgyDiveGame;
	}

	@Override
	public void show() {

		super.show();

		// Create a new stage with a viewport that stretches to fill the screen.
		// A stage is used to hold our UI elements and update/render them.
		stage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
		Gdx.input.setInputProcessor(stage);

		// Create a new table that will be set to fill up it's parent container(the stage).
		// A table is just like an html/excel table that holds UI elements like buttons.
		table = new Table();
		table.setFillParent(true);
		settingsTable = new Table();
		settingsTable.setFillParent(true);

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("score_font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 58;
		parameter.kerning = true;
		parameter.borderStraight = true;
		parameter.borderWidth = 1;
		customFont = generator.generateFont(parameter);
		customFont.setColor(0, 0, 0, 0.70f); // Make our font slightly transparent so it doesn't obscure sharks during game-play
		generator.dispose(); // don't forget to dispose to avoid memory leaks!

		// Load the textureAtlas so we can use our images on this screen.
		TextureAtlas textureAtlas = dodgyDiveGame.getAssetManager().get("dodgy_dive_assets.atlas");


		// Load up the backgroundTexture and use it to make a background image for the UI.
		backgroundTexture = textureAtlas.findRegion("settings_background");
		Image background = new Image(backgroundTexture);
		background.setWidth(WORLD_WIDTH);
		background.setHeight(WORLD_HEIGHT);

		sliderBackground = new TextureRegionDrawable(textureAtlas.findRegion("slider"));
		sliderKnob = new TextureRegionDrawable(textureAtlas.findRegion("slider_knob"));
		checkbox = new TextureRegionDrawable(textureAtlas.findRegion("checkbox"));
		checkboxChecked = new TextureRegionDrawable(textureAtlas.findRegion("checkbox_checked"));
		// Load up the different states for the settings button (unpressed and pressed states) and
		// then set up a new image based button using these textures.
		creditsTexture = textureAtlas.findRegion("credits");
		creditsPressedTexture = textureAtlas.findRegion("credits_pressed");
		ImageButton creditsButton = new ImageButton(new TextureRegionDrawable(creditsTexture),
				new TextureRegionDrawable(creditsPressedTexture));

		// Add a gesture listener to the settings button so we can detect when the user taps the
		// button.
		creditsButton.addListener(new ActorGestureListener() {
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {

				super.tap(event, x, y, count, button);

				// When the button has been tapped change to the GameScreen and dispose of the
				// previous screen
				dodgyDiveGame.setScreen(new CreditsScreen(dodgyDiveGame));
				dispose();
			}
		});


		homeTexture = textureAtlas.findRegion("home");
		homePressedTexture = textureAtlas.findRegion("home_pressed");
		ImageButton homeButton = new ImageButton(new TextureRegionDrawable(homeTexture),
				new TextureRegionDrawable(homePressedTexture));

		homeButton.addListener(new ActorGestureListener() {
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {

				super.tap(event, x, y, count, button);

				// When the button has been tapped change to the GameScreen and dispose of the
				// previous screen
				dodgyDiveGame.setScreen(new StartScreen(dodgyDiveGame));
				dispose();
			}
		});

		scoreboardTexture = textureAtlas.findRegion("scoreboard");
		scoreboardPressedTexture = textureAtlas.findRegion("scoreboard_pressed");
		ImageButton scoreboardButton = new ImageButton(new TextureRegionDrawable(scoreboardTexture),
				new TextureRegionDrawable(scoreboardPressedTexture));

		scoreboardButton.addListener(new ActorGestureListener() {
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {

				super.tap(event, x, y, count, button);

				// When the button has been tapped change to the GameScreen and dispose of the
				// previous screen
				dodgyDiveGame.setScreen(new ScoreboardScreen(dodgyDiveGame));
				dispose();
			}
		});

		// Here we add UI elements to the stage container and table container
		stage.addActor(background);
		stage.addActor(table);
		stage.addActor(settingsTable);

		table.left();
		settingsTable.center().top();
		// Using expand() we make the newly add UI element and table cell fill up as much space
		// as it can. Using pad(16) and align(Align.topRight) we pad the UI element cells by 16px
		// and set it to align itself with the top right of the screen.
		scoreboardButton.padLeft(12);
		table.add(scoreboardButton).size(75, 75).expand().pad(16).align(Align.topRight);
		table.add(homeButton).size(75, 75).pad(16).align(Align.topRight);
		table.add(creditsButton).size(75, 75).pad(16).align(Align.topRight);

		settingsTable.padLeft(0.475f * WORLD_WIDTH);
		settingsTable.padTop((0.270f/(SCALING > 1 ? 1.1f : SCALING)) * WORLD_HEIGHT); // Hack to make it look okay on both the Nexus 5X and 6P...

		final CheckBox backgroundCheckbox = new CheckBox(null, new CheckBox.CheckBoxStyle(checkbox, checkboxChecked, customFont, null));
		settingsTable.add(backgroundCheckbox).pad(0).padBottom((0.0455f/SCALING) * WORLD_HEIGHT).row();
		backgroundCheckbox.setChecked(PREFS.contains("gameBackground") ? PREFS.getString("gameBackground").equalsIgnoreCase("background_radioactive") : false);
		backgroundCheckbox.addListener(
			new ActorGestureListener() {
				@Override
				public void tap(InputEvent event, float x, float y, int count, int button) {
					super.tap(event, x, y, count, button);
					if(backgroundCheckbox.isChecked()) {
						updateGameBackground("background_radioactive");
					}
					else{
						updateGameBackground("background");
					}
				}
			}
		);

		final CheckBox costumeCheckbox = new CheckBox(null, new CheckBox.CheckBoxStyle(checkbox, checkboxChecked, customFont, null));
		settingsTable.add(costumeCheckbox).size(147, 147).pad((0.0455f/SCALING) * WORLD_HEIGHT).padBottom((0.043f/SCALING) * WORLD_HEIGHT).row();
		costumeCheckbox.setChecked(PREFS.contains("diverCostume") ? PREFS.getString("diverCostume").equalsIgnoreCase("diver_alt") : false);
		costumeCheckbox.addListener(
			new ActorGestureListener() {
				@Override
				public void tap(InputEvent event, float x, float y, int count, int button) {
					super.tap(event, x, y, count, button);
					if(costumeCheckbox.isChecked()) {
						updateDiverCostume("diver_alt");
					}
					else{
						updateDiverCostume("diver");
					}
				}
			}
		);

		final Slider musicSlider = new Slider(0, 1, 0.1f, false, new Slider.SliderStyle(sliderBackground, sliderKnob));
		musicSlider.setValue(PREFS.contains("musicVolume") ? PREFS.getFloat("musicVolume") : 0.5f);
		settingsTable.add(musicSlider).width((WORLD_WIDTH/2) - 100).pad((0.0455f/SCALING) * WORLD_HEIGHT).padBottom((0.05f/SCALING) * WORLD_HEIGHT).row();
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
		settingsTable.add(diffSlider).width((WORLD_WIDTH/2) - 100).pad((0.0455f/SCALING) * WORLD_HEIGHT).row();
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

	public void updateDiverCostume(String costumeName) {
		PREFS.putString("diverCostume", costumeName);
		PREFS.flush();
	}

	public void updateGameBackground(String backgroundName) {
		PREFS.putString("gameBackground", backgroundName);
		PREFS.flush();
	}

	public void updateMusicVolume(float volume) {
		PREFS.putFloat("musicVolume", volume);
		PREFS.flush();
	}

	public void updateDifficulty(float difficulty) {
		PREFS.putFloat("difficulty", difficulty);
		PREFS.flush();
	}
	/*
	*
	*   resize() - This function is used whenever the screen is resized and makes the viewport
	*   (basically the screen viewable area) resize to the given width and height. This is an
	*   inherited method so super.resize() is used to ensure it works properly just in case.
	*
	*/
	@Override
	public void resize (int width, int height) {

		super.resize(width, height);
		stage.getViewport().update(width, height, true);
	}

	/*
	*
	*   render() - This function is used to first clear the screen and then draw whatever
	*   we would like to render to the screen.
	*
	*/
	@Override
	public void render (float delta) {

		super.render(delta);
		clearScreen();
		stage.act(delta);
		stage.draw();
	}

	/*
	*
	*   dispose() - This function is used to clean up all of the disposable resources when the
	*   screen is no longer in use.
	*
	*/
	@Override
	public void dispose() {

		super.dispose();
		stage.dispose();
	}

	/*
	*
	*   clearScreen() - This function clears the screen to black.
	*
	*/
	private void clearScreen() {

		Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}

