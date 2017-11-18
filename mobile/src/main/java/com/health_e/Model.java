package com.health_e;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class Model implements Serializable {
    private static final String mfileName = "appData" ;
    private static Model singletonModel;
    private static File mfile;
    private static File mHistory;
    private String name = "def_name", emer_name = "def_contact", age = "def_age", emer_num = "def_contact_num";
    private Profile.UserType userType = Profile.UserType.PATIENT;
    private int hr = 0, temp, bp;
    private double lat, lon;
    private Calendar update;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    class info {
        int temp, bp, hr;
        String date;

        info (int t, int b, int h, Calendar d) {
            temp = t;
            bp = b;
            hr = h;
            date = d.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + d.get(Calendar.DAY_OF_MONTH)
                    + ", " + d.get(Calendar.YEAR);
        }
    }

    private Vector<info> history = new Vector();

    private Model(Context context) {
        readfromFile(context);
    }

    public static synchronized Model getInstance(Context context) {
        if (singletonModel == null) {
            singletonModel = new Model(context);
        }
        return singletonModel;
    }

    public void savetoFile(Context context) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(mfileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream((fileOutputStream));
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("Model", "FileNotFound");

            e.printStackTrace();
        } catch (IOException e) {

        }
    }

    public void readfromFile(Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput(mfileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            singletonModel = (Model) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            this.name = singletonModel.name;
            this.userType = singletonModel.userType;
            this.age = singletonModel.age;
            this.emer_name = singletonModel.emer_name;
            this.emer_num = singletonModel.emer_num;
            this.update = singletonModel.update;
            this.history = singletonModel.history;
        } catch (FileNotFoundException e) {
            Log.e("Model", "FileNotFound");
            mfile = new File(context.getFilesDir(), mfileName);
            e.printStackTrace();
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean getUpdate() {
        Calendar c = Calendar.getInstance();
        if (update == null) { update = Calendar.getInstance(); }
        if (c.get (Calendar.DAY_OF_YEAR) > update.get (Calendar.DAY_OF_YEAR) ||
            c.get (Calendar.YEAR) > update.get (Calendar.YEAR)) {
            return true;
        }
        return false;
    }

    public int historySize() { return history.size(); }

    public String historyInfo (String s, int index) {
        switch (s) {
            case "temp":
                return Integer.toString (history.get (index).temp);
            case "bp":
                return Integer.toString (history.get (index).bp);
            case "hr":
                return Integer.toString (history.get (index).hr);
            case "date":
                return history.get (index).date;
        }

        return null;
    }

    public int getBP() { return bp; }

    public int getTemp() {
        return temp;
    }

    public String getName() {
        return name;
    }

    public Profile.UserType getUserType() {return userType;}

    public String getAge() {
        return age;
    }

    public String getEmerName() {
        return emer_name;
    }

    public String getEmerNum() {
        return emer_num;
    }

    public String getLocation(Context context) {
        Geocoder geo = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geo.getFromLocation(lat, lon, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String prov = addresses.get(0).getAdminArea();
            return address;
//            return address.concat(", " + city + ", " + prov);
        } catch (Exception e) {}

        return "location unavailable";
    }

    public void setUpdate (Calendar u) {
        update = u;
        if (history.size() > 30) { history.remove (0); }
        history.add (new info (temp, bp, hr, update));
    }

    public void setHR (int h) { hr = h; }

    public void setBP (int b) {
        bp = b;
    }

    public void setTemp(int t) {
        temp = t;
    }

    public void setName(String s) {
        name = s;
    }

    public void setUserType(Profile.UserType t) {userType = t;}

    public void setAge(String s) {
        age = s;
    }

    public void setEmerName(String s) {
        emer_name = s;
    }

    public void setEmerNum(String s) {
        emer_num = s;
    }

    public void setLocation(double la, double lo) {
        lat = la;
        lon = lo;
    }
}

