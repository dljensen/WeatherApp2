package edu.noctrl.debra.weatherapp2;

import android.content.res.AssetManager;

/**
 * Created by Debra Jensen and Emily Huizenga on 4/29/16.
 */
public class DataManager {
    private WeatherInfoIO weatherIO;
    private WeatherInfo results;
    private boolean units = true; //boolean for units, initially true to indicate imperial mode
    private String zip; //string to store the zipcode
    private String[] coords = new String[2];

    public boolean getUnits()
    {
        return units;
    }

    public void setUnits(boolean u)
    {
        units = u;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String z)
    {
        zip = z;
    }

    public WeatherInfo getResults()
    {
        return results;
    }

   public void setupCurrent(AssetManager manager){
        weatherIO = new WeatherInfoIO();


        results = weatherIO.loadFromAsset(manager, zip + ".xml");

    }
    //add the current weather fragment

}
