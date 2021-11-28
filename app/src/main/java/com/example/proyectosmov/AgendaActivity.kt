package com.example.proyectosmov

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.proyectosmov.dominio.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import android.widget.TextView


class AgendaActivity : AppCompatActivity() {

    var companyId : String = ""
    var userEmail : String = ""
    var selected_date : Date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        supportActionBar!!.hide()
        val bundle = getIntent().getExtras();
        if(bundle!=null && !bundle.isEmpty){
//            companyId = bundle.getString("companyId").toString();
//            userEmail = bundle.getString("email").toString();
            companyId = "7fPPoOKgo9tmPB7YM4ED"
            userEmail = "pepe@p.com"
        }
        companyId = "7fPPoOKgo9tmPB7YM4ED"
        userEmail = "pepe@p.com"

//        //Asignamos el evento de cambio de fecha en el calendario
        val calendarView = findViewById<com.applandeo.materialcalendarview.CalendarView>(R.id.agenCalendario)
        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                Log.i("AgendaActivity", eventDay.calendar.time.toString())
                selected_date = eventDay.calendar.time
                showTaskActivity(selected_date)
            }
        })

        //Marcar los dias con tareas
        highlightedTasksDays()
    }



    fun showTaskActivity(date: Date){
        val intent = Intent(this@AgendaActivity, TaskOfDayActivity::class.java)
        intent.putExtra("userEmail",userEmail)
        intent.putExtra("companyId",companyId)
        intent.putExtra("selected_date", selected_date.getTime());
        startActivityForResult(intent, 1);

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        highlightedTasksDays()
    }

    fun highlightedTasksDays(){
        val calendarView = findViewById<com.applandeo.materialcalendarview.CalendarView>(R.id.agenCalendario)
        val calendars: MutableList<Calendar> = ArrayList()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("companies").document(companyId)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val company = documentSnapshot.toObject<Company>()
            if(company != null){
                val user = getUserByEmail(company, userEmail)!!
                val tasks = user.scheduled_tasks
                if(tasks != null){
                    val events: MutableList<EventDay> = ArrayList()
                    for(task in tasks){
                        val cal = Calendar.getInstance()
                        events.add(EventDay(cal, R.drawable.icono_agenda))
                        cal.time = task.task_date
                        calendars.add(cal)
                    }
                    calendarView.setEvents(events)
                    calendarView.setHighlightedDays(calendars)
                }
            }
        }
    }



}