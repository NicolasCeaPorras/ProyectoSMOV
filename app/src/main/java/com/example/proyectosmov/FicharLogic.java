package com.example.proyectosmov;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FicharLogic extends AppCompatActivity {
    TextView textoHora;
    Date hora=Calendar.getInstance().getTime();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fichar);
        getSupportActionBar().hide();

        // Enlazar views
        textoHora = (TextView) findViewById(R.id.actualhora);
        String tiempoActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(hora);
        textoHora.setText(tiempoActual);

    }

}
