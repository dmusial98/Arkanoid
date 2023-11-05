package com.example.arkanoid;

import android.graphics.RectF;

import java.util.Random;

public class Ball {
    RectF rect;
    float xVelocity;
    float yVelocity;
    float ballWidth = GameVariables.ballWidthHeightInPixels;
    float ballHeight = GameVariables.ballWidthHeightInPixels;

    public Ball(int screenX, int screenY){

        xVelocity = GameVariables.ballHorizontalSpeed;
        yVelocity = GameVariables.ballVerticalSpeed;
        rect = new RectF();
    }

    public RectF getRect(){
        return rect;
    }

    public void update(long fps){
        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top - ballHeight;
    }

    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = - xVelocity;
    }

    public void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    public void clearObstacleX(float x){
        rect.left = x;
        rect.right = x + ballWidth;
    }

    public void reset(int x, int y){
        rect.left = x / 2;
        rect.top = y - ballWidth;
        rect.right = x / 2 + ballWidth;
        rect.bottom = y - GameVariables.paddleHeight - ballHeight;
    }
}