package utd.smartweather.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import utd.smartweather.R;
import utd.smartweather.adapter.Weather;
import utd.smartweather.adapter.TemperatureArrayAdapter;

public class WeatherActivity extends Activity implements SensorEventListener {

    private boolean isShowingFahrenheit = false;
    private TemperatureArrayAdapter mtemperatureAdapter;
    private ListView mlistView;
    private TextView mAmbientTemperatureLabel;
    private SensorManager mSensorManager;
    private Sensor mTemperature;
    private Button mconversionButton;
    private Context context;

    private final ArrayList<String> days = new ArrayList<String>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
    private final int NO_OF_ROWS = days.size();
    private float[] randomTempValues = new float[NO_OF_ROWS];
    private Weather weather_data[] = new Weather[NO_OF_ROWS];

    /*
     * JNI function declaration
     * params: float[] array, size, boolean convertToFahrenheit
     * return: float[] array
     */
    public native float[] convertTemp(float[] oldArray, int size, boolean convertToFahrenheit);

    //load the library
    static{
        System.loadLibrary("convertTemp");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        context = this;

        mAmbientTemperatureLabel = (TextView) findViewById(R.id.ambientTemp);

        //Reading the ambient temperature
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            mTemperature= mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }
        if (mTemperature == null) {
            mAmbientTemperatureLabel.setText(getString(R.string.sensor_not_available));
        }

        randomTempValues = randomNumberGenerator(randomTempValues); //generate random temerature values

        //prepare the temprature data for the adapter
        for(int i=0;i<NO_OF_ROWS;i++){
            weather_data[i] = new Weather(days.get(i), getWeatherIcon(randomTempValues[i]), randomTempValues[i]);
        }

        mlistView = (ListView)findViewById(R.id.listView);
        mtemperatureAdapter = new TemperatureArrayAdapter(this, R.layout.list_item, weather_data, getisShowInFahrenheit());
        mlistView.setAdapter(mtemperatureAdapter);

        mconversionButton = (Button) findViewById(R.id.button);
        mconversionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowingFahrenheit = !isShowingFahrenheit;
                if(getisShowInFahrenheit()){
                    randomTempValues = convertTemp(randomTempValues, randomTempValues.length, isShowingFahrenheit);
                    weather_data = updateWeatherData(weather_data, randomTempValues);
                    mtemperatureAdapter = new TemperatureArrayAdapter(context, R.layout.list_item, weather_data, getisShowInFahrenheit());
                    mconversionButton.setText(getString(R.string.action_show_celsius));
                }else {
                    randomTempValues = convertTemp(randomTempValues, randomTempValues.length, isShowingFahrenheit);
                    weather_data = updateWeatherData(weather_data, randomTempValues);
                    mtemperatureAdapter = new TemperatureArrayAdapter(context, R.layout.list_item, weather_data, getisShowInFahrenheit());
                    mconversionButton.setText(getString(R.string.action_show_fahrenheit));
                }
                mlistView.setAdapter(mtemperatureAdapter);
                mtemperatureAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        mtemperatureAdapter = new TemperatureArrayAdapter(this, R.layout.list_item, weather_data, getisShowInFahrenheit());
        mlistView.setAdapter(mtemperatureAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float ambient_temperature = event.values[0];
        mAmbientTemperatureLabel.setText(getString(R.string.ambient_temperature)
                + String.valueOf(ambient_temperature) +" "+ getString(R.string.celsius));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    /*
     * getisShowInFahrenheit()
     * params: none
     * return: boolean value of isShowingFahrenheit
     */
    public boolean getisShowInFahrenheit() {
        return isShowingFahrenheit;
    }

    /*
     * updateWeatherData, used to update the converted temperature values returned from JNI
     * params: array of Weather, float array of converted temperature values
     * return: updated array of Weather
     */
    private Weather[] updateWeatherData(Weather weather_data[], float[] tempVals){
        for(int i=0;i<NO_OF_ROWS;i++){
            weather_data[i] = new Weather(days.get(i), getWeatherIcon(tempVals[i]), tempVals[i]);
        }
        return weather_data;
    }

    /*
     * randomNumberGenerator, used to generate random values of temperature
     * params: float array
     * return: float array
     */
    private float[] randomNumberGenerator(float[] array){
        Random random = new Random();
        for(int i=0;i<array.length;i++){
            array[i] = random.nextInt(30);
        }
        return array;
    }

    /*
     * getWeatherIcon, used to set corresponding weather icon for a given temperature value
     * params: float value of temperature
     * return: integer, resource id of the icon
     */
    private int getWeatherIcon(float temp){
        int iconValue = R.drawable.placeholder;
        if(!getisShowInFahrenheit()) {
            if (temp <= 30) {
                if (temp < 20) {
                    if (temp < 10) {
                        iconValue = R.drawable.winter;
                    } else {
                        iconValue = R.drawable.clouds;
                    }
                } else {
                    iconValue = R.drawable.summer;
                }
            }
        }else{
            if (temp <= 62) {
                if (temp < 52) {
                    if (temp < 42) {
                        iconValue = R.drawable.winter;
                    } else {
                        iconValue = R.drawable.clouds;
                    }
                } else {
                    iconValue = R.drawable.summer;
                }
            }
        }
        return iconValue;
    }
}
