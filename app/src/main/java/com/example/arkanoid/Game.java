package com.example.arkanoid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Game extends Activity {

    GameView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new GameView(this);
        setContentView(view);

    }

    class GameView extends SurfaceView implements Runnable {

        Thread gameThread = null;

        SurfaceHolder ourHolder;

        volatile boolean playing;
        boolean paused = true;

        Canvas canvas;
        Paint paint;

        long fps;
        private long timeThisFrame;

        int screenX;
        int screenY;

        Paddle paddle;
        Ball ball;

        Brick[] bricks = new Brick[200];
        int numBricks = 0;

        int score = 0;
        boolean won = false;

        int lives = 3;

        public GameView(Context context) {

            super(context);

            ourHolder = getHolder();
            paint = new Paint();

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            paddle = new Paddle(screenX, screenY);
            ball = new Ball(screenX, screenY);

            createBricksAndRestart();
        }

        public void createBricksAndRestart() {

            ball.reset(screenX, screenY);

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;

            numBricks = 0;
            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 3; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }
            if (lives == 0) {
                score = 0;
                lives = 3;
            }
        }

        @Override
        public void run() {
            while (playing) {
                long startFrameTime = System.currentTimeMillis();
                if (!paused) {
                    won = false;
                    update();
                }
                draw();
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        public void update() {

            paddle.update(fps);

            ball.update(fps);

            // Check for ball colliding with a brick
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 10;
                    }
                }
            }
            // Check for ball colliding with paddle
            if (RectF.intersects(paddle.getRect(), ball.getRect())) {
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top - 2);
            }
            // Bounce the ball back when it hits the bottom of screen
            if (ball.getRect().bottom > screenY) {
                ball.reverseYVelocity();

                lives--;
                paused = true;
                ball.reset(screenX, screenY);
                paddle.reset(screenX, screenY);

                if (lives == 0) {
                    paused = true;
                    createBricksAndRestart();
                }
            }

            // Bounce the ball back when it hits the top of screen
            if (ball.getRect().top < 0)
            {
                ball.reverseYVelocity();
                ball.clearObstacleY(40);
            }

            // If the ball hits left wall bounce
            if (ball.getRect().left < 0)

            {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
            }

            // If the ball hits right wall bounce
            if (ball.getRect().right > screenX - 10) {

                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 42);
            }

            // Pause if cleared screen
            if (score == numBricks * 10 && !paused)
            {
                paused = true;
                createBricksAndRestart();
                score = 0;
                won = true;
                lives = 3;
                ball.reverseYVelocity();
                ball.reset(screenX, screenY);
                paddle.reset(screenX, screenY);
            }

        }

        // Draw the newly updated scene
        public void draw() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();

                canvas.drawColor(Color.argb(255, 224, 100, 11));

                paint.setColor(Color.argb(255, 255, 255, 255));

                canvas.drawRect(paddle.getRect(), paint);
                canvas.drawRect(ball.getRect(), paint);

                paint.setColor(Color.argb(255,164, 224, 11));

                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                paint.setColor(Color.argb(255, 255, 255, 255));

                paint.setTextSize(40);
                canvas.drawText("Score: " + score + "   Lives: " + lives, 10, 50, paint);

                if (won) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!", 10, screenY / 2, paint);
                }

                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE LOST!", 10, screenY / 2, paint);
                }

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
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
                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    if (motionEvent.getX() > screenX / 2)
                        paddle.setMovementState(paddle.RIGHT);
                    else
                        paddle.setMovementState(paddle.LEFT);
                    if(!playing) {
                        playing = true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }
            return true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        view.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.pause();
    }

}
