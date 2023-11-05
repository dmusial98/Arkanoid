package com.example.arkanoid;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Vibrator;

import java.io.IOException;

public class Game extends Activity {

    GameView view;

    public SensorManager sensorManager;
    public Sensor rotationVectorSensor;
    public SensorEventListener rvListener;
    public float[] rotationMatrix;
    public float angle;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new GameView(this);
        setContentView(view);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        rotationMatrix = new float[16];

        rvListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);

                //Remap coordinate system
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedRotationMatrix);
                //Convert to orientations
                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                for (int i = 0; i < 3; i++) {
                    orientations[i] = (float) (Math.toDegrees(orientations[i]));
                }

                angle = orientations[2];
                if (angle > -85 && angle < -5) //right side
                    view.paddle.setMovementState(view.paddle.RIGHT);
                else if (angle > -175 && angle < -95)  //left side
                    view.paddle.setMovementState(view.paddle.LEFT);
                else if (angle < -85 && angle > -95)
                    view.paddle.setMovementState(view.paddle.STOPPED);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        if(GameVariables.controlWithRotation)
            sensorManager.registerListener(rvListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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

        public Paddle paddle;

        Ball ball;

        Brick[] bricks = new Brick[200];
        int numBricks = 0;

        SoundPool soundPool;
        int beep1ID = -1;
        int beep2ID = -1;
        int beep3ID = -1;
        int loseLifeID = -1;
        int explodeID = -1;
        int gameOverID = -1;
        int youWonID = -1;

        int score = 0;
        boolean won = false;

        int lives = GameVariables.livesNumber;

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

            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

            try {
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("game-over.mp3");
                gameOverID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("congratulations-you-won.mp3");
                youWonID = soundPool.load(descriptor, 0);

            } catch (IOException e) {
                Log.e("error", "failed to load sound files");
            }
            
            
            createBricksAndRestart();
        }

        public void createBricksAndRestart() {

            ball.reset(screenX, screenY);

            int brickWidth = screenX / GameVariables.bricksNumberInColumns;
            int brickHeight = screenY / (GameVariables.bricksNumberInRows + 7);

            numBricks = 0;
            for (int column = 0; column < GameVariables.bricksNumberInColumns; column++) {
                for (int row = 0; row < GameVariables.bricksNumberInRows; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }
            if (lives == 0) {
                score = 0;
                lives = GameVariables.livesNumber;
            }
        }

        @Override
        public void run() {

            Log.d("Settings", "rotation: " + GameVariables.controlWithRotation);
            Log.d("Settings", "backgroundColorId: " + GameVariables.BackgroundColor);
            Log.d("Settings", "bricksColorId: " + GameVariables.BricksColor);
            Log.d("Settings", "lives number: " + GameVariables.livesNumber);
            Log.d("Settings", "paddle length: " + GameVariables.paddleLength);
            Log.d("Settings", "control with rotation: " + GameVariables.controlWithRotation);
            Log.d("Settings", "paddle speed: " + GameVariables.paddleSpeed);
            Log.d("Settings", "ball horizontal speed: " + GameVariables.ballHorizontalSpeed);
            Log.d("Settings", "ball vertical speed: " + GameVariables.ballVerticalSpeed);

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

            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        if(GameVariables.vibrations) {
                            vibrator.cancel();
                            vibrator.vibrate(300);
                        }
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 10;
                        if(GameVariables.audio)
                            soundPool.play(explodeID, 1, 1, 0, 0, 1);
                    }
                }
            }
            // Check for ball colliding with paddle
            if (RectF.intersects(paddle.getRect(), ball.getRect())) {
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top - 2);
                if(GameVariables.audio)
                    soundPool.play(beep1ID, 1, 1, 0, 0, 1);
            }
            // Bounce the ball back when it hits the bottom of screen
            if (ball.getRect().bottom > screenY) {
                ball.reverseYVelocity();
                ball.reset(screenX, screenY);
                paddle.reset(screenX, screenY);

                lives--;
                if(GameVariables.audio && lives > 0)
                    soundPool.play(loseLifeID, 1, 1, 0, 0, 1);

                if(GameVariables.audio && lives <= 0)
                    soundPool.play(gameOverID, 1, 1, 0, 0, 1);

                paused = true;
                if (lives == 0) {
                    createBricksAndRestart();
                }
            }

            // Bounce the ball back when it hits the top of screen
            if (ball.getRect().top < 0)
            {
                ball.reverseYVelocity();
                ball.clearObstacleY(GameVariables.ballWidthHeightInPixels);
                if(GameVariables.audio)
                    soundPool.play(beep2ID, 1, 1, 0, 0, 1);
            }

            // If the ball hits left wall bounce
            if (ball.getRect().left < 0)
            {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
                if(GameVariables.audio)
                    soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // If the ball hits right wall bounce
            if (ball.getRect().right > screenX ) {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - GameVariables.ballWidthHeightInPixels - 2);
                if(GameVariables.audio)
                    soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // Pause if cleared screen
            if (score == numBricks * 10 && !paused)
            {
                paused = true;
                if(GameVariables.audio)
                    soundPool.play(youWonID, 1, 1, 0, 0, 1);
                createBricksAndRestart();
                score = 0;
                won = true;
                lives = GameVariables.livesNumber;
                ball.reverseYVelocity();
                ball.reset(screenX, screenY);
                paddle.reset(screenX, screenY);
            }

        }

        public void draw() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();

                canvas.drawColor(GameVariables.BackgroundColor);

                paint.setColor(GameVariables.BricksColor);

                canvas.drawRect(paddle.getRect(), paint);

                canvas.drawRect(ball.getRect(), paint);

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
            paused = true;
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

    @Override
    public void finish()
    {
        super.finish();
        view.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            view.pause();
        }
        return super.onKeyDown(keyCode, event);
    }
}