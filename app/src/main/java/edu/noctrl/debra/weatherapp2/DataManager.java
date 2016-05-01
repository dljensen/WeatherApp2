package edu.noctrl.debra.weatherapp2;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public String[] getLatLon()
    {
        return coords;
    }

    //add the current weather fragment


    public void getCoords(final Context ctx, final CurrentFragment frag)
    {
        System.out.println("Calling get Coords and the zip is " + zip);
        final String lat_long_url = "http://craiginsdev.com/zipcodes/findzip.php?zip=" + zip;
        Downloader<JSONObject> myDownloader = new Downloader<JSONObject>(new Downloader.DownloadListener<JSONObject>()
        {
            @Override
            public JSONObject parseResponse(InputStream in) throws IOException, JSONException {
                System.out.println("Parse response zip is " + zip);


                StringBuilder strBuild = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                //read lines from input
                String line = br.readLine();

                while(line != null){
                    strBuild.append(line);
                    line = br.readLine();
                }
                String result = strBuild.toString();
                JSONObject obj = new JSONObject(result);

                return obj;
            }

            @Override
            public void handleResult(JSONObject result) throws JSONException {
              //try catch block to handle bad zipcodes
               try{
                    coords[0] = result.getString("latitude");
                    coords[1] = result.getString("longitude");

                    getData(frag);
                }
                catch(NullPointerException e){
                    //make a toast saying bad Zip, the zip returned no data
                    System.out.println("zip is " + zip);
                    Toast.makeText(ctx, R.string.badZipToast,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        myDownloader.execute(lat_long_url);
    }

    public void getData(CurrentFragment frag)
    {
        final CurrentFragment current_weather = frag;
        WeatherInfoIO.WeatherListener weatherDownloaded = new WeatherInfoIO.WeatherListener(){
            @Override
            public void handleResult(WeatherInfo res) {
                results = res;
                System.out.println("In Weather Listener!!!!!");
                System.out.println("Location is " + results.location.name);

                try {
                    current_weather.setFields(results, units);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };

        WeatherInfoIO.loadFromUrl("http://forecast.weather.gov/MapClick.php?lat="
                        + coords[0] +
                        "&lon="
                        + coords[1] +
                        "&unit=0&lg=english&FcstType=dwml",
                weatherDownloaded);

    }



}
