package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

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

    //Se ejecuta al dar click a añadir usuario, inserta un usuario en la base de datos
    fun onClickAnadir(v: View?) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val usuario = findViewById<View>(R.id.usuario) as EditText
        val email = findViewById<View>(R.id.correo) as EditText
        val nombre = findViewById<View>(R.id.nombre) as EditText
        val apellidos = findViewById<View>(R.id.apellidos) as EditText
        val telefono = findViewById<View>(R.id.telefono) as EditText
        val jerarquia = findViewById<View>(R.id.jerarquia) as EditText
        val contrasena = findViewById<View>(R.id.contrasena) as EditText
        val administrador = findViewById<View>(R.id.administrador) as Spinner
        val esAdministrador = administrador.selectedItem.toString().equals("Si")

            db.collection("usuarios").document(email.text.toString()).set(
                hashMapOf(
                    "nombreUsuario" to usuario.text.toString(),
                    "nombre" to nombre.text.toString(),
                    "apellidos" to apellidos.text.toString(),
                    "telefono" to telefono.text.toString(),
                    "jerarquia" to jerarquia.text.toString(),
                    "contraseña" to contrasena.text.toString(),
                    "administrador" to esAdministrador
                )
            )

            auth.createUserWithEmailAndPassword(email.text.toString(), contrasena.text.toString())
    }

    //Se ejecuta al dar click a eliminar usuario, elimina un usuario de la base de datos
    fun onClickEliminar(v: View?) {
        val db = FirebaseFirestore.getInstance()
        val email = findViewById<View>(R.id.correoEliminar) as EditText
        db.collection("usuarios").document(email.text.toString()).delete().addOnCompleteListener {
            if (it.isSuccessful) {

            } else {
                alertaNoExisteUsuario()
            }
        }
    }

    //Muestra una alerta si se intenta eliminar un usuario de la BD
    private fun alertaNoExisteUsuario(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error al eliminar")
        builder.setMessage("El usuario que intenta eliminar no existe en la base de datos. Por favor, intentelo de nuevo")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}