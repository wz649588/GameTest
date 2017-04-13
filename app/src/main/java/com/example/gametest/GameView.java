package com.example.gametest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by wz649 on 2017/4/11.
 */

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;//volatile means multithreads synchronize
    private Thread gameThread = null;
    private Player player;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private ArrayList<Star> stars = new ArrayList<Star>();
    private Enemy enemies;
    private Friend friend;
    private Boom boom;
    int screenX;
    int countMisses;
    boolean flag;
    private boolean isGameOver;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        player = new Player(context, screenX, screenY);
        surfaceHolder = getHolder();
        paint = new Paint();
        int starNums = 100;
        for (int i = 0; i < starNums; i++) {
            Star s = new Star(screenX, screenY);
            stars.add(s);
        }
        enemies = new Enemy(context, screenX, screenY);
        friend = new Friend(context, screenX, screenY);
        boom = new Boom(context);
        this.screenX = screenX;
        countMisses = 0;
        isGameOver = false;
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        player.update();
        boom.setX(-250);
        boom.setY(-250);
        for (Star s : stars) {
            s.update(player.getSpeed());
        }
        if (enemies.getX() == screenX) {
            flag = true;
        }
        enemies.update(player.getSpeed());
        if (Rect.intersects(player.getDetectCollision(), enemies.getDetectCollision())) {
            boom.setX(enemies.getX());
            boom.setY(enemies.getY());
            enemies.setX(-200);
        } else {
            if (flag) {
                if (enemies.getDetectCollision().left < 0 && enemies.getDetectCollision().left > -100) {
                    countMisses++;
                    flag = false;
                    if (countMisses == 3) {
                        playing = false;
                        isGameOver = true;
                    }
                }
            }
        }
        friend.update(player.getSpeed());
        if (Rect.intersects(player.getDetectCollision(), friend.getDetectCollision())){
            boom.setX(friend.getX());
            boom.setY(friend.getY());
            playing = false;
            isGameOver = true;
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.WHITE);

            for (Star s : stars) {
                paint.setStrokeWidth(s.getStarWidth());
                canvas.drawPoint(s.getX(), s.getY(), paint);
            }
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            canvas.drawBitmap(
                    enemies.getBitmap(),
                    enemies.getX(),
                    enemies.getY(),
                    paint
            );

            canvas.drawBitmap(
                    boom.getBitmap(),
                    boom.getX(),
                    boom.getY(),
                    paint
            );
            canvas.drawBitmap(
                    friend.getBitmap(),
                    friend.getX(),
                    friend.getY(),
                    paint
            );
            if (isGameOver) {
                paint.setTextSize(150);
                paint.setTextAlign(Paint.Align.CENTER);
                int yPos = (int) ((canvas.getHeight() / 2) - (paint.descent() + paint.ascent()) / 2);
                canvas.drawText("Game Over", canvas.getWidth() / 2, yPos, paint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP://untouching
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN://touching
                player.setBoosting();
                break;
        }
        return true;
    }
}
