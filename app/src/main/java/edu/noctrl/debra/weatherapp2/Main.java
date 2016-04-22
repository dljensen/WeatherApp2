// Written by Debra Jensen & Emily Huizenga
package edu.noctrl.debra.weatherapp2;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class Main extends AppCompatActivity {
   private WeatherInfoIO weatherIO;
    private WeatherInfo results;
    public AssetManager manager;
    private Button searchBtn;
    private EditText zipBox;
    private ImageView weatherImg;
    private TextView location, time, condition, temp, dew, humid, pressure, visibility, speed, gust;
    private RadioButton metric, imperial;

    private String zip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherIO = new WeatherInfoIO();
        manager = this.getAssets();

        searchBtn = (Button)findViewById(R.id.searchBtn);
        weatherImg = (ImageView)findViewById(R.id.image);

        searchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                zipBox = (EditText) findViewById(R.id.zipSearch);
                zip = zipBox.getText() + "";

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

                System.out.println("Time is " + results.current.timestamp);
                System.out.println("Current Conditions: "+ results.current.summary);
                System.out.println("Image URL is " + results.current.imageUrl);
                System.out.println("Location: " + results.location.name);
                setFields(true);   //start in default imperial mode

            }
        });
    }

    public void changeUnits(View view)
    {
        boolean checked =((RadioButton) view).isChecked();
        switch(view.getId())
        {
            case R.id.metricBtn:
                if(checked) {
                    System.out.println("in metric");
                    setFields(false);
                }
                break;
            case R.id.imperialBtn:
                if(checked) {
                    System.out.println("in imperial");
                    setFields(true);
                }
                break;
        }

    }

    public void setFields(boolean mode)
    {
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
        if(!mode)
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


}
