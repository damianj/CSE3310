package comdamianjcse3310_term_project.httpsgithub.cse3310_group_9_term_project;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

public class Shark extends GameObject {
	private int speed;
	private Animation animation = new Animation();

	public Shark(Bitmap res, int x, int y, int w, int h, int s, int frames) {
		super.x = x;
		super.y = y;
		width = w;
		height = h;

		speed = 7 + (int) (new Random().nextDouble() * s / 30);

		//cap shark speed
		speed = speed > 40 ? 40 : speed;

		Bitmap[] image = new Bitmap[frames];

		for(int i = 0; i < image.length; i++) {
			image[i] = Bitmap.createBitmap(res, i * width, 0, width, height);
		}

		animation.setFrames(image);
		animation.setDelay(100 - speed);

	}

	public void update() {
		x -= speed;
		animation.update();
	}

	public void draw(Canvas canvas) {
		try {
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
			canvas.drawBitmap(animation.getImage(), x, y, paint);
		} catch(Exception e) {
			System.err.println("ERROR: " + e.toString());
		}
	}

	@Override
	public int getWidth() {
		//offset slightly for more realistic collision detection
		return width - 10;
	}
}