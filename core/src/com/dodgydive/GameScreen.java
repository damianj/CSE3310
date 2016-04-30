package com.dodgydive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Locale;
import java.util.Random;

/******************************************************************
 * Class that constructs the game screen where the actual game is played.
 * In this class is the logic for the game.
 *
 * @author      Damian Jimenez, jimenez.dmn@gmail.com
 *              <br>Maurice Harris, maurice.harris@mavs.uta.edu
 *              <br>Neunzo Vincent, neunzo.thomas@mavs.uta.edu
 *              <br>Craig Lautenslager, craig.lautenslager@mavs.uta.edu
 ******************************************************************/
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
	private String background_name = PREFS.contains("gameBackground") ? PREFS.getString("gameBackground") : "background";
	private Diver diver;
	private String diver_costume = PREFS.contains("diverCostume") ? PREFS.getString("diverCostume") : "diver";
	private TextureRegion sharkTexture;
	private Array<Shark> sharks = new Array<Shark>();
	private Timer scoreTimer = new Timer();
	private int score = 0;
	private int sharksOnScreen = 10;
	private int spaceBetweenSharks = WORLD_WIDTH / (sharksOnScreen);
	private boolean killedByShark = false;

	/******************************************************************
	 * Constructor method for the class. Set's up a DodgyDiveGame instance
	 ******************************************************************/
	public GameScreen(DodgyDiveGame dodgyDiveGame) {
		this.dodgyDiveGame = dodgyDiveGame;
		this.debugMode = false; /* Can be set to true to show the debug details on screen */
	}

	/******************************************************************
	 * Draws all of the relevant assets on the screen. A table is used
	 * to make sure the alignment of the assets is consistent.
	 ******************************************************************/
	@Override
	public void show() {
		super.show();
		loadScores();

		camera = new OrthographicCamera();
		camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		camera.update();

		viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

		shapeRenderer = new ShapeRenderer();

		batch = new SpriteBatch();

		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("game_music.mp3"));
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("score_font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = scoreFontSize;
		parameter.kerning = true;
		parameter.borderStraight = true;
		parameter.borderWidth = 1;
		scoreFont = generator.generateFont(parameter);
		scoreFont.setColor(0, 0, 0, 0.70f);
		generator.dispose();
		debugFont = dodgyDiveGame.getAssetManager().get("debug_font.fnt");
		glyphLayout = new GlyphLayout();

		TextureAtlas textureAtlas = dodgyDiveGame.getAssetManager().get("dodgy_dive_assets.atlas");
		background = textureAtlas.findRegion(background_name);

		TextureRegion diverTexture = textureAtlas.findRegion(diver_costume);
		diver = new Diver(diverTexture);
		diver.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);

		sharkTexture = textureAtlas.findRegion("shark");

		scoreTimer.scheduleTask(new Timer.Task() {

			@Override
			public void run() {
				score += 10;
			}

		}, 1f, 1f);
		scoreTimer.start();

		crunchSound.setVolume(musicVolume);
		crunchSound.setPosition(0.4f);
		gameMusic.setVolume(musicVolume);
		gameMusic.play();
		gameMusic.setLooping(true);
	}

	/******************************************************************
	 * Keeps track of whenever the screen is resized and makes the
	 * viewport (viewable screen area) resize to the given width and height.
	 ******************************************************************/
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height, true);
	}

	/******************************************************************
	 * Clears the screen and then draws what needs to be rendered on
	 * the screen.
	 ******************************************************************/
	@Override
	public void render(float delta) {
		super.render(delta);
		clearScreen();
		draw();

		if(debugMode) {
			drawDebug();
		}

		update(delta);
	}

	/******************************************************************
	 * Cleans up all of the disposable resources when the screen is no
	 * longer in use.
	 ******************************************************************/
	public void dispose() {
		super.dispose();
		scoreFont.dispose();
		debugFont.dispose();
		batch.dispose();
		shapeRenderer.dispose();
		gameMusic.dispose();
		crunchSound.dispose();
	}

	/******************************************************************
	 * Stop the scoreTimer and clear it of all scheduled tasks, then set
	 * the game's screen back to the startScreen.
	 ******************************************************************/
	private void endGame() {
		gameMusic.stop();

		if(killedByShark) {
			crunchSound.play();
		}

		scoreTimer.stop();
		updateScores(score);
		scoreTimer.clear();
		dodgyDiveGame.setScreen(new StartScreen(dodgyDiveGame));

		while(true) {
			if(!crunchSound.isPlaying()) {
				break; /* Wait for the crunching sound to finish playing */
			}
		}

		dispose();
	}

	/******************************************************************
	 * Draw our assets onto the screen (i.e., sharks, diver, background,
	 * and score).
	 ******************************************************************/
	private void draw() {
		batch.setProjectionMatrix(camera.projection);
		batch.setTransformMatrix(camera.view);

		batch.begin();

		batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
		diver.draw(batch);

		for(Shark shark : sharks) {
			shark.draw(batch);
		}

		drawScore();

		batch.end();
	}

	/******************************************************************
	 * Draws the debug information to the screen to allow better debugging
	 * of the game and game AI.
	 ******************************************************************/
	private void drawDebug() {
		batch.setProjectionMatrix(camera.projection);
		batch.setTransformMatrix(camera.view);

		shapeRenderer.setProjectionMatrix(camera.projection);
		shapeRenderer.setTransformMatrix(camera.view);

		String debugString = String.format(Locale.US, "FPS: %d\nPOS X: %d\nPOS Y: %d",
				Gdx.graphics.getFramesPerSecond(), (int) diver.getX(), (int) diver.getY());
		glyphLayout.setText(debugFont, debugString);

		batch.begin();
		debugFont.draw(batch, debugString, 12, WORLD_HEIGHT - 12);
		batch.end();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		diver.drawDebug(shapeRenderer);

		for(Shark shark : sharks) {
			shark.drawDebug(shapeRenderer);
		}

		shapeRenderer.end();
	}

	/******************************************************************
	 * Draws the score onto the screen and centers it on the bottom of
	 * the screen.
	 ******************************************************************/
	private void drawScore() {
		String scoreString = String.format(Locale.US, "Hi-Score: %d | Score: %07d", PREFS.getInteger("hS1"), score);

		glyphLayout.setText(scoreFont, scoreString);
		scoreFont.draw(batch, scoreString, (WORLD_WIDTH / 2.0f) - (scoreString.length() * scoreFontSize / 3.6f), 50.0f);
	}


	/******************************************************************
	 * Updates the position and state of all objects in the game.
	 ******************************************************************/
	private void update(float delta) {
		diver.update(delta);
		updateSharks(delta);

		if(Gdx.input.isTouched()) {
			diver.swimUp();
		}

		for(Shark shark : sharks) {
			if(shark.hasCollidedWithDiver(diver)) {
				killedByShark = true;
				endGame();
			}
		}

		if(diver.getX() < 0 || diver.getY() < 0 ||
				(diver.getX() + diver.getWidth()) > WORLD_WIDTH
				|| (diver.getY() + diver.getHeight()) > WORLD_HEIGHT) {
			endGame();
		}
	}

	/******************************************************************
	 * Clears the screen to black, to prepare it to draw the next frame.
	 ******************************************************************/
	private void clearScreen() {
		Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/******************************************************************
	 * Create a shark at the right edge of the screen at a random height
	 * on the screen
	 ******************************************************************/
	private void createShark() {
		Shark newShark = new Shark(sharkTexture);
		float x = WORLD_WIDTH;
		float y = new Random().nextFloat() * (WORLD_HEIGHT - newShark.getHeight());

		newShark.setPosition(x, y);
		sharks.add(newShark);
	}

	/******************************************************************
	 * Updates and keeps track of all the sharks on the screen, making sure
	 * that only a specified number of sharks are in the sharks list/screen.
	 * The more sharks the less space between each shark.
	 ******************************************************************/
	private void updateSharks(float delta) {
		for(Shark shark : sharks) {
			shark.update(delta, new Random().nextFloat() * (new Random().nextInt(11) - 5));
		}

		if(sharks.size > 0) {
			Shark oldestShark = sharks.first();
			if(oldestShark.getX() < -(oldestShark.getWidth())) {
				sharks.removeValue(oldestShark, true);
			}
		}

		if(sharks.size == 0) {
			createShark();
		}
		else {
			Shark shark = sharks.peek();
			if(shark.getX() < WORLD_WIDTH - spaceBetweenSharks) {
				createShark();
			}
		}
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

	/******************************************************************
	 * Updates the scores replacing the highest score that newScore is
	 * greater than and propagating those changes down the score "list."
	 ******************************************************************/
	public void updateScores(int newScore) {
		int temp, store = newScore;

		for(int i = 0; i <= 5; i++) {
			if(store > PREFS.getInteger("hS" + (i + 1))) {
				temp = PREFS.getInteger("hS" + (i + 1));
				PREFS.putInteger("hS" + (i + 1), store);
				store = temp;
			}
		}

		PREFS.flush();
	}
}
