package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.example.proyectosmov.R
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter

class AnadirUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_anadir_usuario)
        val administrador = findViewById<View>(R.id.administrador) as Spinner
        val datos = arrayOf("Sí", "No")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, datos
        )
        administrador.adapter = adapter
    }

    fun onClick(v: View?) {
        val db = FirebaseFirestore.getInstance()
        val usuario = findViewById<View>(R.id.usuario) as EditText
        val email = findViewById<View>(R.id.correo) as EditText
        val nombre = findViewById<View>(R.id.nombre) as EditText
        val apellidos = findViewById<View>(R.id.apellidos) as EditText
        val telefono = findViewById<View>(R.id.telefono) as EditText
        val jerarquia = findViewById<View>(R.id.jerarquia) as EditText
        val contrasena = findViewById<View>(R.id.contrasena) as EditText
        val administrador = findViewById<View>(R.id.administrador) as Spinner
        val esAdministrador = administrador.selectedItem.toString().equals("Si")
        db.collection("usuarios").document(email.text.toString()).set(hashMapOf("nombreUsuario" to usuario.text.toString(),
            "nombre" to nombre.text.toString(),
            "apellidos" to apellidos.text.toString(), "telefono" to telefono.text.toString(),
            "jerarquia" to jerarquia.text.toString(),"contraseña" to contrasena.text.toString(),
            "administrador" to esAdministrador))

    }
}