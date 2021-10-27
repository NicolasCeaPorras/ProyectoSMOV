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
    private boolean mActivo;
    private final Handler mHandler;
    Date hora;
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
        iniciarHora();


    }
    /*Basado en este ejemplo: https://stackoverflow.com/questions/6400846/updating-time-and-date-by-the-second-in-android*/
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            if (mActivo) {
                if (textoHora != null) {
                    //Obtenemos la hora del sistema y la salida tiene el formato HH:mm:ss,
                    // despues invocamos el Runnable con un delay de 1 segundo para que se actualice de forma correcta.
                    hora=Calendar.getInstance().getTime();
                    String tiempoActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(hora);
                    textoHora.setText(tiempoActual);
                }
                mHandler.postDelayed(mRunnable, 1000);
            }
        }
    };

    private void iniciarHora(){
        mActivo= true;
        mHandler.post(mRunnable);
    }

}
