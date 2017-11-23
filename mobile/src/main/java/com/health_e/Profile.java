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

    public final String USER_ID = "userId";
    public final String USER_EMAIL = "email";
    public final String USER_NAME = "name";
    public final String USER_TYPE = "userType";
    public final String USER_AGE = "age";
    public final String EMERGENCY_NAME = "emergName";
    public final String EMERGENCY_NUM = "emergNum";
    public final String PHYSICIAN_ID = "physicianId";
    public final String FAMILY_ID = "familyId";
    public final String PATIENT_ID = "patientId";
    public final String LOCATION_ADDRESS = "locationAddress";

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
        result.put(USER_ID, userId);
        result.put(USER_EMAIL, email);
        result.put(USER_NAME, name);
        result.put(USER_TYPE, userType);
        result.put(USER_AGE, age);
        result.put(EMERGENCY_NAME, emergName);
        result.put(EMERGENCY_NUM, emergNum);
        result.put(PHYSICIAN_ID,physicianId);
        result.put(FAMILY_ID,familyId);
        result.put(PATIENT_ID,patientId);
        result.put(LOCATION_ADDRESS,locationAddress);
        return result;
    }
}
