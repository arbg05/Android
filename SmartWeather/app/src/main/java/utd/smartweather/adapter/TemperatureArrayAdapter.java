package utd.smartweather.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import utd.smartweather.R;
import utd.smartweather.WeatherActivity;


/**
 * Created by Arun on 2/16/2016.
 */
public class TemperatureArrayAdapter extends ArrayAdapter<Weather> {

    Context context;
    int layoutResourceId;
    Weather data[] = null;

    public TemperatureArrayAdapter(Context context, int layoutResourceId, Weather[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TemperatureHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TemperatureHolder();
            holder.txtDay = (TextView) row.findViewById(R.id.day);
            holder.imgIcon = (ImageView)row.findViewById(R.id.icon);
            holder.txtValue = (TextView)row.findViewById(R.id.tempval);

            row.setTag(holder);
        }
        else
        {
            holder = (TemperatureHolder)row.getTag();
        }

        Weather weather = data[position];
        holder.txtDay.setText(weather.day);
        holder.imgIcon.setImageResource(weather.icon);
        if(!WeatherActivity.isShowInFahrenheitChecked()) {
            holder.txtValue.setText(String.valueOf((int)weather.tempVal) + " C");
        }else {
            holder.txtValue.setText(String.valueOf((int)weather.tempVal) + " F");
        }

        return row;
    }

    static class TemperatureHolder
    {
        TextView txtDay;
        ImageView imgIcon;
        TextView txtValue;
    }
}
