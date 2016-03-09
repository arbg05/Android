package com.elecfreaks.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;

import java.io.Serializable;

/**
 * Created by Arun on 3/7/2016.
 */
public class BLEObject implements Serializable {
/*    private BluetoothLeService bluetoothLeService;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGattCharacteristic targetGattCharacteristic;
    private BluetoothGattCharacteristic readGattCharacteristic;*/

    private BluetoothHandler bluetoothHandler;

    public BluetoothHandler getBluetoothHandler() {
        return bluetoothHandler;
    }

    public void setBluetoothHandler(BluetoothHandler bluetoothHandler) {
        this.bluetoothHandler = bluetoothHandler;
    }

    /*public BluetoothLeService getBluetoothLeService() {
        return bluetoothLeService;
    }

    public void setBluetoothLeService(BluetoothLeService bluetoothLeService) {
        this.bluetoothLeService = bluetoothLeService;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public BluetoothGattCharacteristic getTargetGattCharacteristic() {
        return targetGattCharacteristic;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void setTargetGattCharacteristic(BluetoothGattCharacteristic targetGattCharacteristic) {
        this.targetGattCharacteristic = targetGattCharacteristic;
    }

    public void setReadGattCharacteristic(BluetoothGattCharacteristic readGattCharacteristic) {
        this.readGattCharacteristic = readGattCharacteristic;
    }

    public BluetoothGattCharacteristic getReadGattCharacteristic() {
        return readGattCharacteristic;
    }*/
}
