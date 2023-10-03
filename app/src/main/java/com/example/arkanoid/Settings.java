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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        gameSpeedTextView = (TextView)findViewById(R.id.gameSpeedTextView);
        gameSpeedSeekbar = (SeekBar)findViewById(R.id.gameSpeedSeekBar);
        gameSpeedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            // When the progress value has changed
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                int stepSize = 25;
                                progress = (progress/stepSize)*stepSize;
                                seekBar.setProgress(progress);
                                gameSpeedTextView.setText("Game speed: " + progress);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {}

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {}
                        });
        platformSpeedTextView = (TextView) findViewById(R.id.platformSpeedTextView);
        platformSpeedSeekbar = (SeekBar) findViewById(R.id.platformSpeedSeekbar);
        platformSpeedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int stepSize = 25;
                progress = (progress/stepSize)*stepSize;
                seekBar.setProgress(progress);
                platformSpeedTextView.setText("Platform speed: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        livesNumberTextView = (TextView) findViewById(R.id.livesNumberTextView);
        livesNumberSeekbar = (SeekBar) findViewById(R.id.livesNumberSeekbar);
        livesNumberSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int stepSize = 1;
                progress = (progress/stepSize)*stepSize;
                seekBar.setProgress(progress);
                livesNumberTextView.setText("Lives number: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        platformSizeTextView = (TextView) findViewById(R.id.platformSizeTextView);
        platformSizeSeekbar = (SeekBar) findViewById(R.id.platformSizeSeekbar);
        platformSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int stepSize = 25;
                progress = (progress/stepSize)*stepSize;
                seekBar.setProgress(progress);
                platformSizeTextView.setText("Platform size: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
