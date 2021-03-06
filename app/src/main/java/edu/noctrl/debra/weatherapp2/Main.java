// Written by Debra Jensen & Emily Huizenga
package edu.noctrl.debra.weatherapp2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends AppCompatActivity implements OnFragmentInteractionListener{

    private String[] zipsArray = new String[5]; //array for 5 zips
    private int zipIndex = 0; //index into zip array
    private SharedPreferences savedItems; // user's favorite searches
    private boolean MODE = false; //which fragment is being looked at
    private double MINSWIPE = 120;
    private DataManager dManager;
    public AssetManager manager;
    private String curTag = "newCurrent";
    private String foreTag = "newForecast";
    private double prevX;
    private double prevY;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        manager = this.getAssets();
        dManager = new DataManager();
        //ask SharedPreferences for the unit mode
        savedItems = getSharedPreferences("weather_data", MODE_PRIVATE);

        dManager.setUnits(savedItems.getBoolean("units", true));

        //ask SharedPreferences for the zip codes saved and populate the zip array
        zipsArray[0] = savedItems.getString("0", "60540"); //default zip on initial open of the app after install
        zipsArray[1] = savedItems.getString("1", "");
        zipsArray[2] = savedItems.getString("2", "");
        zipsArray[3] = savedItems.getString("3", "");
        zipsArray[4] = savedItems.getString("4", "");

        //get the zip index
        zipIndex = savedItems.getInt("zipIndex", 0);

        //set zip to the most recently searched zipcode and display the current conditions fragment
         dManager.setZip( zipsArray[(zipIndex + 5 - 1) % 5]);

        MODE = savedItems.getBoolean("mode", true); //get the saved mode, current or 7-Day
        dManager.setMode(MODE);
        dManager.setDayIndex(0);


    }
    @Override
    protected void onPause(){
        super.onPause();

        //remove the fragment
        removeFrag();

        // get a SharedPreferences.Editor to update the zip codes
        SharedPreferences.Editor preferencesEditor = savedItems.edit();
        for(int i = 0; i < zipsArray.length; i++){

            preferencesEditor.putString(Integer.toString(i), zipsArray[i]); // put zip in SP
        }
        preferencesEditor.putInt("zipIndex", zipIndex);
        preferencesEditor.putBoolean("mode", MODE);
        preferencesEditor.apply(); // store the updated preferences
    }

    @Override
    protected void onResume(){
        super.onResume();

        //DISPLAY THE LAST SEARCHED ZIP CODE IF ON THE CURRENT CONDITIONS FRAGMENT
        if(MODE && dManager.getZip() !=""){
                addCurrentFrag();
        }
        else if (dManager.getZip() !=""){
            add7Frag();
        }

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
                recentZipsDialog();
                return true;
            case R.id.weather_current:
                //show the project 1 activity and set the mode
               // if(!MODE) remove7Frag();
                removeFrag();
                addCurrentFrag();
                MODE = true;
                dManager.setMode(MODE);
                return true;
            case R.id.weather_7day:
                //show 7 Day forecast activity
                //if(MODE) removeCurrentFrag();
                removeFrag();
                add7Frag();
                MODE = false;
                dManager.setMode(MODE);
                return true;
            case R.id.units:
                //dialog to toggle units
                unitsDialog();
                return true;
            default:
                return super.onOptionsItemSelected(mi);
        }
    }

    //add the current weather fragment
    public void addCurrentFrag(){
        if(internetAccess())  //internet is connected
        {
            FragmentManager fragMan = getSupportFragmentManager();
            FragmentTransaction trans = fragMan.beginTransaction();
            CurrentFragment cur = CurrentFragment.newInstance("string", "string");
            trans.add(R.id.main_layout, cur, curTag);
            trans.commit();
            dManager.getCoords(Main.this, cur);
        }
        else
        {
            //toast saying no connectivity
            Toast.makeText(Main.this, R.string.noInternet,
                    Toast.LENGTH_SHORT).show();
        }
    }

    //remove the current weather fragment
  /*  public void removeCurrentFrag(){
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction trans = fragMan.beginTransaction();
        CurrentFragment curRemove = (CurrentFragment) getSupportFragmentManager().findFragmentByTag(curTag);
        trans.remove(curRemove);
        trans.commit();
        System.out.println("In Remove Current Frag");

    }*/

    //remove the fragment displayed
    public void removeFrag(){
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction trans = fragMan.beginTransaction();
        Fragment curRemove = getSupportFragmentManager().findFragmentByTag(curTag);
        trans.remove(curRemove);

        trans.commit();

    }

    //add the 7 day Fragment
    public void add7Frag(){
        if(internetAccess())  //internet is connected
        {
            FragmentManager fragMan = getSupportFragmentManager();
            FragmentTransaction trans = fragMan.beginTransaction();
            ForecastFragment forecast = ForecastFragment.newInstance("string", "string");
            trans.add(R.id.main_layout, forecast, curTag);
            trans.commit();

            dManager.getCoords(Main.this, forecast);
        }
        else
        {
            //toast saying no connectivity
            Toast.makeText(Main.this, R.string.noInternet,
                    Toast.LENGTH_SHORT).show();
        }
    }

    //called when the zip has already returned a full forecast data
    public void addNext7Frag(){
        if(internetAccess())  //internet is connected
        {
            FragmentManager fragMan = getSupportFragmentManager();
            FragmentTransaction trans = fragMan.beginTransaction();
            ForecastFragment forecast = ForecastFragment.newInstance("string","string");
            trans.add(R.id.main_layout, forecast, curTag);
            trans.commit();

            dManager.setData(forecast);

        }
        else
        {
            //toast saying no connectivity
            Toast.makeText(Main.this, R.string.noInternet,
                    Toast.LENGTH_SHORT).show();
        }
    }
    //Swap for animations
    public void swapFragments(){
        FragmentManager fragman = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragman.beginTransaction();
      //  fragTrans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        Fragment curRemove = getSupportFragmentManager().findFragmentByTag(curTag);
        fragTrans.remove(curRemove);
        ForecastFragment forecast = ForecastFragment.newInstance("string", "string");
        fragTrans.add(R.id.main_layout, forecast, curTag);
        fragTrans.commit();

        //dManager.getCoords(Main.this, forecast);

    }

    //remove the 7 frag
   /* public void remove7Frag(){
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction trans = fragMan.beginTransaction();
        ForecastFragment forecastRemover = (ForecastFragment) getSupportFragmentManager().findFragmentByTag(foreTag);
        trans.remove(forecastRemover);
        trans.commit();
        System.out.println("In Remove Forecast Frag");
    }*/

    //set up the current activity

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

    public void recentZipsDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
        builder.setTitle(R.string.pickZipTitle)
                .setItems(zipsArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        dManager.setZip(zipsArray[which]);

                        //REMOVE OLD FRAGMENT, CREATE NEW FRAGMENT OF CURRENT WEATHER
                        removeFrag();

                        if(MODE){
                            addCurrentFrag();
                        }
                        else{
                            dManager.setDayIndex(0);
                            add7Frag();

                        }

                        //put the zip in the recents set
                        zipIndex = (zipIndex + 1) % 5;

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
                        dManager.setZip(zipInput.getText().toString());

                        if(dManager.getZip().equals("") || dManager.getZip().length() > 5 || dManager.getZip().length() < 5)
                        {
                            //create a toast that says bad zip
                            Toast.makeText(Main.this, R.string.badZipToast,
                                    Toast.LENGTH_SHORT).show();
                            //call enterZipDialog again to re-prompt
                            enterZipDialog();
                        }
                        else {
                            removeFrag();

                            //update the current fragment
                            if(MODE) {
                                addCurrentFrag();
                            }
                            else{
                                dManager.setDayIndex(0);
                                add7Frag();
                            }

                            //save the zip in the set
                            zipsArray[zipIndex]=dManager.getZip();
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
                        dManager.setUnits(true);

                        // get a SharedPreferences.Editor to edit the units
                        SharedPreferences.Editor preferencesEditor = savedItems.edit();
                        preferencesEditor.putBoolean("units", true); // store current search
                        preferencesEditor.apply(); // store the updated preferences
                        //REMOVE OLD FRAGMENT, CREATE NEW FRAGMENT OF CURRENT WEATHER
                        //remove old frag
                        removeFrag();
                        if(MODE)
                        addCurrentFrag();
                        else
                            add7Frag();

                    }
                }).
                setNegativeButton(R.string.metric, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        //switch the units to metric and save in savedPreferences
                        dManager.setUnits(false);

                        // get a SharedPreferences.Editor to edit the units
                        SharedPreferences.Editor preferencesEditor = savedItems.edit();
                        preferencesEditor.putBoolean("units", false); // store current search
                        preferencesEditor.apply(); // store the updated preferences

                        removeFrag();
                        if(MODE)
                        addCurrentFrag();
                        else
                            add7Frag();
                    }
                });
        builder.show();
    }
    public void onFragmentInteraction(Uri uri){

    }

    public boolean internetAccess(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo an = cm.getActiveNetworkInfo();
        return an != null && an.isConnected();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = MotionEventCompat.getActionMasked(event);
        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                prevX = event.getX();
                prevY = event.getY();
                return true;
            case (MotionEvent.ACTION_UP) :
                if(prevX - event.getX() > MINSWIPE) {
                    //call left swipe, go to next day
                    if(!MODE && dManager.getDayIndex() < 6)
                    {
                       removeFrag();
                        dManager.setDayIndex(dManager.getDayIndex()+1);
                        addNext7Frag();
                    }
                }
                else if(event.getX()-prevX > MINSWIPE){
                    //call right swipe, go back day
                    if(!MODE && dManager.getDayIndex()>0)
                    {
                      removeFrag();
                        dManager.setDayIndex(dManager.getDayIndex()-1);
                        addNext7Frag();
                    }
                }
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

}
