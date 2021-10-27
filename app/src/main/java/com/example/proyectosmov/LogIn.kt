package com.example.proyectosmov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class LogIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Analiticas de firebase BASADAS
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de firebase completa")
        analytics.logEvent("initScreen", bundle)

        //Setup
        setup()
    }

    private fun setup(){
        val botonAcceder = findViewById<Button>(R.id.botonAcceder)
        val email = findViewById<EditText>(R.id.nombreUsuario)
        val password = findViewById<EditText>(R.id.passwordUsuario)

        botonAcceder.setOnClickListener {
            if (password.text.isNotEmpty() and email.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        showMenu(it.result?.user?.email ?: "")
                    }else{
                        showAlert()
                    }
                }
            }
        }
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showMenu(email: String){
        val menuIntent = Intent(this, menu::class.java).apply{
            putExtra("email", email)
        }
        startActivity(menuIntent)
    }
}