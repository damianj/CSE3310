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

/**
 * Created by mwhar on 4/24/2016.
 */
public class StartScreen extends ScreenAdapter {

    private static final int WORLD_WIDTH = Gdx.graphics.getWidth();
    private static final int WORLD_HEIGHT = Gdx.graphics.getHeight();

    private final DodgyDiveGame dodgyDiveGame;

    private Stage stage;
    private Table table;

    private TextureRegion backgroundTexture;
    private TextureRegion settingsTexture;
    private TextureRegion settingsPressedTexture;
    private TextureRegion scoreboardTexture;
    private TextureRegion scoreboardPressedTexture;
    private TextureRegion creditsTexture;
    private TextureRegion creditsPressedTexture;

    public StartScreen(DodgyDiveGame dodgyDiveGame) {
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

        // Load the textureAtlas so we can use our images on this screen.
        TextureAtlas textureAtlas = dodgyDiveGame.getAssetManager().get("dodgy_dive_assets.atlas");

        // Load up the backgroundTexture and use it to make a background image for the UI.
        backgroundTexture = textureAtlas.findRegion("title_background");
        Image background = new Image(backgroundTexture);
        background.setWidth(WORLD_WIDTH);
        background.setHeight(WORLD_HEIGHT);

        // Load up the different states for the settings button (unpressed and pressed states) and
        // then set up a new image based button using these textures.
        settingsTexture = textureAtlas.findRegion("settings");
        settingsPressedTexture = textureAtlas.findRegion("settings_pressed");
        ImageButton settingsButton = new ImageButton(new TextureRegionDrawable(settingsTexture),
                new TextureRegionDrawable(settingsPressedTexture));

        // Add a gesture listener to the settings button so we can detect when the user taps the
        // button.
        settingsButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {

                super.tap(event, x, y, count, button);

                // When the button has been tapped change to the GameScreen and dispose of the
                // previous screen
                dodgyDiveGame.setScreen(new SettingsScreen(dodgyDiveGame));
                dispose();
            }
        });


        scoreboardTexture = textureAtlas.findRegion("scoreboard");
        scoreboardPressedTexture = textureAtlas.findRegion("scoreboard_pressed");
        //ImageButton scoreboardButton = new ImageButton(new TextureRegionDrawable(scoreboardTexture),
                //new TextureRegionDrawable(scoreboardPressedTexture));

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

        // Load up the different states for the credits button and set up a new image button for it.
        creditsTexture = textureAtlas.findRegion("credits");
        creditsPressedTexture = textureAtlas.findRegion("credits_pressed");
        ImageButton creditsButton = new ImageButton(new TextureRegionDrawable(creditsTexture),
                new TextureRegionDrawable(creditsPressedTexture));

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
        // Here we add UI elements to the stage container and table container
        stage.addActor(background);
        stage.addActor(table);

        scoreboardButton.padLeft(12); // easy fix to make sure our scoreboard button has the same space
                                      // as the rest since it's 12 pixels smaller (width) than the other buttons

        // Using expand() we make the newly add UI element and table cell fill up as much space
        // as it can. Using pad(16) and align(Align.topRight) we pad the UI element cells by 16px
        // and set it to align itself with the top right of the screen.
        table.add(settingsButton).size(90, 90).expand().pad(16).align(Align.bottomRight);
        table.add(scoreboardButton).size(90, 90).pad(16).align(Align.bottomRight);
        table.add(creditsButton).size(90, 90).pad(16).align(Align.bottomRight);

        // Add a gesture listener to the background image so we can detect when the user taps the
        // background and not the buttons.
        background.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {

                super.tap(event, x, y, count, button);

                // When the button has been tapped change to the GameScreen and dispose of the
                // previous screen
                dodgyDiveGame.setScreen(new GameScreen(dodgyDiveGame));
                dispose();
            }
        });
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
