package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.Map
import android.util.Log
import android.view.Gravity
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import com.example.proyectosmov.dominio.*

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter


class PresenciaActivity : AppCompatActivity() {

    var companyId : String = ""
    var userEmail : String = ""
    private val MIN_YEAR = 2020


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presencia)
        supportActionBar!!.hide()
        val bundle = getIntent().getExtras();
        if(bundle!=null && !bundle.isEmpty){
//            companyId = bundle.getString("companyId").toString();
//            userEmail = bundle.getString("email").toString();
            companyId = "Prueba"
            userEmail = "email@pruebas.com"
        }

        //Dar valor a los selectores de mes y año
        val cal = Calendar.getInstance()

        val monthPicker = findViewById<View>(R.id.picker_month) as NumberPicker
        val yearPicker = findViewById<View>(R.id.picker_year) as NumberPicker

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = cal[Calendar.MONTH]

        val year = cal[Calendar.YEAR]
        yearPicker.minValue = MIN_YEAR
        yearPicker.maxValue = year
        yearPicker.value = year

        //Asignar evento al boton
        val bottonVerPresencia = findViewById<Button>(R.id.buttonPresencia)

        bottonVerPresencia.setOnClickListener {
            change_presencia_period()
        }

        get_presencia_mes(cal[Calendar.MONTH],cal[Calendar.YEAR])
    }


    fun get_presencia_mes(month: Int, year: Int) : MutableList<TimeRecord> {
        val db = FirebaseFirestore.getInstance()
        var presencia_mes: MutableList<TimeRecord> = mutableListOf()

        val docRef = db.collection("companies").document(companyId)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val company = documentSnapshot.toObject<Company>()
            if(company != null){
                val user = getUserByEmail(company, userEmail)
                if(user != null){
                    presencia_mes = getUserPresencia(month,year,user)
                }
            }
            draw_presencia(presencia_mes)
        }
        return presencia_mes
    }

    fun draw_presencia(presencia_mes: MutableList<TimeRecord> ){
        val formatDate = SimpleDateFormat("dd/MM/yyyy")
        val formatHour = SimpleDateFormat("HH:mm:ss")

        var table = findViewById<View>(R.id.tablaPresencia) as TableLayout
        table.removeAllViews()
        var num_column = 0
        Log.i("PresenciaActivity", presencia_mes.size.toString())
        for (time_record in presencia_mes){
            val newRow = TableRow(this)
            val lp = TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
            lp.setMargins(30, 20, 30, 0);
            newRow.setLayoutParams(lp);



            val column1 = TextView(this)
            column1.setText(formatDate.format(time_record.creation_date!!))
            val lptext = TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT,
                5.0F
            )
            column1.setLayoutParams(lptext)

            val column2 = TextView(this)
            column2.setText(formatHour.format(time_record.start_hour!!))
            column2.setLayoutParams(lptext)

            val column3 = TextView(this)
            column3.setText(formatHour.format(time_record.end_hour!!))
            column3.setLayoutParams(lptext)

            newRow.addView(column1)
            newRow.addView(column2)
            newRow.addView(column3)
            table!!.addView(newRow,num_column)
            num_column = num_column +1
        }
    }


    fun change_presencia_period(){
        val monthPicker = findViewById<View>(R.id.picker_month) as NumberPicker
        val yearPicker = findViewById<View>(R.id.picker_year) as NumberPicker
        get_presencia_mes(monthPicker.value,yearPicker.value)
        get_presencia_mes(monthPicker.value,yearPicker.value)
    }


}



