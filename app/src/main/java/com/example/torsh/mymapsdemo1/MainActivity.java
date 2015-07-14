package com.example.torsh.mymapsdemo1;

/*
* -- Class for google map utilities --
*
* zoom in/out,
* distance calculation,
* camera angle and movement
* point info popup by tapping the markers
* zoom function retained after configuration change
*
* */

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback{

    private final LatLng LOCATION_COPENHAGEN = new LatLng(55.67, 12.56);
    private final LatLng LOCATION_SKOVLUNDE = new LatLng(55.713597, 12.398044);
    private GoogleMap googleMap;
    private Marker marker;
    private HashMap<Marker, String> hashMap; //= new HashMap<Marker, String>(); // for storage of marker id's

    private int clickCount = 0;
    private double position1Lat, position1Lng, position2Lat, position2Lng;
    private Button buttonMapType, buttonMapReady, buttonGo;
    private EditText lat_value, lon_value;

    CameraPosition currentCameraPosition;
    Bundle savedInstanceState;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_main);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            setContentView(R.layout.land_layout);


        buttonMapType = (Button) findViewById(R.id.btn_satellite);
        buttonMapReady = (Button) findViewById(R.id.btn_zoomCph);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.savedInstanceState = savedInstanceState;
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        googleMap.setMyLocationEnabled(true);

        if (savedInstanceState == null){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION_COPENHAGEN, 13));
        }

        if (savedInstanceState != null){
            currentCameraPosition = savedInstanceState.getParcelable("currentCameraPosition");
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentCameraPosition));
        }


        marker = googleMap.addMarker(
                new MarkerOptions()
                        .title("CPH city population: 579,513")
                        .snippet("Lat: 55.67 Lon:12.56")
                        .position(LOCATION_COPENHAGEN));


        hashMap = new HashMap<>();
        hashMap.put(marker, "cph");

        this.googleMap = googleMap; //initialise googleMap
    }

    //ToDo: retain markers after config change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        currentCameraPosition = googleMap.getCameraPosition();
        outState.putParcelable("currentCameraPosition", currentCameraPosition);
        //Toast.makeText(getBaseContext(), "iwasbundle", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        currentCameraPosition = savedInstanceState.getParcelable("currentCameraPosition");
        //Toast.makeText(getBaseContext(), currentCameraPosition.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //ToDo: calculate accumulated distance for multiple connected lines
    public void onClick_distanceBetween2points(View v){
        if ( !hashMap.isEmpty() )
            googleMap.clear();

        Toast.makeText(getBaseContext(), "Tap 2 points on the map", Toast.LENGTH_SHORT).show();

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                clickCount++;
                //Toast.makeText(getBaseContext(), String.valueOf(clickCount), Toast.LENGTH_SHORT).show();
                if (clickCount == 1) {
                    position1Lat = latLng.latitude;
                    position1Lng = latLng.longitude;
                } else if (clickCount == 2) {
                    position2Lat = latLng.latitude;
                    position2Lng = latLng.longitude;
                } else {
                    position1Lng = 0;
                    position1Lat = 0;
                    position2Lat = 0;
                    position2Lng = 0;
                }

                showPointedPosition(latLng);
                if (clickCount == 2)
                    calculateDistBetween2points();
            }
        });
    }



    public void showPointedPosition(LatLng latLng){
        // create marker
        MarkerOptions markerOptions = new MarkerOptions();
        // set position
        markerOptions.position(latLng);
        //set the title
        markerOptions.title("Lat:" +latLng.latitude +" Lon:"+ latLng.longitude);
        // clear previously tapped position
        //googleMap.clear();
        // Animating to the touched position
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        // Placing a marker on the touched position
        googleMap.addMarker(markerOptions);
        // count and clear markers after 3 clicks
        if ( clickCount > 2 ) {
            googleMap.clear();
            clickCount = 0;
            Toast.makeText(getBaseContext(), "Map Cleared", Toast.LENGTH_SHORT).show();
        }
    }



    // calculate distance between 2 tapped points on the map
    public float calculateDistBetween2points(){

        addLineBetween2points();

        float[] dist = new float[1];
        Location.distanceBetween(position2Lat, position2Lng, position1Lat, position1Lng, dist);
        Toast.makeText(getBaseContext(), "Distance: " +String.valueOf((Math.round((dist[0]/1000)*1000)/1000.0))+ " Km", Toast.LENGTH_SHORT).show();
        return dist[0];
    }


    public void onClick_goto_location(View v){


        buttonGo = (Button) findViewById(R.id.btn_go);
        lat_value = (EditText) findViewById(R.id.edit_lat);
        lon_value = (EditText) findViewById(R.id.edit_lon);

        String lat_str = lat_value.getText().toString();
        String lon_str = lon_value.getText().toString();

        LatLngValidator latLngValidator = new LatLngValidator();
        if (!latLngValidator.validateLatLng(lat_str, lon_str)) {
            Toast.makeText(getBaseContext(), latLngValidator.getStatusMessage(), Toast.LENGTH_SHORT).show();
        } else {
            final LatLng LOCATION_ELSE = new LatLng(latLngValidator.getLat(), latLngValidator.getLon());

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LOCATION_ELSE, 8);

            googleMap.animateCamera(cameraUpdate, 18000, null);
            googleMap.addMarker(new MarkerOptions()
                .title("Your location")
                .snippet("Lat: " + lat_str +" Lon: " + lon_str )
                .position(LOCATION_ELSE));
        }
    }



    public void onClick_goto_city(View v){

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(LOCATION_SKOVLUNDE);

        googleMap.animateCamera(cameraUpdate);
        googleMap.addMarker(new MarkerOptions()
                .title("Skovlunde")
                .snippet("Lat:55.713597, Lon:12.398044")
                .position(LOCATION_SKOVLUNDE));
    }


    //ToDo: code repeat / refactoring;
    public void onClick_zoomCPH(View v){

        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.abc_ic_menu_copy_mtrl_am_alpha)) //R.drawable.direction_arrow does not exist !!?
                .position(LOCATION_COPENHAGEN)
                .flat(true)
                .rotation(245));

        float zoomLevel_current = googleMap.getCameraPosition().zoom;
        if ( zoomLevel_current <= 13.0 ) {

            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(LOCATION_COPENHAGEN)
                    .zoom(18) // close zoom level
                    .bearing(90) // 90 changes orientation of camera to east
                    .tilt(30) // set the tilt of camera to 30 degrees
                    .build();

            // Animate the change in camera view over 4 seconds
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    10000, null);

            buttonMapReady.setText("Reset");
        }


        buttonMapReady.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float zoomLevel_current = googleMap.getCameraPosition().zoom;
                if ( zoomLevel_current <= 13.0 ) {

                    marker = googleMap.addMarker(
                            new MarkerOptions()
                                    .title("CPH city population: 579,513")
                                    .snippet("Lat: 55.67 Lon:12.56")
                                    .position(LOCATION_COPENHAGEN));

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(LOCATION_COPENHAGEN)
                            .zoom(18) // close zoom level
                            .bearing(90) // 90 changes orientation of camera to east
                            .tilt(30) // set the tilt of camera to 30 degrees
                            .build();

                    // Animate the change in camera view over 4 seconds
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                            10000, null);

                    buttonMapReady.setText("Reset");

                } else {

                    CameraPosition cameraPositionReset = CameraPosition.builder()
                            .target(LOCATION_COPENHAGEN)
                            .zoom(13)
                            .bearing(0)
                            .tilt(0)
                            .build();

                    // Animate the change in camera view over 4 seconds
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionReset),
                            10000, null);

                    buttonMapReady.setText("ZoomCPH");
                }
                //Toast.makeText(getBaseContext(), String.valueOf(googleMap.getCameraPosition().zoom), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onClick_showSatellite(View v){

        if ( googleMap.getMapType() == 1 ) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            buttonMapType.setText("Map");
        }

        buttonMapType.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( googleMap.getMapType() == 1 ){
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    buttonMapType.setText("Map");
                } else {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    buttonMapType.setText("Satellite");
                }

            }
        });
    }

    public void addLineBetween2points(){
        // Polylines for marking paths and routes on the map.
        googleMap.addPolyline(new PolylineOptions().geodesic(true)
                        .add(new LatLng(position1Lat, position1Lng))
                        .add( new LatLng(position2Lat, position2Lng))
                        .width(3)
        );
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
