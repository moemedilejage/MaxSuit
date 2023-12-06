package com.imegga.suitcase;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener  {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap googleMap;
    private Marker marker;
    private Button saveButton;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private String id;
    private double latitude, longitude;
    private boolean locationChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_map);

        saveButton = findViewById(R.id.save_item_location);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("items").child(id);

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationChanged = true){
                    databaseReference.child("latTag").setValue(latitude);
                    databaseReference.child("lonTag").setValue(longitude);
                }
                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        double latitude = getIntent().getDoubleExtra("latitude", 0.0);
        double longitude = getIntent().getDoubleExtra("longitude", 0.0);

        LatLng defaultLocation = new LatLng(latitude, longitude);
        marker = googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Item Location"));

        float zoomLevel = 14.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, zoomLevel));

        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        // Remove the previous marker if exists
        if (marker != null) {
            marker.remove();
        }
        // Add a new marker at the clicked location
        marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        locationChanged = true;
    }

}