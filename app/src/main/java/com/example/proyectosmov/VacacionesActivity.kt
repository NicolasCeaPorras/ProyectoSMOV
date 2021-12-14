package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.*
import android.os.Looper
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.proyectosmov.dominio.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class VacacionesActivity : AppCompatActivity() {
    var companyID : String = ""
    var email : String = ""
    var selected_date : Date = Date()
    var selected_item : ScheduledTask? = null
    var Click= 0
    var diasDisp= 21
    var fechaSal = Date()
    var fechaReg = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vacaciones)
        supportActionBar!!.hide()
        val bundle = getIntent().getExtras();
        if (bundle != null && !bundle.isEmpty) {
            //Quitar comentarios para que sea dinamico
            companyID = bundle.getString("company").toString()
            email = bundle.getString("email").toString()

        }
        //Comentar para que sea dinamico
        //companyID = "Pruebas"
        //email = "email@pruebas.com"

        val calendarView =
            findViewById<com.applandeo.materialcalendarview.CalendarView>(R.id.vacaCalendario)
        val fechaSalView = findViewById<TextView>(R.id.fechSal)
        val fechaRegView = findViewById<TextView>(R.id.fechReg)
        val diasDispView = findViewById<TextView>(R.id.diasVaca)
        val boton = findViewById<Button>(R.id.buttonVacaciones)

        boton.visibility =View.INVISIBLE
        //desactivamos el calendario al inicio
        calendarView.setVisibility(View.INVISIBLE)
        //Ponemos los dias disponibles por defecto
        diasDispView.setText(diasDisp.toString())
        //Asignamos el evento de cambio de fecha en el calendario

        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                Log.i("AgendaActivity", eventDay.calendar.time.toString())
                selected_date = eventDay.calendar.time
                if (Click == 1) {
                    fechaSalView.setText(selected_date.toString())
                    fechaSal = selected_date
                    Log.i("Vacaciones", "La fecha es: " + selected_date.toString())

                } else {
                    fechaRegView.setText(selected_date.toString())
                    fechaReg = selected_date
                    Log.i("Vacaciones", "La fecha es: " + selected_date.toString())
                }
            }
        })
        //Obtenemos los diasDisponibles que le quedan al usuario
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("companies").document(companyID)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val company = documentSnapshot.toObject<Company>()
            if (company != null) {
                Log.i("VacacionesAct", "Dentro del object Company 1")

                val user = getUserByEmail(company, email)
                if (user != null) {
                    var diasVaca : Int = user.diasVac!!
                    //SI EL USUARIO NO TIENE DIAS ASIGNADOS PORQUE ES ANTIGUO
                    //LE PONEMOS POR DEFECTO 14 (CAMBIAR EN EL FUTURO)
                    if(diasDisp==null) {
                        //Para testear si el usuario es antiguo
                        diasDisp = 14;
                    }else
                        Log.i("VacacionesAct", "Aqui llego")

                    diasDisp = getDiasDispVac(diasVaca,user)
                    Log.i("VacacionesAct", "Diasdisp de la BD: "+diasDisp.toString())
                    diasDispView.text = diasDisp.toString()
                }
            }

        }
    }

    fun clickFechaSal(v: View?) {
        val calendarView = findViewById<com.applandeo.materialcalendarview.CalendarView>(R.id.vacaCalendario)
        calendarView.setVisibility(View.VISIBLE)
        Click=1
        val boton = findViewById<Button>(R.id.buttonVacaciones)
        boton.visibility =View.VISIBLE
    }

    fun clickFechaReg(v: View?) {
        val calendarView = findViewById<com.applandeo.materialcalendarview.CalendarView>(R.id.vacaCalendario)
        calendarView.setVisibility(View.VISIBLE)
        Click=0
        val boton = findViewById<Button>(R.id.buttonVacaciones)
        boton.visibility =View.VISIBLE
    }

    fun onClickGuardar(view: android.view.View) {
        val diasDispView = findViewById<TextView>(R.id.diasVaca)
        var intervalo = 0
        Toast.makeText(this, "¡Fechas Guardadas!", Toast.LENGTH_SHORT).show()
        val boton = findViewById<Button>(R.id.buttonVacaciones)

        var cal1: Calendar = Calendar.getInstance()
        var cal2: Calendar = Calendar.getInstance()

        cal1.time = fechaSal
        cal2.time = fechaReg
        if (cal2.get(Calendar.DAY_OF_YEAR) < (cal1.get(Calendar.DAY_OF_YEAR)))
            Toast.makeText(
                this,
                "¡Error, la fecha de llegada no puede ser anterior a la de salida!",
                Toast.LENGTH_SHORT
            ).show()
        else {
            //Calculo del los dias disponibles que le quedan al usuario de vacaciones una vez ha seleccionado las fechas
            if (diasDisp < (cal2.get(Calendar.DAY_OF_YEAR)) - (cal1.get(Calendar.DAY_OF_YEAR))) {
                Toast.makeText(this, "¡Error, has escogido demasiados dias!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                diasDisp =
                    (diasDisp - ((cal2.get(Calendar.DAY_OF_YEAR)) - (cal1.get(Calendar.DAY_OF_YEAR))))

                Log.i("Vacaciones", "Los dias son: " + diasDisp.toString())
                Log.i("Vacaciones", "Los dias son: " + fechaReg.day.toString())
                Log.i("Vacaciones", "Los dias son: " + fechaSal.day.toString())

                diasDispView.text = diasDisp.toString()
                boton.isEnabled = false;
                //Desactivación del boton 2 segundos y activación sucesiva
                Handler(Looper.getMainLooper()).postDelayed({ boton.isEnabled = true }, 2000)

                val db = FirebaseFirestore.getInstance()
                val docRef = db.collection("companies").document(companyID)
                docRef.get().addOnSuccessListener { documentSnapshot ->
                    val company = documentSnapshot.toObject<Company>()
                    if (company != null) {
                        Log.i("VacacionesAct", "Dentro del object Company")

                        val user = getUserByEmail(company, email)
                        if (user != null) {
                            var vacPeriodo: HolidayPeriod = HolidayPeriod()
                            //Damos los valores nuevos
                            vacPeriodo.creation_date = Date()
                            vacPeriodo.days_left = diasDisp
                            vacPeriodo.start_date = fechaSal
                            vacPeriodo.end_date = fechaReg

                            //Comprobación de que el la lista contiene o no algo
                            if (user.holiday_periods == null) {
                                var lista: MutableList<HolidayPeriod> = mutableListOf()
                                user.holiday_periods = lista
                                user.holiday_periods!!.add(vacPeriodo)
                            } else user.holiday_periods!!.add(vacPeriodo)

                            //Actualizamos la BD con los nuevos valores
                            db.collection("companies").document(companyID)
                                .update("users", company.users)
                                .addOnSuccessListener {
                                    Log.d("VacacionesAct", "Datos actualizados sin problemas")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        "VacacionesAct",
                                        "Fallo en la actualizacion",
                                        e
                                    )
                                }
                        }
                    }

                }

            }
        }
    }
}