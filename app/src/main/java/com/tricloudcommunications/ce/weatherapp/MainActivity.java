package com.tricloudcommunications.ce.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {

    RelativeLayout contentMainBackground;
    ImageView nearMeImageView;
    ImageView localConditionIconImageView;
    ImageView localForecastHighIconImageView;
    ImageView localForecastLowIconImageView;
    ImageView localGPSFix;
    EditText cityInput;
    Switch unitSwitch;
    GridLayout forecastGridLauout;
    TextView localTempTextView;
    TextView localLocationTextView;
    TextView localConditionTextView;
    TextView localConditionDescriptionTextView;
    TextView localForeCastHighTextView;
    TextView localForecastLowTextView;
    String city = "";
    String unitType = "";
    Boolean weatherSearch = false;

    //Local weather variables
    String localLongitude = "";
    String localLatitude = "";
    String localName;
    String localCondition = "";
    String localConditionDescription = "";
    String localConditionIcon = "";
    double localForecastHigh;
    String localForecastHighFinal = "";
    double localForecastLow;
    String localForecastLowFinal = "";
    double localTemp;
    String localTempFinal = "";
    String localLocationCountry = "";
    String localLocationSunRise = "";
    String localLocationSunSunset = "";

    //Flickr Variables
    String flickrPhotoID = "";
    String flickrPhotoOwnerID = "";
    String flickrPhotoFarmID = "";
    String flickrPhotoServerID = "";
    String flickrPhotoSecret = "";


    public void getWeather(View view) {

        weatherSearch = true;

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(cityInput.getWindowToken(), 0);

        city = String.valueOf(cityInput.getText()).trim(); //Remove white spaces from the begining and end of string
        //city = city.trim();//Remove white spaces from the begining and end of string
        //city = city.replaceAll(" ", "%20");// Replaces the whitespace between characters in the string with '%20' for propoer http url format

        Log.i("Button Status: ", "Button has been clicked and City Entered: " + city);

        try {

            city = URLEncoder.encode(city, "UTF-8");// Properly format the string to URL format

            if (city.length() > 1) {

                Log.i("City Status: ", city);
                Log.i("Unit Type", unitType);

                executeWeather();

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

    public void localFixGPS(View view){

        weatherSearch = false;
        cityInput.setText("");
        executeWeather();

        Log.i("GPS Fix Triggered", "true");

    }

    public void executeWeather() {

        String weatherAppId = "";//use your Openweathermap.org api app id

        //start and execute the LocalWeather() class that you wrote below
        LocalWeather locWeather = new LocalWeather();

        if (weatherSearch.equals(false)) {

            localGPSFix.setVisibility(View.INVISIBLE);

            locWeather.execute("http://api.openweathermap.org/data/2.5/weather?lat=" + localLatitude + "&lon=" + localLongitude + "&units=" + unitType + "&appid="+weatherAppId);

            //Log.i("Weather Search is: ", weatherSearch.toString() + " lat:" + localLatitude + " lon:" + localLongitude + " unitType:" + unitType);


        } else {

            localGPSFix.setVisibility(View.VISIBLE);
            nearMeImageView.setVisibility(View.INVISIBLE);

            locWeather.execute("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=" + unitType + "&appid="+weatherAppId);//Global cities
            //locWeather.execute("http://api.openweathermap.org/data/2.5/weather?q=" + city + ",us&units=" + unitType + "&appid="+weatherAppId);//US cities only

            //Log.i("Weather Search is: ", weatherSearch.toString() + " City:" + city + " unitType:" + unitType);
        }

    }

    public void executeWeatherIconDownloader(String iconImageName) {

        //start and execute the ImageDownloader() class to download the weather icon image
        ImageDownloader downloadImageTask = new ImageDownloader();
        Bitmap myImage;

        try {

            myImage = downloadImageTask.execute("http://openweathermap.org/img/w/" + iconImageName + ".png").get();
            //myImage = downloadImageTask.execute("http://openweathermap.org/img/w/10d.png").get();

            //set the image in the Image View
            localConditionIconImageView.setImageBitmap(myImage);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setFlickrGlobalVariables(
            String flickrPhotoID,
            String flickrPhotoOwnerID,
            String flickrPhotoFarmID,
            String flickrPhotoServerID,
            String flickrPhotoSecret
    ){

        //Log.i("Photo ID", photoID);

        //String values of all the data we want from the JSON response
        //flickrPhotoID = photoID;
        //flickrPhotoOwnerID = photOwnerID;

        //FlickrImageDownloader flickrImageDownloader = new FlickrImageDownloader();
        //flickrImageDownloader.execute("http://www.flickr.com/photos/"+photoOwnerID+"/"+photoID);

        //start and execute the ImageDownloader() class to download the weather icon image
        FlickrImageDownloader downloadImageTask = new FlickrImageDownloader();
        Bitmap myImage;

        try {

            //myImage = downloadImageTask.execute("http://openweathermap.org/img/w/"+iconImageName+".png").get();
            //myImage = downloadImageTask.execute("http://openweathermap.org/img/w/10d.png").get();
            //myImage = downloadImageTask.execute("https://www.flickr.com/photos/"+photoOwnerID+"/"+photoID).get();
            myImage = downloadImageTask.execute("https://farm"+flickrPhotoFarmID+".staticflickr.com/"+flickrPhotoServerID+"/"+flickrPhotoID+"_"+flickrPhotoSecret+".jpg").get();

            //Source: http://stackoverflow.com/questions/7466900/how-to-use-relativelayout-setbackgrounddrawable-with-a-bitmap
            Drawable drawable = new BitmapDrawable(getResources(), myImage);
            contentMainBackground.setBackground(drawable);



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

        //Requesting Location Updates - Source: https://developer.android.com/guide/topics/location/strategies.html
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // Called when a new location is found by the network location provider.
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                localLongitude = String.valueOf(longitude);
                localLatitude = String.valueOf(latitude);
                nearMeImageView.setVisibility(View.VISIBLE);

                //Call the executeWeather function which will execute the localWeather class
                executeWeather();

                Log.i("longitude", String.valueOf(longitude));
                Log.i("latitude", String.valueOf(latitude));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
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
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (lastKnownLocation != null){

            // Called when a new location is found by the network location provider.
            double longitude = lastKnownLocation.getLongitude();
            double latitude = lastKnownLocation.getLatitude();
            localLongitude = String.valueOf(longitude);
            localLatitude = String.valueOf(latitude);
            //nearMeImageView.setVisibility(View.VISIBLE);
            //locationManager.removeUpdates(locationListener);

            Log.i("Last Known Location", lastKnownLocation.toString());

        }

        //Set up the global variables needed for this app
        contentMainBackground = (RelativeLayout) findViewById(R.id.content_main);
        nearMeImageView = (ImageView) findViewById(R.id.nearMeImageView);
        localConditionIconImageView = (ImageView) findViewById(R.id.localConditionIconImageView);
        localForecastHighIconImageView = (ImageView) findViewById(R.id.localForeCastHighIconImageView);
        localForecastLowIconImageView = (ImageView) findViewById(R.id.localForecastLowIconImageView);
        localGPSFix = (ImageView) findViewById(R.id.locationGPSFixImageView);
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


        //Check the position if the forecast unit type  like Celcius of Ferehenhight
        if (unitSwitch.isChecked()) {
            Log.i("Switch Status", String.valueOf(unitSwitch.getTextOn()));
            unitType = "metric";

        } else {
            Log.i("Switch Status", String.valueOf(unitSwitch.getTextOff()));
            unitType = "imperial";
        }


        //Switch Even listener. Listen for when the switch is toggled
        unitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //Check the position if the forecast unit type  like Celcius of Ferehenhight
                if (unitSwitch.isChecked()) {
                    Log.i("Switch Status", String.valueOf(unitSwitch.getTextOn()));
                    unitType = "metric";

                    executeWeather();

                } else {
                    Log.i("Switch Status", String.valueOf(unitSwitch.getTextOff()));
                    unitType = "imperial";

                    executeWeather();
                }

            }

        });


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
                String localLocationTime = jsonObject.getString("dt");

                JSONArray jsonArrLocalWeatherInfo = new JSONArray(localWeatherInfo);
                for (int i = 0; i < jsonArrLocalWeatherInfo.length(); i++){

                    JSONObject jsonPartLocalWeatherInfo = jsonArrLocalWeatherInfo.getJSONObject(i);

                    localCondition = jsonPartLocalWeatherInfo.getString("main");
                    localConditionDescription = jsonPartLocalWeatherInfo.getString("description");
                    localConditionIcon = jsonPartLocalWeatherInfo.getString("icon");
                    localForecastHighIconImageView.setVisibility(View.VISIBLE);
                    localForecastLowIconImageView.setVisibility(View.VISIBLE);
                    
                    //Set the data in the TextView
                    localConditionTextView.setText(localCondition);
                    localConditionDescriptionTextView.setText(localConditionDescription);

                    //Call the executeWetherIconDownloader function which will execute the ImageDownLoader class whioh sets the images in the Images View
                    executeWeatherIconDownloader(localConditionIcon);

                    Log.i("Main", jsonPartLocalWeatherInfo.getString("main"));
                    Log.i("Main Description", jsonPartLocalWeatherInfo.getString("description"));
                    Log.i("Main Icon", jsonPartLocalWeatherInfo.getString("icon"));

                }

                JSONObject sysInfo = jsonObject.getJSONObject("sys");
                localLocationCountry = sysInfo.getString("country");
                localLocationSunRise = sysInfo.getString("sunrise");
                localLocationSunSunset = sysInfo.getString("sunset");

                //code for converting localLocationSunRise from Unix time to data format
                long sunRise = Long.valueOf(localLocationSunRise)*1000;// its need to be in milisecond
                Date encodedSunrise = new java.util.Date(sunRise);
                String finalSunRise = new SimpleDateFormat("MM dd, yyyy hh:mma").format(encodedSunrise);

                //code for  converting localLocationSunSet from Unix time to data format
                long sunSet = Long.valueOf(localLocationSunSunset)*1000;// its need to be in milisecond
                Date encodedSunset = new java.util.Date(sunSet);
                String finalSunSet = new SimpleDateFormat("MM dd, yyyy hh:mma").format(encodedSunset);


                //Initialize the FlickrAPIDataDownload() class which downloads the API JSON data and trigger a call to the setFlickrGlobalVariables() method with then down load the image from Flickr and set it as the background image.
                try {
                    String flickrAppKey = ""; //Use your flickr App Key
                    String flickrTextTag = URLEncoder.encode(localConditionDescription,"UTF-8");// Properly format the string to URL format
                    String flickrTagsTag = URLEncoder.encode(localName, "UTF-8");

                    /*
                     Source1: https://www.flickr.com/services/api/explore/flickr.photos.search
                     Source2: https://www.flickr.com/services/api/misc.urls.html
                     Note: To get back a clean JSON response (no extra text in the front or back)-
                     Make sure that you 'Do not sign call' by adding this to the end of the url string '&format=json&nojsoncallback=1'
                    */
                    FlickrAPIDataDownload getFlickrDataTask = new FlickrAPIDataDownload();
                    //getFlickrDataTask.execute("http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key="+flickrAppKey+"&tags=sun&media=photos&per_page=1&page=1&format=json");
                    getFlickrDataTask.execute("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key="+flickrAppKey+"&text="+flickrTextTag+"&tags="+flickrTagsTag+"&media=photos&per_page=1&page=1&format=json&nojsoncallback=1");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //Log.i("Local Temp", mainInfo.getString("temp"));
                //Log.i("Local Name", localName);
                Log.i("Location Country", localLocationCountry);
                Log.i("Location Sunrise", localLocationSunRise);
                Log.i("Location Sunset", localLocationSunSunset);
                Log.i("Local Sun Rise Time", finalSunRise);
                Log.i("Local Sun Set Time", finalSunSet);

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

            } catch (Exception e) {

                e.printStackTrace();

            }

            return null;
        }

    }

    public class FlickrAPIDataDownload extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //relativeLayout.setBackgroundColor(Color.DKGRAY);

        }

        @Override
        protected String doInBackground(String... params) {

            /*
               Note: the code below from the source below is for making a HTTPS connection and then reading the response
               from that secure connection. In this case it's for Flickr but can be used in any other case where a secure
               Https connection needs to be establish before communicating with a host.
               Source1: https://www.codota.com/android/scenarios/52fcbd97da0ab8e225ec74c6/javax.net.ssl.HttpsURLConnection?tag=dragonfly
               Source2: http://stackoverflow.com/questions/16504527/how-to-do-an-https-post-from-android
            */

            String result = "";

            try{
                //Set up Https request
                URL url = new URL(params[0]);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                String input = params[0];
                Log.i("Flickr API Content","Sending: "+input);
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();
                os.close();

                //Read the data from the response
                InputStream is = conn.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while (data !=-1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                //Log.i("Flickr API Content","API call sent with result "+result);
                return result;

            } catch (IOException e) {
                //Log.e("Flickr API Content","Error sending API call", e);
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);



            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONObject photoObject = jsonObject.getJSONObject("photos");
                JSONArray photoArray = photoObject.getJSONArray("photo");

                /*
                NOTE:
                  We have only one photo in this exercise, if we wanted or had more photos
                  the we would use the array key number to get the photo with that key

                  Take a look at the chosen answer from this source:
                  http://stackoverflow.com/questions/19205527/json-parsing-in-android-flickr-api
                  Example of JSON for more than one Image in the photo array
                    JSONObject json = new JSONObject(jsonString);
                    JSONObject photos = json.getJSONObject("photos");
                    JSONArray photo = photos.getJSONArray("photo");
                    if (photo.length() > 0) {
                        JSONObject first = photo.getJSONObject(0);
                        String picOwner = first.getString("owner");
                        String picID = first.getString("id");
                    }
                */

                JSONObject flickerPhotos = photoArray.getJSONObject(0);

                flickrPhotoID = flickerPhotos.getString("id");
                flickrPhotoOwnerID = flickerPhotos.getString("owner");
                flickrPhotoFarmID = flickerPhotos.getString("farm");
                flickrPhotoServerID = flickerPhotos.getString("server");
                flickrPhotoSecret = flickerPhotos.getString("secret");


                setFlickrGlobalVariables(flickrPhotoID,flickrPhotoOwnerID,flickrPhotoFarmID,flickrPhotoServerID,flickrPhotoSecret);

                Log.i("Photo Object ID", flickrPhotoID);
                Log.i("Photo Object Owner", flickrPhotoOwnerID);
                Log.i("Photo Object farm", flickrPhotoFarmID);
                Log.i("Photo Object server", flickrPhotoServerID);
                Log.i("Photo Object secret", flickrPhotoSecret);


            } catch (JSONException e) {

                e.printStackTrace();

            }

            //Log.i("On Post Execute", result);
        }
    }

    public class FlickrImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            String result;
            URL url;
            HttpURLConnection urlConnection;

            try {

                //Set up Https request
                url = new URL(urls[0]);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                String input = urls[0];
                Log.i("Flickr API Content","Sending: "+input);
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();
                os.close();

                InputStream inputStream = conn.getInputStream();
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
