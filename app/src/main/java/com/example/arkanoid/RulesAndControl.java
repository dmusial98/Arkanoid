package com.example.arkanoid;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class RulesAndControl extends Activity {

    Button textToSpeechButton;
    TextToSpeech textToSpeech;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_and_control);
        textToSpeechButton = (Button)findViewById(R.id.textToSpeechButton);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        // Adding OnClickListener
        textToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak("Hello, it's your voice",TextToSpeech.QUEUE_FLUSH,null);
            }
        });

    }

}
