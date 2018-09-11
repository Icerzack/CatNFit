package com.example.max.rcthrowmqtt;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class SettingsActivity extends MainActivity {

    static String sessionWText="50", oneDWText="1", firstTimeText="10:00", secondTimeText="15:00", thirdTimeText="20:00", masterFeedHighText="1", slaveFeedHighText="1",delay="3";
    static String firstTimeHour, firstTimeMinute, secondTimeHour, secondTimeMinute, thirdTimeHour,thirdTimeMinute;
    static boolean firstVisited = false;
    static String distanceText;
    MainActivity mainActivity = new MainActivity();
    MainActivity mainActivity2 = new MainActivity();
    public static final String APP_PREFERENCES = "myOptions";
    public static final String APP_PREFERENCES_SESSIONWEIGHT = "SessionWeight";
    public static final String APP_PREFERENCES_ONEDOSEWEIGHT = "OneDoseWeight";
    public static final String APP_PREFERENCES_FIRSTTIMEFEEDING = "FirstTimeFeeding";
    public static final String APP_PREFERENCES_SECONDTIMEFEEDING = "SecondTimeFeeding";
    public static final String APP_PREFERENCES_THIRDTIMEFEEDING = "ThirdTimeFeeding";
    public static final String APP_PREFERENCES_user = "user";
    public static final String APP_PREFERENCES_PASS = "password";
    public static final String APP_PREFERENCES_DELAYFEED = "DelayBetweenFeed";
    public static final String APP_PREFERENCES_DISTANCE = "Distance";


    static SharedPreferences mSettings;
    TimePickerDialog timePickerDialog;
    EditText server;
    EditText sessionW;
    EditText oneDW;
    EditText firstTimeFeeding;
    EditText secondTimeFeeding;
    EditText thirdTimeFeeding;
    EditText user;
    EditText password;
    static EditText delayPodziv;
    EditText distance;
    TextView a;
    Button save, reconnect, timerButton;
    long mLastClickTime = 0;
    boolean flag = true;
    int hour, minutee;
    int sessionWInt;
    int oneDWInt;
    int delayInt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.app_name_for_SettingsActivity);
        timerButton=(Button)findViewById(R.id.timerButton);
        ListView listView = (ListView) findViewById(R.id.list);
        final ArrayList<String> timers = new ArrayList<>();
        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, timers);
        listView.setAdapter(adapter);
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePickerDialog(true);
                timers.add(0, hour+":"+minutee);
                adapter.notifyDataSetChanged();
            }
        });
        distance = (EditText)findViewById(R.id.editText9) ;
        mSettings = getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
        server = (EditText)findViewById(R.id.editText12);
        server.setText(MainActivity.mSettings.getString(MainActivity.APP_PREFERENCES_SEVERADDRESS, ""));
//        name = (EditText)findViewById(R.id.editText14);
//        pass = (EditText)findViewById(R.id.editText15);
        firstTimeFeeding = (EditText) findViewById(R.id.editText6);
        secondTimeFeeding =(EditText)findViewById(R.id.editText7);
        thirdTimeFeeding =(EditText)findViewById(R.id.editText8);
        user = (EditText)findViewById(R.id.editText14);
        user.setText(MainActivity.mSettings.getString(MainActivity.APP_PREFERENCES_NAMESERVER, ""));
        password = (EditText)findViewById(R.id.editText15);
        password.setText(MainActivity.mSettings.getString(MainActivity.APP_PREFERENCES_PASSSERVER, ""));
        reconnect = (Button)findViewById(R.id.button8);
        delayPodziv = (EditText)findViewById(R.id.editText16);
        delayPodziv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                try {
                    delayInt = Integer.parseInt(delayPodziv.getText().toString());
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
                // обрабатываем нажатие кнопки поиска
                if (delayInt>30 || delayInt<1) {
                    delayPodziv.setText("7");
                }

                return false;
            }
        });
        sessionW = (EditText)findViewById(R.id.editText);
        sessionW.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                try {
                    sessionWInt = Integer.parseInt(sessionW.getText().toString());
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
                    // обрабатываем нажатие кнопки поиска
                    if (sessionWInt>250 || sessionWInt<30) {
                        sessionW.setText("50");
                    }

                return false;
            }
        });
        oneDW = (EditText)findViewById(R.id.editText5);
        oneDW.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                try {
                    oneDWInt = Integer.parseInt(oneDW.getText().toString());
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
                // обрабатываем нажатие кнопки поиска
                if (oneDWInt>5 || oneDWInt<1) {
                    oneDW.setText("1");
                }

                return false;
            }
        });

        reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this , MainActivity.class));
            }
        });

        Calendar now = Calendar.getInstance( TimeZone.getDefault() );
        final int hour = now.get(Calendar.HOUR_OF_DAY);
        final int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);
        save = (Button)findViewById(R.id.button3);
        save.setOnClickListener(new SaveAndPublish());
        if (MainActivity.mSettings.contains(MainActivity.APP_PREFERENCES_SEVERADDRESS)) {
            MainActivity.editText.setText(MainActivity.mSettings.getString(MainActivity.APP_PREFERENCES_SEVERADDRESS, ""));

        }
        if (MainActivity.mSettings.contains(MainActivity.APP_PREFERENCES_NAMESERVER)) {
            MainActivity.name.setText(MainActivity.mSettings.getString(MainActivity.APP_PREFERENCES_NAMESERVER, ""));

        }
        if (MainActivity.mSettings.contains(MainActivity.APP_PREFERENCES_PASSSERVER)) {
            MainActivity.pass.setText(MainActivity.mSettings.getString(MainActivity.APP_PREFERENCES_PASSSERVER, ""));

        }
        Connect();
        firstTimeFeeding.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    firstTimeFeeding.setText(selectedHour + ":" + selectedMinute);
                    firstTimeText = firstTimeFeeding.getText().toString();

                    try {
                        publish1Time();
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                    firstTimeHour = ""+selectedHour;
                    firstTimeMinute = ""+selectedMinute;



                }
            }, hour, minute, true);

            mTimePicker.setTitle("Выберите время");
            mTimePicker.show();
        }
        });

        secondTimeFeeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        secondTimeFeeding.setText( selectedHour + ":" + selectedMinute);
                        secondTimeHour = ""+selectedHour;
                        secondTimeMinute = ""+selectedMinute;
                        secondTimeText = secondTimeFeeding.getText().toString();

                        try {
                            publish2Time();
                        } catch (UnsupportedEncodingException | MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Выберите время");
                mTimePicker.show();
            }
        });
        thirdTimeFeeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        thirdTimeFeeding.setText( selectedHour + ":" + selectedMinute);
                        thirdTimeHour = ""+selectedHour;
                        thirdTimeMinute = ""+selectedMinute;
                        thirdTimeText = thirdTimeFeeding.getText().toString();

                        try {
                            publish3Time();
                        } catch (UnsupportedEncodingException | MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Выберите время");
                mTimePicker.show();
            }
        });



