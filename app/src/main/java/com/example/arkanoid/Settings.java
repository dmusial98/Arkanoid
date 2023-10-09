package com.example.arkanoid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class Settings extends Activity {

    SeekBar gameSpeedSeekbar;
    TextView gameSpeedTextView;
    SeekBar platformSpeedSeekbar;
    TextView platformSpeedTextView;
    SeekBar livesNumberSeekbar;
    TextView livesNumberTextView;
    SeekBar platformSizeSeekbar;
    TextView platformSizeTextView;
    TextView brickColumnNumberTextView;
    SeekBar brickColumnNumberSeekbar;
    TextView brickRowNumberTextView;
    SeekBar brickRowNumberSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        gameSpeedTextView = (TextView)findViewById(R.id.gameSpeedTextView);
        gameSpeedTextView.setText("Game speed: 2");
        gameSpeedSeekbar = (SeekBar)findViewById(R.id.gameSpeedSeekBar);
        gameSpeedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            // When the progress value has changed
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                progress += 1;
                                GameVariables.ballHorizontalSpeed = progress * GameVariables.ballVerticalMaxSpeed * 0.25f;
                                GameVariables.ballVerticalSpeed = progress * GameVariables.ballHorizontalMaxSpeed * 0.25f;
                                gameSpeedTextView.setText("Game speed: " + progress);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {}

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {}
                        });
        platformSpeedTextView = (TextView) findViewById(R.id.platformSpeedTextView);
        platformSpeedTextView.setText("Platform speed: 2");
        platformSpeedSeekbar = (SeekBar) findViewById(R.id.platformSpeedSeekbar);
        platformSpeedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 1;
                platformSpeedTextView.setText("Platform speed: " + progress);
                GameVariables.paddleSpeed = progress * 0.25f * GameVariables.paddleSpeedMax;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        livesNumberTextView = (TextView) findViewById(R.id.livesNumberTextView);
        livesNumberTextView.setText("Lives number: 3");
        livesNumberSeekbar = (SeekBar) findViewById(R.id.livesNumberSeekbar);
        livesNumberSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 1;
                GameVariables.livesNumber = progress;
                livesNumberTextView.setText("Lives number: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        platformSizeTextView = (TextView) findViewById(R.id.platformSizeTextView);
        platformSizeTextView.setText("Platform size: 2");
        platformSizeSeekbar = (SeekBar) findViewById(R.id.platformSizeSeekbar);
        platformSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 1;
                GameVariables.paddleLength = GameVariables.paddleLengthMax * progress * 0.25f;
                platformSizeTextView.setText("Platform size: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        brickColumnNumberTextView = (TextView) findViewById(R.id.brickColumnNumberTextView);
        brickColumnNumberTextView.setText("Brick column number: 8");
        brickColumnNumberSeekbar = (SeekBar) findViewById(R.id.brickColumnNumberSeekbar);
        brickColumnNumberSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 1;
                GameVariables.bricksNumberInColumns = progress;
                brickColumnNumberTextView.setText("Brick column number: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        brickRowNumberTextView = (TextView) findViewById(R.id.brickRowNumberTextView);
        brickRowNumberTextView.setText("Brick row number: 3");
        brickRowNumberSeekbar = (SeekBar) findViewById(R.id.brickRowNumberSeekbar);
        brickRowNumberSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 1;
                GameVariables.bricksNumberInRows = progress;
                brickRowNumberTextView.setText("Brick row number: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
