package com.example.eduar.activitatgeolocalitzacioicomunicacions;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Bidi;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, View.OnClickListener {
    private GoogleMap mMap;

    private ToggleButton btnAnimacio;
    private Spinner cmbTipusMapa;
    private Button btnCentrar;
    private Button mSaveButton;

    DatabaseReference databasePunts;

    Marker marcadors;

    //Marcador al bosc de la coma.
    private static final LatLng INS_BOSC_DE_LA_COMA = new LatLng(42.1727, 2.47631);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //getting the reference of artists node
        databasePunts = FirebaseDatabase.getInstance().getReference("points");

        configurarGUI();

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

        //Establim el mapa tipus hibrid (per veure relleu i carrateres)
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.getUiSettings().setCompassEnabled(true);

        //Establim els controls de zoom que proporciona Google Maps API
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //Activem el boto "Mt Location"
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMapLongClickListener(alFerUnClicLlarg());

    }

    @NonNull
    @org.jetbrains.annotations.Contract(pure = true)
    private GoogleMap.OnMapLongClickListener alFerUnClicLlarg() {
        return new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.i(latLng.toString(), "User long click");
                String coordenades = latLng.toString();

                Intent intent = new Intent(getApplicationContext(), AfegirMarker.class);
                Bundle dades = new Bundle();
                dades.putString("LatLng", coordenades);
                intent.putExtras(dades);
                startActivity(intent);

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        databasePunts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    marcadors = child.getValue(Marker.class);

                    //Agafem el valor de la latitud i la longitud i la limitem a només dos decimals.
                    BigDecimal bdLat = new BigDecimal(marcadors.getLatitude());
                    BigDecimal bdLng = new BigDecimal(marcadors.getLongitude());

                    bdLat = bdLat.setScale(2, RoundingMode.HALF_UP);
                    bdLng = bdLng.setScale(2, RoundingMode.HALF_UP);

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(marcadors.getLatitude(), marcadors.getLongitude()))
                            .title(marcadors.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .snippet(bdLat.toString() + " , " + bdLng.toString()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCentrar:
                centrar();
                break;
        }
    }

    private void centrar() {
        if (btnAnimacio.isChecked()) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(INS_BOSC_DE_LA_COMA, 15), 2000, null);
        } else {
            //Moure les coordenades al punt que ens interessa
            mMap.moveCamera(CameraUpdateFactory.newLatLng(INS_BOSC_DE_LA_COMA));
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String tipus = (String) parent.getItemAtPosition(position);

        if (tipus.compareTo("Normal") == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (tipus.compareTo("Híbrid") == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (tipus.compareTo("Topogràfic") == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (tipus.compareTo("Satèl·lit") == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
    }

    public void configurarGUI() {
        cmbTipusMapa = (Spinner) findViewById(R.id.cmbTipusMapa);
        cmbTipusMapa.setOnItemSelectedListener(this);

        btnCentrar = (Button) findViewById(R.id.btnCentrar);
        btnCentrar.setOnClickListener(this);

        btnAnimacio = (ToggleButton) findViewById(R.id.tglBtnAnimacio);
    }

    public void cercaPoblacioPerNom(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.txtCerca);
        String localitzacio = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(localitzacio, 1);
        Address address = list.get(0);
        //String ciutat = address.getLocality();

        double lat = address.getLatitude();
        double lng = address.getLongitude();

        if (localitzacio == "") {
            LatLng ll = new LatLng(0, 0);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 2000), 2000, null);

        } else {
            anarLocalitzacioZoom(lat, lng, 12);
        }

    }

    private void anarLocalitzacioZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15), 2000, null);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
