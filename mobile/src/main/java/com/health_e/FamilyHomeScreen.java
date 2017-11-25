package com.health_e;

import android.content.Intent;
import android.net.Uri;
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
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.BarGraphSeries;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class FamilyHomeScreen extends AppCompatActivity {

    private FirebaseAuth auth;
    private Profile profile;
    private String patient_address;
//    LineGraphSeries<DataPoint> hRateSeries = new LineGraphSeries<>();
//    LineGraphSeries<DataPoint> bPressureSeries  = new LineGraphSeries<>();
//    LineGraphSeries<DataPoint> bTemperatureSeries = new LineGraphSeries<>();
    BarGraphSeries<DataPoint> hRateSeries = new BarGraphSeries<>();
    BarGraphSeries<DataPoint> bPressureSeries  = new BarGraphSeries<>();
    BarGraphSeries<DataPoint> bTemperatureSeries = new BarGraphSeries<>();

    int dataSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_home_screen);
        auth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        patient_address = "";
        // loading family user profile
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(auth!=null && auth.getCurrentUser()!=null) {
                    profile = dataSnapshot.child("users").child(auth.getCurrentUser().getUid()).getValue(Profile.class);
//                    Toast.makeText(getApplicationContext(),"patientId:" + profile.patientId,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"User profile query failed",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("loadProfile:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(profileListener);

        // loading related patient's data records
        try {
            mDatabase.child("records").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> userIds = dataSnapshot.getChildren();
                    for (DataSnapshot userId : userIds) {
                        if(profile!=null) {
                            if (userId.getKey().equals(profile.patientId)) {
//                              Toast.makeText(getApplicationContext(),"patient found" ,Toast.LENGTH_SHORT).show();
                                Iterable<DataSnapshot> dataRecords = userId.getChildren();
                                for (DataSnapshot uDataRecord : dataRecords) {
                                    DataRecord dataRecord = uDataRecord.getValue(DataRecord.class);
                                    if (dataRecord!=null) {
                                        hRateSeries.appendData(new DataPoint(dataSize, Integer.valueOf(dataRecord.heartRate)), true, 30);
                                        bPressureSeries.appendData(new DataPoint(dataSize, Integer.valueOf(dataRecord.bloodPressure)), true, 30);
                                        bTemperatureSeries.appendData(new DataPoint(dataSize, Integer.valueOf(dataRecord.temperature)), true, 30);
                                        //                                  Toast.makeText(getApplicationContext(), "received", Toast.LENGTH_SHORT).show();
                                        dataSize++;
                                        //                                  Toast.makeText(getApplicationContext(),"BP:" + dataRecord.bloodPressure,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch ( Exception e){
            Toast.makeText(getApplicationContext(),"patient records query failed",Toast.LENGTH_SHORT).show();
        }

        ValueEventListener locationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    patient_address = (String) dataSnapshot.child("users").child(profile.patientId).child(profile.LOCATION_ADDRESS).getValue();
//                    Toast.makeText(getApplicationContext(),"address read done" ,Toast.LENGTH_SHORT).show();
                    TextView loc = (TextView) findViewById(R.id.location);
                    String message = "Patient's location: \n" + patient_address;
                    loc.setText(message);
                }catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(),"address query failed",Toast.LENGTH_SHORT).show();
                    patient_address = "";
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
                if (auth!=null && auth.getCurrentUser()!=null){
                    Toast.makeText(getApplicationContext(), auth.getCurrentUser().getEmail() + " Signed out",
                            Toast.LENGTH_LONG).show();
                }
                if (auth != null){
                    auth.signOut();
                }
                finish();
            }
        });

        Button showOnMap = (Button) findViewById(R.id.showOnMap);
        showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!patient_address.equals("")) {
                    String map = "http://maps.google.co.in/maps?q=" + patient_address;
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    startActivity(i);
                }
            }
        });

        GraphView hRateGraph = (GraphView) findViewById(R.id.hRateGraph);
        hRateGraph.setTitle("Heart Rate");
        hRateGraph.getViewport().setXAxisBoundsManual(true);
        hRateGraph.getViewport().setMinX (0);
        hRateGraph.getViewport().setMaxX (30);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        hRateGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
        hRateGraph.getGridLabelRenderer().setPadding(40);
        hRateGraph.addSeries(hRateSeries);

        GraphView bPressureGraph = (GraphView) findViewById(R.id.bPressureGraph);
        bPressureGraph.setTitle("Blood Pressure");
        bPressureGraph.getViewport().setXAxisBoundsManual(true);
        bPressureGraph.getViewport().setMinX (0);
        bPressureGraph.getViewport().setMaxX (30);
//        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        bPressureGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
        bPressureGraph.getGridLabelRenderer().setPadding(40);
        bPressureGraph.addSeries(bPressureSeries);

        GraphView bTemperatureGraph = (GraphView) findViewById(R.id.bTemperatureGraph);
        bTemperatureGraph.setTitle("Body Temperature");
        bTemperatureGraph.getViewport().setXAxisBoundsManual(true);
        bTemperatureGraph.getViewport().setMinX (0);
        bTemperatureGraph.getViewport().setMaxX (30);
//        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        bTemperatureGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
        bTemperatureGraph.getGridLabelRenderer().setPadding(40);
        bTemperatureGraph.addSeries(bTemperatureSeries);
    }
}
