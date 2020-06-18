package com.example.fitnessapp.Introduction;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fitnessapp.R;

import static java.lang.Thread.sleep;

public class splash extends AppCompatActivity {

    private static final String TAG = "Splash";
    private TextView repeatText, eatText, sleepText, trainText;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*
        repeatText = (TextView) findViewById(R.id.repeatText);
        eatText = (TextView) findViewById(R.id.eatText);
        sleepText = (TextView) findViewById(R.id.sleepText);
        trainText = (TextView) findViewById(R.id.trainText);
        */

        iv = (ImageView) findViewById(R.id.iv);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.splasher);

        iv.startAnimation(myanim);
        final Intent i = new Intent(this, IntroActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    Log.d(TAG, "splashScreen: call intent");
                    finish();
                }
            }
        };
        timer.start();
    }
}