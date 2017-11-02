package com.health_e;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class Input extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    Model appData;
    String phoneNo;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Input","onCreate");
        setContentView(R.layout.activity_input);
        appData = Model.getInstance(getApplicationContext());

        final EditText temp = (EditText) findViewById(R.id.tempInput);
        final EditText blood = (EditText) findViewById(R.id.bloodInput);

        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp.getText().toString().length() > 0 && blood.getText().toString().length() > 0) {
                    appData.setTemp (Integer.valueOf(temp.getText().toString()));
                    appData.setBP (Integer.valueOf (blood.getText().toString()));
                    appData.setUpdate (Calendar.getInstance());
                    sendSMSMessage();
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Input.this);
                    builder.setTitle ("WARNING")
                            .setMessage("Not all fields have been completed!")
                            .setNeutralButton("Okay", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    protected void sendSMSMessage() {
        Calendar c = Calendar.getInstance();
        int am = c.get(Calendar.AM_PM);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        String AM = ((am == Calendar.AM) ? "AM" : "PM");
        String HOUR = ((hour == 0) ? "12" : Integer.toString (hour));
        String MINUTE = ((minute < 10) ? "0".concat (Integer.toString (minute)) : Integer.toString (minute));

        String phoneNo = appData.getEmerNum();
        String message = appData.getName() + ", age " + appData.getAge() + ", has saved their daily information at "
                + HOUR + ":" + MINUTE + " " + AM + ". \nTemperature: "
                + appData.getTemp() + " C \nBlood pressure: " + appData.getBP() + " mmHg";

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    protected void onPause(){
        super.onPause();
        Log.i("Input", "onPause");
    }
    protected void onStop(){
        super.onStop();
        Log.i("Input", "onStop");
    }

    protected void onDestroy(){
        super.onDestroy();
        Log.i("Input", "onDestroy");
    }
}
