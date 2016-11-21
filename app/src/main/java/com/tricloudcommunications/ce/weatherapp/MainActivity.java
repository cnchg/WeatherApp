package com.tricloudcommunications.ce.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompatBase;
import android.support.v4.graphics.ColorUtils;
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
import android.widget.ImageView;
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
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ImageView nearMeImageView;
    ImageView localConditionIconImageView;
    ImageView localForecastHighIconImageView;
    ImageView localForecastLowIconImageView;
    EditText cityInput;
    Switch unitSwitch;
    GridLayout forecastGridLauout;
    TextView localTempTextView;
    TextView localLocationTextView;
    TextView localConditionTextView;
    TextView localConditionDescriptionTextView;
    TextView localForeCastHighTextView;
    TextView localForecastLowTextView;
    String unitType = "";

    //Local weather variables
    String localLongitude = "";
    String localLatitude = "";
    String localName;
    String localCondition = "";
    String localConditionDeescription = "";
    String localConditionIcon = "";
    double localForecastHigh;
    String localForecastHighFinal = "";
    double localForecastLow;
    String localForecastLowFinal = "";
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

                //DownLoadTask task = new DownLoadTask();
                //task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + city + ",us&units=" + unitType + "&appid=968ed395d494be9817a5c648ed7aa697");

                //start and execute the LocalWeather() class that you wrote below
                LocalWeather locWeather = new LocalWeather();
                locWeather.execute("http://api.openweathermap.org/data/2.5/weather?q=" + city + ",us&units=" + unitType + "&appid=968ed395d494be9817a5c648ed7aa697");


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
        nearMeImageView.setVisibility(View.VISIBLE);

        Log.i("longitude", String.valueOf(longitude));
        Log.i("latitude", String.valueOf(latitude));


    }

    public void executeLocalWeather(){

        //start and execute the LocalWeather() class that you wrote below
        LocalWeather locWeather = new LocalWeather();
        locWeather.execute("http://api.openweathermap.org/data/2.5/weather?lat="+localLatitude+"&lon="+localLongitude+"&units="+unitType+"&appid=968ed395d494be9817a5c648ed7aa697");


    }

    public void executeWeatherIconDownloader(String iconImageName){

        //start and execute the ImageDownloader() class to download the weather icon image
        ImageDownloader downloadImageTask = new ImageDownloader();
        Bitmap myImage;

        try {

            myImage = downloadImageTask.execute("http://openweathermap.org/img/w/"+iconImageName+".png").get();
            //myImage = downloadImageTask.execute("http://openweathermap.org/img/w/10d.png").get();

            //set the image in the Image View
            localConditionIconImageView.setImageBitmap(myImage);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


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

        nearMeImageView = (ImageView) findViewById(R.id.nearMeImageView);
        localConditionIconImageView = (ImageView) findViewById(R.id.localConditionIconImageView);
        localForecastHighIconImageView = (ImageView) findViewById(R.id.localForeCastHighIconImageView);
        localForecastLowIconImageView = (ImageView) findViewById(R.id.localForecastLowIconImageView);
        cityInput = (EditText) findViewById(R.id.enterLocationEditText);
        unitSwitch = (Switch) findViewById(R.id.unitSwitch);
        forecastGridLauout = (GridLayout) findViewById(R.id.forecastGridLayout);
        localTempTextView = (TextView) findViewById(R.id.localTempTextView);
        localLocationTextView = (TextView) findViewById(R.id.localLocationTextView);
        localConditionTextView = (TextView) findViewById(R.id.localConditionTextView);
        localConditionDescriptionTextView = (TextView) findViewById(R.id.localConditionDescriptionTextView);
        localForeCastHighTextView = (TextView) findViewById(R.id.localForeCastHighTextView);
        localForecastLowTextView = (TextView) findViewById(R.id.localForecastLowTextView);


        /* Set the background alpha color for the forecastGridLayoutint
            alpha = 85;
            textView.setBackgroundColor(ColorUtils.setAlphaComponent(Color.Red,alpha));

            Source: http://stackoverflow.com/questions/15319635/manipulate-alpha-bytes-of-java-android-color-int
        */
        int alpha = 85;
        forecastGridLauout.setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK,alpha));

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
                localForecastHigh = mainInfo.getDouble("temp_max");
                localForecastHighFinal = String.format("%.0f", localForecastHigh);
                localForecastLow = mainInfo.getDouble("temp_min");
                localForecastLowFinal = String.format("%.0f", localForecastLow);

                //Set the data in the TextView
                localLocationTextView.setText(localName);
                localTempTextView.setText(localTempFinal + (char) 0x00B0); // to add the degree symbol to the TextView use: localTempTextView.setText(localTempFinal + (char) 0x00B0)
                localForeCastHighTextView.setText(localForecastHighFinal + (char) 0x00B0);// to add the degree symbol to the TextView use: localTempTextView.setText(localTempFinal + (char) 0x00B0)
                localForecastLowTextView.setText(localForecastLowFinal + (char) 0x00B0);// to add the degree symbol to the TextView use: localTempTextView.setText(localTempFinal + (char) 0x00B0)

                String localWeatherInfo = jsonObject.getString("weather");
                JSONArray jsonArrLocalWeatherInfo = new JSONArray(localWeatherInfo);
                for (int i = 0; i < jsonArrLocalWeatherInfo.length(); i++){

                    JSONObject jsonPartLocalWeatherInfo = jsonArrLocalWeatherInfo.getJSONObject(i);

                    localCondition = jsonPartLocalWeatherInfo.getString("main");
                    localConditionDeescription = jsonPartLocalWeatherInfo.getString("description");
                    localConditionIcon = jsonPartLocalWeatherInfo.getString("icon");
                    localForecastHighIconImageView.setVisibility(View.VISIBLE);
                    localForecastLowIconImageView.setVisibility(View.VISIBLE);
                    
                    //Set the data in the TextView
                    localConditionTextView.setText(localCondition);
                    localConditionDescriptionTextView.setText(localConditionDeescription);

                    //Call the executeWetherIconDownloader function which will execute the ImageDownLoader class whioh sets the images in the Images View
                    executeWeatherIconDownloader(localConditionIcon);

                    Log.i("Main", jsonPartLocalWeatherInfo.getString("main"));
                    Log.i("Main Description", jsonPartLocalWeatherInfo.getString("description"));
                    Log.i("Main Icon", jsonPartLocalWeatherInfo.getString("icon"));

                }

                Log.i("Local Temp", mainInfo.getString("temp"));
                Log.i("Local Name", localName);


            } catch (JSONException e) {

                e.printStackTrace();

            }

            Log.i("Coordinates API Content", result);

        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {

            String result;
            URL url;
            HttpURLConnection urlConnection;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
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
