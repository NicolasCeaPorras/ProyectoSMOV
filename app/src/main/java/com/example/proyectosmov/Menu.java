package com.example.proyectosmov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Menu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().hide();
        Bundle bundle = this.getIntent().getExtras();
        TextView email = findViewById(R.id.textoEmail);
        TextView nombre = findViewById(R.id.textoNombre);
        email.setText("Email: "+bundle.getString("email"));
    }
    //Evento que al hacer click te lleva a la pantalla de fichar
    public void clickFichar (View v){
        startActivity(new Intent(Menu.this,FicharLogic.class));
    }
    //Evento que al hacer click te cierra la sesion y te redirecciona al login
    public void clickCerrarSesion(View v){startActivity(new Intent(Menu.this,LogIn.class));}
    //Evento que al hacer click te lleva a la pantalla de Agenda
    public void clickAgenda (View v){ startActivity(new Intent(Menu.this,AgendaActivity.class)); }
    //Evento que al hacer click te cierra la sesion y te redirecciona a la pantalla de Vacaciones
    public void clickVacaciones(View v){startActivity(new Intent(Menu.this,VacacionesActivity.class));}
}