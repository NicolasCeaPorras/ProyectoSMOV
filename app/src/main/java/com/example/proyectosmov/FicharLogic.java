package com.example.proyectosmov;

import static com.example.proyectosmov.dominio.EntityKt.getUserByEmail;

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
        if(bundle!=null){
            Log.d(TAG,"Bundle no es nulo");
            //Debug para testear la BD
            if(bundle.isEmpty()==true){
                Log.d(TAG,"Bundle esta vacio");
            }
            email = bundle.getString("email");
            //email = "a@a.com";
            company = bundle.getString("company");
        }
        //email = "a@a.com";
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
                .setColor(Color.parseColor("#0A43AC"));


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
        //Comprobar si existe entradaFichar y salidaFichar en la BD anteriormente.
        //TO DO (HAY QUE USAR LA NUEVA BD)
        DocumentReference docRef = mDatabase.collection("companies").document(company);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                              @Override
                                              public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                  Company comp = documentSnapshot.toObject(Company.class);
                                                  if (comp != null) {
                                                      Log.i("FicharLog", "Dentro del object Company Entrada");
                                                      User user = getUserByEmail(comp, email);
                                                      if (user != null) {
                                                          if (user.getActive_input() == null) {
                                                              Log.i(TAG, "No hay activeInput guardado, botón a Entrada...");
                                                              List<TimeRecord> lista = new ArrayList<>();
                                                              hEntrada.setText(null);
                                                              hSalida.setText(null);
                                                              entrada.setText("Entrada");
                                                          } else {
                                                              Log.i(TAG, "Existe un activeInput, boton a Salida...");
                                                              //Damos formato a la hora
                                                              Date inHour = user.getActive_input().getStart_hour();
                                                              String inTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(inHour);
                                                              //Cambiamos la vista
                                                              hEntrada.setText(inTime);
                                                              hSalida.setText(null);
                                                              entrada.setText("Salida");
                                                              //Guardamos el valor de la hora de entrada y la fecha de creación
                                                              dateEntrada = user.getActive_input().getStart_hour();
                                                              dateCreacion = user.getActive_input().getCreation_date();
                                                          }
                                                          mDatabase.collection("companies").document(company).update("users", comp.getUsers()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                              @Override
                                                              public void onSuccess(Void aVoid) {
                                                                  Log.d(TAG, "Actualización de usuario sin problemas!");
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
        //Timer entre click para cada boton de 2 segundos
        Button boton = findViewById(R.id.buttonFichar);
        boton.setEnabled(false);
        //Desactivación del boton 2 segundos
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            boton.setEnabled(true);
        }, 2000);
        if (entrada.getText().toString().equals("Salida")) {
            Log.d(TAG, "onClicked: " + entrada.getText().toString());
            hora = Calendar.getInstance().getTime();
            String tiempoActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(hora);
            hSalida.setText(tiempoActual);
            entrada.setText("Entrada");
            //Añadimos el nuevo campo "salidaFichar" y le pasamos como valor el TimeStamp de la hora actual a la DB.
            Map<String, Object> horaSalida = new HashMap<>();
            //Añadimos el nuevo campo "botonFichar" y le pasamos como valor el String del boton de fichar.
            Map<String, Object> stringBtnFichar = new HashMap<>();
            stringBtnFichar.put("btnFichar",entrada.getText().toString());
            ficharSalida = Timestamp.now();
            horaSalida.put("salidaFichar",ficharSalida);


            //CODIGO NUEVO  TESTEADO
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
                                    Log.d(TAG, "Actualización de usuario sin problemas!");
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Fallo al actualizar usuario", e);
                                        }
                                    });
                             /*
                             String oficina = sLista.getSelectedItem().toString();
                             MessageDigest md = null;
                             try {
                                 md = MessageDigest.getInstance("MD5");
                                 Number hashPassword =  BigInteger(1, md.digest(oficina.toByteArray())).toString(16).padStart(32, '0');
                             } catch (NoSuchAlgorithmException e) {
                                 e.printStackTrace();
                             }
                             */
                        }
                    }
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
            Map<String, Object> stringBtnFichar = new HashMap<>();
            stringBtnFichar.put("btnFichar",entrada.getText().toString());
            Map<String, Object> horaEntrada = new HashMap<>();
            ficharEntrada = Timestamp.now();
            horaEntrada.put("entradaFichar",ficharEntrada);

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
                             //cosa que no es muy útil, pero por si acaso.
                             dateEntrada = ai.getStart_hour();
                             dateCreacion = ai.getCreation_date();
                             if (user.getActive_input() == null) {
                                 Log.i(TAG, "No existe activeInput, creando...");
                                 //List<TimeRecord> lista = new ArrayList<>();
                                 //user.setTime_records(lista);
                                 //user.getTime_records().add(tiempoFichar);
                                 user.setActive_input(ai);
                             }
                             mDatabase.collection("companies").document(company).update("users", comp.getUsers()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     Log.d(TAG, "Actualización de usuario sin problemas!");
                                 }
                             })
                                     .addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {
                                             Log.w(TAG, "Fallo al actualizar usuario", e);
                                         }
                                     });
                             /*
                             HASH NO FUNCIONAL
                             String oficina = sLista.getSelectedItem().toString();
                             MessageDigest md = null;
                             try {
                                 md = MessageDigest.getInstance("MD5");
                                 Number hashPassword =  BigInteger(1, md.digest(oficina.toByteArray())).toString(16).padStart(32, '0');
                             } catch (NoSuchAlgorithmException e) {
                                 e.printStackTrace();
                             }
                             */
                         }
                     }
                }
            });
        }
    }

}