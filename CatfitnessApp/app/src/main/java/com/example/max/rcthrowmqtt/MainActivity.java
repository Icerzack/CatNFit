package com.example.max.rcthrowmqtt;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    NotificationManager notificationManager;

    boolean flag = false;

    static MqttAndroidClient client;
    static EditText editText;
    static EditText name;
    static EditText pass;
    static Button button;
    static TextView textView,nameT,passT,status;

    private static final String TAG = "ResultOfPaho: ";
    static MqttConnectOptions options;
    String[] data = new String[10];


    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_SEVERADDRESS = "ADDRESS";
    public static final String APP_PREFERENCES_NAMESERVER = "NAMESERVER";
    public static final String APP_PREFERENCES_PASSSERVER = "PASSSERVER";
    public static final String APP_PREFERENCES_ONEDOSEWEIGHT = "ONEDOSEWEIGHT";
    public static final String APP_PREFERENCES_SESSIONWEIGHT= "SESSIONWEIGHT";
    public static SharedPreferences mSettings;

    static String clientId;
    String serverName;
    static String textName;
    static String textPass;
    static String topic;
    static String sessionWeight,oneDoseWeight;

    FileInputStream fin;
    FileOutputStream fos;


     static ArrayList<String>dataList=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name_for_MainActivity);
        SharedPreferences sp = getSharedPreferences("MY_SETTINGS", Context.MODE_PRIVATE);
        boolean hasVisited = sp.getBoolean("hasVisited", false);

//        if (!hasVisited) {
//            SharedPreferences.Editor e = sp.edit();
//            e.putBoolean("hasVisited", true);
//            e.apply();
//
//        }
//        else{
//           Connect();
//        }

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        //Initializing//
        editText = (EditText)findViewById(R.id.editText2);
        button = (Button)findViewById(R.id.button);
        textView = (TextView)findViewById(R.id.textView3);
        name=(EditText)findViewById(R.id.editText3);
        pass=(EditText)findViewById(R.id.editText4);
        status=(TextView)findViewById(R.id.textView4);

        //Initializing//
        options = new MqttConnectOptions();
        clientId = MqttClient.generateClientId();
        //Listeners//

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Connect();
                }
                catch (Exception e){
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_LONG);
                    toast1.show();
                }

            }
        });
