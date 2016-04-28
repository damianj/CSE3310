package com.dodgydive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

/******************************************************************
 * Class that draws and updates a shark enemy. This is used in an
 * Array of sharks to spawn several sharks on screen while the game
 * is being played.
 *
 * @author      Damian Jimenez, jimenez.dmn@gmail.com
 *              <br>Maurice Harris, maurice.harris@mavs.uta.edu
 *              <br>Neunzo Vincent, neunzo.thomas@mavs.uta.edu
 *              <br>Craig Lautenslager, craig.lautenslager@mavs.uta.edu
 ******************************************************************/
public class Shark {
	private static final Preferences PREFS = Gdx.app.getPreferences("Game_Settings");
	private static final float COLLISION_WIDTH = 160f;
	private static final float COLLISION_HEIGHT = 60f;
	private static final int TILE_WIDTH = 207;
	private static final int TILE_HEIGHT = 133;
	private static final float FRAME_DURATION = 0.15f;
	private final float SWIM_SPEED = PREFS.contains("difficulty") ? PREFS.getFloat("difficulty") : 175f;
	private final Rectangle collisionRect;
	private final Animation swimAnimation;
	private static float prevRand = 0.0f;
	private float x;
	private float y;
	private float animationTimer = 0;

	/******************************************************************
	 * Constructor method for the class. Set's up the Shark instance
	 * and set's the texture that will be used to display the Shark, as
	 * well as creating the animation and collision rectangle for the Shark.
	 ******************************************************************/
	public Shark(TextureRegion sharkTexture) {
		TextureRegion[][] sharkTextures = new TextureRegion(sharkTexture).split(TILE_WIDTH, TILE_HEIGHT);

		Array<TextureRegion> sharkFrames = new Array<TextureRegion>();
		for(TextureRegion[] sharkTextureArr : sharkTextures) {
			for(TextureRegion aSharkTexture : sharkTextureArr) {
				sharkFrames.add(aSharkTexture);
			}
		}

		swimAnimation = new Animation(FRAME_DURATION, sharkFrames, Animation.PlayMode.LOOP);
		collisionRect = new Rectangle(x, y, COLLISION_WIDTH, COLLISION_HEIGHT);
	}

	/*
	*
	*   update() - This function is used to update position and animation frame of the shark.
	*
	*/
	public void update(float delta, float rand) {
		// Create a bias so the sharks tend to move in a general y-direction instead of being completely random
		if(prevRand <= 0 && rand > 0) {
			rand *= ((new Random().nextInt(10) - 7) <= 0) ? -1 : 1;
		} else if(prevRand > 0 && rand <= 0) {
			rand *= ((new Random().nextInt(10) - 3) >= 0) ? -1 : 1;
		}
		prevRand = rand;
		// Every frame move the shark to the left
		animationTimer += delta;

		setPosition(x - (SWIM_SPEED * delta), y + rand);
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getWidth() {
		return TILE_WIDTH;
	}

	public float getHeight() {
		return TILE_HEIGHT;
	}

	/*
	*
	*   draw() - This function is called when the shark is drawn to the screen.
	*/
	public void draw(SpriteBatch batch) {

		// Using animationTimer get the current frame of the diver's swim animation and draw it
		// in the appropriate place on screen.
		TextureRegion sharkTexture = swimAnimation.getKeyFrame(animationTimer);

		float textureX = collisionRect.x - sharkTexture.getRegionWidth() - 2;
		float textureY = collisionRect.y - sharkTexture.getRegionHeight() - 32;

		batch.draw(sharkTexture, textureX + sharkTexture.getRegionWidth(),
				textureY + sharkTexture.getRegionHeight());
	}

	/*
	*
	*   drawDebug() - This function is called when the debugMode is set to true and we want to see
	*   where the collision geometry is  for the shark.
	*
	*/
	public void drawDebug(ShapeRenderer shapeRenderer) {
		shapeRenderer.rect(collisionRect.x, collisionRect.y,
				collisionRect.width, collisionRect.height);
	}

	/*
	*
	*   setPosition() - This function sets the position of the shark and then its collision rect
	*
	*/
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		updateCollisionRect();
	}

	/*
	*
	*   hasCollidedWithDiver() - This function checks to see if the diver's rect has intersected
	*   the shark's rect and returns true or false
	*
	*/
	public boolean hasCollidedWithDiver(Diver diver) {

		Rectangle diverCollisionRect = diver.getCollisionRect();

		return Intersector.overlaps(diverCollisionRect, this.collisionRect);
	}

	// This function just sets the collision rect's position to the shark position
	private void updateCollisionRect() {
		collisionRect.setPosition(x, y);
	}


}
