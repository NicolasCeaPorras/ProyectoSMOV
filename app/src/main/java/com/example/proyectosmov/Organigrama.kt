package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.example.proyectosmov.dominio.Company
import com.example.proyectosmov.dominio.getAnUser
import com.example.proyectosmov.dominio.getUserByEmail
import com.example.proyectosmov.dominio.getUserCount
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

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
        webView.setBackgroundResource(R.drawable.fondologin);
        webView.setBackgroundColor(0x00000000);
        webView.clearCache(true)
        val bundle = this.intent.extras
        val compania = bundle!!.getString("company")

        // Llamada a la base de datos
        val db = FirebaseFirestore.getInstance()


        // Lista con todas las jerarquias
        var lista: MutableList<Array<String>> = mutableListOf(arrayOf("",""))
        lista.removeAt(0)

        db.collection("companies").document(compania.toString()).get().addOnSuccessListener { documentSnapshot ->
            val company = documentSnapshot.toObject<Company>()
            if (company != null) {
                val usuarios : Int = getUserCount(company)
                for(i in 0..(usuarios-1)){
                    val user = getAnUser(company, i)
                    lista.add(arrayOf(user?.jefe as String,user.name as String))
                }

                for (item in lista) {
                    organizationChart.addChildToParent(item[1],item[0])
                    webView.loadData(organizationChart.getChart(), "text/html", "UTF-8")
                }
            }
        }
/*
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
*/
        organizationChart.addChildToParent("","")   // NO QUITAR ESTA LINEA SI LA QUITAS SE ROMPE VANPIRO ESITEN


        webView.getSettings().setJavaScriptEnabled(true)
        webView.loadData(organizationChart.getChart(), "text/html", "UTF-8")
    }
}

