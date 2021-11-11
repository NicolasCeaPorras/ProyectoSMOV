package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = FirebaseFirestore.getInstance()
        setContentView(R.layout.activity_menu)
        supportActionBar!!.hide()
        val bundle = this.intent.extras
        val email = findViewById<TextView>(R.id.textoEmail)
        val nombre = findViewById<TextView>(R.id.textoNombre)
        val imagenPerfil = findViewById<ImageView>(R.id.imagenPerfil)
        val gestionUsuarios = findViewById<LinearLayout>(R.id.gestionUsuarios)
        val lineaGestionUsuarios = findViewById<View>(R.id.lineaGestionUsuarios)
        //Extraigo del bundle el email y lo escribo en el TextView correspondiente
        email.text = bundle!!.getString("email")
        //Buscar el nombre del usuario en la base de datos y escribirlo en el TextView correspondiente
        db.collection("usuarios").document(email.text.toString()).get().addOnSuccessListener {
            nombre.setText("¡Bienvenido, "+it.get("nombre") as String?+"!")
            if (it.get("administrador") as Boolean) {
                imagenPerfil.setImageDrawable(resources.getDrawable(R.drawable.icono_administrador))
                gestionUsuarios.visibility = View.VISIBLE
                lineaGestionUsuarios.visibility = View.VISIBLE
            }
            else
                imagenPerfil.setImageDrawable(resources.getDrawable(R.drawable.icono_perfil))
        }
    }

    //Evento que al hacer click te lleva a la pantalla de fichar
    fun clickFichar(v: View?) {
        val intent = Intent(this@Menu, FicharLogic::class.java)
        val email = findViewById<TextView>(R.id.textoEmail).text.toString()
        intent.putExtra("email",email)
        startActivity(intent)
    }
    //Evento que al hacer click te cierra la sesion actualmente activa y te redirecciona al login
    fun clickCerrarSesion(v: View?) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@Menu, LogIn::class.java))
    }
    //Evento que al hacer click te lleva a la pantalla de organigrama
    fun clickOrganigrama(v: View?) {
        startActivity(Intent(this@Menu, Organigrama::class.java))
    }
    //Evento que al hacer click te lleva a la pantalla de Agenda
    fun clickAgenda(v: View?) {
        startActivity(Intent(this@Menu, AgendaActivity::class.java))
    }

    //Evento que al hacer click te cierra la sesion y te redirecciona a la pantalla de Vacaciones
    fun clickVacaciones(v: View?) {
        startActivity(Intent(this@Menu, VacacionesActivity::class.java))
    }

    //Evento que al hacer click te lleva a la pantalla de gestion de usuarios
    //Unicamente accesible si el usuario que ha iniciado sesion es administrador
    fun clickGestionUsuarios(v: View?){
        startActivity(Intent(this@Menu,GestionUsuarios::class.java))
    }
    //Evento que al hacer click te lleva a la pantalla de mi presencia
    fun clickMiPresencia(v: View?) {
        val intent = Intent(this@Menu, PresenciaActivity::class.java)
        val email = findViewById<TextView>(R.id.textoEmail).text.toString()
        intent.putExtra("email",email)
        startActivity(intent)
    }
}