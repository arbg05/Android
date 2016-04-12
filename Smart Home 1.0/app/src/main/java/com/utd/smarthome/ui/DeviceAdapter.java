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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.utd.smarthome.R;

public class DeviceAdapter extends ArrayAdapter<Device> {

	private List<Device> deviceList;
	private Context context;
	
	
	
	public DeviceAdapter(List<Device> deviceList, Context ctx) {
		super(ctx, R.layout.img_row_layout, deviceList);
		this.deviceList = deviceList;
		this.context = ctx;
	}
	
	public int getCount() {
		return deviceList.size();
	}

	public Device getItem(int position) {
		return deviceList.get(position);
	}

	public long getItemId(int position) {
		return deviceList.get(position).hashCode();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		DeviceHolder holder = new DeviceHolder();
		
		// First let's verify the convertView is not null
		if (convertView == null) {
			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.img_row_layout, null);
			// Now we can fill the layout with the right values
			TextView tv = (TextView) v.findViewById(R.id.functionName);
			TextView output = (TextView) v.findViewById(R.id.output);

			
			holder.functionNameView = tv;
			holder.output = output;
			
			v.setTag(holder);
		}
		else 
			holder = (DeviceHolder) v.getTag();
		
		Device p = deviceList.get(position);
		holder.functionNameView.setText(p.getFunctionName());
        holder.output.setText(p.getOutput());
		
		return v;
	}
	
	/* *********************************
	 * We use the holder pattern        
	 * It makes the view faster and avoid finding the component
	 * **********************************/
	
	private static class DeviceHolder {
		public TextView functionNameView;
		public TextView output;
	}

}
