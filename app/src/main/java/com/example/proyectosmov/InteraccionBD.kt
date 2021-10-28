package com.example.proyectosmov

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InteraccionBD {

    val db = Firebase.firestore //Interaccion con la base de datos

    fun leerDatos(coleccion: String, documento: String, campo: String) : String{
        var consulta = ""
        db.collection(coleccion).document(documento).get().addOnSuccessListener{
            consulta = it.get(campo) as String
        }
        if(consulta.equals("")){
            return "Error en la lectura"
        }
        else{
            return consulta
        }
    }
}