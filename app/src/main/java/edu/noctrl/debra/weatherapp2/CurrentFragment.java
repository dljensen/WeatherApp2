package edu.noctrl.debra.weatherapp2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentFragment.OnFragmentInteractionListener} interface
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //method to write weather data to the screen
    public void setFields(WeatherInfo results, boolean units)
    {
        //CHECK THAT THERE IS A ZIP SAVED
        double myTemp = results.current.temperature;
        double myDew = results.current.dewPoint;
        double myPressure = results.current.pressure;
        double myWindSpeed = results.current.windSpeed;
        double myVisibility = results.current.visibility;
        double myGust = results.current.gusts;

        String imageURL = results.current.imageUrl; //get the url of the weather image

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

        weatherImg = (ImageView) getView().findViewById(R.id.image);
        String fileName = Uri.parse(imageURL).getLastPathSegment();


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


        //add the image
        if(new File(ctx.getCacheDir(), fileName).exists()){
            //get image from cache and set
        }
        else{
            //download the image and put it in the view
        // show The Image in a ImageView
        new DownloadImageTask(weatherImg, ctx)
                .execute(imageURL);
        }

    }


}
