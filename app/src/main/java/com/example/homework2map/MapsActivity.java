package com.example.homework2map;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String location;
    private double lat;
    private double longi;
    private JSONObject info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getLocation(View view){
        //Fei driving
        EditText data =(EditText) findViewById(R.id.editText);
        location = data.getText().toString();
        Geocoder geo = new Geocoder(getApplicationContext());
        List<Address> locationdata = new ArrayList<Address>();
        try {
          locationdata = geo.getFromLocationName(location, 1);
        }
        catch(Exception e){
        }
        lat = locationdata.get(0).getLatitude();
        longi = locationdata.get(0).getLongitude();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //End of Fei drivin, Eric driving now
        Thread thread = new Thread(new Runnable(){
           @Override
           public void run(){
               String url = "https://api.darksky.net/forecast/d883eb7308b7cba51cebd62726c3c70b/"+lat+","+longi;
               JsonObjectRequest json = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
                   @Override
                   public void onResponse(JSONObject response) {
                       try {
                           info =response;
                       } catch (Exception e) {
                       }
                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       Log.e("ERROR", "Error occurred ", error);
                   }
               });
               MySingleton.getInstance(MapsActivity.this).addToRequestQueue(json);
               final TextView temp = (TextView)findViewById(R.id.textView);
               final TextView humid = (TextView)findViewById(R.id.textView2);
               final TextView wind = (TextView)findViewById(R.id.textView3);
               final TextView precip = (TextView)findViewById(R.id.textView4);
               while(info==null){

               }
               try {
                   final JSONObject jsonobj = info.getJSONObject("currently");
                   runOnUiThread(new Runnable() {
                       public void run() {
                           try {
                               temp.setText("Temperature: " + jsonobj.getString("temperature"));
                               humid.setText("Humidity: " + jsonobj.getString("humidity"));
                               wind.setText("WindSpeed: " + jsonobj.getString("windSpeed"));
                               if (jsonobj.getString("precipProbability").equals("0")) {
                                   precip.setText("Precipitation: None");
                               } else {
                                   precip.setText("Precipitation: " + jsonobj.getString("precipType"));
                               }
                           }
                           catch(Exception e){

                           }
                       }
                   });
               }
               catch(Exception e){
               }
           }
       });
       thread.start();
    }

    public static void hideKeyboard(Activity act) {
        //Eric Driving
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = act.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(act);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
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
        mMap = googleMap;
        hideKeyboard(this);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, longi);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(sydney).title(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
