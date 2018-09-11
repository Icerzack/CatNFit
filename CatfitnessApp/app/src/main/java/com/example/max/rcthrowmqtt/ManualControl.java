package com.example.max.rcthrowmqtt;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class ManualControl extends MainActivity {
    RadioButton firstFeed, secondFeed;
    int feederAtTheMoment;
    int callerAtTheMoment;
    Button feed, call;
//Button call1, call2;Button feed1, feed2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        setTitle(R.string.app_name_for_ManualControl);
        call = (Button)findViewById(R.id.button13);
        feed = (Button) findViewById(R.id.button12);
        firstFeed = (RadioButton) findViewById(R.id.radioButton3);
        secondFeed = (RadioButton) findViewById(R.id.radioButton4);
        firstFeed.setOnClickListener(radioButtonClickListener);
        secondFeed.setOnClickListener(radioButtonClickListener);

        call.setOnTouchListener(new View.OnTouchListener() {
            Timer timer;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(feederAtTheMoment == 1){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            timer = new Timer();
                            TimerTask timerTask = new TimerTask() {
                                public void run() {
                                    try {
                                        publishCall1();
                                    } catch (UnsupportedEncodingException | MqttException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            timer.schedule(timerTask, 500, 1000);
                            break;
                        case MotionEvent.ACTION_UP:
                            try {
                                publishStopCall1();
                            } catch (UnsupportedEncodingException | MqttException e) {
                                e.printStackTrace();
                            }
                            timer.cancel();
                            break;
                    }
                }
                if(feederAtTheMoment == 2){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            timer = new Timer();
                            TimerTask timerTask = new TimerTask() {
                                public void run() {
                                    try {
                                        publishCall2();
                                    } catch (UnsupportedEncodingException | MqttException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            timer.schedule(timerTask, 500, 1000);
                            break;
                        case MotionEvent.ACTION_UP:
                            try {
                                publishStopCall2();
                            } catch (UnsupportedEncodingException | MqttException e) {
                                e.printStackTrace();
                            }
                            timer.cancel();
                            break;
                    }
                }

                return false;
            }
        });

        feed.setOnTouchListener(new View.OnTouchListener() {
            Timer timer;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(feederAtTheMoment == 1){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            timer = new Timer();
                            TimerTask timerTask = new TimerTask() {
                                public void run() {
                                    try {
                                        publishFeed1();
                                    } catch (UnsupportedEncodingException | MqttException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            timer.schedule(timerTask, 500, 1000);
                            break;
                        case MotionEvent.ACTION_UP:
                            timer.cancel();
                            break;
                    }
                }
                if(feederAtTheMoment == 2){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            timer = new Timer();
                            TimerTask timerTask = new TimerTask() {
                                public void run() {
                                    try {
                                        publishFeed2();
                                    } catch (UnsupportedEncodingException | MqttException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            timer.schedule(timerTask, 500, 1000);
                            break;
                        case MotionEvent.ACTION_UP:
                            timer.cancel();
                            break;
                    }
                }

                return true;
            }
        });
    }
    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton)v;
            switch (rb.getId()) {
                case R.id.radioButton3:
                    feederAtTheMoment = 1;
                    callerAtTheMoment = 1;
                    secondFeed.setChecked(false);
                    break;
                case R.id.radioButton4:
                    feederAtTheMoment = 2;
                    callerAtTheMoment = 2;
                    firstFeed.setChecked(false);
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Пожалуйста, укажите кормушку!", Toast.LENGTH_SHORT);
            }
        }
    };
}
