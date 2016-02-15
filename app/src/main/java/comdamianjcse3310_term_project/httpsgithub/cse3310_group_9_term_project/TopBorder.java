package comdamianjcse3310_term_project.httpsgithub.cse3310_group_9_term_project;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class TopBorder extends GameObject {
	private Bitmap image;

	public TopBorder(Bitmap res, int x, int y, int h) {
		height = h;
		width = 60;

		this.x = x;
		this.y = y;

		dx = GamePanel.MOVE_SPEED;
		image = Bitmap.createBitmap(res, 0, 0, width, height);
	}

	public void update() {
		x += dx;
	}

	public void draw(Canvas canvas) {
		try {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setDither(true);
			canvas.drawBitmap(image, x, y, paint);
		} catch(Exception e) {
			System.err.println("ERROR: " + e.toString());
		}
	}

}