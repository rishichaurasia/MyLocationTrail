package com.example.mylocationtrail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT =1 ;
    BluetoothAdapter mBluetoothAdapter;
    ListView mListView;
    ArrayList<Device> arrayList;
    ArrayAdapter<Device> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        mBluetoothAdapter =BluetoothAdapter.getDefaultAdapter();
        mListView= findViewById(R.id.listDevices);
        arrayList=new ArrayList<>();
        checkBluetoothEnabled();
        discoverDevices();

    }
    private void checkBluetoothEnabled(){
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,REQUEST_ENABLE_BT);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(this, "Bluetooth is Enabled", Toast.LENGTH_LONG).show();
            }
            else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "Bluetooth Enabling Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    class Device{
        String deviceName;
        String deviceAddress;

        Device(String deviceName, String deviceAddress){
            this.deviceName = deviceName;
            this.deviceAddress = deviceAddress;
        }

        @NonNull
        @Override
        public String toString() {
            return deviceName + "(" + deviceAddress + ")";
        }
    }

    private void discoverDevices(){
        mBluetoothAdapter.startDiscovery();
        IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiver, intentFilter);
        arrayAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        mListView.setAdapter(arrayAdapter);
    }
    BroadcastReceiver myReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action= intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Device newDevice = new Device(device.getName(), device.getAddress());
                arrayList.add(newDevice);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

}
