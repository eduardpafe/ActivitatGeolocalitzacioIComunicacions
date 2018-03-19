package com.example.eduar.activitatgeolocalitzacioicomunicacions;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AfegirMarker extends AppCompatActivity {
    DatabaseReference databasePunts;
    private GoogleMap mMap;
    Button buttonAfegirPunt;
    EditText txtNom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afegir_marker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonAfegirPunt = (Button) findViewById(R.id.btnAfegir);
        txtNom = (EditText) findViewById(R.id.txtNom);


       //getting the reference of artists node
        databasePunts = FirebaseDatabase.getInstance().getReference("points");

        buttonAfegirPunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePoint();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void savePoint() {
        //Agafant els valor per guardar
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> ciutat = null;
        Bundle dadesR = getIntent().getExtras();


        String coordenades = dadesR.getString("LatLng");
        String[] parts = coordenades.split(",");
        parts[0] = parts[0].substring(10, parts[0].length());
        parts[1] = parts[1].substring(0, parts[0].length() - 1);

        Double lat = Double.parseDouble(parts[0]);
        Double lng = Double.parseDouble(parts[1]);
        System.out.println(lat);
        System.out.println(lng);


        try {
            ciutat = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String nomMarker = txtNom.getText().toString().trim();

        if (!TextUtils.isEmpty(nomMarker)) {
            String id = databasePunts.push().getKey();

            //Creem l'objecte Marker
            Marker puntNou = new Marker(ciutat.get(0).getAddressLine(0).toString(), txtNom.getText().toString(), 99, lat, lng);

            //Guardem el punt
            databasePunts.child(id).setValue(puntNou);

            //Enviem toast, senyal d'exit.
            Toast.makeText(this, "Punt guardat.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Siusplau, entra el punt del Marker.", Toast.LENGTH_LONG).show();

        }

    }

}
