package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.app.AlertDialog
import com.example.proyectosmov.dominio.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener


class GestionUsuarios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_gestion_usuarios)
        val accion = findViewById<View>(R.id.accion) as Spinner
        val datosUsuario = findViewById<LinearLayout>(R.id.datosUsuario)
        val datos = arrayOf("Añadir usuario", "Editar usuario")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos)
        accion.adapter = adapter
        //Al cambiar de opcion en el Spinner de accion se hacen invisibles de nuevo los campos del usuario
        //Evitando así posibles errores
        accion.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                datosUsuario.visibility = View.INVISIBLE
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        val administrador = findViewById<View>(R.id.administrador) as Spinner
        val datosAdministrador = arrayOf("Sí", "No")
        val adapterAdministrador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, datosAdministrador
        )
        administrador.adapter = adapterAdministrador
    }

    //Al introducir un email se da a comprobar
    // Si lo que queremos es editar un usuario se comprueba que existe ese usuario
    // Si lo que queremos es añadir un usuario se comprueba que no exista previamente ese usuario
    fun clickComprobar(v: View) {
        val accion = findViewById<View>(R.id.accion) as Spinner
        val db = FirebaseFirestore.getInstance()
        val bundle = getIntent().getExtras();
        var compania = ""
        if (bundle != null && !bundle.isEmpty)
            compania = bundle.getString("company").toString()


        val email = findViewById<View>(R.id.correo) as EditText
        val datosUsuario = findViewById<LinearLayout>(R.id.datosUsuario)
        //comprueba que el usuario ha introducido un email
        if (email.text.toString() == "" || compania == "" ){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setMessage("Introduce un usuario, por favor")
            builder.setPositiveButton("Aceptar", null)

            val dialog = builder.create()
            dialog.show()
        }
        //Si ha introducido un email y la accion es Añadir Usuario
        else if (accion.selectedItem.toString() == "Añadir usuario") {
            db.collection("companies").document(compania).get()
                .addOnSuccessListener { documentSnapshot ->
                    val company = documentSnapshot.toObject<Company>()
                    if (company == null) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Error")
                        builder.setMessage("No existe esa compañía")
                        builder.setPositiveButton("Aceptar", null)

                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        //Comprueba que el usuario no exista previamente
                        val usuario = getUserByEmail(company, email.text.toString())
                        if (usuario != null) {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Error")
                            builder.setMessage("Ya existe un usuario con ese email")
                            builder.setPositiveButton("Aceptar", null)

                            val dialog = builder.create()
                            dialog.show()
                        } else {
                            //Si no existe previamente se permite al usuario añadir los campos de los datos del usuario
                            val nUsuario = findViewById<View>(R.id.usuario) as EditText
                            val nombre = findViewById<View>(R.id.nombre) as EditText
                            val vacaciones = findViewById<View>(R.id.vacaciones) as EditText
                            val telefono = findViewById<View>(R.id.telefono) as EditText
                            val jefe = findViewById<View>(R.id.jefe) as EditText
                            val contrasena = findViewById<View>(R.id.contrasena) as EditText
                            val administrador = findViewById<View>(R.id.administrador) as Spinner

                            nUsuario.setText("")
                            nombre.setText("")
                            vacaciones.setText("")
                            telefono.setText("")
                            jefe.setText("")
                            contrasena.setText("")
                            administrador.setSelection(1)

                            datosUsuario.visibility = View.VISIBLE
                            //Se sustituye la accion del boton de accion por la funcion que añade un usuario
                            val botonAccion = findViewById<View>(R.id.botonAccion) as Button
                            botonAccion.text = accion.selectedItem.toString()
                            botonAccion.setOnClickListener { onClickAnadir(v) }
                        }
                    }
                }
        }
        //Si la accion es Editar Usuario
        else if (accion.selectedItem.toString() == "Editar usuario") {
            db.collection("companies").document(compania).get()
                .addOnSuccessListener { documentSnapshot ->
                    val company = documentSnapshot.toObject<Company>()
                    if (company == null) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Error")
                        builder.setMessage("No existe esa compañia")
                        builder.setPositiveButton("Aceptar", null)

                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        //Se comprueba que el usuario exista previamente
                        val usuario = getUserByEmail(company, email.text.toString())
                        if (usuario == null) {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Error")
                            builder.setMessage("No existe un usuario con ese email")
                            builder.setPositiveButton("Aceptar", null)

                            val dialog = builder.create()
                            dialog.show()
                        } else {
                            //Si existe se recupera su informacion y se escribe en los campos correspondientes permitiendo la edicion
                            val nUsuario = findViewById<View>(R.id.usuario) as EditText
                            val nombre = findViewById<View>(R.id.nombre) as EditText
                            val vacaciones = findViewById<View>(R.id.vacaciones) as EditText
                            val telefono = findViewById<View>(R.id.telefono) as EditText
                            val jefe = findViewById<View>(R.id.jefe) as EditText
                            val contrasena = findViewById<View>(R.id.contrasena) as EditText
                            val administrador = findViewById<View>(R.id.administrador) as Spinner

                            nUsuario.setText(usuario.user_name)
                            nombre.setText(usuario.name)
                            vacaciones.setText(usuario.diasVac.toString())
                            telefono.setText(usuario.phone_number)
                            jefe.setText(usuario.jefe)
                            contrasena.setText(usuario.password)
                            if (usuario.admin == true)
                                administrador.setSelection(0)
                            else
                                administrador.setSelection(1)

                            datosUsuario.visibility = View.VISIBLE
                            val accion = findViewById<View>(R.id.accion) as Spinner
                            val botonAccion = findViewById<View>(R.id.botonAccion) as Button
                            //Se sustituye la funcion del boton de accion por la de la funcion que permite editar usuarios
                            botonAccion.text = accion.selectedItem.toString()
                            botonAccion.setOnClickListener { onClickEditar(v) }
                        }
                    }
                }
        }
    }

    //Se ejecuta al dar click al boton de añadir usuario
    //Inserta un usuario en la base de datos
    fun onClickAnadir(v: View?) {
        //Recupero toda la informacion que el usuario ha escrito en los campos
        //Para introducirla en un usuario e insertarlo en la BD
        val db = FirebaseFirestore.getInstance()
        val usuario = findViewById<View>(R.id.usuario) as EditText
        val email = findViewById<View>(R.id.correo) as EditText
        val nombre = findViewById<View>(R.id.nombre) as EditText
        val vacaciones = findViewById<View>(R.id.vacaciones) as EditText
        val telefono = findViewById<View>(R.id.telefono) as EditText
        val bundle = getIntent().getExtras();
        var compania = ""
        if (bundle != null && !bundle.isEmpty)
            compania = bundle.getString("company").toString()
        val jefe = findViewById<View>(R.id.jefe) as EditText
        val contrasena = findViewById<View>(R.id.contrasena) as EditText
        val administrador = findViewById<View>(R.id.administrador) as Spinner
        val esAdministrador = administrador.selectedItem.toString().equals("Sí")
        val user = User(
            nombre.text.toString(),
            usuario.text.toString(),
            jefe.text.toString(),
            email.text.toString(),
            contrasena.text.toString(),
            telefono.text.toString(),
            esAdministrador,
            null,
            null,
            null,
            null,
            Integer.parseInt(vacaciones.text.toString())
        )
        //Añado el usuario a la base de datos
        db.collection("companies").document(compania)
            .set(hashMapOf("users" to FieldValue.arrayUnion(user)), SetOptions.merge())
            .addOnSuccessListener {
                val toast =
                    Toast.makeText(v?.context, "Usuario añadido con éxito", Toast.LENGTH_SHORT)
                toast.show()
            }
    }
    //Se ejecuta al dar click al boton de Editar Usuario
    //Edita un usuario ya existente en la base de datos
    fun onClickEditar(v: View?) {
        //Se recupera toda la informacion del usuario de los campos
        val db = FirebaseFirestore.getInstance()
        val usuario = findViewById<View>(R.id.usuario) as EditText
        val email = findViewById<View>(R.id.correo) as EditText
        val nombre = findViewById<View>(R.id.nombre) as EditText
        val vacaciones = findViewById<View>(R.id.vacaciones) as EditText
        val telefono = findViewById<View>(R.id.telefono) as EditText
        val jefe = findViewById<View>(R.id.jefe) as EditText
        val bundle = getIntent().getExtras();
        var compania = ""
        if (bundle != null && !bundle.isEmpty)
            compania = bundle.getString("company").toString()
        val contrasena = findViewById<View>(R.id.contrasena) as EditText
        val administrador = findViewById<View>(R.id.administrador) as Spinner
        val esAdministrador = administrador.selectedItem.toString().equals("Sí")
        //Se recupera la compañia de la base de datos y se inserta el usuario con los datos editados
        db.collection("companies").document(compania).get().addOnSuccessListener { documentSnapshot ->
            val company = documentSnapshot.toObject<Company>()
            if (company != null) {
                val user = getUserByEmail(company, email.text.toString())
                if (user != null){
                    user.user_name = usuario.text.toString()
                    user.name = nombre.text.toString()
                    user.diasVac = Integer.parseInt(vacaciones.text.toString())
                    user.phone_number = telefono.text.toString()
                    user.jefe = jefe.text.toString()
                    user.password = contrasena.text.toString()
                    user.admin = esAdministrador
                    //Se inserta el usuario editado en la base de datos
                    db.collection("companies").document(compania).update("users",company.users).addOnSuccessListener {
                        val toast =
                            Toast.makeText(v?.context, "Usuario editado con éxito", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }
            }
        }
    }
}