package com.dodgydive;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Created by mwhar on 4/22/2016.
 */
public class Diver {

    private static final float COLLISION_WIDTH = 118f;
    private static final float COLLISION_HEIGHT = 56f;
    private static final float DOWN_ACCEL = 0.125f;
    private static final float SWIM_UP_ACCEL = 2f;
    private static final int TILE_WIDTH = 120;
    private static final int TILE_HEIGHT = 58;
    private static final float FRAME_DURATION = 0.15f;

    private final Rectangle collisionRect;
    private final Animation swimAnimation;

    private float x = 0;
    private float y = 0;
    private float ySpeed = 0;
    private float animationTimer = 0;

    public Diver(TextureRegion diverTexture) {

        // We split up all the frames of the diver texture according to the specified dimensions and
        // then insert them into a 2d array of textures
        TextureRegion[][] diverTextures = new TextureRegion(diverTexture).split(TILE_WIDTH, TILE_HEIGHT);

        // We loop through the previous array and add each frame to a list for minor convenience
        Array<TextureRegion> sharkFrames = new Array<TextureRegion>();
        for(int i = 0; i < diverTextures.length; i++)
        {
            for(int j = 0; j < diverTextures[i].length; j++)
                sharkFrames.add(diverTextures[i][j]);
        }

        // Set up an animation for the diver swimming using the list of frames and have it loop
        swimAnimation = new Animation(FRAME_DURATION, sharkFrames, Animation.PlayMode.LOOP);
        collisionRect = new Rectangle(x, y, COLLISION_WIDTH, COLLISION_HEIGHT);
    }

    /*
    *
    *   update() - This function is used to update position and animation frame of the diver.
    *
    */
    public void update(float delta) {
        // Update the animationTimer by the amount of time since the last frame was displayed
        animationTimer += delta;

        // Make the diver sink towards the bottom
        ySpeed -= DOWN_ACCEL;
        setPosition(x, y + ySpeed);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.TILE_WIDTH;
    }

    public float getHeight() {
        return this.TILE_HEIGHT;
    }

    /*
    *
    *   draw() - This function is called when the diver is drawn to the screen.
    */
    public void draw(SpriteBatch batch) {
        // Using animationTimer get the current frame of the diver's swim animation and draw it
        // in the appropriate place on screen.
        TextureRegion diverTexture = swimAnimation.getKeyFrame(animationTimer);

        float textureX = collisionRect.x - diverTexture.getRegionWidth();
        float textureY = collisionRect.y - diverTexture.getRegionHeight();

        batch.draw(diverTexture, textureX + diverTexture.getRegionWidth(),
                textureY + diverTexture.getRegionHeight());
    }

    /*
    *
    *   drawDebug() - This function is called when the debugMode is set to true and we want to see
    *   where the collision geometry is for the diver.
    *
    */
    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(collisionRect.x, collisionRect.y,
                collisionRect.width, collisionRect.height);
    }

    /*
    *
    *   setPosition() - This function sets the position of the diver and then its collision rect
    *
    */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateCollisionRect();
    }

    /*
    *
    *   swimUp() - This function is called whenever the user taps the screen and makes the diver
    *   move upwards.
    *
    */
    public void swimUp() {
        ySpeed = SWIM_UP_ACCEL;
        setPosition(x, y + ySpeed);
    }

    // This function just sets the collision rect's position to the diver position
    private void updateCollisionRect() {
        collisionRect.setPosition(x, y);
    }

    public Rectangle getCollisionRect()
    {
        return this.collisionRect;
    }
}
