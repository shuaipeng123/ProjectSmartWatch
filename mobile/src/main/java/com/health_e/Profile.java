package com.health_e;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anikakht on 11/16/2017.
 */

@IgnoreExtraProperties
public class Profile {
    public enum UserType{
        PATIENT, PHYSICIAN, FAMILY
    }

    public String userId;
    public String email;
    public String name;
    public UserType userType;
    public String age;
    public String emergName;
    public String emergNum;
    public String physicianId;
    public String familyId;
    public String patientId;
    public String locationAddress;

    public Profile() {
        // Default constructor required for calls to DataSnapshot.getValue(Profile.class)
    }

    public Profile(String userId, String email, String name, UserType userType,
                   String age, String emergName, String emergNum, String physicianId,
                   String familyId, String patientId, String locationAddress) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.userType = userType;
        this.age = age;
        this.emergName = emergName;
        this.emergNum = emergNum;;
        this.physicianId = physicianId;
        this.familyId = familyId;
        this.patientId = patientId;
        this.locationAddress = locationAddress;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("email", email);
        result.put("name", name);
        result.put("userType", userType);
        result.put("age", age);
        result.put("emergName", emergName);
        result.put("emergNum", emergNum);
        result.put("physicianId",physicianId);
        result.put("familyId",familyId);
        result.put("patientId",patientId);
        result.put("locationAddress",locationAddress);
        return result;
    }
}
