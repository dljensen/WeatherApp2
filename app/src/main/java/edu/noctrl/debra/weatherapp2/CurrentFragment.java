package edu.noctrl.debra.weatherapp2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CurrentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView weatherImg;
    private TextView location, time, condition, temp, dew, humid, pressure, visibility, speed, gust;
    private Context ctx;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CurrentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentFragment newInstance(String param1, String param2) {
        CurrentFragment fragment = new CurrentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            ctx = context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onResume(){
        super.onResume();
        //get views
        location = (TextView) getView().findViewById(R.id.locationResult);
        time = (TextView) getView().findViewById(R.id.timeResult);
        condition = (TextView) getView().findViewById(R.id.conditionResult);
        temp = (TextView) getView().findViewById(R.id.tempResult);
        dew = (TextView) getView().findViewById(R.id.dewResult);
        humid = (TextView) getView().findViewById(R.id.humidityResults);
        pressure = (TextView) getView().findViewById(R.id.pressureResult);
        visibility = (TextView) getView().findViewById(R.id.visibilityResult);
        speed = (TextView) getView().findViewById(R.id.windResult);
        gust = (TextView) getView().findViewById(R.id.gustResult);
    }

    //method to write weather data to the screen
    public void setFields(final WeatherInfo results, boolean units) throws FileNotFoundException {
        //CHECK THAT THERE IS A ZIP SAVED
        String myTemp;
        String myDew;
        String myPressure;
        String myWindSpeed;
        String myVisibility;
        Double myGust = results.current.gusts;

        String imageURL = results.current.imageUrl; //get the url of the weather image



        //convert to metric
        if(!units)
        {
            myTemp = Double.toString(((int)(((results.current.temperature-32)/1.8)*100))/100.0)+ "\u2103"; //convert to Celsius
            myDew = Double.toString(((int)(((results.current.dewPoint-32)/1.8)*100))/100.0) + "\u2103"; //convert to Celsius
            myPressure = Double.toString(((int)((results.current.pressure*25.4)*100))/100.0) + " mm"; //convert to mm
            if(Double.isNaN(results.current.windSpeed))
                myWindSpeed ="N/A";
            else
            myWindSpeed = Double.toString(((int)((results.current.windSpeed * 1.609)*100))/100.0) + " km/h"; //convert to km/h

            myVisibility = Double.toString(((int)((results.current.visibility * 1.609)*100))/100.0) + " km"; //convert to km

        }
        else {
            myTemp = Double.toString(results.current.temperature) + "\u2109";
            myDew = Double.toString(results.current.dewPoint) + "\u2109";
            myPressure = Double.toString(results.current.pressure) + " in";
            if(Double.isNaN(results.current.windSpeed))
                myWindSpeed ="N/A";
            else
            myWindSpeed = Double.toString(results.current.windSpeed) + " mph";
            myVisibility = Double.toString(results.current.visibility) + " mi";
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
        temp.setText(myTemp);
        dew.setText(myDew);
        pressure.setText(myPressure);
        visibility.setText(myVisibility);
        speed.setText(results.current.windDirectionStr()+ " @ " + myWindSpeed);


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = Double.toString(results.location.latitude);
                String lon = Double.toString(results.location.longitude);
                String geo = "geo:"+lat+","+lon;

                geoIntent(Uri.parse(geo));
            }
        });


       weatherImg = (ImageView) getView().findViewById(R.id.image);
        String fileName = Uri.parse(imageURL).getLastPathSegment();

        //add the image
       if(new File(ctx.getCacheDir(), fileName).exists()){
            //get image from cache and set
           File pic = new File(ctx.getCacheDir(), fileName);
            FileInputStream in = new FileInputStream(pic);
            Bitmap bm = BitmapFactory.decodeStream(in);

            weatherImg.setImageBitmap(bm);
        }
        else{
            //download the image and put it in the view
        // show The Image in a ImageView
        new DownloadImageTask(weatherImg, ctx)
                .execute(imageURL);
        }

        //generate notifications for alerts
        if(!results.alerts.isEmpty())
        {
            for(String alert : results.alerts)
            {
                generateAlerts(alert);
            }
        }
    }

    public void geoIntent(Uri field){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(field);
        if(intent.resolveActivity(ctx.getPackageManager()) != null)
            startActivity(intent);
    }

    public void generateAlerts(String alert)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.iconstorm).setContentTitle(getString(R.string.alertTitle))
                .setContentText(getString(R.string.alertText));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(alert));


       PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
        mBuilder.setContentIntent(contentIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());

    }

}
