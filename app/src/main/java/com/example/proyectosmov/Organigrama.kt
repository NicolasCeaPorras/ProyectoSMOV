package com.example.proyectosmov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button

class Organigrama : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organigrama)
        funcion()
    }

    fun funcion(){
        val organizationChart= OrganizationChart.getInstance(this)
        val webView = findViewById<WebView>(R.id.organigrama_webview)

        organizationChart.addChildToParent("Jacob","<div style=\\\"color:red; font-style:italic\\\">President</div>","Mike")
        organizationChart.addChildToParent("Jacob1","Mike")
        organizationChart.addChildToParent("Jacob2","Mike")
        organizationChart.addChildToParent("Jacob3","Mike")
        organizationChart.addChildToParent("Calson1","Jacob1")
        organizationChart.addChildToParent("Calson2","Jacob1")
        organizationChart.addChildToParent("Calson3","Jacob1")
        organizationChart.addChildToParent("Calson4","Jacob1")
        organizationChart.addChildToParent("Marcocacho","Calson1")
        organizationChart.addChildToParent("PutoAmo","Calson1")
        webView.getSettings().setJavaScriptEnabled(true)
        webView.loadData(organizationChart.getChart(), "text/html", "UTF-8")
    }
}