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
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Input extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    Model appData;
    String phoneNo;
    String message;
//    private FirebaseAuth auth;
//    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Input","onCreate");
        setContentView(R.layout.activity_input);
        appData = Model.getInstance(getApplicationContext());

//        auth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance().getReference();

        final FirebaseIF firebaseIF = new FirebaseIF();

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
                    if (!FirebaseIF.loggedIn())                    {
                        Toast.makeText(getApplicationContext(), "not logged in!!",
                                Toast.LENGTH_LONG).show();
                    }else{
                        firebaseIF.writeNewRecord(1, Integer.toString(appData.getTemp()),
                                Integer.toString(appData.getBP()), Integer.toString(appData.getHR()),
                                "NA");
                    }
                    // TODO send Msg commented
//                    sendSMSMessage();
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

//    public void writeNewRecord(int index, String temperature, String bloodPressure, String heartRate,
//                                String stepCnt) {
//        // Create new post at /user-posts/$userid/$postid and at
//        // /posts/$postid simultaneously
//        String key = mDatabase.child("records").push().getKey();
//        DataRecord dataRecord = new DataRecord(auth.getCurrentUser().getUid(),index, temperature, bloodPressure, heartRate, stepCnt);
//        Map<String, Object> dataRecordValues = dataRecord.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
////        childUpdates.put("/records/" + key, dataRecordValues);
////        childUpdates.put("/records/" + auth.getCurrentUser().getUid() , dataRecordValues);
//        Toast.makeText(getApplicationContext(), getDate(), Toast.LENGTH_LONG).show();
//        childUpdates.put("/records/" + auth.getCurrentUser().getUid() +"/" + getDate() , dataRecordValues);
//        mDatabase.updateChildren(childUpdates);
//    }

//    private String getDate(){
//        Calendar c = Calendar.getInstance();
//        Integer year = c.get(Calendar.YEAR);
//        Integer month = c.get(Calendar.MONTH);
//        Integer day = c.get(Calendar.DAY_OF_MONTH);
//        Integer HH = c.get(Calendar.HOUR_OF_DAY);
//        Integer MM = c.get(Calendar.MINUTE) +1; //month is zero based
//        Integer SS = c.get(Calendar.SECOND);
//        return year.toString()+"-"+month.toString()+"-"+day.toString() + ","+HH.toString() + ":"+ MM.toString()+ ":"+SS.toString();
//    }
}

