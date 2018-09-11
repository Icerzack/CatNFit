package com.example.max.rcthrowmqtt;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.TimeZone;

public class LOG extends MainActivity {
//    SQLiteDatabase db;
    TextView logText;
    EditText topic, message;
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
//        db = getBaseContext().openOrCreateDatabase("buh.db", MODE_PRIVATE, null);

//        db.execSQL("create table if not exists logBase (time TEXT, topic TEXT, message TEXT)");
//        showList();
        logText = (TextView)findViewById(R.id.textView35);
        setTitle(R.string.app_name_for_LOG);
        logText.setMovementMethod(new ScrollingMovementMethod());

        send = (Button)findViewById(R.id.button4);
        topic = (EditText) findViewById(R.id.editText13);
        message = (EditText) findViewById(R.id.editText17);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic3=topic.getText().toString();
                String message3 = message.getText().toString();
                byte[] encodedPayload3 = new byte[0];
                try {
                    encodedPayload3 = message3.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MqttMessage mmessage3 = new MqttMessage(encodedPayload3);
                try {
                    client.publish(topic3, mmessage3);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            //                            int i=0;
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                db = getBaseContext().openOrCreateDatabase("buh.db", MODE_PRIVATE, null);
                Calendar now = Calendar.getInstance( TimeZone.getDefault() );
//                db.execSQL("insert into logBase values ('"+now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE)+":"+now.get(Calendar.SECOND)+"',"+topic+", '"+new String(message.getPayload())+"');");
                logText.append("\n"+now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE)+":"+now.get(Calendar.SECOND)+"         " + topic + "           " + new String(message.getPayload()));
//                db.close();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
//        db.close();
    }
//    private void showList(){
//        Cursor query = db.rawQuery("select * from cost;", null);
//        if(query.moveToFirst()){
//            do{
//                String time = query.getString(0);
//                String topic = query.getString(1);
//                String message = query.getString(2);
//                logText.append(time+"         " + topic + "           " +message+"\n");
//            }
//            while (query.moveToNext());
//
//        }
//        query.close();
//    }
}
