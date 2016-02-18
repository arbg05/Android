package utd.smartweather.adapter;

/**
 * Created by Arun on 2/16/2016.
 */
public class Weather {
    public String day;
    public int icon;
    public float tempVal;
    public Weather(){
        super();
    }

    public Weather(String day, int icon, float tempVal) {
        super();
        this.day = day;
        this.icon = icon;
        this.tempVal = tempVal;
    }
}
