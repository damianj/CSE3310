package comdamianjcse3310_term_project.httpsgithub.cse3310_group_9_term_project;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class BubbleTrail extends GameObject {
	public BubbleTrail(int x, int y) {
		super.x = x;
		super.y = y;
	}

	public void update() {
		x -= 10;
	}

	public void draw(Canvas canvas) {
		float r = 2.0f;
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);

		for(int i = 11; i <= 11; i++) {
			paint.setAlpha(new Random().nextInt(128) + 80);
			int randOffX = new Random().nextInt(10) - 5;
			int randOffY = new Random().nextInt(10) - 5;
			canvas.drawCircle(x - r + randOffX, y - r + randOffY, r, paint);
		}
	}
}