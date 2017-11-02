package com.health_e;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements SensorEventListener {

    float bpm, HeartRate[]={0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}, HeartRateAvg = 0, gravity[] = {0f, 0f, 0f}, linear_acceleration[] = {0f, 0f, 0f};
    int HR_i = 0;
    double totAcc, fallAcc, FallCounter = 0, threshold = 5;
    String nodeId;
    final int CONNECTION_TIME_OUT_MS = 5000;

    boolean over = false;
    boolean sendAttackFlag = false;
    int sendAttackFlagCnt = 0;
    AlertDialog popup;
    CountDownTimer time;
    long[] pattern = {400, 400};

    TextView heart;

    SensorManager sensorManager;
    Sensor sensorAcc, sensorHeart;

    Handler handler, timerHandler;
    int interval = 100; // read sensor data each 1000 ms
    boolean flag = false, detected = false, timerFlag = false;
    float sim_scale = 2.0f;
    boolean sim_flag = false;

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            timerFlag = true;
            timerHandler.postDelayed(this, 2000);
        }
    };

    private final Runnable processSensors = new Runnable() {
        @Override
        public void run() {
            // Do work with the sensor values.
            flag = true;
            // The Runnable is posted to run again here:
            handler.postDelayed(this, interval);
        }
    };


    private void HeartAttackSim(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);

        // TODO: heart attack simulation
//        final Button HeartAttackBtn = (Button) findViewById(R.id.sendBtn);
//        HeartAttackBtn.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                sim_flag = !sim_flag;
//
//            }
//        });

        ToggleButton toggle = (ToggleButton) findViewById(R.id.TglBtn);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sim_flag = true;
                } else {
                    sim_flag = false;
                }
            }
        });

        retrieveDeviceNode();

        timerHandler = new Handler();
        handler = new Handler();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorHeart = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorHeart, SensorManager.SENSOR_DELAY_NORMAL);

        heart = (TextView) findViewById(R.id.heartVal);
    }


    public void fallDetectionAction() {
        final Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v1.vibrate(pattern, 0);
        detected = true;

        retrieveDeviceNode();

        // Fall detected popup
        popup = new AlertDialog.Builder(MainActivity.this)
                .setTitle("FALL DETECTED!")
                .setCancelable(false)
                .setMessage("")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        time.cancel();
                        v1.cancel();
                        detected = false;
                        sendInfo("fall", 0);
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        time.cancel();
                        v1.cancel();
                        detected = false;
                    }
                }).show();

        // Countdown from ten seconds to prompt user of phone call
        time = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                popup.setMessage("Do you need help?\n" +
                        (int) millisUntilFinished / 1000 + " seconds remaining");
            }

            @Override
            public void onFinish() {
                popup.cancel();
                v1.cancel();
                detected = false;
                sendInfo("fall", 0);
            }
        }.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(processSensors);
        timerHandler.post(task);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Update the heart rate
        if (timerFlag) {
            sendInfo("heart", bpm);// (sim_flag ? bpm/sim_scale : bpm));
            timerFlag = false;

            HR_i++;
            if(HR_i>9) {
                HR_i = 0;
                over = true;
            }

            if (over)
            {
                HeartRateAvg = 0;
                for (int i=0;i<10;i++)
                {
                    HeartRateAvg = HeartRateAvg + HeartRate[i];
                }
                HeartRateAvg = HeartRateAvg/10;
                if ((bpm>HeartRateAvg*1.3 || bpm<HeartRateAvg*0.7) && !sendAttackFlag && sendAttackFlagCnt>15)
                {
                    sendInfo("attack",0);
                    sendAttackFlag = true;
                    Toast.makeText(getApplicationContext(), "HeartAttack", Toast.LENGTH_SHORT).show();
                }
                sendAttackFlagCnt++;
                if (sendAttackFlag) {
                    sendAttackFlagCnt++;
                    if (sendAttackFlagCnt>10)
                    {
                        sendAttackFlagCnt=0;
                        sendAttackFlag = false;
                    }
                }
            }
            HeartRate[HR_i] = bpm;
            sendInfo ("avg", HeartRateAvg);
        }

        // Update the accelerometer
        if (flag) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                final float alpha = (float) 0.8;
                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                // Remove the gravity contribution with the high-pass filter.
                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];

                totAcc = Math.sqrt(linear_acceleration[0] * linear_acceleration[0] +
                        linear_acceleration[1] * linear_acceleration[1] +
                        linear_acceleration[2] * linear_acceleration[2]);

                fallAcc = linear_acceleration[2];
            }

            // Update the heart rate
            if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                bpm = event.values[0];
                if (sim_flag)
                    bpm = bpm/sim_scale;

                String message = String.valueOf ((int) bpm) + " bpm";
                heart.setText(message);
            }

            flag = false;

            FallCounter = ((totAcc > threshold) ? FallCounter + 1 : 0);

            if (FallCounter == 5 && !detected) {
                fallDetectionAction();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void retrieveDeviceNode() {
        final GoogleApiClient client = getGoogleApiClient(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.SECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    private void sendInfo(final String message, final float data) {
        final GoogleApiClient client = getGoogleApiClient(this);
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, message, double2ByteArray(data));
                    client.disconnect();
                }
            }).start();
        }
    }

    public static byte[] double2ByteArray(double value) {
        return ByteBuffer.allocate(8).putDouble(value).array();
    }
}