package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import com.example.proyectosmov.R
import android.widget.Toast
import com.example.proyectosmov.dominio.ScheduledTask
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*
import android.os.Looper
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener


class VacacionesActivity : AppCompatActivity() {
    var companyId : String = ""
    var userEmail : String = ""
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
        if(bundle!=null && !bundle.isEmpty){
           companyId = bundle.getString("company").toString();
           userEmail = bundle.getString("email").toString();

            //companyId = "7fPPoOKgo9tmPB7YM4ED"
            //userEmail = "pepe@p.com"
        }
        val calendarView = findViewById<com.applandeo.materialcalendarview.CalendarView>(R.id.vacaCalendario)
        val fechaSalView = findViewById<TextView>(R.id.fechSal)
        val fechaRegView = findViewById<TextView>(R.id.fechReg)
        val diasDispView = findViewById<TextView>(R.id.diasVaca)
        //companyId = "7fPPoOKgo9tmPB7YM4ED"
        //userEmail = "pepe@p.com"



        //desactivamos el calendario al inicio
        calendarView.setVisibility(View.INVISIBLE)
        //Ponemos los dias disponibles por defecto
        diasDispView.setText(diasDisp.toString())
        //Asignamos el evento de cambio de fecha en el calendario

        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                Log.i("AgendaActivity", eventDay.calendar.time.toString())
                selected_date = eventDay.calendar.time
                if(Click==1){
                    fechaSalView.setText(selected_date.toString())
                    fechaSal = selected_date
                    Log.i("Vacaciones", "La fecha es: " + selected_date.toString())

                }else {
                    fechaRegView.setText(selected_date.toString())
                    fechaReg = selected_date
                    Log.i("Vacaciones", "La fecha es: " + selected_date.toString())
                }
            }
        })





    }
    fun clickFechaSal(v: View?) {
        val calendarView = findViewById<com.applandeo.materialcalendarview.CalendarView>(R.id.vacaCalendario)
        calendarView.setVisibility(View.VISIBLE)
        Click=1

    }

    fun clickFechaReg(v: View?) {
        val calendarView = findViewById<com.applandeo.materialcalendarview.CalendarView>(R.id.vacaCalendario)
        calendarView.setVisibility(View.VISIBLE)
        Click=0
    }

    fun onClickGuardar(view: android.view.View) {
        val diasDispView = findViewById<TextView>(R.id.diasVaca)
        var intervalo = 0
        Toast.makeText(this, "¡Fechas Guardadas!", Toast.LENGTH_SHORT).show()
        val boton = findViewById<Button>(R.id.buttonVacaciones)

        var cal1 : Calendar = Calendar.getInstance()
        var cal2 : Calendar = Calendar.getInstance()

        cal1.time = fechaSal
        cal2.time = fechaReg


            diasDisp = (diasDisp - ((cal2.get(Calendar.DAY_OF_YEAR)) - (cal1.get(Calendar.DAY_OF_YEAR))))

        /*
        else if (fechaSal.month<fechaReg.month) {
            val yearMonthObject: YearMonth = YearMonth.of(fechaSal.year, fechaSal.month)
            val yearMonth2Object: YearMonth = YearMonth.of(fechaReg.year, fechaReg.month)
            val dias1: Int = yearMonthObject.lengthOfMonth() //28
            val dias2: Int = yearMonth2Object.lengthOfMonth() //28

            diasDisp = (diasDisp - ((dias1+fechaReg.day) - (dias2+fechaSal.day)))
        }
        */

        Log.i("Vacaciones", "Los dias son: " + diasDisp.toString())
        Log.i("Vacaciones", "Los dias son: " + fechaReg.day.toString())
        Log.i("Vacaciones", "Los dias son: " + fechaSal.day.toString())

        diasDispView.text = diasDisp.toString()
        boton.isEnabled = false;
        //Desactivación del boton 2 segundos y activación sucesiva
        Handler(Looper.getMainLooper()).postDelayed({ boton.isEnabled = true }, 2000)
    }

}