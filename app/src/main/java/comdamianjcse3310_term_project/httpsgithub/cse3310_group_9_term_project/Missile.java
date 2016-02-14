package comdamianjcse3310_term_project.httpsgithub.cse3310_group_9_term_project;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

public class Missile extends GameObject{
    private int score;
    private int speed;
    private Random rand = new Random();
    private Animation animation = new Animation();

    public Missile(Bitmap res, int x, int y, int w, int h, int s, int numFrames) {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;

        speed = 7 + (int) (rand.nextDouble()*score/30);

        //cap missile speed
        speed = speed > 40 ? 40 : speed;

        Bitmap[] image = new Bitmap[numFrames];

        for(int i = 0; i<image.length;i++) {
            image[i] = Bitmap.createBitmap(res, 0, i*height, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(100-speed);

    }
    public void update() {
        x -= speed;
        animation.update();
    }
    public void draw(Canvas canvas) {
        try {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setDither(true);
            canvas.drawBitmap(animation.getImage(), x, y, paint);
        }
        catch(Exception e) {
        }
    }

    @Override
    public int getWidth() {
        //offset slightly for more realistic collision detection
        return width-10;
    }
}