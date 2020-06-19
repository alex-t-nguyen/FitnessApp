package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fitnessapp.Introduction.IntroActivity;
import com.example.fitnessapp.Login.MainActivity;

import static java.lang.Thread.sleep;

public class splash extends AppCompatActivity {

    private static final String TAG = "Splash";
    private TextView repeatText, eatText, sleepText, trainText;
    private ImageView iv;
    private Thread timer;
    private boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent i = new Intent(this, IntroActivity.class);
        final Intent splashIntent = getIntent();
        //setContentView(R.layout.activity_splash);
        Log.d(TAG, "onCreate");

        //iv = (ImageView) findViewById(R.id.iv);
        //Animation myanim = AnimationUtils.loadAnimation(this, R.anim.splasher);

        //iv.startAnimation(myanim);
        startActivity(new Intent(splash.this, IntroActivity.class));
        finish();
        /*
            timer = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (splashIntent.getBooleanExtra("Splash", true)) {
                        splashIntent.putExtra("Splash",false);
                        startActivity(i);
                        finish();
                    }
                }
            };
            timer.start();

         */

    }

}