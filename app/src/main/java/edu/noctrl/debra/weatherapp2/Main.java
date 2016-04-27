// Written by Debra Jensen & Emily Huizenga
package edu.noctrl.debra.weatherapp2;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Set;
import java.util.TreeSet;

public class Main extends AppCompatActivity implements CurrentFragment.OnFragmentInteractionListener{

    private WeatherInfoIO weatherIO;
    private WeatherInfo results;
    public AssetManager manager;
    private ImageView weatherImg;
    private TextView location, time, condition, temp, dew, humid, pressure, visibility, speed, gust;
    private boolean units = true; //boolean for units, initially true to indicate imperial mode
    private String zip; //string to store the zipcode
    private SharedPreferences savedItems; // user's favorite searches
    private Set<String> savedZips = new TreeSet<String>(); //set to store zip codes
    private String[] zipsArray = {"","","","",""}; //array for zips
    private int zipIndex = 0; //index into zip array

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //ask SharedPreferences for the unit mode
        savedItems = getSharedPreferences("weather_data", MODE_PRIVATE);
        units = savedItems.getBoolean("units", true);

        //ask SharedPreferences for the zip codes saved and populate the zip array
        savedZips = savedItems.getStringSet("zips", savedZips);
        for(String zip : savedZips){
            System.out.println("Current Zip: " + zip);
            zipsArray[zipIndex]=zip;
            zipIndex = (zipIndex+1) % 5;
        }

    }
    @Override
    protected void onPause(){
        super.onPause();

        // get a SharedPreferences.Editor to update the zip codes
        savedZips.clear();
        for(int i = 0; i < zipsArray.length; i++){

            //savedZips.add("60181");
            savedZips.add(zipsArray[i]);
        }
        SharedPreferences.Editor preferencesEditor = savedItems.edit();
        preferencesEditor.putStringSet("zips", savedZips); // store current search
        preferencesEditor.apply(); // store the updated preferences

    }

    //Methods for Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.weather_menu, menu);
        return true;
    }

    @Override //what to do when the menu item is selected
    public boolean onOptionsItemSelected(MenuItem mi){
        switch(mi.getItemId()){
            case R.id.about:
                //show a dialog with some source info
                //www.weather.gov
                aboutDialog();
                return true;
            case R.id.enter_zipcode:
                //dialog to enter zipcode
                enterZipDialog();
                return true;
            case R.id.recent_zipcodes:
                //show last 5 zipcodes entered
                return true;
            case R.id.weather_current:
                //show the project 1 activity
                addCurrentFrag();
                return true;
            case R.id.weather_7day:
                //show 7 Day forecast activity
                return true;
            case R.id.units:
                //dialog to toggle units
                unitsDialog();
                return true;
            default:
                return super.onOptionsItemSelected(mi);
        }
    }


    //method to write weather data to the screen
    public void setFields()
    {
        //CHECK THAT THERE IS A ZIP SAVED

        double myTemp = results.current.temperature;
        double myDew = results.current.dewPoint;
        double myPressure = results.current.pressure;
        double myWindSpeed = results.current.windSpeed;
        double myVisibility = results.current.visibility;
        double myGust = results.current.gusts;


        //get text views
        location = (TextView) findViewById(R.id.locationResult);
        time = (TextView) findViewById(R.id.timeResult);
        condition = (TextView) findViewById(R.id.conditionResult);
        temp = (TextView) findViewById(R.id.tempResult);
        dew = (TextView) findViewById(R.id.dewResult);
        humid = (TextView) findViewById(R.id.humidityResults);
        pressure = (TextView) findViewById(R.id.pressureResult);
        visibility = (TextView) findViewById(R.id.visibilityResult);
        speed = (TextView) findViewById(R.id.windResult);
        gust = (TextView) findViewById(R.id.gustResult);

        //convert to metric
        if(!units)
        {
            myTemp = ((int)(((myTemp-32)/1.8)*100))/100.0; //convert to Celsius
            myDew = ((int)(((myDew-32)/1.8)*100))/100.0; //convert to Celsius
            myPressure = ((int)((myPressure*25.4)*100))/100.0; //convert to mm
            myWindSpeed = ((int)((myWindSpeed * 1.609)*100))/100.0; //convert to km/h
            myVisibility = ((int)((myVisibility * 1.609)*100))/100.0; //convert to km
            temp.setText(myTemp + "\u2103");
            dew.setText(myDew + "\u2103");
            pressure.setText(myPressure + " mm");
            visibility.setText(myVisibility + " km");
            speed.setText(results.current.windDirectionStr()+ " @ " + myWindSpeed + " km/h");


        }
        else {
            myTemp = results.current.temperature;
            myDew = results.current.dewPoint;
            myPressure = results.current.pressure;
            myWindSpeed = results.current.windSpeed;
            myVisibility = results.current.visibility;
            temp.setText(myTemp + "\u2109");
            dew.setText(myDew + "\u2109");
            pressure.setText(myPressure + " in");
            visibility.setText(myVisibility + " mi");
            speed.setText(results.current.windDirectionStr()+ " @ " + myWindSpeed + " mph");

        }

        //populate fields
        time.setText(results.current.timestamp);
        condition.setText(results.current.summary);
        location.setText(results.location.name);
        humid.setText(results.current.humidity+"%");
        if(Double.isNaN(myGust))
            gust.setText("N/A");
        else
            gust.setText(myGust +" knots");
    }


    //add the current fragment
    public void addCurrentFrag(){
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction trans = fragMan.beginTransaction();
        trans.add(R.id.main_layout, CurrentFragment.newInstance("string","string"), "New Current");
        trans.commit();
    }

    //set up the current activity
    private void setupCurrent(){
        weatherIO = new WeatherInfoIO();
        manager = this.getAssets();

        weatherImg = (ImageView)findViewById(R.id.image);

        //switch on the zip code
        switch (zip)
        {
            case "10024":
                weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.img_10024));
                break;
            case "60540":
                weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.img_60540));
                break;
            case "63101":
                weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.img_63101));
                break;
            case "73301":
                weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.img_73301));
                break;
            case "90001":
                weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.img_90001));
                break;

        }
        results = weatherIO.loadFromAsset(manager, zip + ".xml");

        setFields();   //start in default imperial mode
    }

    public void aboutDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
        builder.setMessage(R.string.aboutText).
                setTitle(R.string.aboutTitle).
                setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        //do nothing
                    }
                });
        builder.show();
    }

    public void enterZipDialog()
    {
        //Make edit text for user to enter in
        final EditText zipInput = new EditText(this);
        zipInput.setInputType(InputType.TYPE_CLASS_TEXT);

        //Make alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
        builder.setView(zipInput);
        builder.setTitle(R.string.enterZipTitle).
                setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        //collect the user input
                        zip= zipInput.getText().toString();

                        if(zip.equals("") || zip.length() > 5)
                        {
                            //create a toast that says bad zip
                            //call enterZipDialog again to re-prompt
                            enterZipDialog();
                        }
                        else {
                            //call the search function
                            setupCurrent();

                            //save the zip in the set
                            zipsArray[zipIndex]=zip;
                            zipIndex = (zipIndex + 1) % 5;
                        }
                    }
                }).
                setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });
        builder.show();
    }

    public void unitsDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
        builder.setTitle(R.string.unitTitle).
                setPositiveButton(R.string.imperial, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        //switch the units to imperial and save in savedPreferences
                        units = true;

                        // get a SharedPreferences.Editor to edit the units
                        SharedPreferences.Editor preferencesEditor = savedItems.edit();
                        preferencesEditor.putBoolean("units", true); // store current search
                        preferencesEditor.apply(); // store the updated preferences

                        setFields();

                    }
                }).
                setNegativeButton(R.string.metric, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        //switch the units to metric and save in savedPreferences
                        units = false;

                        // get a SharedPreferences.Editor to edit the units
                        SharedPreferences.Editor preferencesEditor = savedItems.edit();
                        preferencesEditor.putBoolean("units", false); // store current search
                        preferencesEditor.apply(); // store the updated preferences

                        setFields();
                    }
                });
        builder.show();
    }
    public void onFragmentInteraction(Uri uri){

    }
}
