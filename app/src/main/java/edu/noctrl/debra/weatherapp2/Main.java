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
import android.widget.Toast;

public class Main extends AppCompatActivity implements CurrentFragment.OnFragmentInteractionListener{

    private String[] zipsArray = new String[5]; //array for 5 zips
    private int zipIndex = 0; //index into zip array
    private SharedPreferences savedItems; // user's favorite searches
    private boolean MODE = true; //which fragment is being looked at
    private DataManager dManager;
    public AssetManager manager;
    private String curTag = "newCurrent";


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
        zipsArray[0] = savedItems.getString("0", "");
        zipsArray[1] = savedItems.getString("1", "");
        zipsArray[2] = savedItems.getString("2", "");
        zipsArray[3] = savedItems.getString("3", "");
        zipsArray[4] = savedItems.getString("4", "");

        //get the zip index
        zipIndex = savedItems.getInt("zipIndex", 0);

        //set zip to the most recently searched zipcode and display the current conditions fragment
        //THIS SHOULD DISPLAY WHICHEVER FRAGMENT THEY WERE LOOKING AT LAST
         dManager.setZip( zipsArray[(zipIndex + 5 - 1) % 5]);

        MODE = savedItems.getBoolean("mode", true); //get the saved mode, current or 7-Day

    }
    @Override
    protected void onPause(){
        super.onPause();

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
                addCurrentFrag();
                MODE = true;
                return true;
            case R.id.weather_7day:
                //show 7 Day forecast activity
                MODE = false;
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
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction trans = fragMan.beginTransaction();
        CurrentFragment cur = CurrentFragment.newInstance("string","string");
        trans.add(R.id.main_layout, cur, curTag);
        trans.commit();

        dManager.getCoords(Main.this, cur);

    }

    //remove the current weather fragment
    public void removeCurrentFrag(){
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction trans = fragMan.beginTransaction();
        CurrentFragment curRemove = (CurrentFragment) getSupportFragmentManager().findFragmentByTag(curTag);
        trans.remove(curRemove);
        trans.commit();

    }

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
                        removeCurrentFrag();
                        addCurrentFrag();

                        //put the zip in the recents set
                        zipsArray[zipIndex]=dManager.getZip();
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

                            removeCurrentFrag();
                            addCurrentFrag();

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
                        removeCurrentFrag();
                        addCurrentFrag();

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

                        removeCurrentFrag();
                        addCurrentFrag();
                    }
                });
        builder.show();
    }
    public void onFragmentInteraction(Uri uri){

    }

}
