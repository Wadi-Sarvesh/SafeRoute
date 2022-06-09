package com.wadis.saferoute;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener , LocationListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener
        , OnMapReadyCallback , ExampleDialog.ExampleDialogListener {


    ArrayList<LatLng> listPoints;
    ArrayList<Marker> MarkerIdList;



    SearchView Search_bar;

    //json parsing
    //Button Jsonparse;
    TextView txtJson;
    ProgressDialog pd;
    List<Address> addressList = null;


    //new
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    //Button satView;
    //FloatingActionButton fab;




    /*Location currentLocation;
    FusedLocationProviderClient fusedlocationproviderclient;*/
    private static final int REQUEST_CODE = 99;


    //private static final LatLng Sydney = new LatLng(-33.87365, 151.20689);
    private  static  LatLng latLng;
    private static final double DEFAULT_RADIUS_METERS = 500;
    private static final double RADIUS_OF_EARTH_METERS = 6371009;

    /*private static final int MAX_WIDTH_PX = 50;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;*/

    /*private static final int PATTERN_DASH_LENGTH_PX = 100;
    private static final int PATTERN_GAP_LENGTH_PX = 200;
    private static final Dot DOT = new Dot();
    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);*/

    private GoogleMap map;

    private List<DraggableCircle> circles = new ArrayList<>(1);


    private int fillColorArgb;
    private int strokeColorArgb;

    /*private SeekBar fillHueBar;
    private SeekBar fillAlphaBar;
    private SeekBar strokeWidthBar;
    private SeekBar strokeHueBar;
    private SeekBar strokeAlphaBar;
    private Spinner strokePatternSpinner;
    private CheckBox clickabilityCheckbox;*/


    // These are the options for stroke patterns. We use their
    // string resource IDs as identifiers.

    private static final int[] PATTERN_TYPE_NAME_RESOURCE_IDS = {
            R.string.pattern_solid, // Default
            R.string.pattern_dashed,
            R.string.pattern_dotted,
            R.string.pattern_mixed,
    };

    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;
        if(currentUserLocationMarker != null){

            currentUserLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title("Current Location").snippet(location.getLatitude()+","+location.getLongitude())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentUserLocationMarker = map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomBy(17));
        //DraggableCircle circle = new DraggableCircle(latLng, DEFAULT_RADIUS_METERS);
        //circles.add(circle);
        listPoints.clear();

        if(googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest =new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest , this);
        }




    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void applyCordinates(Double latitude, Double longitude) {

        LatLng point = new LatLng(latitude, longitude);
        View view = Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.map)).getView();
        assert view != null;
        LatLng radiusLatLng = map.getProjection().fromScreenLocation(new Point(
                view.getHeight() /2, view.getWidth() /2));
        Log.d("Height","Value " + view.getHeight());
        Log.d("Width","Value " + view.getWidth());



        // Create the circle.
        DraggableCircle circle = new DraggableCircle(point, 3000);
        circles.add(circle);


    }

    private class DraggableCircle {
        private final Marker centerMarker;
        private final Marker radiusMarker;
        private final Circle circle;
        private double radiusMeters;




        public DraggableCircle(LatLng center, double radiusMeters) {


            this.radiusMeters = radiusMeters;
            centerMarker = map.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(true)
                    .title("Coordinates")
                    .snippet(center.latitude+","+center.longitude)
                    .visible(false)
                    );

            radiusMarker = map.addMarker(new MarkerOptions()
                    .position(toRadiusLatLng(center, radiusMeters))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE))
                    .title("Coordinates")
                    .snippet(toRadiusLatLng(center, radiusMeters).latitude+","+toRadiusLatLng(center, radiusMeters).longitude)
                    .visible(false)
                    );
            //Random r = new Random();
            //final int random = r.nextInt(1000);
            circle = map.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radiusMeters)
                    .strokeWidth(15)
                    .strokeColor(strokeColorArgb)
                    .fillColor(fillColorArgb)
                    .clickable(true));
            //circle.setTag(random);
            /*GoogleMap.OnCircleClickListener onCircleClickListener = new GoogleMap.OnCircleClickListener() {
                @Override
                public void onCircleClick(Circle circle) {
                    if (circle.getTag() != 0) {
                        // parse it
                        switch (circle.getTag()) {
                            case ID_1: break;
                            case ID_2: break;
                        }
                    }
                    Log.d("gd","circle clicked!");
                }
            };
            map.setOnCircleClickListener(onCircleClickListener);*/
        }


        public boolean onMarkerMoved(Marker marker) {
            if (marker.equals(centerMarker)) {
                circle.setCenter(marker.getPosition());
                radiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), radiusMeters));
                return true;
            }
            if (marker.equals(radiusMarker)) {
                radiusMeters =
                        toRadiusMeters(centerMarker.getPosition(), radiusMarker.getPosition());
                circle.setRadius(radiusMeters);
                return true;
            }
            return false;
        }

        /*public void onStyleChange() {
            circle.setStrokeWidth(15);
            circle.setStrokeColor(strokeColorArgb);
            circle.setFillColor(fillColorArgb);
        }

        public void setStrokePattern(List<PatternItem> pattern) {
            circle.setStrokePattern(pattern);
        }

        public void setClickable(boolean clickable) {
            circle.setClickable(clickable);
        }*/
    }

    /** Generate LatLng of radius marker */
    private static LatLng toRadiusLatLng(LatLng center, double radiusMeters) {
        double radiusAngle = Math.toDegrees(radiusMeters / RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Toolbar Report_option =findViewById(R.id.report_option);
        Search_bar= findViewById(R.id.search_bar);

        Search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                 String search_query = Search_bar.getQuery().toString();
                 LatLng current_latlng =new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());

                List<Address> Searchlist = null;
                if(search_query != null || !search_query.equals("")){
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        Searchlist=geocoder.getFromLocationName(search_query,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(!Searchlist.isEmpty()){
                    Address search_address = Searchlist.get(0);
                    LatLng search_latlng = new LatLng(search_address.getLatitude(),search_address.getLongitude());
                    map.addMarker(new MarkerOptions().position(search_latlng).title(search_query).snippet(search_latlng.latitude+","+search_latlng.longitude));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(search_latlng,17));
                    String url = getRequestUrl(current_latlng,search_latlng);
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);}
                    else
                    { Toast.makeText(getApplicationContext(),"Location not found",Toast.LENGTH_SHORT).show();}
                }
                else if(search_query.equals(""))
                {Toast.makeText(getApplicationContext(),"Please enter the location",Toast.LENGTH_SHORT).show();}
                return false;


            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        //Json parsing
        //Jsonparse = (Button) findViewById(R.id.jsonparser);


        //txtJson = (TextView) findViewById(R.id.tvJsonItem);

        /*Jsonparse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JsonTask().execute("https://api.covid19india.org/raw_data13.json");
            }
        });*/
        //Button satView = findViewById(R.id.satelliteView);

        /*FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }

            private void openDialog() {
                ExampleDialog exampleDialog = new ExampleDialog();
                exampleDialog.show(getSupportFragmentManager(), "report dialog");
            }
        });*/
        /*satView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(map.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                {
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else
                {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });*/

        MarkerIdList = new ArrayList<>();
        listPoints = new ArrayList<>();
        listPoints.clear();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }

        //fusedlocationproviderclient = LocationServices.getFusedLocationProviderClient(this);
        //fetchLastLocation();

        /*fillHueBar = findViewById(R.id.fillHueSeekBar);
        fillHueBar.setMax(MAX_HUE_DEGREES);
        fillHueBar.setProgress(MAX_HUE_DEGREES / 2);

        fillAlphaBar = findViewById(R.id.fillAlphaSeekBar);
        fillAlphaBar.setMax(MAX_ALPHA);
        fillAlphaBar.setProgress(MAX_ALPHA / 2);

        strokeWidthBar = findViewById(R.id.strokeWidthSeekBar);
        strokeWidthBar.setMax(MAX_WIDTH_PX);
        strokeWidthBar.setProgress(MAX_WIDTH_PX / 3);

        strokeHueBar = findViewById(R.id.strokeHueSeekBar);
        strokeHueBar.setMax(MAX_HUE_DEGREES);
        strokeHueBar.setProgress(0);

        strokeAlphaBar = findViewById(R.id.strokeAlphaSeekBar);
        strokeAlphaBar.setMax(MAX_ALPHA);
        strokeAlphaBar.setProgress(MAX_ALPHA);*/

        /*strokePatternSpinner = findViewById(R.id.strokePatternSpinner);
        strokePatternSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(PATTERN_TYPE_NAME_RESOURCE_IDS)));

        clickabilityCheckbox = findViewById(R.id.toggleClickability);*/

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    //inflater for report menu toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report, menu);
        return true;

    }
    //To do when menuitem is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.report_option:
                openDialog();
                break;

            case R.id.change_view:
                if(map.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                {
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else
                {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                break;

            case R.id.parse_json:
                new JsonTask().execute("https://api.covid19india.org/raw_data13.json");
                break;



        }
        return true;

    }
    private void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "report dialog");
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();



        }

        protected String doInBackground(String... params) {

            int count= 0;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String jsonbuffer;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    //Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

               jsonbuffer = buffer.toString();
                if(jsonbuffer != null)
                {


                    try {
                        ArrayList<String> coordinates_list = new ArrayList<>();
                        ArrayList<String> district_list = new ArrayList<>();
                        JSONObject json = new JSONObject(jsonbuffer);
                        JSONArray jsonResponse = json.getJSONArray("raw_data");
                        Geocoder geocoder = new Geocoder(MainActivity.this);

                        for(int i=0;i<300;i++){
                            count++;
                            JSONObject object = jsonResponse.getJSONObject(i);
                            String detecteddistrict = object.getString("detecteddistrict");
                            district_list.add(detecteddistrict);
                            //Log.d("district_list",district_list.toString());
                            int progress = 100* count/300;
                            pd.setMessage("Loading Hotspots... " + (int) progress + "%");

                            try {
                                addressList = geocoder.getFromLocationName(detecteddistrict,1);
                                //Log.d("addresslist",addressList.toString());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            if(!addressList.isEmpty())
                            {
                                Address address =  addressList.get(0);
                                String Lat= String.valueOf(address.getLatitude());
                                String Lng = String.valueOf(address.getLongitude());
                                coordinates_list.add(Lat + "," + Lng);
                                //Log.d("coordinates_list",coordinates_list.toString());
                            //txtJson.setText(detecteddistrict);
                            //JsonLocationMarker(detecteddistrict);
                            }
                        }

                    return coordinates_list.toString();

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }




            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }

            result=result.substring(1, result.length() - 1);
            Log.d("coordinates_list",result);
            String str[] = result.split(",");
            List<String> coordinates_list;
            coordinates_list = Arrays.asList(str);
            JsonLocationMarker(coordinates_list);



        }
    }

        private void JsonLocationMarker(List<String> string )
        {

            for(int k = 1;k<string.size()-1;k+=2)
            {



                  double lat= Double.parseDouble(string.get(k+1));
                  double lng = Double.parseDouble(string.get(k));
                LatLng JsonPoint = new LatLng(lat,lng);
                //Log.d("latitudess",(String.valueOf(lat)));
                //Log.d("lngtds",(String.valueOf(lng)));
                /*View view = Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.map)).getView();
                assert view != null;
                LatLng radiusLatLng = map.getProjection().fromScreenLocation(new Point(
                        view.getHeight() /2, view.getWidth() /2));
                Log.d("Height","Value " + view.getHeight());
                Log.d("Width","Value " + view.getWidth());*/

                // Create the circle.
                DraggableCircle circle = new DraggableCircle(JsonPoint,3000);
                circles.add(circle);


                //addressList.clear();
            }


        }
    /*private void fetchLastLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedlocationproviderclient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MainActivity.this);
                }
            }
        });
    }*/


   /* private String[] getResourceStrings(int[] resourceIds) {
        String[] strings = new String[resourceIds.length];
        for (int i = 0; i < resourceIds.length; i++) {
            strings[i] = getString(resourceIds[i]);
        }
        return strings;
    }*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Override the default content description on the view, for accessibility mode.
        googleMap.setContentDescription(getString(R.string.map_circle_description));
        /*LatLng myloc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(myloc);
        map.animateCamera(CameraUpdateFactory.newLatLng(myloc));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myloc, 10));
        map.addMarker(markerOptions);*/

        map = googleMap;
        map.setPadding(0,160,35,0);
        map.setOnMarkerDragListener(this);
        map.setOnMapLongClickListener(this);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {buildGoogleApiClient();
        map.setMyLocationEnabled(true);}






        //passing location from json file to geocoder to get co-ordinates





        fillColorArgb = Color.HSVToColor(
                240, new float[]{360, 1, 1});
        strokeColorArgb = Color.HSVToColor(
                255, new float[]{350, 1, 1});

        /*fillHueBar.setOnSeekBarChangeListener(this);
        fillAlphaBar.setOnSeekBarChangeListener(this);

        strokeWidthBar.setOnSeekBarChangeListener(this);
        strokeHueBar.setOnSeekBarChangeListener(this);
        strokeAlphaBar.setOnSeekBarChangeListener(this);

        strokePatternSpinner.setOnItemSelectedListener(this);*/



        // Move the map so that it is centered on the initial circle
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4.0f));

        // Set up the click listener for the circle.
        googleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                // Flip the red, green and blue components of the circle's stroke color.
                circle.setStrokeColor(circle.getStrokeColor() ^ 0x00ffffff);
            }
        });

        //List<PatternItem> pattern = getSelectedPattern(strokePatternSpinner.getSelectedItemPosition());
        /*for (DraggableCircle draggableCircle : circles) {
            draggableCircle.setStrokePattern(pattern);
        }*/
        listPoints.clear();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("LIst Size", String.valueOf(listPoints.size()));
                if(listPoints.size()<=1)
                {   Log.d("check","Entering if 1 ");
                listPoints.add(marker.getPosition());}
                Log.d("LIst Size", String.valueOf(listPoints.size()));



                //If first marker is added to list by clicking then only another click should be initiated for adding 2nd marker
                if(listPoints.size()== 2)
                { Log.d("check","Entering if 2 ");
                    Log.d("Latlanglist","1st element" + listPoints.get(0));
                    Log.d("Latlang list","2nd element" + listPoints.get(1));
                    String url = getRequestUrl(listPoints.get(0),listPoints.get(1));
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                    MarkerIdList.clear();
                    listPoints.clear();
                }
                return false;
            }
        });
    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=driving";
        String param =   str_org +"&" +str_dest  +"&" +mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?&" + param+ "&key=" + getString(R.string.map_key);
        Log.d("URL",url);
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString ="";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL  url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream =httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line=bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);

            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream != null)
            {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
    return responseString;
    }

    public class TaskRequestDirections extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
             String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String,String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String,String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;

        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            for(List<HashMap<String, String>> path : lists)
            {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                for (HashMap<String, String> point : path)
                {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    points.add(new LatLng(lat,lng));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);

            }
            if(polylineOptions != null)
            {map.addPolyline(polylineOptions);}
            else
            {Toast.makeText(getApplicationContext(),"Direction Not Found",Toast.LENGTH_SHORT).show();}
        }
    }

    public  boolean checkUserLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
    }




    /*private List<PatternItem> getSelectedPattern(int pos) {
        switch (PATTERN_TYPE_NAME_RESOURCE_IDS[pos]) {
            case R.string.pattern_solid:
                return null;
            case R.string.pattern_dotted:
                return PATTERN_DOTTED;
            case R.string.pattern_dashed:
                return PATTERN_DASHED;
            case R.string.pattern_mixed:
                return PATTERN_MIXED;
            default:
                return null;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.strokePatternSpinner) {
            for (DraggableCircle draggableCircle : circles) {
                draggableCircle.setStrokePattern(getSelectedPattern(i));
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Don't do anything here.

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == fillHueBar) {
            fillColorArgb =
                    Color.HSVToColor(Color.alpha(fillColorArgb), new float[]{progress, 1, 1});
        } else if (seekBar == fillAlphaBar) {
            fillColorArgb = Color.argb(progress, Color.red(fillColorArgb),
                    Color.green(fillColorArgb), Color.blue(fillColorArgb));
        } else if (seekBar == strokeHueBar) {
            strokeColorArgb =
                    Color.HSVToColor(Color.alpha(strokeColorArgb), new float[]{progress, 1, 1});
        } else if (seekBar == strokeAlphaBar) {
            strokeColorArgb = Color.argb(progress, Color.red(strokeColorArgb),
                    Color.green(strokeColorArgb), Color.blue(strokeColorArgb));
        }

        for (DraggableCircle draggableCircle : circles) {
            draggableCircle.onStyleChange();
        }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.

    }*/

    @Override
    public void onMapLongClick(LatLng point) {

        if(listPoints.size()==2)
        {
            listPoints.clear();
            map.clear();

        }
        // We know the center, let's place the outline at a point 3/4 along the view.
       /* View view = Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.map)).getView();
        assert view != null;
        LatLng radiusLatLng = map.getProjection().fromScreenLocation(new Point(
                view.getHeight() /2, view.getWidth() /2));
        Log.d("Height","Value " + view.getHeight());
        Log.d("Width","Value " + view.getWidth());



        // Create the circle.
        DraggableCircle circle = new DraggableCircle(point, toRadiusMeters(point, radiusLatLng));
        circles.add(circle);*/

    }
    /*public void toggleClickability(View view) {
        boolean clickable = ((CheckBox) view).isChecked();
        // Set each of the circles to be clickable or not, based on the
        // state of the checkbox.
        for (DraggableCircle draggableCircle : circles) {
            draggableCircle.setClickable(clickable);
        }
    }*/


    @Override
    public void onMarkerDragStart(Marker marker) {
        onMarkerMoved(marker);

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        onMarkerMoved(marker);

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        onMarkerMoved(marker);
    }

    private void onMarkerMoved(Marker marker) {
        for (DraggableCircle draggableCircle : circles) {
            if (draggableCircle.onMarkerMoved(marker)) {
                break;
            }
        }
    }




}