//        saveButton.setOnClickListener(new ShowCatsResult());
        //Listeners//



    }

    public  void starting() {
        client = new MqttAndroidClient(this.getApplicationContext(), editText.getText().toString(), clientId);
            }

    public static void options(){
    options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
    options.setUserName(name.getText().toString());
    options.setPassword(pass.getText().toString().toCharArray());

    }

    @Override
    protected void onPause() {
        notifications();
        super.onPause();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_SEVERADDRESS, editText.getText().toString());
        editor.putString(APP_PREFERENCES_NAMESERVER, name.getText().toString());
        editor.putString(APP_PREFERENCES_PASSSERVER, pass.getText().toString());
        editor.apply();
    }

    void writeFileSD() {
        try {

            fos = openFileOutput("content.txt", MODE_PRIVATE);
            fos.write(data[1].getBytes());
            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
        }
        catch(IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSettings.contains(APP_PREFERENCES_SEVERADDRESS)) {
            editText.setText(mSettings.getString(APP_PREFERENCES_SEVERADDRESS, ""));

        }
        if (mSettings.contains(APP_PREFERENCES_NAMESERVER)) {
            name.setText(mSettings.getString(APP_PREFERENCES_NAMESERVER, ""));

        }
        if (mSettings.contains(APP_PREFERENCES_PASSSERVER)) {
            pass.setText(mSettings.getString(APP_PREFERENCES_PASSSERVER, ""));

        }

    }

    class Settings implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (SettingsActivity.firstVisited) {

                SettingsActivity.sessionWText = SettingsActivity.mSettings.getString(SettingsActivity.APP_PREFERENCES_SESSIONWEIGHT, "");
                SettingsActivity.oneDWText = SettingsActivity.mSettings.getString(SettingsActivity.APP_PREFERENCES_ONEDOSEWEIGHT, "");

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                if (SettingsActivity.firstVisited){Toast toast = Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT);
                    toast.show();}

            }
            else  {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                Toast toast = Toast.makeText(getApplicationContext(), "Васяяяя я в else", Toast.LENGTH_SHORT);
                toast.show();
            try {
                SettingsActivity.sessionWText = dataList.get(0);
                SettingsActivity.oneDWText = dataList.get(1);
                }
                catch (Exception e){
                    Toast toast1 = Toast.makeText(getApplicationContext(), "catch сработал", Toast.LENGTH_LONG);
                    toast1.show();
                }
                SettingsActivity.firstVisited = true;
            }
        }
    }

    void publish() throws UnsupportedEncodingException, MqttException {


                    String topic="/settings/sessionWeight";
                    String topic2="/settings/oneDoseWeight";
                    String topic3="/settings/distance";
                    String topic4="/settings/masterHigh";
                    String topic5="/settings/slaveHigh";
                    String topic6="/settings/delay";

//                    String topic6="/settings/firstHigh";
//                    String topic7="/settings/secondHigh";

                    String message = SettingsActivity.sessionWText;
                    String message2 = SettingsActivity.oneDWText;
                    String message3 = SettingsActivity.distanceText;
                    String message4 = SettingsActivity.masterFeedHighText;
                    String message5 = SettingsActivity.slaveFeedHighText;
                    String message6 = SettingsActivity.delay;

//                    String message6 = SettingsActivity.masterFeedHighText;
//                    String message7 = SettingsActivity.slaveFeedHighText;


                    byte[] encodedPayload  = new byte[0];
                    byte[] encodedPayload2 = new byte[0];
                    byte[] encodedPayload3 = new byte[0];
                    byte[] encodedPayload4 = new byte[0];
                    byte[] encodedPayload5 = new byte[0];
                    byte[] encodedPayload6 = new byte[0];
//                    byte[] encodedPayload6 = new byte[0];
//                    byte[] encodedPayload7 = new byte[0];

                        encodedPayload  = message.getBytes("UTF-8");
                        encodedPayload2 = message2.getBytes("UTF-8");
                        encodedPayload3 = message3.getBytes("UTF-8");
                        encodedPayload4 = message4.getBytes("UTF-8");
                        encodedPayload5 = message5.getBytes("UTF-8");
                        encodedPayload6 = message6.getBytes("UTF-8");
//                        encodedPayload6 = message6.getBytes("UTF-8");
//                        encodedPayload7 = message7.getBytes("UTF-8");

                        MqttMessage mmessage = new MqttMessage(encodedPayload);
                        MqttMessage mmessage2 = new MqttMessage(encodedPayload2);
                        MqttMessage mmessage3 = new MqttMessage(encodedPayload3);
                        MqttMessage mmessage4 = new MqttMessage(encodedPayload4);
                        MqttMessage mmessage5 = new MqttMessage(encodedPayload5);
                        MqttMessage mmessage6 = new MqttMessage(encodedPayload6);
//                        MqttMessage mmessage6 = new MqttMessage(encodedPayload6);
//                        MqttMessage mmessage7 = new MqttMessage(encodedPayload7);

                        client.publish(topic, mmessage);
                        client.publish(topic2, mmessage2);
                        client.publish(topic3, mmessage3);
                        client.publish(topic4, mmessage4);
                        client.publish(topic5, mmessage5);
                        client.publish(topic6, mmessage6);
//                        client.publish(topic6, mmessage6);
//                        client.publish(topic7, mmessage7);



    }

    void publishCall1()throws UnsupportedEncodingException, MqttException{
        String topic="m";
        String message = "3";
        byte[] encodedPayload  = new byte[0];
        encodedPayload  = message.getBytes("UTF-8");
        MqttMessage mmessage = new MqttMessage(encodedPayload);
        client.publish(topic, mmessage);

    }

    void publishFeed1()throws UnsupportedEncodingException, MqttException{

        String topic2="m";
        String message2 = "1";
        byte[] encodedPayload2 = new byte[0];
        encodedPayload2 = message2.getBytes("UTF-8");
        MqttMessage mmessage2 = new MqttMessage(encodedPayload2);
        client.publish(topic2, mmessage2);

    }

    void publishStop1()throws UnsupportedEncodingException, MqttException{
        String topic3="m";
        String message3 = "2";
        byte[] encodedPayload3 = new byte[0];
        encodedPayload3 = message3.getBytes("UTF-8");
        MqttMessage mmessage3 = new MqttMessage(encodedPayload3);
        client.publish(topic3, mmessage3);
    }

    void publishCall2()throws UnsupportedEncodingException, MqttException{

        String topic4="s";
        String message4 = "3";
        byte[] encodedPayload4 = new byte[0];
        encodedPayload4 = message4.getBytes("UTF-8");
        MqttMessage mmessage4 = new MqttMessage(encodedPayload4);
        client.publish(topic4, mmessage4);
    }

    void publishFeed2()throws UnsupportedEncodingException, MqttException{

        String topic5="s";
        String message5 = "1";
        byte[] encodedPayload5 = new byte[0];
        encodedPayload5 = message5.getBytes("UTF-8");
        MqttMessage mmessage5 = new MqttMessage(encodedPayload5);
        client.publish(topic5, mmessage5);

    }

    void publishStop2()throws UnsupportedEncodingException, MqttException{

        String topic6="s";
        String message6 = "2";
        byte[] encodedPayload6 = new byte[0];
        encodedPayload6 = message6.getBytes("UTF-8");
        MqttMessage mmessage6 = new MqttMessage(encodedPayload6);
        client.publish(topic6, mmessage6);
    }

    void publish1Time()throws UnsupportedEncodingException, MqttException{
        String topic3="/settings/firstTimeFeeding";
        String message3 = SettingsActivity.firstTimeText;
        byte[] encodedPayload3 = new byte[0];
        encodedPayload3 = message3.getBytes("UTF-8");
        MqttMessage mmessage3 = new MqttMessage(encodedPayload3);
        client.publish(topic3, mmessage3);
    }

    void publish2Time()throws UnsupportedEncodingException, MqttException{
        String topic4="/settings/secondTimeFeeding";
        String message4 = SettingsActivity.secondTimeText;
        byte[] encodedPayload4 = new byte[0];
        encodedPayload4 = message4.getBytes("UTF-8");
        MqttMessage mmessage4 = new MqttMessage(encodedPayload4);
        client.publish(topic4, mmessage4);
    }

    void publish3Time()throws UnsupportedEncodingException, MqttException{
        String topic5="/settings/thirdTimeFeeding";
        String message5 = SettingsActivity.thirdTimeText;
        byte[] encodedPayload5 = new byte[0];
        encodedPayload5 = message5.getBytes("UTF-8");
        MqttMessage mmessage5 = new MqttMessage(encodedPayload5);
        client.publish(topic5, mmessage5);
    }

    void publishStopCall1() throws UnsupportedEncodingException, MqttException{
        String topic5="m";
        String message5 = "4";
        byte[] encodedPayload5 = new byte[0];
        encodedPayload5 = message5.getBytes("UTF-8");
        MqttMessage mmessage5 = new MqttMessage(encodedPayload5);
        client.publish(topic5, mmessage5);
    }

    void publishStopCall2() throws UnsupportedEncodingException, MqttException{
        String topic5="s";
        String message5 = "4";
        byte[] encodedPayload5 = new byte[0];
        encodedPayload5 = message5.getBytes("UTF-8");
        MqttMessage mmessage5 = new MqttMessage(encodedPayload5);
        client.publish(topic5, mmessage5);
    }

    void startTrainingNOW()throws UnsupportedEncodingException, MqttException{
        String topic5="manual";
        String message5 = "1";
        byte[] encodedPayload5 = new byte[0];
        encodedPayload5 = message5.getBytes("UTF-8");
        MqttMessage mmessage5 = new MqttMessage(encodedPayload5);
        client.publish(topic5, mmessage5);
    }

    void notifications(){
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.sync)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.mqtt))
                .setTicker("Подключились")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Статус: Подключено")
                .setContentText("Вы подключены к MQTT-серверу")
                .setPriority(Notification.PRIORITY_HIGH);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Notification notification = builder.build();
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();



        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.item:
                if (SettingsActivity.firstVisited) {

                    SettingsActivity.sessionWText = SettingsActivity.mSettings.getString(SettingsActivity.APP_PREFERENCES_SESSIONWEIGHT, "");
                    SettingsActivity.oneDWText = SettingsActivity.mSettings.getString(SettingsActivity.APP_PREFERENCES_ONEDOSEWEIGHT, "");
                    SettingsActivity.firstTimeText = SettingsActivity.mSettings.getString(SettingsActivity.APP_PREFERENCES_FIRSTTIMEFEEDING, "");
                    SettingsActivity.secondTimeText = SettingsActivity.mSettings.getString(SettingsActivity.APP_PREFERENCES_SECONDTIMEFEEDING, "");
                    SettingsActivity.thirdTimeText = SettingsActivity.mSettings.getString(SettingsActivity.APP_PREFERENCES_THIRDTIMEFEEDING, "");
                    SettingsActivity.masterFeedHighText = SettingsActivity.mSettings.getString(SettingsActivity.APP_PREFERENCES_user, "");
                    SettingsActivity.slaveFeedHighText = SettingsActivity.mSettings.getString(SettingsActivity.APP_PREFERENCES_PASS, "");

                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);

                }
                else {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    SettingsActivity.firstVisited=true;
                }
                return true;
            case R.id.action_otcht:
                Intent intent2 = new Intent(MainActivity.this, CatsProgress.class);
                startActivity(intent2);
                return true;
            case R.id.action_settings:
                Intent intent3 = new Intent(MainActivity.this, ManualControl.class);
                startActivity(intent3);
                return true;
            case R.id.instruction:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Инструкция к тренажеру.")
                        .setMessage("1.Включите ведущую кормушку.\n\n" +
                                "2.Включите ведомую кормушку.\n\n" +
                                "3.С помощью компьютера или смартфона подключитесь к точке доступа \"Master.\"\n\n" +
                                "3.Используя браузер своего усройства перейдите по адресу 192.168.4.1 .\n\n" +
                                "4.Заполните поля формы, для этого укажите название и пароль своей домашней Wi-Fi сети, зарегистрируйтесь на сервере MQTT, и укажите название сервера и порт, имя пользователя и пароль.\n\n" +
                                "5.Нажмите кнопку применить настройки. Устройство перезагрузится автоматически и через вашу домащнюю сеть подключиться к узазанному серверу MQTT. Они уже готовы тренировать вашего питомца по расписанию в 10:00, 15:00 и 20:00 с настройками по умолчанию.\n\n" +
                                "6.Для изменения настроек тренажера, получения информации о тренировках, о событиях, которые происходят с элементами тренажера, вам необходимо в этом приложении указать те же настройки которые вы применяли на ведущей кормушке.\n\n" +
                                "Желаем вашему питомцу продуктивных тренировок и здоровья !")
                        .setIcon(R.drawable.icon)
                        .setCancelable(false)
                        .setNegativeButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.about:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("О программе")
                        .setMessage("\"Тренажер для домашних кошек Cat&Fit\" разработан Аленой и Максимом Кузнецовыми, учащимися 6-го и  8-го классов СОШ № 35, ГБОУ МАН г. Севастополя. \n\nwww.cat-n-fit.com\n\nmaxalkuz@gmail.com ")
                        .setIcon(R.drawable.info)
                        .setCancelable(false)
                        .setNegativeButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert1 = builder1.create();
                alert1.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    void Connect(){

    starting();

    try {
        options();
        IMqttToken token = client.connect(options);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                status.setText("Статус: Подключено");

                int qos = 2;

                try {
                    client.subscribe("#", qos);
                    client.subscribe("/settings/sessionWeight", qos);
                    client.subscribe("/settings/oneDoseWeight", qos);
                    client.subscribe("/settings/firstTimeFeeding", qos);
                    client.subscribe("/settings/secondTimeFeeding", qos);
                    client.subscribe("/settings/thirdTimeFeeding", qos);
                    client.subscribe("/settings/firstHigh", qos);
                    client.subscribe("/settings/secondHigh", qos);
                    client.subscribe("/settings/feedTopic", qos);

                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Toast toast1 = Toast.makeText(getApplicationContext(), "Подключились", Toast.LENGTH_SHORT);
                toast1.show();
                Log.d(TAG, "onSuccess");
                Toast toast = Toast.makeText(getApplicationContext(), "Подписались", Toast.LENGTH_SHORT);
                toast.show();

                flag = true;
                client.setCallback(new MqttCallback() {

                    @Override
                    public void connectionLost(Throwable cause) {
                        notificationManager.cancel(1);
                    }

                    //                            int i=0;
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {




//                                data[i]=new String((message.getPayload()));
                        dataList.add(new String(message.getPayload()));
//                                sessionWeight=data[0];
//                                oneDoseWeight=data[1];
//                                i++;

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });


            }
            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                // Something went wrong e.g. connection timeout or firewall problems
                Log.d(TAG, "onFailure");
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Не подключились", Toast.LENGTH_SHORT);
                toast.show();

            }
        });
    } catch (MqttException e) {
        e.printStackTrace();
    }
}

    public boolean connected(){
        return flag;
    }
    }


