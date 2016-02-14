package comdamianjcse3310_term_project.httpsgithub.cse3310_group_9_term_project;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;



public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    public static final int WIDTH = 2560;
    public static final int HEIGHT = 1440;
    public static final int MOVE_SPEED = -5;
    private static File score_dir;
    private static AssetManager assets;
    private long bubbleStartTime;
    private long missileStartTime;
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<BubbleTrail> bubble;
    private ArrayList<Missile> missiles;
    private ArrayList<TopBorder> topBorder;
    private ArrayList<BotBorder> botBorder;
    private Random rand = new Random();
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown = true;
    private boolean botDown = true;
    private boolean newGameCreated;

    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean disappear;
    private boolean started;
    private int best;

    public GamePanel(Context context) {
        super(context);
        score_dir = new File(context.getFilesDir().getAbsolutePath());
        assets = context.getAssets();
        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);
        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;
        while(retry && counter<1000) {
            counter++;
            try {thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;

            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);
        bubble = new ArrayList<>();
        missiles = new ArrayList<>();
        topBorder = new ArrayList<>();
        botBorder = new ArrayList<>();
        bubbleStartTime=  System.nanoTime();
        missileStartTime = System.nanoTime();
        thread = new MainThread(getHolder(), this);
        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            if(!player.getPlaying() && newGameCreated && reset) {
                player.setPlaying(true);
                player.setUp(true);
            }
            if(player.getPlaying()) {

                if(!started)started = true;
                reset = false;
                player.setUp(true);
            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP) {
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }

    public void update() {
        if(player.getPlaying()) {
            if(botBorder.isEmpty()) {
                player.setPlaying(false);
                return;
            }
            if(topBorder.isEmpty()) {
                player.setPlaying(false);
                return;
            }

            bg.update();
            player.update();

            //calculate the threshold of height the border can have based on the score
            //max and min border heart are updated, and the border switched direction when either max or
            //min is met

            int progressDenom = 20;
            maxBorderHeight = 30 + player.getScore()/ progressDenom;
            //cap max border height so that borders can only take up a total of 1/2 the screen
            maxBorderHeight = maxBorderHeight > (HEIGHT/4) ? (HEIGHT/4) : maxBorderHeight;
            minBorderHeight = 5 + player.getScore()/ progressDenom;

            //check bottom border collision
            for(int i = 0; i<botBorder.size(); i++) {
                if(collision(botBorder.get(i), player)) {
                    player.setPlaying(false);
                }
            }

            //check top border collision
            for(int i = 0; i <topBorder.size(); i++) {
                if(collision(topBorder.get(i),player)) {
                    player.setPlaying(false);
                }
            }

            //update top border
            this.updateTopBorder();

            //udpate bottom border
            this.updateBottomBorder();

            //add missiles on timer
            long missileElapsed = (System.nanoTime()-missileStartTime)/1000000;
            if(missileElapsed >(2000 - player.getScore()/4)) {
                //first missile always goes down the middle
                if(missiles.size()==0) {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.
                            missile),WIDTH + 10, HEIGHT/2, 45, 15, player.getScore(), 13));
                }
                else {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10, (int)(rand.nextDouble()*(HEIGHT - (maxBorderHeight * 2))+maxBorderHeight),45,15, player.getScore(),13));
                }

                //reset timer
                missileStartTime = System.nanoTime();
            }
            //loop through every missile and check collision and remove
            for(int i = 0; i<missiles.size();i++) {
                //update missile
                missiles.get(i).update();

                if(collision(missiles.get(i),player)) {
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }
                //remove missile if it is way off the screen
                if(missiles.get(i).getX()<-100) {
                    missiles.remove(i);
                    break;
                }
            }

            //add bubble on timer
            long elapsed = (System.nanoTime() - bubbleStartTime)/1000000;
            if(elapsed > 120) {
                bubble.add(new BubbleTrail(player.getX(), player.getY()+10));
                bubbleStartTime = System.nanoTime();
            }

            for(int i = 0; i<bubble.size();i++) {
                bubble.get(i).update();
                if(bubble.get(i).getX()<-10) {
                    bubble.remove(i);
                }
            }
        }
        else {
            player.resetDY();
            if(!reset) {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                disappear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion),player.getX(),
                        player.getY()-30, 100, 100, 25);
            }

            explosion.update();
            long resetElapsed = (System.nanoTime()-startReset)/1000000;

            if(resetElapsed > 2500 && !newGameCreated) {
                newGame();
            }


        }

    }
    public boolean collision(GameObject a, GameObject b) {
        return Rect.intersects(a.getRectangle(), b.getRectangle());
    }
    
    @Override
    public void draw(Canvas canvas) {
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);

        if(canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            if(!disappear) {
                player.draw(canvas);
            }
            //draw bubble
            for(BubbleTrail sp: bubble) {
                sp.draw(canvas);
            }
            //draw missiles
            for(Missile m: missiles) {
                m.draw(canvas);
            }


            //draw topBorder
            for(TopBorder tb: topBorder) {
                tb.draw(canvas);
            }

            //draw botBorder
            for(BotBorder bb: botBorder) {
                bb.draw(canvas);
            }
            //draw explosion
            if(started) {
                explosion.draw(canvas);
            }
            drawText(canvas);
            canvas.restoreToCount(savedState);

        }
    }

    public void updateTopBorder() {
        //every 50 points, insert randomly placed top blocks that break the pattern
        if(player.getScore()%50 ==0) {
            topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
            ),topBorder.get(topBorder.size()-1).getX()+20,0,(int)((rand.nextDouble()*(maxBorderHeight
            ))+1)));
        }
        for(int i = 0; i<topBorder.size(); i++) {
            topBorder.get(i).update();
            if(topBorder.get(i).getX()<-20) {
                topBorder.remove(i);
                //remove element of arraylist, replace it by adding a new one

                //calculate topdown which determines the direction the border is moving (up or down)
                if(topBorder.get(topBorder.size()-1).getHeight()>=maxBorderHeight) {
                    topDown = false;
                }
                if(topBorder.get(topBorder.size()-1).getHeight()<=minBorderHeight) {
                    topDown = true;
                }
                //new border added will have larger height
                if(topDown) {
                    topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topBorder.get(topBorder.size()-1).getX()+20,
                            0, topBorder.get(topBorder.size()-1).getHeight()+1));
                }
                //new border added wil have smaller height
                else {
                    topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topBorder.get(topBorder.size()-1).getX()+20,
                            0, topBorder.get(topBorder.size()-1).getHeight()-1));
                }

            }
        }

    }
    public void updateBottomBorder() {
        //every 40 points, insert randomly placed bottom blocks that break pattern
        if(player.getScore()%40 == 0) {
            botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                    botBorder.get(botBorder.size()-1).getX()+20,(int)((rand.nextDouble()
                    *maxBorderHeight)+(HEIGHT-maxBorderHeight))));
        }

        //update bottom border
        for(int i = 0; i<botBorder.size(); i++) {
            botBorder.get(i).update();

            //if border is moving off screen, remove it and add a corresponding new one
            if(botBorder.get(i).getX() < -20) {
                botBorder.remove(i);
                //determine if border will be moving up or down
                if (botBorder.get(botBorder.size() - 1).getY() <= HEIGHT-maxBorderHeight) {
                    botDown = true;
                }
                if (botBorder.get(botBorder.size() - 1).getY() >= HEIGHT - minBorderHeight) {
                    botDown = false;
                }

                if (botDown) {
                    botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), botBorder.get(botBorder.size() - 1).getX() + 20, botBorder.get(botBorder.size() - 1
                    ).getY() + 1));
                }
                else {
                    botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), botBorder.get(botBorder.size() - 1).getX() + 20, botBorder.get(botBorder.size() - 1
                    ).getY() - 1));
                }
            }
        }
    }
    public void newGame() {
        disappear = false;

        botBorder.clear();
        topBorder.clear();

        missiles.clear();
        bubble.clear();

        minBorderHeight = 5;
        maxBorderHeight = 30;

        if(player.getScore() > best) {
            try {
                FileWriter fw = new FileWriter(score_dir + "/hi-score.txt", false);
                fw.write(Integer.valueOf(player.getScore()) + "\n");
                fw.close();
            }
            catch(IOException e) {
                System.exit(1);
            }
            best = player.getScore();
        }

        try {
            Scanner fileInput;
            fileInput = new Scanner(new File(score_dir + "/hi-score.txt"));
            best = fileInput.nextInt();
        }
        catch(FileNotFoundException e) {
            best = 0;
        }

        player.resetDY();
        player.resetScore();
        player.setY(HEIGHT/2);
        //create initial borders

        //initial top border
        for(int i = 0; i*20 < WIDTH+40;i++) {
            //first top border create
            if(i == 0) {
                topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), i * 20, 0, 10));
            }
            else {
                topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), i * 20, 0, topBorder.get(i - 1).getHeight() + 1));
            }
        }
        //initial bottom border
        for(int i = 0; i*20 < WIDTH+40; i++) {
            //first border ever created
            if(i==0) {
                botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),i*20,HEIGHT - minBorderHeight));
            }
            //adding borders until the initial screen is filed
            else {
                botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), i * 20, botBorder.get(i - 1).getY() - 1));
            }
        }

        newGameCreated = true;


    }
    public void drawText(Canvas canvas) {
        Paint static_text = new Paint();
        static_text.setAntiAlias(false);
        static_text.setColor(Color.BLACK);
        static_text.setTextSize(90);
        static_text.setTypeface(Typeface.createFromAsset(assets, "fonts/score_font.ttf"));
        canvas.drawText("SCORE: " + player.getScore(), 10, HEIGHT - 10, static_text);
        canvas.drawText("BEST: " + best, WIDTH - (45 * ("BEST: " + best).length()) - 10, HEIGHT - 10, static_text);

        if(!player.getPlaying() && newGameCreated && reset) {
            Paint newGameText = new Paint();
            newGameText.setAntiAlias(true);
            newGameText.setTextSize(120);
            newGameText.setTypeface(Typeface.createFromAsset(assets, "fonts/main_font.otf"));
            canvas.drawText("PRESS TO START", WIDTH/2-50, HEIGHT/2, newGameText);

            newGameText.setTextSize(60);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH/2-50, HEIGHT/2 + 60, newGameText);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH/2-50, HEIGHT/2 + 110, newGameText);
        }
    }


}