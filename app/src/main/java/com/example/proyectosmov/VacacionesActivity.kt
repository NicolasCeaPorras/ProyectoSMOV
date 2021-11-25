package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import com.example.proyectosmov.R
import android.widget.Toast
import com.example.proyectosmov.dominio.ScheduledTask
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

class VacacionesActivity : AppCompatActivity() {
    var companyId : String = ""
    var userEmail : String = ""
    var selected_date : Date = Date()
    var selected_item : ScheduledTask? = null
    var Click= 0
    var diasDisp= 14
    var fechaSal = Date()
    var fechaReg = Date()
    var diasDispL : Long = 0


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
        val calendarView = findViewById<CalendarView>(R.id.vacaCalendario)
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


        calendarView.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
            selected_date = SimpleDateFormat("dd-M-yyyy").parse(dayOfMonth.toString() + "-" + (month.toInt() + 1).toString() + "-" + year.toString())
            Log.i("Vacaciones", "La fecha es: " + selected_date.toString())
            if(Click==1){
                fechaSalView.setText(selected_date.toString())
                fechaSal = selected_date

            }else if(Click==0) {
                fechaRegView.setText(selected_date.toString())
                fechaReg = selected_date

            }
        })


    }
    fun clickFechaSal(v: View?) {
        val calendarView = findViewById<CalendarView>(R.id.vacaCalendario)
        calendarView.setVisibility(View.VISIBLE)
        Click=1

    }

    fun clickFechaReg(v: View?) {
        val calendarView = findViewById<CalendarView>(R.id.vacaCalendario)
        calendarView.setVisibility(View.VISIBLE)
        Click=0

    }

    fun onClickGuardar(view: android.view.View) {
        val diasDispView = findViewById<TextView>(R.id.diasVaca)

        Toast.makeText(this, "¡Fechas Guardadas!", Toast.LENGTH_LONG).show()
        if(fechaSal.month==fechaReg.month) {
            val yearMonthObject: YearMonth = YearMonth.of(fechaSal.year, fechaSal.month)
            val yearMonth2Object: YearMonth = YearMonth.of(fechaReg.year, fechaReg.month)
            val dias1: Int = yearMonthObject.lengthOfMonth() //28
            val dias2: Int = yearMonth2Object.lengthOfMonth() //28
            diasDisp = (diasDisp - ((fechaReg.day) - (fechaSal.day)))
        }
        else if (fechaSal.month<fechaReg.month) {
            val yearMonthObject: YearMonth = YearMonth.of(fechaSal.year, fechaSal.month)
            val yearMonth2Object: YearMonth = YearMonth.of(fechaReg.year, fechaReg.month)
            val dias1: Int = yearMonthObject.lengthOfMonth() //28
            val dias2: Int = yearMonth2Object.lengthOfMonth() //28

            diasDisp = (diasDisp - ((dias1+fechaReg.day) - (dias2+fechaSal.day)))
        }
        Log.i("Vacaciones", "Los dias son: " + diasDisp.toString())
        diasDispView.setText(diasDisp.toString())
    }

}