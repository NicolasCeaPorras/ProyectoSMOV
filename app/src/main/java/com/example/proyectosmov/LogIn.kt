package com.example.proyectosmov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

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
        val password = findViewById<EditText>(R.id.passwordUsuario)
        val error = findViewById<TextView>(R.id.errorLogin)

        // Cuando se pulsa el boton Acceder se valida el usuario en firebase
        botonAcceder.setOnClickListener {

            // Condiciones para que el login pueda ser correcto
            if (password.text.isNotEmpty() and email.text.isNotEmpty()){
                if (email.text.contains("@") and email.text.contains(".")) {
                    error.text = ""
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showMenu(
                                    it.result?.user?.email ?: ""
                                )  // Se enseña el menu de la aplicacion y se pasa el mail como parametro
                            } else {
                                showAlert() // Muestra el mensaje de error
                            }
                        }
                }
                else{
                    error.text = "No se ha introducido una dirección de mail válida"
                }
            }
            else{
                error.text = "Los campos usuario y contraseña no pueden estar vacios"
            }
        }
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error de autenticación")
        builder.setMessage("Su usuario o contraseña no existen en la base de datos. Por favor, intentelo de nuevo")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showMenu(email: String){
        val menuIntent = Intent(this, Menu::class.java).apply{
            putExtra("email", email)
        }
        startActivity(menuIntent)
    }
}