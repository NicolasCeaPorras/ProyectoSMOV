package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.example.proyectosmov.dominio.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions

class GestionUsuarios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_gestion_usuarios)
        val administrador = findViewById<View>(R.id.administrador) as Spinner
        val datos = arrayOf("Sí", "No")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, datos
        )
        administrador.adapter = adapter
    }


    //Se ejecuta al dar click a añadir usuario, inserta un usuario en la base de datos y en la autentificacion
    fun onClickAnadir(v: View?) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val usuario = findViewById<View>(R.id.usuario) as EditText
        val email = findViewById<View>(R.id.correo) as EditText
        val nombre = findViewById<View>(R.id.nombre) as EditText
        val telefono = findViewById<View>(R.id.telefono) as EditText
        val jefe = findViewById<View>(R.id.jefe) as EditText
        val compania  = findViewById<View>(R.id.compania) as EditText
        val contrasena = findViewById<View>(R.id.contrasena) as EditText
        val administrador = findViewById<View>(R.id.administrador) as Spinner
        val esAdministrador = administrador.selectedItem.toString().equals("Sí")
        val user = User(nombre.text.toString(),usuario.text.toString(),jefe.text.toString(),email.text.toString(), contrasena.text.toString(),
                            telefono.text.toString(),esAdministrador,null,null,null,null)
            //Añado el usuario a la base de datos y tambien lo añado a la autentificación
            db.collection("companies").document(compania.text.toString()).set(hashMapOf("users" to FieldValue.arrayUnion(user)), SetOptions.merge()).addOnSuccessListener {
                auth.createUserWithEmailAndPassword(email.text.toString(), contrasena.text.toString())
                val toast = Toast.makeText(this,"Usuario añadido con éxito",Toast.LENGTH_SHORT)
                toast.show()
            }
    }

}