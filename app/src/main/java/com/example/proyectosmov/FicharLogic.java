package com.example.proyectosmov;

import static com.example.proyectosmov.dominio.EntityKt.getListOffice;
import static com.example.proyectosmov.dominio.EntityKt.getUserByEmail;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectosmov.dominio.ActiveInput;
import com.example.proyectosmov.dominio.Company;
import com.example.proyectosmov.dominio.Office;
import com.example.proyectosmov.dominio.TimeRecord;
import com.example.proyectosmov.dominio.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class FicharLogic extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FicharLogic";
    Button entrada;
    TextView textoHora, hEntrada, hSalida;
    private boolean activo;
    private final Handler mHandler;
    private String email,company;
    private String user;
    private Timestamp ficharEntrada, ficharSalida;
    private String btnString;
    private Bundle bundle;
    private Date dateEntrada,dateCreacion;
    MyVectorClock vectorAnalogClock;
    Spinner sLista;
    private Date hora;
    private FirebaseFirestore mDatabase;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    //Cambios para la nueva BD
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
        if (bundle != null) {
            Log.d(TAG, "Bundle no es nulo");
            //Debug para testear la BD
            if (bundle.isEmpty() == true) {
                Log.d(TAG, "Bundle esta vacio");
            }
            email = bundle.getString("email");
            //email = "a@a.com";
            company = bundle.getString("company");
        }
        //email = "a@a.com";
        Log.d(TAG, "El valor de email es:" + email);


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
        calendar.add(Calendar.HOUR, 0);

        vectorAnalogClock = findViewById(R.id.clock);

        //customization of the clock, color, size, opacity, etc
        vectorAnalogClock.setCalendar(calendar)
                //.setDiameterInDp(150)
                .setOpacity(1.0f)
                .setShowSeconds(true)
                .setColor(Color.BLACK);


        //Actualizar Spinner con las oficinas de la base de datos
        sLista = findViewById(R.id.selOficina);


        DocumentReference docRef = mDatabase.collection("companies").document(company);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Company comp = documentSnapshot.toObject(Company.class);
                if (comp != null) {
                    Log.i("FicharLog", "Dentro del object Company Entrada");
                    //obtenemos el array con todas las oficinas y lo pasamos al spinner para que se complete
                    arrayList = getListOffice(comp);
                    arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sLista.setAdapter(arrayAdapter);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Fallo al actualizar usuario", e);
                    }
                });



        sLista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*Actualizado el tiempo cuando se selecciona para eliminar el Bug #01 del Reloj.
                El bug era que cuando se seleccionaba una oficina el reloj se reiniciaba, para ello el cambio
                que se realiza es que cada vez que se selecciona una oficina se crea una nueva instancia del reloj para no peder la
                hora actual*/
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR,0);
                vectorAnalogClock.setCalendar(calendar);
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });
        //Obtenemos datos de la sesi??n guardados en la Base de datos y si hay hora de entrada en el input guardado
        //Se cambia el bot??n de Entrada a Salida
        docRef = mDatabase.collection("companies").document(company);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                              @Override
                                              public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                  Company comp = documentSnapshot.toObject(Company.class);
                                                  if (comp != null) {
                                                      Log.i("FicharLog", "Dentro del object Company Entrada");
                                                      User user = getUserByEmail(comp, email);
                                                      if (user != null) {
                                                          if (user.getActive_input() == null) {
                                                              Log.i(TAG, "No hay activeInput guardado, bot??n a Entrada...");
                                                              List<TimeRecord> lista = new ArrayList<>();
                                                              hEntrada.setText(null);
                                                              hSalida.setText(null);
                                                              entrada.setText("Entrada");
                                                          } else {
                                                              Log.i(TAG, "Existe un activeInput, boton a Salida...");
                                                              //Damos formato a la hora de forma legible
                                                              Date inHour = user.getActive_input().getStart_hour();
                                                              String inTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(inHour);
                                                              //Cambiamos la vista
                                                              hEntrada.setText(inTime);
                                                              hSalida.setText(null);
                                                              entrada.setText("Salida");
                                                              //Guardamos el valor de la hora de entrada y la fecha de creaci??n y lo actualizamos posteriormente en la base de datos
                                                              dateEntrada = user.getActive_input().getStart_hour();
                                                              dateCreacion = user.getActive_input().getCreation_date();
                                                          }
                                                          mDatabase.collection("companies").document(company).update("users", comp.getUsers()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                              @Override
                                                              public void onSuccess(Void aVoid) {
                                                                  Log.d(TAG, "Actualizaci??n de usuario sin problemas!");
                                                              }
                                                          })
                                                                  .addOnFailureListener(new OnFailureListener() {
                                                                      @Override
                                                                      public void onFailure(@NonNull Exception e) {
                                                                          Log.w(TAG, "Fallo al actualizar usuario", e);
                                                                      }
                                                          });
                                                      }
                                                  }
                                              }
                                          });
    }

    private void actualizarHora() {
        activo = true;
        mHandler.post(mRunnable);
    }

    @Override
    public void onClick(View v) {
        //Timer entre click para cada boton de 2 segundos para que no se produzcan errores de doble pulsaci??n
        Button boton = findViewById(R.id.buttonFichar);
        boton.setEnabled(false);
        //Desactivaci??n del boton 2 segundos
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            boton.setEnabled(true);
        }, 2000);
        /*
        Si en el bot??n pone "Salida", guardamos la hora en la que acaba de fichar y lo mostramos por pantalla en la zona inferior
        Adem??s el texto del bot??n cambia a "Entrada", una vez hecho esto tanto la hora de llegada y la creaci??n de la fecha
        Que estaban guardados en un input temporal y la hora de salida que acabamos de obtener y se guarda al completo en la Base de Datos.
         */
        if (entrada.getText().toString().equals("Salida")) {
            Log.d(TAG, "onClicked: " + entrada.getText().toString());
            hora = Calendar.getInstance().getTime();
            String tiempoActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(hora);
            hSalida.setText(tiempoActual);
            entrada.setText("Entrada");
            //A??adimos el nuevo campo "salidaFichar" y le pasamos como valor el TimeStamp de la hora actual a la DB.
            Map<String, Object> horaSalida = new HashMap<>();
            //A??adimos el nuevo campo "botonFichar" y le pasamos como valor el String del boton de fichar.
            Map<String, Object> stringBtnFichar = new HashMap<>();
            stringBtnFichar.put("btnFichar",entrada.getText().toString());
            ficharSalida = Timestamp.now();
            horaSalida.put("salidaFichar",ficharSalida);
            //Guardar todos los valores de las fechas de entrada y salida de la oficina en la Base de Datos.
            DocumentReference docRef = mDatabase.collection("companies").document(company);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Company comp = documentSnapshot.toObject(Company.class);
                    if (comp!=null){
                        Log.i("FicharLog", "Dentro del object Company Entrada");
                        User user = getUserByEmail(comp,email);
                        if(user!=null){
                            TimeRecord tiempoFichar = new TimeRecord();
                            //Recuperamos los valores del activeInput Anterior
                            tiempoFichar.setStart_hour(dateEntrada);
                            tiempoFichar.setCreation_date(dateCreacion);
                            //Ponemos la nueva fecha de salida.
                            tiempoFichar.setEnd_hour(hora);

                            if (user.getTime_records() == null){
                                Log.i(TAG, "No existe lista, creando...");
                                List<TimeRecord> lista = new ArrayList<>();
                                user.setTime_records(lista);
                                user.getTime_records().add(tiempoFichar);
                                //Borramos el ative input actual
                                user.setActive_input(null);
                            }else{
                                user.getTime_records().add(tiempoFichar);
                                //Borramos el ative input actual
                                user.setActive_input(null);
                            }
                            mDatabase.collection("companies").document(company).update("users",comp.getUsers()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Actualizaci??n de usuario sin problemas!");
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Fallo al actualizar usuario", e);
                                        }
                                    });
                        }
                    }
                }
            });
     }
            /*
        Si en el bot??n pone "Entrada", guardamos la hora en la que acaba de entrar a fichar y lo mostramos por pantalla en la zona inferior
        Adem??s el texto del bot??n cambia a "Salida", una vez hecho se guarda tanto la hora de llegada y la creaci??n de la fecha
        En un input temporal y se guarda en la Base de Datos.
         */
        else if ( entrada.getText().toString().equals("Entrada")) {
            Log.d( TAG,"onClicked: "+entrada.getText().toString());
            hora = Calendar.getInstance().getTime();
            String tiempoActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(hora);
            hEntrada.setText(tiempoActual);
            hSalida.setText(null);
            entrada.setText("Salida");
            Map<String, Object> stringBtnFichar = new HashMap<>();
            stringBtnFichar.put("btnFichar",entrada.getText().toString());
            Map<String, Object> horaEntrada = new HashMap<>();
            ficharEntrada = Timestamp.now();
            horaEntrada.put("entradaFichar",ficharEntrada);
            //Guardar los valores de las fechas de entrada y fecha de craci??n de la hora para fichar en la oficina en la Base de Datos.
            DocumentReference docRef = mDatabase.collection("companies").document(company);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                     Company comp = documentSnapshot.toObject(Company.class);
                     if (comp!=null){
                         Log.i("FicharLog", "Dentro del object Company Entrada");
                         User user = getUserByEmail(comp,email);
                         if(user!=null){
                             ActiveInput ai = new ActiveInput();
                             ai.setStart_hour(hora);
                             ai.setCreation_date(new Date());
                             //Asignamos los valores a las variables de entrada por si el usuario
                             //decide dar de seguido al boton de salida una vez ha dado al de entrada
                             //cosa que no es muy ??til, pero por si acaso.
                             dateEntrada = ai.getStart_hour();
                             dateCreacion = ai.getCreation_date();
                             if (user.getActive_input() == null) {
                                 Log.i(TAG, "No existe activeInput, creando...");
                                 user.setActive_input(ai);
                             }
                             mDatabase.collection("companies").document(company).update("users", comp.getUsers()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     Log.d(TAG, "Actualizaci??n de usuario sin problemas!");
                                 }
                             })
                                     .addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {
                                             Log.w(TAG, "Fallo al actualizar usuario", e);
                                         }
                                     });
                         }
                     }
                }
            });
        }
    }

}