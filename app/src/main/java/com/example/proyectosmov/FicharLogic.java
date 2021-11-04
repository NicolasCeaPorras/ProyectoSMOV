package com.example.proyectosmov;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kotlin.jvm.internal.Intrinsics;


public class FicharLogic extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FicharLogic";
    Button entrada;
    TextView textoHora, hEntrada, hSalida;
    private boolean activo;
    private final Handler mHandler;
    private String email;
    private String user;
    private Timestamp ficharEntrada, ficharSalida;
    private Bundle bundle;
    MyVectorClock vectorAnalogClock;
    Spinner sLista;
    Date hora;
    private FirebaseFirestore mDatabase;
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
        mDatabase = FirebaseFirestore.getInstance();
        bundle = getIntent().getExtras();
        if(bundle!=null){
            Log.d(TAG,"Bundle no es nulo");
            //Debug para testear la BD
            if(bundle.isEmpty()==true){
                Log.d(TAG,"Bundle esta vacio");
            }
            email = bundle.getString("email");
        }

        Log.d(TAG,"El valor de email es:"+email);


        // Enlazar views
        entrada = (Button) findViewById(R.id.buttonFichar);
        textoHora = (TextView) findViewById(R.id.actualhora);
        hEntrada = (TextView) findViewById(R.id.horaentrada);
        hSalida = (TextView) findViewById(R.id.horasalida);
        entrada.setOnClickListener(this);
        actualizarHora();
        // Reloj parte automatico
        // //Referencia: De donde viene el codigo: https://github.com/TurkiTAK/vector-analog-clock
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,0);

        vectorAnalogClock = findViewById(R.id.clock);

        //customization
        vectorAnalogClock.setCalendar(calendar)
                //.setDiameterInDp(150)
                .setOpacity(1.0f)
                .setShowSeconds(true)
                .setColor(Color.BLACK);


        //Actualizar Spinner
         sLista= findViewById(R.id.selOficina);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Oficina 1");
        arrayList.add("Oficina 2");
        arrayList.add("Teletrabajo");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sLista.setAdapter(arrayAdapter);
        sLista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Actualizado el tiempo cuando se selecciona para eliminar el Bug #01 del Reloj.
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR,0);
                vectorAnalogClock.setCalendar(calendar);
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

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
            //Añadimos el nuevo campo "salidaFichar" y le pasamos como valor el TimeStamp de la hora actual a la DB.
            Map<String, Object> horaSalida = new HashMap<>();
            ficharSalida = Timestamp.now();
            horaSalida.put("salidaFichar",ficharSalida);
            mDatabase.collection("usuarios").document(email).set(horaSalida, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                }
            })          //Comprobación de Google FireStore para saber si hemos escrito correctamente.
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });


        }
        else if ( entrada.getText().toString().equals("Entrada")) {
            Log.d( TAG,"onClicked: "+entrada.getText().toString());
            hora = Calendar.getInstance().getTime();
            String tiempoActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(hora);
            hEntrada.setText(tiempoActual);
            hSalida.setText(null);
            entrada.setText("Salida");
            Map<String, Object> horaEntrada = new HashMap<>();
            ficharEntrada = Timestamp.now();
            horaEntrada.put("entradaFichar",ficharEntrada);
            mDatabase.collection("usuarios").document(email).set(horaEntrada, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                }
            })          //Comprobación de Google FireStore para saber si hemos escrito correctamente.
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });

        }
    }
}
