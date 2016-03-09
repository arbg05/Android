package com.elecfreaks.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.elecfreaks.ble.BLEDeviceListAdapter;
import com.elecfreaks.ble.BLEObject;
import com.elecfreaks.ble.BluetoothHandler;
import com.elecfreaks.ble.BluetoothHandler.OnConnectedListener;
import com.elecfreaks.ble.BluetoothHandler.OnScanListener;
import com.elecfreaks.ui.Protocol.OnReceivedRightDataListener;

import java.io.Serializable;

public class MainActivity extends Activity implements Serializable{

	private Button scanButton;
	private ListView bleDeviceListView;
	private BLEDeviceListAdapter listViewAdapter;
	
	public static BluetoothHandler bluetoothHandler;
	private boolean isConnected;
    private boolean isValidBLEModule;
	private Protocol protocol;
	
	private static final boolean INPUT = false;
	private static final boolean OUTPUT = true;
	private static final boolean LOW = false;
	private static final boolean HIGH = true;
	
	private boolean digitalVal[];
	private int analogVal[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		scanButton = (Button) findViewById(R.id.scanButton);
		bleDeviceListView = (ListView) findViewById(R.id.bleDeviceListView);
		listViewAdapter = new BLEDeviceListAdapter(this);
		digitalVal = new boolean[14];
		analogVal = new int[14];
		
		bluetoothHandler = new BluetoothHandler(this);
		bluetoothHandler.setOnConnectedListener(new OnConnectedListener() {
			
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
                if(isValidBLE) {
                    Intent intent = new Intent(MainActivity.this, DeviceDetail.class);
                    BLEObject bleObject = new BLEObject();
                    /*bleObject.setBluetoothLeService(bluetoothHandler.getmBluetoothLeService());
                    bleObject.setBluetoothAdapter(bluetoothHandler.getBluetoothAdapter());
                    bleObject.setTargetGattCharacteristic(bluetoothHandler.getTargetGattCharacteristic());
                    bleObject.setReadGattCharacteristic(bluetoothHandler.getReadGattCharacteristic());*/
                    bleObject.setBluetoothHandler(bluetoothHandler);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("BLEObject", bleObject);
                    //intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
		protocol = new Protocol(this, new Transmitter(this, bluetoothHandler));
		protocol.setOnReceivedDataListener(recListener);
	}
	
	private OnReceivedRightDataListener recListener = new OnReceivedRightDataListener() {
		
		@Override
		public int onReceivedData(String str) {
			// TODO Auto-generated method stub	    	
			try {
				JSONObject readJSONObject = new JSONObject(str);
				int type = readJSONObject.getInt("T");
				int value = readJSONObject.getInt("V");
				
				switch(type){
					case Protocol.ANALOG:{
						int pin = readJSONObject.getInt("P");
						analogVal[pin] = value;
					}break;
					case Protocol.DIGITAL:{
						int pin = readJSONObject.getInt("P");
						digitalVal[pin] = (value>0)?HIGH:LOW;
					}break;
					case Protocol.TEMPERATURE:{
						float temperature = ((float)value)/100;
					}break;
					case Protocol.HUMIDITY:{
						float humidity = ((float)value)/100;
					}break;
					default:break;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
			return 0;
		}
	};

	public void scanOnClick(final View v){
		if(!isConnected){
			bleDeviceListView.setAdapter(bluetoothHandler.getDeviceListAdapter());
			bleDeviceListView.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String buttonText = (String) ((Button)v).getText();
						if(buttonText.equals("scanning")){
							showMessage("scanning...");
							return ;
						}
						BluetoothDevice device = bluetoothHandler.getDeviceListAdapter().getItem(position).device;
						// connect
						bluetoothHandler.connect(device.getAddress());
					}
			});
			bluetoothHandler.setOnScanListener(new OnScanListener() {
				@Override
				public void onScanFinished() {
					// TODO Auto-generated method stub
					((Button)v).setText("scan");
					((Button)v).setEnabled(true);
				}
				@Override
				public void onScan(BluetoothDevice device, int rssi, byte[] scanRecord) {}
			});
			((Button)v).setText("scanning");
			((Button)v).setEnabled(false);
			bluetoothHandler.scanLeDevice(true);
		}else{
			setConnectStatus(false);
		}
	}
	
	public void setConnectStatus(boolean isConnected){
		this.isConnected = isConnected;
		if(isConnected){
			showMessage("Connection successful");
			scanButton.setText("break");
		}else{
			bluetoothHandler.onPause();
    		bluetoothHandler.onDestroy();
    		scanButton.setText("scan");
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
