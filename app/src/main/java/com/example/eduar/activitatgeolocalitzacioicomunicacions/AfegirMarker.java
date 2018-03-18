package com.example.eduar.activitatgeolocalitzacioicomunicacions;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class AfegirMarker extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afegir_marker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        //Intent i = new Intent();
        //Bundle b = new Bundle();

        EditText ciutat = (EditText) findViewById(R.id.txtCiutat);
        EditText nom = (EditText) findViewById(R.id.txtNom);

        Marker point = new Marker(ciutat.getText().toString(), nom.getText().toString(), 12, 42.3,42.4);
        finish();
    }

}
