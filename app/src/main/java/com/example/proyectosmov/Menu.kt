package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.example.proyectosmov.R
import android.widget.TextView
import android.content.Intent
import android.view.View
import com.example.proyectosmov.FicharLogic
import com.example.proyectosmov.LogIn
import com.example.proyectosmov.AgendaActivity
import com.example.proyectosmov.VacacionesActivity

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = FirebaseFirestore.getInstance()
        setContentView(R.layout.activity_menu)
        supportActionBar!!.hide()
        val bundle = this.intent.extras
        val email = findViewById<TextView>(R.id.textoEmail)
        val nombre = findViewById<TextView>(R.id.textoNombre)
        email.text = bundle!!.getString("email")
        db.collection("usuarios").document("00001").get().addOnSuccessListener {
            nombre.setText("¡Bienvenido, "+it.get("nombre") as String?+"!")
        }
    }

    //Evento que al hacer click te lleva a la pantalla de fichar
    fun clickFichar(v: View?) {
        startActivity(Intent(this@Menu, FicharLogic::class.java))
    }

    //Evento que al hacer click te cierra la sesion y te redirecciona al login
    fun clickCerrarSesion(v: View?) {
        startActivity(Intent(this@Menu, LogIn::class.java))
    }

    //Evento que al hacer click te lleva a la pantalla de Agenda
    fun clickAgenda(v: View?) {
        startActivity(Intent(this@Menu, AgendaActivity::class.java))
    }

    //Evento que al hacer click te cierra la sesion y te redirecciona a la pantalla de Vacaciones
    fun clickVacaciones(v: View?) {
        startActivity(Intent(this@Menu, VacacionesActivity::class.java))
    }
}