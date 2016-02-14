package comdamianjcse3310_term_project.httpsgithub.cse3310_group_9_term_project;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BotBorder extends GameObject {

    private Bitmap image;
    public BotBorder(Bitmap res, int x, int y) {
        height = 200;
        width = 20;

        this.x = x;
        this.y = y;
        dx = GamePanel.MOVE_SPEED;

        image = Bitmap.createBitmap(res, 0, 0, width, height);

    }
    public void update() {
        x +=dx;

    }
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawBitmap(image, x, y, paint);

    }
}