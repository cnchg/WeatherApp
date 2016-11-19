package com.tricloudcommunications.ce.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompatBase;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText cityInput;
    Switch unitSwitch;
    TextView localTempTextView;
    TextView localLocationTextView;
    TextView localConditionTextView;
    TextView localConditionDescriptionTextView;
    String unitType = "";

    //Local weather variables
    String localLongitude = "";
    String localLatitude = "";
    String localName;
    String localCondition = "";
    String localConditionDeescription = "";
    String localConditionIcon = "";
    String localForecastHigh = "";
    String localForecastLow  = "";
    double localTemp;
    String localTempFinal = "";

    public void getWeather(View view) {

        String city = String.valueOf(cityInput.getText()).trim();
        //city = city.trim();
        city = city.replaceAll(" ", "%20");

        Log.i("Button Status: ", "Button has been clicked and City Entered: " + city);

        try {

            if (city.length() > 1) {

                Log.i("City Status: ", city);
                Log.i("Unit Type", unitType);

                DownLoadTask task = new DownLoadTask();
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + city + ",us&units=" + unitType + "&appid=968ed395d494be9817a5c648ed7aa697");


            } else {

                Log.i("City Status: ", "No City Entered");
                Log.i("Unit Type", unitType);

                Log.i("City Status: ", "A City Was Not Entered");
                Toast.makeText(getApplicationContext(), "Please Enter A City or a Zip Code", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Somw thing Went Wrong", Toast.LENGTH_LONG).show();
        }


    }

    public void getCurrentLocation(){

        //Get the Current user location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Conditions for asking user permission to access location information
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        localLongitude = String.valueOf(longitude);
        localLatitude = String.valueOf(latitude);

        Log.i("longitude", String.valueOf(longitude));
        Log.i("latitude", String.valueOf(latitude));


    }

    public void executeLocalWeather(){

        //start and execute the LocalWeather() class that you wrote below
        LocalWeather locWeather = new LocalWeather();
        locWeather.execute("http://api.openweathermap.org/data/2.5/weather?lat="+localLatitude+"&lon="+localLongitude+"&units="+unitType+"&appid=968ed395d494be9817a5c648ed7aa697");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Will will pop up when user click editView.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        cityInput = (EditText) findViewById(R.id.enterLocationEditText);
        unitSwitch = (Switch) findViewById(R.id.unitSwitch);
        localTempTextView = (TextView) findViewById(R.id.localTempTextView);
        localLocationTextView = (TextView) findViewById(R.id.localLocationTextView);
        localConditionTextView = (TextView) findViewById(R.id.localConditionTextView);
        localConditionDescriptionTextView = (TextView) findViewById(R.id.localConditionDescriptionTextView);

        //Call the get user GPS location data like latitude and longitude
        getCurrentLocation();

        //Check the position if the forecast unit type  like Celcius of Ferehenhight
        if (unitSwitch.isChecked()) {
            Log.i("Switch Status", String.valueOf(unitSwitch.getTextOn()));
            unitType = "metric";

        } else {
            Log.i("Switch Status", String.valueOf(unitSwitch.getTextOff()));
            unitType = "imperial";
        }

        //Call the localWeather function which will execute the localWeather class
        executeLocalWeather();

        //Switch Even listener. Listen for when the switch is toggled
        unitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //Check the position if the forecast unit type  like Celcius of Ferehenhight
                if (unitSwitch.isChecked()) {
                    Log.i("Switch Status", String.valueOf(unitSwitch.getTextOn()));
                    unitType = "metric";
                    executeLocalWeather();

                } else {
                    Log.i("Switch Status", String.valueOf(unitSwitch.getTextOff()));
                    unitType = "imperial";
                    executeLocalWeather();
                }


            }

        });


    }

    public class DownLoadTask extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try{

                JSONObject jsonObject = new JSONObject(result);

                JSONObject mainInfo = jsonObject.getJSONObject("main");
                Log.i("Main Temp", mainInfo.getString("temp"));


                String weatherInfo = jsonObject.getString("weather");
                JSONArray jsonArrWeatherInfo = new JSONArray(weatherInfo);
                for (int i = 0; i < jsonArrWeatherInfo.length(); i++){

                    JSONObject jsonPartWeatherInfo = jsonArrWeatherInfo.getJSONObject(i);

                    Log.i("main", jsonPartWeatherInfo.getString("main"));
                    Log.i("description", jsonPartWeatherInfo.getString("description"));

                    //currentTemp.setText("Temp: " + jsonPartWeatherInfo.getString("main"));
                    Log.i("API Content", result);

                }

            }catch (Exception e){

                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Something Went Wrong",Toast.LENGTH_LONG).show();
            }

        }
    }

    public class LocalWeather extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlconnection;

            try {

                url = new URL (urls[0]);
                urlconnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlconnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1){

                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();

            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONObject mainInfo = jsonObject.getJSONObject("main");

                localName = jsonObject.getString("name");
                localTemp = mainInfo.getDouble("temp");
                localTempFinal = String.format("%.0f", localTemp);
                localForecastHigh = mainInfo.getString("temp_max");
                localForecastLow = mainInfo.getString("temp_min");

                //Set the data in the TextView
                localLocationTextView.setText(localName);
                localTempTextView.setText(localTempFinal + (char) 0x00B0); // to add the degree symbol to the TextView use: localTempTextView.setText(localTempFinal + (char) 0x00B0)

                String locaWeatherInfo = jsonObject.getString("weather");
                JSONArray jsonArrLocalWeatherInfo = new JSONArray(locaWeatherInfo);
                for (int i = 0; i < jsonArrLocalWeatherInfo.length(); i++){

                    JSONObject jsonPartLocalWeatherInfo = jsonArrLocalWeatherInfo.getJSONObject(i);

                    localCondition = jsonPartLocalWeatherInfo.getString("main");
                    localConditionDeescription = jsonPartLocalWeatherInfo.getString("description");
                    localConditionIcon = jsonPartLocalWeatherInfo.getString("icon");
                    
                    //Set the data in the TextView
                    localConditionTextView.setText(localCondition);
                    localConditionDescriptionTextView.setText(localConditionDeescription);

                    Log.i("Main", jsonPartLocalWeatherInfo.getString("main"));
                    Log.i("Main Description", jsonPartLocalWeatherInfo.getString("description"));

                }

                Log.i("Local Temp", mainInfo.getString("temp"));
                Log.i("Local Name", localName);


            } catch (JSONException e) {

                e.printStackTrace();

            }

            Log.i("Coordinates API Content", result);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
