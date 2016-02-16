package comdamianjcse3310_term_project.httpsgithub.cse3310_group_9_term_project;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Background {

	private Bitmap image;
	private int x, y, dx;

	public Background(Bitmap res) {
		image = res;
		y = 0;
	}

	public void update() {
		x += dx;
		x = x < -GamePanel.WIDTH ? 0 : x;
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		canvas.drawBitmap(image, x, y, paint);
		if(x < 0) {
			canvas.drawBitmap(image, x + GamePanel.WIDTH, y, paint);
		}
	}

	public void setVector(int dx) {
		this.dx = dx;
	}
}