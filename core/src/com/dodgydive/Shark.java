package com.dodgydive;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Created by mwhar on 4/24/2016.
 */
public class Shark {
    private static final float COLLISION_WIDTH = 160f;
    private static final float COLLISION_HEIGHT = 60f;
    private static final int TILE_WIDTH = 207;
    private static final int TILE_HEIGHT = 133;
    private static final float FRAME_DURATION = 0.15f;
    private static final float SWIM_SPEED = 108f;

    private final Rectangle collisionRect;
    private final Animation swimAnimation;

    private float x;
    private float y;
    private float animationTimer = 0;

    public Shark(TextureRegion sharkTexture) {
        TextureRegion[][] sharkTextures = new TextureRegion(sharkTexture).split(TILE_WIDTH, TILE_HEIGHT);

        Array<TextureRegion> sharkFrames = new Array<TextureRegion>();
        for(int i = 0; i < sharkTextures.length; i++)
        {
            for(int j = 0; j < sharkTextures[i].length; j++)
            sharkFrames.add(sharkTextures[i][j]);
        }

        swimAnimation = new Animation(FRAME_DURATION, sharkFrames, Animation.PlayMode.LOOP);
        collisionRect = new Rectangle(x, y, COLLISION_WIDTH, COLLISION_HEIGHT);
    }

    /*
    *
    *   update() - This function is used to update position and animation frame of the shark.
    *
    */
    public void update(float delta) {

        // Every frame move the shark to the left
        animationTimer += delta;
        setPosition(x - (SWIM_SPEED * delta), y);
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
