package com.health_e;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anikakht on 11/22/2017.
 */

public class FirebaseIF {
    private DatabaseReference mDatabase;
    private static FirebaseAuth auth;

    public FirebaseIF() {
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static boolean loggedIn() {
        if (auth.getCurrentUser() != null) {
            if (auth.getCurrentUser().getUid() != null) {
                return true;
            }
        }
        return false;
    }

    public void updateUserLocation(String address) {
        if (auth!=null && auth.getCurrentUser()!=null &&
                mDatabase != null){
            mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("locationAddress").setValue(address);
        }
    }


    public void writeNewRecord(int index, String temperature, String bloodPressure, String heartRate,
                               String stepCnt) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        if (auth!=null && auth.getCurrentUser()!=null &&
                mDatabase != null) {
            String key = mDatabase.child("records").push().getKey();
            DataRecord dataRecord = new DataRecord(auth.getCurrentUser().getUid(), index, temperature,
                    bloodPressure, heartRate, stepCnt, getDate());
            Map<String, Object> dataRecordValues = dataRecord.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            //        childUpdates.put("/records/" + key, dataRecordValues);
            //        childUpdates.put("/records/" + auth.getCurrentUser().getUid() , dataRecordValues);
            childUpdates.put("/records/" + auth.getCurrentUser().getUid() + "/" + getTimeStamp(), dataRecordValues);
            mDatabase.updateChildren(childUpdates);
        }
    }

    private String getDate() {
        Calendar c = Calendar.getInstance();
        Integer year = c.get(Calendar.YEAR);
        Integer month = c.get(Calendar.MONTH);
        Integer day = c.get(Calendar.DAY_OF_MONTH);
        Integer HH = c.get(Calendar.HOUR_OF_DAY);
        Integer MM = c.get(Calendar.MINUTE) + 1; //month is zero based
        Integer SS = c.get(Calendar.SECOND);
        return year.toString() + "-" + month.toString() + "-" + day.toString() + "," + HH.toString() + ":" + MM.toString() + ":" + SS.toString();
    }

    private long getTimeStamp() {
        Long tsLong = System.currentTimeMillis()/1000;
//        String ts = tsLong.toString();
        return tsLong;
    }
}