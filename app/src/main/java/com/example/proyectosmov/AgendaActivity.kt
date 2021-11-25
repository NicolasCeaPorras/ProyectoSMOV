package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.proyectosmov.dominio.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import java.text.SimpleDateFormat


class AgendaActivity : AppCompatActivity() {

    var companyId : String = ""
    var userEmail : String = ""
    var selected_date : Date = Date()
    var selected_item : ScheduledTask? = null

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

        //Mostramos las tareas de hoy
        showTasks(selected_date)

        //Asignar evento al boton de añadir
        val buttonAdd = findViewById<ImageView>(R.id.buttonAdd)

        buttonAdd.setOnClickListener {
            addTasck()
        }

        //Asignar evento al boton de eliminar
        val buttonRemove = findViewById<ImageView>(R.id.buttonRemove)

        buttonRemove.setOnClickListener {
            removeTask()
        }

        //Asignamos el evento de cambio de fecha en el calendario
        val calendarView = findViewById<CalendarView>(R.id.agenCalendario)
        calendarView.setOnDateChangeListener(OnDateChangeListener { view, year, month, dayOfMonth ->
            selected_date = SimpleDateFormat("dd-M-yyyy").parse(dayOfMonth.toString()+"-"+(month.toInt()+1).toString()+"-"+year.toString())
            showTasks(selected_date)
        })
    }

    fun addTasck(){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("companies").document(companyId)
        Log.i("AgendaActivity", "Aqui entra")

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val company = documentSnapshot.toObject<Company>()
            if(company != null){
                Log.i("AgendaActivity", "Aqui 2")

                val user = getUserByEmail(company, userEmail)
                if(user != null){
                    val descTask = findViewById<View>(R.id.nota) as EditText
                    val task_date = selected_date

                    Log.i("AgendaActivity", descTask.text.toString())
                    Log.i("AgendaActivity", task_date.toString())

                    var newTask : ScheduledTask = ScheduledTask()
                    //Asignamos los nuevos valores
                    newTask.description = descTask.text.toString()
                    newTask.creation_date = Date()
                    newTask.task_date = task_date

                    //Lo añadimos a la lista de tareas
                    if(user.scheduled_tasks == null){
                        var new_list: MutableList<ScheduledTask> = mutableListOf()
                        user.scheduled_tasks = new_list
                        user.scheduled_tasks!!.add(newTask)
                    } else {
                        user.scheduled_tasks!!.add(newTask)
                    }

                    //Guardamos el resultado en Base de datos
                    db.collection("companies").document(companyId).update("users",company.users).addOnCompleteListener({
                        Log.i("AgendaaActivity","Actualizado")
                        descTask.setText("")
                    })
                }

            }
            //Actualizamos la lista de tareas a mostrar
            showTasks(selected_date)
        }
    }


    fun removeTask(){
        Log.i("AgendaActivity", selected_item.toString())
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("companies").document(companyId)
        Log.i("AgendaActivity", "Aqui entra")

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val company = documentSnapshot.toObject<Company>()
            if(company != null){
                val user = getUserByEmail(company, userEmail)
                if(user != null){
                    //Lo borramos de la lista
                    user.scheduled_tasks!!.remove(selected_item)

                    //Guardamos el resultado en Base de datos
                    db.collection("companies").document(companyId).update("users",company.users).addOnCompleteListener({
                        Log.i("AgendaaActivity","Actualizado")
                        showTasks(selected_date)
                    })
                }

            }
        }
    }


    fun showTasks(date: Date){
        Log.i("AgendaActivity", date.toString())

        var listView = findViewById<View>(R.id.listTasks) as ListView
        var list: ArrayList<ScheduledTask> = ArrayList()
        lateinit var arrayAdapter: ArrayAdapter<ScheduledTask>
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, list)
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("companies").document(companyId)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val company = documentSnapshot.toObject<Company>()
            if(company != null){
                val user = getUserByEmail(company, userEmail)!!
                var tasks_of_the_day : MutableList<ScheduledTask> = gettasksByUserAndDate(user,date)
                Log.i("AgendaActivity", tasks_of_the_day.size.toString())

                for (task in tasks_of_the_day){
                    list.add(task)
                    arrayAdapter.notifyDataSetChanged()
                    listView.adapter = arrayAdapter
                    selected_item = null
                }
                if(tasks_of_the_day.size == 0){
                    //Para borrar si hay alguna anterior
                    arrayAdapter.notifyDataSetChanged()
                    listView.adapter = arrayAdapter
                    selected_item = null
                }
                // Añadimos el evento de seleccionar uno de la lista
                listView.setOnItemClickListener { parent, view, position, id ->
                    Log.i("AgendaActivity", position.toString())
                    listView.setSelection(position)
                    parent.setSelection(position)
                    selected_item = list.get(position)
                }
            }

        }


    }


}