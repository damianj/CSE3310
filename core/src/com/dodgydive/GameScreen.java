package com.dodgydive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;


/**
 * Created by mwhar on 4/20/2016.
 */
public class GameScreen extends ScreenAdapter {

    private static final int WORLD_WIDTH = Gdx.graphics.getWidth();
    private static final int WORLD_HEIGHT = Gdx.graphics.getHeight();
    private static final Preferences PREFS = Gdx.app.getPreferences("Game_Settings");

    private final DodgyDiveGame dodgyDiveGame;

    private boolean debugMode;

    private Music gameMusic;
    private Music crunchSound = Gdx.audio.newMusic(Gdx.files.internal("crunch_sound.mp3"));
    private float musicVolume = PREFS.contains("musicVolume") ? PREFS.getFloat("musicVolume") : 0.5f;

    private BitmapFont scoreFont;
    private BitmapFont debugFont;
    private GlyphLayout glyphLayout;
    private int scoreFontSize = 58;

    private Viewport viewport;
    private Camera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private TextureRegion background;
    private String background_name = PREFS.contains("gameBackground") ? PREFS.getString("gameBackground") : "background"; // background is default, background_radioactive is the other one

    private TextureRegion diverTexture;
    private Diver diver;
    private String diver_costume = PREFS.contains("diverCostume") ? PREFS.getString("diverCostume") : "diver"; // diver is default, diver_alt is the other "costume"

    private TextureRegion sharkTexture;
    private Array<Shark> sharks = new Array<Shark>();

    private Timer scoreTimer = new Timer();
    private int score = 0;

    private int sharksOnScreen = 10;
    private int spaceBetweenSharks = WORLD_WIDTH/(sharksOnScreen);
    private boolean killedByShark = false;

    public GameScreen(DodgyDiveGame dodgyDiveGame) {

        // Set the game instance that this screen belongs to so we can use its asset manager
        this.dodgyDiveGame = dodgyDiveGame;

        // Set debugMode to true so we can see collision lines, fps, etc.
        // debugMode = true;
    }

    /*
    *
    *   resize() - This function is used whenever the screen is resized and makes the viewport
    *   (basically the screen viewable area) resize to the given width and height. This is an
    *   inherited method so super.resize() is used to ensure it works properly just in case.
    *
    */
    @Override
    public void resize(int width, int height) {

        super.resize(width, height);

        // Update screen's viewport to match new width and height
        viewport.update(width, height);
    }

    /*
    *
    *   show() - This function is called whenever this screen becomes the main focus of the game.
    *   In this function we instantiate our variables.
    *
    */
    @Override
    public void show() {

        super.show();

        loadScores();

        // Set up the camera to be orthographic and put it at the center of the screen.
        // The camera allows us to view a portion of the game world.
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();

        // Make it so the screen's viewport stretches to fit the screen
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // Create a new ShapeRenderer so we can draw the collision lines if we use debug mode
        shapeRenderer = new ShapeRenderer();

        // Create a new SpriteBatch for drawing images in batches of draw calls
        batch = new SpriteBatch();

        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("game_music.mp3"));
        // Generate the score font on the fly from a .ttf file
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("score_font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = scoreFontSize;
        parameter.kerning = true;
        parameter.borderStraight = true;
        parameter.borderWidth = 1;
        scoreFont = generator.generateFont(parameter);
        scoreFont.setColor(0, 0, 0, 0.70f); // Make our font slightly transparent so it doesn't obscure sharks during game-play
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        // Get the debug font for debug info from the atlas
        debugFont = dodgyDiveGame.getAssetManager().get("debug_font.fnt");
        glyphLayout = new GlyphLayout();

        // Use asset manager to get the texture atlas (one big indexed image of all the game's assets)
        // and find the region of the atlas for the game's background
        TextureAtlas textureAtlas = dodgyDiveGame.getAssetManager().get("dodgy_dive_assets.atlas");
        background = textureAtlas.findRegion(background_name);

        // Again use the asset manager to find the texture for the diver character, create the diver
        // instance and then set it's position to the center of 1/4th of the screen width
        diverTexture = textureAtlas.findRegion(diver_costume);
        diver = new Diver(diverTexture);
        diver.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);

        // Load up the texture for the shark
        sharkTexture = textureAtlas.findRegion("shark");

        // Set and start the scoreTimer to increment the score by 10 every second
        scoreTimer.scheduleTask(new Timer.Task() {

            @Override
            public void run()
            {
                score += 10;
            }

        }, 1f, 1f);
        scoreTimer.start();

