package com.example.max.rcthrowmqtt;

import android.content.Intent;

import android.graphics.Typeface;
import android.os.CountDownTimer;

import android.os.Bundle;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;

public class CatsProgress extends MainActivity {

    TextView lastFeeding, distance, firstFeeder, high;
    String clientId;
    Button startTrainingNow;
    static MqttConnectOptions options;

    int counter;
    int specialCounter=0;
    static int sumD;
    static int sumH;

    static int result;
    static int hresult;
    ArrayList<String> myArray = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cats_progress);
        setTitle(R.string.app_name_for_CatsProgress);

        lastFeeding = (TextView) findViewById(R.id.textView18);
        startTrainingNow = (Button)findViewById(R.id.button5);
        startTrainingNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startTrainingNOW();
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        firstFeeder = (TextView) findViewById(R.id.textView19);
        high = (TextView) findViewById(R.id.textView21);
        distance = (TextView)findViewById(R.id.textView13);

        client.setCallback(new MqttCallback() {


            @Override
            public void connectionLost(Throwable cause) {

            }

            //                            int i=0;
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                int number = 0;
                int number2 = 0;
                int number3 = 0;

                try {
                    number = Integer.parseInt(SettingsActivity.distanceText);
                    number2 = Integer.parseInt(SettingsActivity.masterFeedHighText);
                    number3 = Integer.parseInt(SettingsActivity.slaveFeedHighText);
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }

                if(topic.equals("/settings/feedTopic")){
                    String messagee;
                    messagee = new String(message.getPayload());
                for (String retval : messagee.split(" ")) {
                    myArray.add(retval);
                 }

                    int k1 = Integer.parseInt(myArray.get(2));
                    int k2 = Integer.parseInt(myArray.get(3));
                    lastFeeding.setText(myArray.get(0));
                    firstFeeder.setText("выдано корма: "+myArray.get(1)+"г");
                    result = number *(k1+k2-1);
                    hresult = k1*number2 + k2*number3;
                    distance.setText("пробежка: "+result+"м");
                    high.setText("восхождение: " +hresult+"м");
                    Toast toast = Toast.makeText(getApplicationContext(), "Counter: "+counter, Toast.LENGTH_LONG);
                    toast.show();
//                    if(counter == 3){
//
//                        ReportActivity.massivD[specialCounter]=sumD;
//                        ReportActivity.massivH[specialCounter]=sumH;
//                        for(int i = specialCounter+1; i<7; i++){
//                            ReportActivity.massivD[i]=0;
//                            ReportActivity.massivH[i]=0;
//
//                        }
//                        sumD=0;
//                        sumH=0;
//                        Toast toast1 = Toast.makeText(getApplicationContext(), "Got it !!!", Toast.LENGTH_LONG);
//                        toast1.show();
//                        specialCounter++;
//                        counter = 0;
//                    }
//
//                    sumD+=result;
//                    sumH+=hresult;
//                    counter++;
//

                            myArray.clear();



}

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        clientId = MqttClient.generateClientId();
        options = new MqttConnectOptions();

    }


    public void starting() {
        client = new MqttAndroidClient(this.getApplicationContext(), MainActivity.topic, clientId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maincatsprogress, menu);
        return true;
    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//         получим идентификатор выбранного пункта меню
        int id = item.getItemId();



//         Операции для выбранного пункта меню
        switch (id) {
            case R.id.item:
                Intent intent = new Intent(CatsProgress.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.connecting:
                Intent intent0 = new Intent(CatsProgress.this, MainActivity.class);
                startActivity(intent0);
                return true;
            case R.id.action_report:
                Intent intent2 = new Intent(CatsProgress.this, ReportActivity.class);
                startActivity(intent2);
                return true;
            case R.id.action_settings:
                Intent intent3 = new Intent(CatsProgress.this, ManualControl.class);
                startActivity(intent3);
                return true;
            case R.id.logFile:
                Intent intent4 = new Intent(CatsProgress.this, LOG.class);
                startActivity(intent4);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }




}