//        if (MainActivity.mSettings.contains(MainActivity.APP_PREFERENCES_SEVERADDRESS)) {
//            server.setText(MainActivity.mSettings.getString(MainActivity.APP_PREFERENCES_SEVERADDRESS, ""));
//
//        }
//        if (MainActivity.mSettings.contains(MainActivity.APP_PREFERENCES_NAMESERVER)) {
//            name.setText(MainActivity.mSettings.getString(MainActivity.APP_PREFERENCES_NAMESERVER, ""));
//
//        }
//        if (MainActivity.mSettings.contains(MainActivity.APP_PREFERENCES_PASSSERVER)) {
//            pass.setText(MainActivity.mSettings.getString(MainActivity.APP_PREFERENCES_PASSSERVER, ""));
//
//        }
        sessionW.setText(sessionWText);
        oneDW.setText(oneDWText);
        firstTimeFeeding.setText(firstTimeText);
        secondTimeFeeding.setText(secondTimeText);
        thirdTimeFeeding.setText(thirdTimeText);
        user.setText(masterFeedHighText);
        password.setText(slaveFeedHighText);
        delayPodziv.setText(delay);
        distance.setText(distanceText);



}
    public void openTimePickerDialog(boolean is24r){
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(SettingsActivity.this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setCanceledOnTouchOutside(true);
        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timePickerDialog.cancel();

            }
        });
        timePickerDialog.setTitle("Set Alarm Time");
        timePickerDialog.show();
    }
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calNow = Calendar.getInstance();
            hour = hourOfDay;
            minutee=minute;
            Calendar calSet = (Calendar) calNow.clone();
            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);
            if(calSet.compareTo(calNow) <= 0){
                calSet.add(Calendar.DATE, 1);
            }

        }};
    class SaveAndPublish implements View.OnClickListener{


        @Override
        public void onClick(View view) {

            sessionWText = sessionW.getText().toString();
            oneDWText = oneDW.getText().toString();
            firstTimeText = firstTimeFeeding.getText().toString();
            secondTimeText = secondTimeFeeding.getText().toString();
            thirdTimeText = thirdTimeFeeding.getText().toString();
            masterFeedHighText = user.getText().toString();
            slaveFeedHighText = password.getText().toString();
            delay = delayPodziv.getText().toString();
            distanceText = distance.getText().toString();

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(APP_PREFERENCES_SESSIONWEIGHT, sessionWText);
            editor.putString(APP_PREFERENCES_ONEDOSEWEIGHT, oneDWText);
            editor.putString(APP_PREFERENCES_FIRSTTIMEFEEDING, firstTimeText);
            editor.putString(APP_PREFERENCES_SECONDTIMEFEEDING, secondTimeText);
            editor.putString(APP_PREFERENCES_THIRDTIMEFEEDING, thirdTimeText);
            editor.putString(APP_PREFERENCES_user, masterFeedHighText);
            editor.putString(APP_PREFERENCES_PASS, slaveFeedHighText);
            editor.putString(APP_PREFERENCES_DELAYFEED, delay);
            editor.putString(APP_PREFERENCES_DISTANCE, distanceText);
            editor.apply();
            Toast toast = Toast.makeText(getApplicationContext(), "Сохранены :"+
                    "\n"+ mSettings.getString(APP_PREFERENCES_SESSIONWEIGHT, "")+
                    "\n"+ mSettings.getString(APP_PREFERENCES_ONEDOSEWEIGHT, "")+
                    "\n"+ mSettings.getString(APP_PREFERENCES_FIRSTTIMEFEEDING, "")+
                    "\n"+ mSettings.getString(APP_PREFERENCES_SECONDTIMEFEEDING,"")+
                    "\n"+ mSettings.getString(APP_PREFERENCES_THIRDTIMEFEEDING, "")+
                    "\n"+ mSettings.getString(APP_PREFERENCES_user, "")+
                    "\n"+ mSettings.getString(APP_PREFERENCES_PASS, "")+
                    "\n"+ mSettings.getString(APP_PREFERENCES_DELAYFEED, "")+
                    "\n"+ mSettings.getString(APP_PREFERENCES_DISTANCE, ""), Toast.LENGTH_LONG);
            toast.show();
            SettingsActivity.this.finish();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);

            try {
                mainActivity.publish();
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }


        }
    }


}