        crunchSound.setVolume(musicVolume);
        crunchSound.setPosition(0.4f);
        gameMusic.setVolume(musicVolume); // sets the volume to half the maximum volume
        gameMusic.play();
        gameMusic.setLooping(true);
    }

    /*
    *
    *   render() - This function is used to first clear the screen and then draw whatever
    *   we would like to render to the screen.
    *
    */
    @Override
    public void render(float delta) {

        super.render(delta);

        // Clear the screen of the previous frame and then draw the new frame
        clearScreen();
        draw();

        // If debugMode is true then display debug information
        if(debugMode)
            drawDebug();

        // Update the information for all objects on the screen
        update(delta);
    }

    /*
    *
    *   dispose() - This function is used to clean up all of the disposable resources when the
    *   screen is no longer in use.
    *
    */
    public void dispose() {

        super.dispose();

        scoreFont.dispose();
        debugFont.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }

    /*
    *
    *   end() - In this function we stop the scoreTimer and clear it of all scheduled tasks (adding
    *   to the score) and then set the game's screen back to the startScreen.
    *
    */
    private void endGame() {
        gameMusic.stop();

        if(killedByShark) {
            crunchSound.play();
        }

        scoreTimer.stop();
        updateScores(score);
        scoreTimer.clear();
        dodgyDiveGame.setScreen(new StartScreen(dodgyDiveGame));
        dispose();
    }

    /*
    *
    *   draw() - This function is used to describe what we would like to draw to the screen.
    *
    */
    private void draw() {
        // Setup the batch's projection and transform matrices to match the camera's
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);

        // Begin drawing
        batch.begin();

        batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        diver.draw(batch);

        // Loop through the sharks arrayList and draw each shark
        for(Shark shark: sharks)
        {
            shark.draw(batch);
        }

        // Draw the score string onto the screen
        drawScore();
        batch.end();
    }

    /*
    *
    *   drawDebug() - This function is used to describe what debug information we would like to draw
    *   to the screen.
    *
    */
    private void drawDebug() {

        // Setup the batch's projection and transform matrices to match the camera's
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);

        // Setup the shapeRenderer's projection and transform matrices to match the camera's
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);

        // Setup the string and glyphLayout for debug information
        String debugString = String.format(Locale.US, "FPS: %d\nPOS X: %d\nPOS Y: %d",
                Gdx.graphics.getFramesPerSecond(), (int)diver.getX(), (int)diver.getY());
        glyphLayout.setText(debugFont, debugString);

        // Begin drawing debug information
        batch.begin();
        debugFont.draw(batch, debugString, 12, WORLD_HEIGHT - 12);
        batch.end();

        // Begin collision shape drawing for diver and sharks
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        diver.drawDebug(shapeRenderer);

        for(Shark shark: sharks)
        {
            shark.drawDebug(shapeRenderer);
        }

        shapeRenderer.end();
    }

    /*
    *
    *   draw() - This function draws a string representing the user's score in the game.
    *
    */
    private void drawScore() {

        // Format the scoreString to have the most recent score
        String scoreString = String.format(Locale.US, "Hi-Score: %d | Score: %07d", PREFS.getInteger("hS1"), score);
        glyphLayout.setText(scoreFont, scoreString);

        // Begin drawing the score
        scoreFont.draw(batch, scoreString, (WORLD_WIDTH/2.0f) - (scoreString.length()*scoreFontSize/3.6f), 50.0f);
    }

    /*
    *
    *   update() - This function is used to update the position and state of all objects in the
    *   game.
    *
    */
    private void update(float delta) {

        // Move the diver and the sharks to their new locations
        diver.update(delta);
        updateSharks(delta);

        // If the space key is pressed or the screen is tapped then make the diver swim up
        if(Gdx.input.isTouched())
        {
            diver.swimUp();
        }

        // Check each shark to see if it has collided with the user if it has end the game (take the
        // user back to the start screen).
        for(Shark shark: sharks)
        {
            if(shark.hasCollidedWithDiver(diver))
            {
                killedByShark = true;
                endGame();
            }
        }

        // If the user goes outside the bounds of the game world then end the game
        if(diver.getX() < 0 || diver.getY() < 0 ||
                (diver.getX() + diver.getWidth()) > WORLD_WIDTH
                || (diver.getY() + diver.getHeight()) > WORLD_HEIGHT)
        {
            endGame();
        }
    }

    /*
    *
    *   clearScreen() - This function clears the screen to black.
    *
    */
    private void clearScreen() {

        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g,
                Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /*
    *
    *   createShark() - This function creates a new shark at the right edge of the screen at a
    *   random height.
    *
    */
    private void createShark() {

        // Instantiate new shark
        Shark newShark = new Shark(sharkTexture);

        // Calculate and set the new shark's position then add it to the list of sharks
        float x = WORLD_WIDTH;
        float y = MathUtils.random(WORLD_HEIGHT - newShark.getHeight());
        newShark.setPosition(x, y);
        sharks.add(newShark);
    }

    /*
    *
    *   updateSharks() - This function updates and keeps track of all the sharks on the screen,
    *   making sure that only the specified number of sharksOnScreen are in the sharks list and on
    *   the screen. The more sharks the less space between each shark.
    *
    */
    private void updateSharks(float delta) {

        // Update the positions of every shark on the screen
        for (Shark shark : sharks) {
            shark.update(delta, new Random().nextFloat() * (new Random().nextInt(10) - 5));
        }

        // If there are sharks on the screen
        if (sharks.size > 0) {
            // Check if the oldest shark on the screen has gone past the left edge of the screen,
            // if so then remove that shark from the list of sharks.
            Shark oldestShark = sharks.first();
            if (oldestShark.getX() < -(oldestShark.getWidth())) {
                sharks.removeValue(oldestShark, true);
            }
        }

        // If there are no sharks on the screen create a shark otherwise create a shark if there
        // is enough space for it on screen (the amount of space specified by spaceBetweenSharks).
        if (sharks.size == 0) {
            createShark();
        } else {
            Shark shark = sharks.peek();
            if (shark.getX() < WORLD_WIDTH - spaceBetweenSharks) {
                createShark();
            }
        }
    }

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

    public void updateScores(int newScore) {
        int temp, store = newScore;

        for(int i = 0; i <= 5; i++) {
            if(store > PREFS.getInteger("hS" + (i+1))) {
                temp = PREFS.getInteger("hS" + (i + 1));
                PREFS.putInteger("hS" + (i + 1), store);
                store = temp;
            }
        }

        PREFS.flush();
    }
}
