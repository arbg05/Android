package com.elecfreaks.ui;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elecfreaks.ble.BLEObject;
import com.elecfreaks.ui.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DeviceDetail extends Activity {
    EditText commandText;
    Button sendButton;
    private UUID readUUID =
            UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        commandText = (EditText) findViewById(R.id.edittext_command);
        sendButton = (Button) findViewById(R.id.button_send);
        // Get the intent
        Intent intent = getIntent();
        //Bundle bundle = intent.getExtras();
       // final BLEObject bleObject = (BLEObject) bundle.getSerializable("BLEObject");


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.bluetoothHandler.getTargetGattService()!=null){
                    String data = String.valueOf(commandText.getText());
                    String[] commands = data.split("\n");
                    List<String> list = Arrays.asList(commands);
                    for(int i=0;i<list.size(); i++){
                        String temp = list.get(i);
                        temp += "\n";
                        list.set(i,temp);
                    }
                    BluetoothGattCharacteristic bleBluetoothReadGattCharacteristic = MainActivity.bluetoothHandler.getTargetGattService().getCharacteristic(readUUID);
                    for(int i=0;i<list.size(); i++){
                        if(!list.get(i).isEmpty()) {
                            bleBluetoothReadGattCharacteristic.setValue(list.get(i));
                            boolean val = MainActivity.bluetoothHandler.getmBluetoothLeService().writeCharacteristic(bleBluetoothReadGattCharacteristic);
                            Toast.makeText(getApplicationContext(), "Val: " + val, Toast.LENGTH_SHORT).show();
                        }
                   }
                }
            }
        });

    }


}
