package com.example.torddis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SharedPreferences session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        session=this.getSharedPreferences("sesiones", Context.MODE_PRIVATE);
        if(this.revisarSesion()){
            Intent intent = new Intent(getApplicationContext(), Menu_opciones.class);
            startActivity(intent);
        }
    }
     public void ocIniciarSesion(View view){
         Intent intent = new Intent(this, Iniciar_sesion.class);
         startActivity(intent);
     }
     public void ocRegistrarTutor(View view){
        Intent intent = new Intent(this, Registrar_tutor.class);
        startActivity(intent);
    }
    public boolean revisarSesion(){
        return this.session.getBoolean("sesion_guardada",false);

    }
}