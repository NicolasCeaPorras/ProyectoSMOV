package com.example.proyectosmov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class menu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().hide();

        LinearLayout fichar = findViewById(R.id.fichar);
    }

    public void clickFichar (View v){
        startActivity(new Intent(menu.this,FicharLogic.class));
    }
}