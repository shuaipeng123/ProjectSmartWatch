package com.health_e;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anikakht on 11/17/2017.
 */

@IgnoreExtraProperties
public class DataRecord {
    public String userId;
    public int index;
    public String temperature;
    public String bloodPressure;
    public String heartRate;
    public String stepCnt;
//        public Calendar date;

    public DataRecord() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public DataRecord(String userId, int index, String temperature, String bloodPressure, String heartRate,
                      String stepCnt) {
        this.userId = userId;
        this.index = index;
        this.temperature = temperature;
        this.bloodPressure = bloodPressure;
        this.heartRate = heartRate;
        this.stepCnt = stepCnt;
//            this.date = date;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("index", index);
        result.put("temperature", temperature);
        result.put("bloodPressure", bloodPressure);
        result.put("heartRate", heartRate);
        result.put("stepCnt", stepCnt);
//            result.put("date", date);
        return result;
    }
}
