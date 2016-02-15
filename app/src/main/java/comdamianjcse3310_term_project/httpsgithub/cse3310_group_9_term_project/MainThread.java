package comdamianjcse3310_term_project.httpsgithub.cse3310_group_9_term_project;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
	private final SurfaceHolder surfaceHolder;
	private GamePanel gamePanel;
	private boolean running;
	public static Canvas canvas;

	public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	@Override
	public void run() {
		long startTime;
		long timeMillis;
		long waitTime;
		long totalTime = 0;
		int frameCount = 0;
		int FPS = 30;
		long targetTime = 1000 / FPS;

		while(running) {
			startTime = System.nanoTime();
			canvas = null;

			//try locking the canvas for pixel editing
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized(surfaceHolder) {
					this.gamePanel.update();
					this.gamePanel.draw(canvas);
				}
			} catch(Exception e) {
				System.err.println("ERROR: " + e.toString());
			} finally {
				if(canvas != null) {
					try {
						surfaceHolder.unlockCanvasAndPost(canvas);
					} catch(Exception e) {
						System.err.println("ERROR: " + e.toString());
						e.printStackTrace();
					}
				}
			}

			timeMillis = (System.nanoTime() - startTime) / 1000000;
			waitTime = targetTime - timeMillis;

			try {
				sleep(waitTime);
			} catch(Exception e) {
				System.err.println("ERROR: " + e.toString());
			}

			totalTime += System.nanoTime() - startTime;
			frameCount++;
			if(frameCount == FPS) {
				System.out.println(1000.0 / ((totalTime / frameCount) / 1000000.0));
				frameCount = 0;
				totalTime = 0;
			}
		}
	}

	public void setRunning(boolean b) {
		running = b;
	}
}