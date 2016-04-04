/*
 * Copyright (C) 2012 jfrankie (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.utd.smarthome.ui;


/*
 * Copyright (C) 2012 Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.utd.smarthome.R;
import com.utd.smarthome.ble.BLEDeviceListAdapter;
import com.utd.smarthome.ble.BluetoothHandler;

public class MainActivity extends Activity {

	// The data to show
	List<Device> deviceList = new ArrayList<Device>();
	DeviceAdapter aAdpt;

	public static BluetoothHandler bluetoothHandler;
	private boolean isConnected;
	private boolean isValidBLEModule;
	private int mWhichDevice = 0;
	private UUID readUUID =
			UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private static final int FILE_SELECT_CODE = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //initList();
        // We get the ListView component from the layout
        ListView lv = (ListView) findViewById(R.id.listView);

        aAdpt = new DeviceAdapter(deviceList, this);
        lv.setAdapter(aAdpt);

		//ARUN
		bluetoothHandler = new BluetoothHandler(this);
		bluetoothHandler.setOnConnectedListener(new BluetoothHandler.OnConnectedListener() {
			@Override
			public void onConnected(boolean isConnected) {
				// TODO Auto-generated method stub
				setConnectStatus(isConnected);
			}
		});

		bluetoothHandler.setOnValidBLEModuleListener(new BluetoothHandler.OnValidBLEModuleListener() {
            @Override
            public void onValidBLEModule(boolean isValidBLE) {
                setValidBLEStatus(isValidBLE);
                if (isValidBLE) {
                    if (bluetoothHandler.getTargetGattService() != null) {
                        String data = String.valueOf(deviceList.get(mWhichDevice).getCode());
                        String[] commands = data.split("\n");
                        List<String> list = Arrays.asList(commands);
                        for (int i = 0; i < list.size(); i++) {
                            String temp = list.get(i);
                            temp += "\n";
                            list.set(i, temp);
                        }
                        BluetoothGattCharacteristic bleBluetoothReadGattCharacteristic = bluetoothHandler.getTargetGattService().getCharacteristic(readUUID);
                        for (int i = 0; i < list.size(); i++) {
                            if (!list.get(i).isEmpty()) {
                                bleBluetoothReadGattCharacteristic.setValue(list.get(i));
                                boolean val = MainActivity.bluetoothHandler.getmBluetoothLeService().writeCharacteristic(bleBluetoothReadGattCharacteristic);
                                Toast.makeText(getApplicationContext(), "Val: " + val, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });
        
        // React to user clicks on item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, final int position,
                                    long id) {
                // We know the View is a TextView so we can cast it
                mWhichDevice = position;
                TextView clickedView = (TextView) view.findViewById(R.id.functionName);
                Toast.makeText(MainActivity.this, "Device " + deviceList.get(position).getCode(), Toast.LENGTH_SHORT).show();

                //ARUN
                setConnectStatus(false);
                if (!isConnected) {
                    bluetoothHandler.setOnScanListener(new BluetoothHandler.OnScanListener() {
                        @Override
                        public void onScanFinished() {
                            // TODO Auto-generated method stub
							/*((Button)v).setText("scan");
							((Button)v).setEnabled(true);*/
                            showMessage("Scan finished");
                            BluetoothDevice device = null;
                            BLEDeviceListAdapter deviceListAdapter = bluetoothHandler.getDeviceListAdapter();
                            for (int i = 0; i < deviceListAdapter.getCount(); i++) {
                                if (deviceListAdapter.getItem(i).device.getName().equals(deviceList.get(position).getDeviceName())) {
                                    device = deviceListAdapter.getItem(i).device;
                                    break;
                                }
                            }
                            // connect
                            if (device == null) {
                                showMessage("No device found!!");
                            } else {
                                bluetoothHandler.connect(device.getAddress());
                            }
                        }

                        @Override
                        public void onScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        }
                    });
					/*((Button) v).setText("scanning");
					((Button) v).setEnabled(false);*/
                    showMessage("Scanning");
                    bluetoothHandler.scanLeDevice(true);

                } else {
                    setConnectStatus(false);
                }
            }
        });
        
          // we register for the contextmneu        
          registerForContextMenu(lv);
        }
   
    
   // We want to create a context Menu when the user long click on an item
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;
		
		// We know that each row in the adapter is a Map
		Device device =  aAdpt.getItem(aInfo.position);
		
		menu.setHeaderTitle("Options for " + device.getDeviceName());
		menu.add(1, 1, 1, "Details");
		//menu.add(1, 2, 2, "Delete");
		
	}

    // This method is called when user selects an Item in the Context menu
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId){
            case 1:
                AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) item.getMenuInfo();
                //deviceList.remove(aInfo.position);
                showDetail(deviceList.get(aInfo.position));
                break;
            default:
                showMessage("Wrong option selected");
        }
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		//deviceList.remove(aInfo.position);
		aAdpt.notifyDataSetChanged();
		return true;
	}

    private void showDetail(Device device){

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(device.getDeviceName());

        String alert1 = "Function Name: " + device.getFunctionName();
        String alert2 = "Code: \n" + device.getCode();
        alertDialogBuilder.setMessage(alert1 + "\n\n" + alert2);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
                dialog.dismiss();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


	private void initList(String path) throws FileNotFoundException {
        // We populate the planets
       
        /*deviceList.add(new Device("BLE", "CC41-A", "OpenDoor", "put 14 0"));
		deviceList.add(new Device("BLE", "CC41-A", "OpenDoor", "put 14 100"));*/
        deviceList.clear();

        path = path.replace("/mimetype/","");
        InputStream textFileInputStream = new FileInputStream(new File(path));//getResources().openRawResource(R.raw.control);
        String stext = readTextFile(textFileInputStream);
		String controlData[] = stext.split("\n\r");

        for(String deviceData: controlData){
            String line[] = deviceData.trim().split("\\r?\\n");
            String deviceSpecs[] = new String[0];
            String code =  "";
            for(int i=0; i<line.length;i++){
                if(i==0){
                    deviceSpecs = line[i].split(" ");
                    continue;
                }
                code = code + line[i] + "\n";
            }
            if(deviceSpecs.length == 3) {
                deviceList.add(new Device(deviceSpecs[0], deviceSpecs[1], deviceSpecs[2], code.trim()));
            }
        }
        //showMessage(stext);
        aAdpt.notifyDataSetChanged();
    }

    public String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }
    
    
    // Handle user click
    public void addDevice(View view) throws IOException {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);


        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select txt file"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    //ARUN
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String path = uri.getPath();
                    try {
                        initList(path);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //ARUN
	public void setConnectStatus(boolean isConnected){
		this.isConnected = isConnected;
		if (isConnected) {
			showMessage("Connection successful");
            //scanButton.setText("break");
		} else{
            bluetoothHandler.onPause();
			bluetoothHandler.onDestroy();
			//scanButton.setText("scan");
		}
	}

	public void setValidBLEStatus(boolean isValid){
		this.isValidBLEModule = isValid;
		if(isValid){
			showMessage("Valid BLE Module!!");
		}else {
			showMessage("Invalid BLE Module!!");
		}
	}

	private void showMessage(String str){
		Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
	}

   
}
