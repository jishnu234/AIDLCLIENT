package com.example.aidlclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.example.aidlclient.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private String firstString, secondString;
    private static final String TAG = "AIDLClient";
    com.example.aidlclient.AidlAdditionInter aidlAdditionInter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater(), null, false);
        setContentView(mainBinding.getRoot());

        mainBinding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstString = mainBinding.firstNno.getText().toString();
                secondString = mainBinding.secondNo.getText().toString();

                if (validateNumber(firstString, secondString)) {
                    performAddition(firstString, secondString);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(aidlAdditionInter == null) {
            Log.d(TAG, "onStart: aidl interface is null");
            Intent intent = new Intent("AIDL_Connection_flag");
            intent.setPackage("com.example.aidlserver");
//            getApplicationContext().bindService(intent, new ServiceConnection() {
//                @Override
//                public void onServiceConnected(ComponentName name, IBinder service) {
//                    aidlAdditionInter = AidlAdditionInter.Stub.asInterface(service);
//                }
//
//                @Override
//                public void onServiceDisconnected(ComponentName name) {
//
//                }
//            }, BIND_AUTO_CREATE);
        }
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlAdditionInter = com.example.aidlclient.AidlAdditionInter.Stub.asInterface(service);
            Log.d(TAG, "onServiceConnected: aidl interface initialized");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            aidlAdditionInter = null;
            Log.d(TAG, "onServiceDisconnected: aidl is set to be null");
        }
    };

    private void performAddition(String firstString, String secondString) {
        double firstNo = Double.parseDouble(firstString);
        double secondNo = Double.parseDouble(secondString);

        try {
            Log.d(TAG, "performAddition: AIDL server is called for performing addition");
            double result = aidlAdditionInter.performSum(firstNo, secondNo);
            mainBinding.resultText.setText(String.valueOf(result));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean validateNumber(String firstNo, String secondNo) {
        if (firstNo.isEmpty() || secondNo.isEmpty())
            return false;
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }
}