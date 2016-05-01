package com.dodgydive;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/******************************************************************
 * Class that draws and updates the diver character that the player is in
 * control of while playing the game.
 *
 * @author      Damian Jimenez, jimenez.dmn@gmail.com
 *              <br>Maurice Harris, maurice.harris@mavs.uta.edu
 *              <br>Neunzo Vincent, neunzo.thomas@mavs.uta.edu
 *              <br>Craig Lautenslager, craig.lautenslager@mavs.uta.edu
 ******************************************************************/
public class Diver {
	private static final float COLLISION_WIDTH = 118f;
	private static final float COLLISION_HEIGHT = 56f;
	private static final float SWIM_UP_ACCELERATION = 2f;
	private static final int TILE_WIDTH = 120;
	private static final int TILE_HEIGHT = 58;
	private static final float FRAME_DURATION = 0.15f;
	private final Rectangle collisionRect;
	private final Animation swimAnimation;
    private float downAcceleration = 0.125f;
	private float x = 0;
	private float y = 0;
	private float ySpeed = 0;
	private float animationTimer = 0;

	/******************************************************************
	 * Constructor method for the class. Set's up the diver instance
	 * and set's the texture that will be used to display the diver, as
	 * well as creating the animation and collision rectangle for the diver.
	 ******************************************************************/
	public Diver(TextureRegion diverTexture, TextureRegion deathTexture) {
		TextureRegion[][] diverTextures = new TextureRegion(diverTexture).split(TILE_WIDTH, TILE_HEIGHT);

		Array<TextureRegion> diverFrames = new Array<TextureRegion>();
		for(TextureRegion[] diverTextureArr : diverTextures) {
			for(TextureRegion aDiverTexture : diverTextureArr) diverFrames.add(aDiverTexture);
		}

		swimAnimation = new Animation(FRAME_DURATION, diverFrames, Animation.PlayMode.LOOP);
		collisionRect = new Rectangle(x, y, COLLISION_WIDTH, COLLISION_HEIGHT);
	}

	/******************************************************************
	 * Updates the position of the diver.
	 *
	 * @param delta float that will be used to update the animation timer.
	 ******************************************************************/
	public void update(float delta) {
		animationTimer += delta;
		ySpeed -= downAcceleration;
		setPosition(x, y + ySpeed);
	}

	/******************************************************************
	 * Getter method for the x-position of the diver.
	 *
	 * @return float representing the x-position of the diver
	 ******************************************************************/
	public float getX() {
		return this.x;
	}

	/******************************************************************
	 * Getter method for the y-position of the diver.
	 *
	 * @return float representing the y-position of the diver
	 ******************************************************************/
	public float getY() {
		return this.y;
	}

	/******************************************************************
	 * Getter method for the width of the diver.
	 *
	 * @return float representing the width of the diver
	 ******************************************************************/
	public float getWidth() {
		return TILE_WIDTH;
	}

	/******************************************************************
	 * Getter method for the height of the diver.
	 *
	 * @return float representing the height of the diver
	 ******************************************************************/
	public float getHeight() {
		return TILE_HEIGHT;
	}

	/******************************************************************
	 * Draws the diver onto the screen
	 *
	 * @param batch SpriteBatch that will draw the diver onto the screen
	 ******************************************************************/
	public void draw(SpriteBatch batch) {

        TextureRegion diverTexture = swimAnimation.getKeyFrame(animationTimer);

        float textureX = collisionRect.x - diverTexture.getRegionWidth();
		float textureY = collisionRect.y - diverTexture.getRegionHeight();

		batch.draw(diverTexture, textureX + diverTexture.getRegionWidth(), textureY + diverTexture.getRegionHeight());
	}

	/******************************************************************
	 * Renders the debug collision rectangle for the diver
	 *
	 * @param shapeRenderer ShapeRenderer that draws the collision rectangle
	 ******************************************************************/
	public void drawDebug(ShapeRenderer shapeRenderer) {
		shapeRenderer.rect(collisionRect.x, collisionRect.y, collisionRect.width, collisionRect.height);
	}

	/******************************************************************
	 * Sets the position of the diver and its collision rectangle
	 *
	 * @param x float value to be used to set the x-position of the diver
	 * @param y float value to be used to set the y-position of the diver
	 ******************************************************************/
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		updateCollisionRect();
	}

	/******************************************************************
	 * Make the diver object go up on screen.
	 ******************************************************************/
	public void swimUp() {
		ySpeed = SWIM_UP_ACCELERATION;
		setPosition(x, y + ySpeed);
	}

	/******************************************************************
	 * Sets the collision rectangles position to the diver's position
	 ******************************************************************/
	private void updateCollisionRect() {
		collisionRect.setPosition(x, y);
	}

	/******************************************************************
	 * Getter method for the collision rectangle.
	 *
	 * @return Rectangle representing the collision rectangle for the object.
	 ******************************************************************/
	public Rectangle getCollisionRect() {
		return this.collisionRect;
	}
}
