package com.health_e;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FamilyHomeScreen extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private DataRecord dataRecord;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_home_screen);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // loading family user profile
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    profile = dataSnapshot.child("users").child(auth.getCurrentUser().getUid()).getValue(Profile.class);
//                    Toast.makeText(getApplicationContext(),"patientId:" + profile.patientId,Toast.LENGTH_SHORT).show();
                }catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Database query failed",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("loadProfile:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(profileListener);

        // loading related patient's data records
        ValueEventListener dataRecordListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    dataRecord = dataSnapshot.child("records").child(profile.patientId).getValue(DataRecord.class);
//                    Toast.makeText(getApplicationContext(),"patient data query done" ,Toast.LENGTH_SHORT).show();
                }catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(),"patient data query failed",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ldDataRcrd:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(dataRecordListener);

        ValueEventListener locationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    String address = (String) dataSnapshot.child("users").child(profile.patientId).child("locationAddress").getValue();
//                    Toast.makeText(getApplicationContext(),"address read done" ,Toast.LENGTH_SHORT).show();

                    TextView loc = (TextView) findViewById(R.id.location);
                    String message = "Patient's location: \n" + address;
                    loc.setText(message);
                }catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(),"address query failed",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("address:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(locationListener);

        Button signOut = (Button) findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), auth.getCurrentUser().getEmail() + " Signed out",
                        Toast.LENGTH_LONG).show();
                auth.signOut();
                finish();
            }
        });
    }
}
