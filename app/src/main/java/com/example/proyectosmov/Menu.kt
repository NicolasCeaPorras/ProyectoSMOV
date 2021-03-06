package com.example.proyectosmov


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.proyectosmov.dominio.Company
import com.google.firebase.auth.FirebaseAuth
import com.example.proyectosmov.dominio.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject

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
        val compania = bundle!!.getString("company")
        //Busco la compañia en la base de datos y extraigo el usuario que ha iniciado sesion
        db.collection("companies").document(compania.toString()).get().addOnSuccessListener { documentSnapshot ->
            val company = documentSnapshot.toObject<Company>()
            if (company != null) {
                val usuario = getUserByEmail(company, email.text.toString())

                //Una vez tenemos el usuario escribo sus datos en los campos correspondientes
                nombre.text = "¡Bienvenido, " + usuario?.name as String? + "!"
                //Si el usuario es administrador se le habilita la opcion de gestion de usuarios y se le pone una foto distinta
                if (usuario?.admin!!) {
                    imagenPerfil.setImageDrawable(resources.getDrawable(R.drawable.icono_administrador))
                    //Asigno propiedades al layout para hacerlo visible y utilizable.
                    gestionUsuarios.visibility = View.VISIBLE
                    gestionUsuarios.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    //Hago visible la linea de debajo del layout
                    lineaGestionUsuarios.visibility = View.VISIBLE
                    //Inserto el evento cuando hace click
                    lineaGestionUsuarios.setOnClickListener(View.OnClickListener {
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(this@Menu, LogIn::class.java))
                    })
                } else
                    //si no es administrador se le pone una imagen de usuario normal y no se le habilita la opcion
                    imagenPerfil.setImageDrawable(resources.getDrawable(R.drawable.icono_perfil))
            }
        }
    }

    //Evento que al hacer click te lleva a la actividad de fichar
    fun clickFichar(v: View?) {
        val bundle = this.intent.extras
        val compania = bundle!!.getString("company")
        val intent = Intent(this@Menu, FicharLogic::class.java)
        val email = findViewById<TextView>(R.id.textoEmail).text.toString()
        intent.putExtra("email",email)
        intent.putExtra("company",compania)
        startActivity(intent)
    }
    //Evento que al hacer click te cierra la sesion actualmente activa y te redirecciona al login
    fun clickCerrarSesion(v: View?) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@Menu, LogIn::class.java))
    }
    //Evento que al hacer click te lleva a la actividad de organigrama
    fun clickOrganigrama(v: View?) {
        val bundle = this.intent.extras
        val compania = bundle!!.getString("company")
        val intent = Intent(this@Menu, Organigrama::class.java)
        intent.putExtra("company",compania)
        startActivity(intent)
    }
    //Evento que al hacer click te lleva a la actividad de Agenda
    fun clickAgenda(v: View?) {
        val bundle = this.intent.extras
        val compania = bundle!!.getString("company")
        val email = findViewById<TextView>(R.id.textoEmail).text.toString()
        val intent = Intent(this@Menu, AgendaActivity::class.java)
        intent.putExtra("email",email)
        intent.putExtra("company",compania)
        startActivity(intent)
    }

    //Evento que al hacer click te lleva a la actividad de Vacaciones
    fun clickVacaciones(v: View?) {
        val bundle = this.intent.extras
        val compania = bundle!!.getString("company")
        val email = findViewById<TextView>(R.id.textoEmail).text.toString()
        val intent = Intent(this@Menu, VacacionesActivity::class.java)
        intent.putExtra("email",email)
        intent.putExtra("company",compania)
        startActivity(intent)
    }

    //Evento que al hacer click te lleva a la actividad de gestion de usuarios
    //Unicamente accesible si el usuario que ha iniciado sesion es administrador
    fun clickGestionUsuarios(v: View?){
        val bundle = this.intent.extras
        val compania = bundle!!.getString("company")
        val intent = Intent(this@Menu,GestionUsuarios::class.java)
        intent.putExtra("company",compania)
        startActivity(intent)

    }
    //Evento que al hacer click te lleva a la actividad de TimeSheet
    fun clickMiPresencia(v: View?) {
        val bundle = this.intent.extras
        val compania = bundle!!.getString("company")
        val email = findViewById<TextView>(R.id.textoEmail).text.toString()
        val intent = Intent(this@Menu, PresenciaActivity::class.java)
        intent.putExtra("email",email)
        intent.putExtra("company",compania)
        startActivity(intent)
    }
}