package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.google.firebase.firestore.FirebaseFirestore

class Organigrama : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organigrama)
        supportActionBar!!.hide()
        logicaOrganigrama()
    }

    fun logicaOrganigrama(){
        val organizationChart= OrganizationChart.getInstance(this)
        val webView = findViewById<WebView>(R.id.organigrama_webview)


        // Llamada a la base de datos
        val db = FirebaseFirestore.getInstance()


        // Lista con todas las jerarquias
        var lista: MutableList<Array<String>> = mutableListOf(arrayOf("",""))
        lista.removeAt(0)

        db.collection("usuarios").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    lista.add(arrayOf(document.get("jefe") as String,document.get("nombre") as String)); // Lista con tupla jerarquia, nombre

                        }
                for (item in lista) {
                    organizationChart.addChildToParent(item[1],item[0])
                    webView.loadData(organizationChart.getChart(), "text/html", "UTF-8")
                }
            }

        organizationChart.addChildToParent("","")   // NO QUITAR ESTA LINEA SI LA QUITAS SE ROMPE


        webView.getSettings().setJavaScriptEnabled(true)
        webView.loadData(organizationChart.getChart(), "text/html", "UTF-8")
    }
}

