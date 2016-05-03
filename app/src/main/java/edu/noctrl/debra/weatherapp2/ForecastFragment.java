package edu.noctrl.debra.weatherapp2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 7Day Forecast Fragment
 * Written by Debra Jensen and Emily Huizenga
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context ctx;


    TextView location, timeDay, amFore, pmFore, desc, lowTemp, highTemp, amLabel, pmLabel;
    ImageView img;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForecastFragment newInstance(String param1, String param2) {
        ForecastFragment fragment = new ForecastFragment();
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
        return inflater.inflate(R.layout.fragment_forecast, container, false);
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
        ctx = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
        location = (TextView) getView().findViewById(R.id.locationResult);
        timeDay = (TextView) getView().findViewById(R.id.dayResult);
        amFore = (TextView) getView().findViewById(R.id.forecastAM);
        pmFore = (TextView) getView().findViewById(R.id.forecastPM);
        desc= (TextView) getView().findViewById(R.id.forecastView);
        lowTemp = (TextView) getView().findViewById(R.id.low);
        highTemp = (TextView) getView().findViewById(R.id.high);
        amLabel = (TextView) getView().findViewById(R.id.am);
        pmLabel = (TextView) getView().findViewById(R.id.pm);
        img = (ImageView) getView().findViewById(R.id.image);
    }

    public void setFields(final WeatherInfo results, boolean units, int index)
    {
        final DayForecast day = results.forecast.get(index);


        //image
        String imgUrl = day.icon;
        String fileName = Uri.parse(imgUrl).getLastPathSegment();
        new DownloadImageTask(img, ctx)
                .execute(imgUrl);

        //set fields
        location.setText(results.location.name);
        amLabel.setBackgroundColor(0xffccffff);
        pmLabel.setBackgroundColor(0xffb84dff);
        if(day.amForecast != null)  //am forecast data is occasionally null depending on time of day
        {
            timeDay.setText(day.amForecast.timeDesc);   //set day to the am desc
            System.out.println("Forecast Details is "+ day.amForecast.details);
            amFore.setText(day.amForecast.description);
            desc.setText(day.amForecast.details);       //set the visible desc to the am as default
            desc.setBackgroundColor(0xffccffff);
            setTemp(units, true, day);
        }
        else {
            timeDay.setText(day.pmForecast.timeDesc); //set day time to the pm desc
            desc.setText(day.pmForecast.details);     //set the visible desc to the pm as default
            System.out.println("Forecast Details is "+ day.pmForecast.details);
            desc.setBackgroundColor(0xffb84dff);

        }

        //set pm data if not null
        if(day.pmForecast != null)
        {
            pmFore.setText(day.pmForecast.description);
            System.out.println("Forecast Details is "+ day.amForecast.details);

            setTemp(units, false, day);
        }
        amLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(day.amForecast !=null) {
                    desc.setText(day.amForecast.details);
                    desc.setBackgroundColor(0xffccffff);
                }

            }
        });

        pmLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(day.pmForecast !=null) {
                    desc.setText(day.pmForecast.details);
                    System.out.println("Forecast Details is "+ day.pmForecast.details);
                    desc.setBackgroundColor(0xffb84dff);
                }

            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = Double.toString(results.location.latitude);
                String lon = Double.toString(results.location.longitude);
                String geo = "geo:"+lat+","+lon;

                geoIntent(Uri.parse(geo));
            }
        });


    }
    public void geoIntent(Uri field){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(field);
        if(intent.resolveActivity(ctx.getPackageManager()) != null)
            startActivity(intent);
    }

    public void setTemp (boolean units, boolean dayTime, DayForecast day)
    {
        if(dayTime) //setting amTemp
        {
            if(units) //setting imperial
            {
                highTemp.setText(Double.toString(day.amForecast.temperature) + "\u2109");
            }
            else //setting metric
                highTemp.setText(Double.toString(((int)(((day.amForecast.temperature-32)/1.8)*100))/100.0)+ "\u2103");
        }
        else //setting night time
        {
            if(units) //setting imperial
            {
                lowTemp.setText(Double.toString(day.pmForecast.temperature) + "\u2109");
            }
            else //setting metric
                lowTemp.setText(Double.toString(((int)(((day.pmForecast.temperature-32)/1.8)*100))/100.0)+ "\u2103");
        }
    }
}
