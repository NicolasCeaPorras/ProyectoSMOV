package com.example.proyectosmov;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FicharLogic extends AppCompatActivity {
    TextView textoHora;
    private boolean activo;
    private final Handler mHandler;
    Date hora;
    //Referencia usada para comprender el hilo: https://stackoverflow.com/questions/6400846/updating-time-and-date-by-the-second-in-android
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            if (activo) {
                if (textoHora != null) {
                    //Actualizamos la hora, en funcion del formato, HH:mm:ss, y lo ejecutamos en el manejador con el runnable.
                    hora=Calendar.getInstance().getTime();
                    String tiempoActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(hora);
                    textoHora.setText(tiempoActual);
                }
                mHandler.postDelayed(mRunnable, 1000);
            }
        }
    };

    public FicharLogic() {
        mHandler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fichar);
        getSupportActionBar().hide();

        // Enlazar views
        textoHora = (TextView) findViewById(R.id.actualhora);
        actualizarHora();

    }
    private void actualizarHora(){
        activo=true;
        mHandler.post(mRunnable);
    }
}
