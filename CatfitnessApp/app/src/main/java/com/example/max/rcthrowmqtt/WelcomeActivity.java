package com.example.max.rcthrowmqtt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends MainActivity {



    private static final String MY_SETTINGS = "my_settings";
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE);
        boolean hasVisited = sp.getBoolean("hasVisited", false);

        if (!hasVisited) {
            button = (Button) findViewById(R.id.button2);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    MainActivity.ConnectToMQTTBroker connectToMQTTBroker = new MainActivity.ConnectToMQTTBroker();
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            });
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("hasVisited", true);
            e.apply();
        } else {
            setContentView(R.layout.welcome);
            new CountDownTimer(4000, 1000) {
                public void onTick(long millisUntilFinished) {
                    Connect();
                }

                public void onFinish() {
                    Intent intent = new Intent(WelcomeActivity.this, CatsProgress.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
                    finishIt();

                }
            }
                    .start();

        }
    }

    public void finishIt(){
        this.finish();
    }


    }

