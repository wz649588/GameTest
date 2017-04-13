package com.example.gametest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by wz649 on 2017/4/12.
 */

public class Friend {

    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 1;
    private int maxX;
    private int maxY;
    private int minX;
    private int minY;
    private Rect detectCollision;

    public Friend(Context context, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.friend);
        maxX = screenX;
        maxY = screenY - bitmap.getHeight();
        minX = 0;
        minY = 0;
        Random generator = new Random();
        speed = generator.nextInt(6) + 10;
        x = screenX;
        y = generator.nextInt(maxY);
        detectCollision = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void update(int playerSpeed) {
        x -= playerSpeed;
        x -= speed;
        if (x < minX-bitmap.getWidth()) {
            Random generator = new Random();
            speed = generator.nextInt(10) + 10;
            x = maxX;
            y = generator.nextInt(maxY);
        }
        detectCollision.left = x;
        detectCollision.right = x+bitmap.getWidth();
        detectCollision.top = y;
        detectCollision.bottom = y + bitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rect getDetectCollision() {
        return detectCollision;
    }
}
