package com.example.torddis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.torddis.adapterRcVw.AdapterObjeto;
import com.example.torddis.interfaces.APIBase;
import com.example.torddis.models.Objeto;
import com.example.torddis.models.UsuarioLogeado;
import com.example.torddis.webService.Asynchtask;
import com.example.torddis.webService.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActObjetos extends AppCompatActivity implements Asynchtask,SearchView.OnQueryTextListener {

    RecyclerView rcvObjetos;
    SearchView txtBuscarObjeto;
    AdapterObjeto adapterObjeto;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_objetos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//mostrar flecha atras
        getSupportActionBar().setTitle("Lista de objetos");

        txtBuscarObjeto=findViewById(R.id.txtBuscarObjeto);
        txtBuscarObjeto.setOnQueryTextListener(this);
        rcvObjetos = (RecyclerView) findViewById(R.id.rcvObjetos);
        rcvObjetos.setHasFixedSize(true);
        rcvObjetos.setLayoutManager(new LinearLayoutManager(this));
        rcvObjetos.setItemAnimator(new DefaultItemAnimator());
        this.obtenerObjetos();
    }

    private void obtenerObjetos() {
        WebService ws= new WebService(ActObjetos.this,"GET",APIBase.URLBASE+"monitoreo/permisos-objeto/?tutor_id="+ UsuarioLogeado.unTutor.getId(),this);
        ws.execute();
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

    @Override
    public void processFinish(String result) throws JSONException {
       try{
            JSONObject jsonResult=  new JSONObject(result);
            if(jsonResult.has("objetos")){
                String msg=(jsonResult.getString("objetos").equals("activado"))?"Objeto habilitado":"Objeto Deshabilitado";
                builder=new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("Mensaje")
                        .setMessage(msg)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActObjetos.this.obtenerObjetos();
                            }
                        });
                builder.show();
            }
        }catch(Exception e){

        }
        ArrayList<Objeto> lsObjetosws= new ArrayList<Objeto>();
        JSONArray JSONlista =  new JSONArray(result);
        for(int i=0; i< JSONlista.length();i++){
            JSONObject jsonObjecto=  JSONlista.getJSONObject(i);
            Objeto unObjeto=new Objeto();
            unObjeto.setId(jsonObjecto.getInt("id"));
            unObjeto.setNombre(jsonObjecto.getString("nombre"));
            unObjeto.setFoto_objeto(jsonObjecto.getString("foto_objeto"));
            unObjeto.setHabilitado(jsonObjecto.getBoolean("habilitado"));
            lsObjetosws.add(unObjeto);
        }
        adapterObjeto=new AdapterObjeto(this,lsObjetosws);
        rcvObjetos.setAdapter(adapterObjeto);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapterObjeto.filtrado(newText);
        return false;
    }
}