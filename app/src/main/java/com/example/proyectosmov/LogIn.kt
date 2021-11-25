package com.example.proyectosmov

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectosmov.dominio.Company
import com.example.proyectosmov.dominio.getUserByEmail
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class LogIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        //Analiticas de firebase BASADAS
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de firebase completa")
        analytics.logEvent("initScreen", bundle)

        // Inicio de la lógica de la pantalla de inicio
        setup()
    }

    private fun setup(){
        val botonAcceder = findViewById<Button>(R.id.botonAcceder)
        val email = findViewById<EditText>(R.id.nombreUsuario)
        val company = findViewById<EditText>(R.id.companiaUsuario)
        val password = findViewById<EditText>(R.id.passwordUsuario)
        val error = findViewById<TextView>(R.id.errorLogin)
        val carga = findViewById<pl.droidsonroids.gif.GifImageView>(R.id.gifCarga)

        // Cuando se pulsa el boton Acceder se valida el usuario en firebase
        botonAcceder.setOnClickListener {
            botonAcceder.isClickable = false
            botonAcceder.visibility = View.INVISIBLE
            carga.visibility = View.VISIBLE
            val db = FirebaseFirestore.getInstance()

            if (company.text.isNullOrEmpty() or email.text.isNullOrEmpty() or password.text.isNullOrEmpty()) {
                error.text = "Ninguno de los campos puede estar vacio. Por favor inserte los datos"
                botonAcceder.isClickable = true
                botonAcceder.visibility = View.VISIBLE
                carga.visibility = View.INVISIBLE
            } else {
                db.collection("companies").document(company.text.toString()).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val company = documentSnapshot.toObject<Company>()
                        if (company == null) {
                            error.text =
                                "No existe dicha compañia en nuestra base de datos. Por favor vuelva a introducir los datos"
                            botonAcceder.isClickable = true
                            botonAcceder.visibility = View.VISIBLE
                            carga.visibility = View.INVISIBLE
                        } else {
                            val user = getUserByEmail(company, email.text.toString())
                            if (user == null) {
                                error.text =
                                    "Datos de usuario o contraseña incorrectos. Por favor vuelva a introducir los datos"
                                botonAcceder.isClickable = true
                                botonAcceder.visibility = View.VISIBLE
                                carga.visibility = View.INVISIBLE
                            } else {


                                // Condiciones para que el login pueda ser correcto
                                if (password.text.isNotEmpty() and email.text.isNotEmpty()) {
                                    if (email.text.contains("@") and email.text.contains(".")) {
                                        error.text = ""
                                        FirebaseAuth.getInstance()
                                            .signInWithEmailAndPassword(
                                                email.text.toString(),
                                                password.text.toString()
                                            )
                                            .addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    showMenu(
                                                        it.result?.user?.email ?: ""

                                                    )  // Se enseña el menu de la aplicacion y se pasa el mail como parametro
                                                } else {
                                                    error.text = "Su usuario o contraseña no existen en la base de datos. Por favor, intentelo de nuevo"
                                                    botonAcceder.isClickable = true
                                                    botonAcceder.visibility = View.VISIBLE
                                                    carga.visibility = View.INVISIBLE
                                                }
                                            }
                                    } else {
                                        error.text =
                                            "No se ha introducido una dirección de mail válida"
                                        botonAcceder.isClickable = true
                                        botonAcceder.visibility = View.VISIBLE
                                        carga.visibility = View.INVISIBLE
                                    }
                                } else {
                                    error.text =
                                        "Los campos usuario y contraseña no pueden estar vacios"
                                    botonAcceder.isClickable = true
                                    botonAcceder.visibility = View.VISIBLE
                                    carga.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun showMenu(email: String){
        val menuIntent = Intent(this, Menu::class.java).apply{
            putExtra("email", email)
            putExtra("company", findViewById<EditText>(R.id.companiaUsuario).text.toString())
        }
        startActivity(menuIntent)
    }
}