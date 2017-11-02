package com.health_e;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

/**
 * Created by anikakht on 6/6/2017.
 */

public class msgReceiver extends WearableListenerService {
    private static final String TAG = "DataLayerSample";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChenged: " + dataEventBuffer);
            Toast.makeText(getApplication().getBaseContext(), String.valueOf(dataEventBuffer), Toast.LENGTH_LONG).show();
        }
    }

    GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).build();


    ConnectionResult connectionResult = googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

    public ConnectionResult getConnectionResult() {
        return connectionResult;
    }



}
