package com.example.torsh.mymapsdemo1;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

//ToDo: calculate distance between 2 positions
public class MainActivity extends ActionBarActivity implements OnMapReadyCallback{

    private final LatLng LOCATION_COPENHAGEN = new LatLng(55.67, 12.56);
    private final LatLng LOCATION_SKOVLUNDE = new LatLng(55.713597, 12.398044);
    private GoogleMap googleMap;
    private Marker marker;
    private HashMap hashMap = new HashMap<Marker, String>(); // for storage of marker id's
    private int clickCount = 0;
    private double position1Lat, position1Lng, position2Lat, position2Lng;
    private Button buttonMapType;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonMapType = (Button) findViewById(R.id.btn_satellite);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION_COPENHAGEN, 13));
        marker = googleMap.addMarker(
                new MarkerOptions()
                        .title("CPH city population: 579,513")
                        .snippet("Lat: 55.67 Lon:12.56")
                        .position(LOCATION_COPENHAGEN));

        hashMap.put(marker, "cph");
        String marker_Id = String.valueOf(hashMap.get(marker));

        this.googleMap = googleMap; //initialise googleMap
    }


    public void onClick_distanceBetween2points(View v){
        if ( !hashMap.isEmpty() )
            googleMap.clear();

        Toast.makeText(getBaseContext(), "Tap 2 points on the map", Toast.LENGTH_SHORT).show();

        googleMap.setOnMapClickListener( new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                clickCount++;
                //Toast.makeText(getBaseContext(), String.valueOf(clickCount), Toast.LENGTH_SHORT).show();
                if ( clickCount == 1 ){
                    position1Lat = latLng.latitude;
                    position1Lng = latLng.longitude;
                } else if ( clickCount == 2) {
                    position2Lat = latLng.latitude;
                    position2Lng = latLng.longitude;
                } else {
                    position1Lng = 0; position1Lat = 0; position2Lat = 0; position2Lng = 0;
                }

                showPointedPosition(latLng);
                if ( clickCount == 2 )
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
            Toast.makeText(getBaseContext(), "try again", Toast.LENGTH_SHORT).show();
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

    public void onClick_goto_city(View v){
        //LatLng skovlunde = new LatLng();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(LOCATION_SKOVLUNDE);
        googleMap.animateCamera(cameraUpdate);
        googleMap.addMarker( new MarkerOptions()
        .title("Skovlunde")
        .snippet("Lat:55.713597, Lon:12.398044")
        .position(LOCATION_SKOVLUNDE));
    }

// Todo: toggling zoomCph / zoomOut to onMapReady settings
    public void onClick_zoomCPH(View v){

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION_COPENHAGEN, 13));

        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.abc_ic_menu_copy_mtrl_am_alpha)) //R.drawable.direction_arrow does not exist !!?
                .position(LOCATION_COPENHAGEN)
                .flat(true)
                .rotation(245));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(LOCATION_COPENHAGEN)
                .zoom(18) // close zoom level
                .bearing(90) // 90 changes orientation of camera to east
                .tilt(30) // set the tilt of camera to 30 degrees
                .build();

        // Animate the change in camera view over 2 seconds
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                4000, null);
    }

//ToDo: try this toggling without setOnClickListener
    public void onClick_showSatellite(View v){

        buttonMapType.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( googleMap.getMapType() == 1 ){
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    buttonMapType.setText("Normal");
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
//                .add(new LatLng(55.67, 12.56))  // Copenhagen
//                .add(new LatLng(55.661187, 12.516758))  // Valby
//                .add(new LatLng(55.713597, 12.398044))  // Skovlunde
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
