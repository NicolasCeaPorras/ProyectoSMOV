package com.example.proyectosmov;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FicharLogic extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FicharLogic";
    Button entrada;
    TextView textoHora, hEntrada, hSalida;
    private boolean activo;
    private final Handler mHandler;
    Date hora;
    //Referencia usada para comprender el hilo: https://stackoverflow.com/questions/6400846/updating-time-and-date-by-the-second-in-android
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            if (activo) {
                if (textoHora != null) {
                    //Actualizamos la hora, en funcion del formato, HH:mm:ss, y lo ejecutamos en el manejador con el runnable.
                    hora = Calendar.getInstance().getTime();
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
        entrada = (Button) findViewById(R.id.buttonFichar);
        textoHora = (TextView) findViewById(R.id.actualhora);
        hEntrada = (TextView) findViewById(R.id.horaentrada);
        hSalida = (TextView) findViewById(R.id.horasalida);
        entrada.setOnClickListener(this);
        actualizarHora();

    }

    private void actualizarHora() {
        activo = true;
        mHandler.post(mRunnable);
    }

    @Override
    public void onClick(View v) {

        if (entrada.getText().toString().equals("Salida")) {
            Log.d(TAG, "onClicked: " + entrada.getText().toString());
            hora = Calendar.getInstance().getTime();
            String tiempoActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(hora);
            hSalida.setText(tiempoActual);
            entrada.setText("Entrada");

        }
        else if ( entrada.getText().toString().equals("Entrada")) {
            Log.d( TAG,"onClicked: "+entrada.getText().toString());
            hora = Calendar.getInstance().getTime();
            String tiempoActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(hora);
            hEntrada.setText(tiempoActual);
            hSalida.setText(null);
            entrada.setText("Salida");

        }
    }
}
