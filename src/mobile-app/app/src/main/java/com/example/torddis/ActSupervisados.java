package com.example.torddis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.torddis.adapterRcVw.AdapterSupervisado;
import com.example.torddis.interfaces.APIBase;
import com.example.torddis.models.Supervisado;
import com.example.torddis.models.UsuarioLogeado;
import com.example.torddis.webService.Asynchtask;
import com.example.torddis.webService.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActSupervisados extends AppCompatActivity implements Asynchtask {
    RecyclerView rcvSupervisados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_supervisados);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//mostrar flecha atras
        getSupportActionBar().setTitle("Lista de niños");

        rcvSupervisados = (RecyclerView) findViewById(R.id.rcvSupervisados);
        rcvSupervisados.setHasFixedSize(true);
        rcvSupervisados.setLayoutManager(new LinearLayoutManager(this));
        rcvSupervisados.setItemAnimator(new DefaultItemAnimator());
    }
    private void obtenerSupervisados() {
        WebService ws= new WebService(ActSupervisados.this,"GET", APIBase.URLBASE+"persona/supervisado/?tutor_id="+ UsuarioLogeado.unTutor.getId(),this);
        ws.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.obtenerSupervisados();
    }

    @Override
    public void processFinish(String result) throws JSONException {
        ArrayList<Supervisado> lsSupervisadosws= new ArrayList<Supervisado>();
        JSONArray JSONlista =  new JSONArray(result);
        for(int i=0; i< JSONlista.length();i++){
            JSONObject jsonObjecto=  JSONlista.getJSONObject(i);
            Supervisado unSupervisado=new Supervisado();
            unSupervisado.setId(jsonObjecto.getInt("id"));
            unSupervisado.setTutor_id(jsonObjecto.getInt("tutor_id"));
            unSupervisado.setPersona_id(jsonObjecto.getInt("persona_id"));
            unSupervisado.setPersona__nombres(jsonObjecto.getString("persona__nombres"));
            unSupervisado.setPersona__apellidos(jsonObjecto.getString("persona__apellidos"));
            unSupervisado.setPersona__fecha_nacimiento(jsonObjecto.getString("persona__fecha_nacimiento"));
            unSupervisado.setPersona__foto_perfil(jsonObjecto.getString("persona__foto_perfil"));
            unSupervisado.setPersona__edad(jsonObjecto.getString("persona__edad"));
            lsSupervisadosws.add(unSupervisado);
        }
        AdapterSupervisado adapterSupervisado=new AdapterSupervisado(this,lsSupervisadosws);
        rcvSupervisados.setAdapter(adapterSupervisado);
    }
    public void ocCuentaSupervisados(View view){
        Intent intent = new Intent(getApplicationContext(), ActCuentaSupervisado.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}