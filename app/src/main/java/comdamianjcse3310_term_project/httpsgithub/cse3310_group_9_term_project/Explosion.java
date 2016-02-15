package comdamianjcse3310_term_project.httpsgithub.cse3310_group_9_term_project;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Explosion {
	private int x;
	private int y;
	private int width;
	private int height;
	private int row;
	private Animation animation = new Animation();

	public Explosion(Bitmap res, int x, int y, int w, int h, int numFrames) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;

		Bitmap[] image = new Bitmap[numFrames];

		for(int i = 0; i < image.length; i++) {
			row += ((i % 5 == 0) && (i > 0)) ? 1 : 0;
			image[i] = Bitmap.createBitmap(res, (i - (5 * row)) * width, row * height, width, height);
		}
		animation.setFrames(image);
		animation.setDelay(10);
	}

	public void draw(Canvas canvas) {
		if(!animation.playedOnce()) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setDither(true);
			canvas.drawBitmap(animation.getImage(), x, y, paint);
		}

	}

	public void update() {
		if(!animation.playedOnce()) {
			animation.update();
		}
	}

	public int getHeight() {
		return height;
	}
}