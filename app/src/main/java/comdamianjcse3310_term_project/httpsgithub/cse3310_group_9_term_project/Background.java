package comdamianjcse3310_term_project.httpsgithub.cse3310_group_9_term_project;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background {

    private Bitmap image;
    private int x, y, dx;

    public Background(Bitmap res) {
        image = res;
    }
    public void update() {
        x += dx;
        x = x < -GamePanel.WIDTH ? 0 : x;
    }
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y,null);
        if(x<0) {
            canvas.drawBitmap(image, x+GamePanel.WIDTH, y, null);
        }
    }
    public void setVector(int dx) {
        this.dx = dx;
    }
}