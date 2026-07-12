package com.example.torddis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.torddis.adapterRcVw.AdapterHistorial;
import com.example.torddis.clasesGenerales.Dialog;
import com.example.torddis.interfaces.APIBase;
import com.example.torddis.models.Historial;
import com.example.torddis.models.Objeto;
import com.example.torddis.models.Supervisado;
import com.example.torddis.models.UsuarioLogeado;
import com.example.torddis.webService.Asynchtask;
import com.example.torddis.webService.WebService;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActHistorial extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, Asynchtask {
    TextInputEditText txtFechaHistorial;
    TextView txtSeleccionadoSup;
    List<Supervisado> ltSupervisados;
    int supervisadoId = 0, anio_seleccionado = 0, mes_seleccionado = 0, dia_seleccionado = 0;
    AdapterHistorial adapterHistorial;
    RecyclerView rcvHistorial;
    String listado="h";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_historial);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//mostrar flecha atras
        getSupportActionBar().setTitle("Historial");
        txtFechaHistorial=findViewById(R.id.txtFechaHistorial);
        txtSeleccionadoSup=findViewById(R.id.txtSeleccionadoSup);
        findViewById(R.id.txtFechaHistorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        rcvHistorial = (RecyclerView) findViewById(R.id.rcvHistorial);
        rcvHistorial.setHasFixedSize(true);
        rcvHistorial.setLayoutManager(new LinearLayoutManager(this));
        rcvHistorial.setItemAnimator(new DefaultItemAnimator());

        ListarHistorial();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.datePickerDialog,
                this,
                this.anio_seleccionado == 0? Calendar.getInstance().get(Calendar.YEAR): this.anio_seleccionado,
                this.mes_seleccionado == 0? Calendar.getInstance().get(Calendar.MONTH): this.mes_seleccionado,
                this.dia_seleccionado == 0? Calendar.getInstance().get(Calendar.DAY_OF_MONTH): this.dia_seleccionado
        );
        datePickerDialog.show();
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
    public void ocListarSupervisados(View view){
        listado="s";
        WebService ws= new WebService(ActHistorial.this,"GET", APIBase.URLBASE+"persona/supervisado/?tutor_id="+UsuarioLogeado.unTutor.getId(),this);
        ws.execute();
    }
    public void ListarHistorial(){
        Date date = new Date();
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String fecha=dateFormat.format(date);
        WebService ws= new WebService(ActHistorial.this,"GET", APIBase.URLBASE+"monitoreo/historial/?tutor_id="+UsuarioLogeado.unTutor.getId()+"&fecha_actual="+fecha,this);
        ws.execute();
    }
    public void ocConsultarHistorial(View view){
        if(txtFechaHistorial.getText().toString().equals("") || supervisadoId==0){
            Dialog.showDialog("Faltan datos por seleccionar", this);
        }else {
            listado="h";
            WebService ws = new WebService(ActHistorial.this, "GET", APIBase.URLBASE + "monitoreo/historial/?supervisado_id="+supervisadoId+"&fecha="+txtFechaHistorial.getText().toString(), this);
            ws.execute();
        }
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year+"-"+(month+1)+"-"+dayOfMonth;
        this.anio_seleccionado = year;
        this.mes_seleccionado = month;
        this.dia_seleccionado = dayOfMonth;
        txtFechaHistorial.setText(date);
    }

    @Override
    public void processFinish(String result) throws JSONException {
        if (listado.equals("h")) {
            ArrayList<Historial> lsHistorial = new ArrayList<Historial>();
            JSONArray JSONlista = new JSONArray(result);
            for (int i = 0; i < JSONlista.length(); i++) {
                JSONObject jsonObjecto = JSONlista.getJSONObject(i);
                Historial unaHistoria = new Historial();
                unaHistoria.setId(jsonObjecto.getInt("id"));
                unaHistoria.setObservacion(jsonObjecto.getString("observacion"));
                unaHistoria.setImagen_evidencia(jsonObjecto.getString("imagen_evidencia"));
                unaHistoria.setFecha_hora(jsonObjecto.getString("fecha_hora"));
                unaHistoria.setTipo_distraccion__nombre(jsonObjecto.getString("tipo_distraccion__nombre"));
                unaHistoria.setTipo_distraccion__id(jsonObjecto.getInt("tipo_distraccion_id"));
                lsHistorial.add(unaHistoria);
            }
            adapterHistorial = new AdapterHistorial(this, lsHistorial);
            rcvHistorial.setAdapter(adapterHistorial);
            listado="E";
        }else if(listado.equals("s")){
            listarSupervisados(result);
            listado="E";
        }else{
            JSONArray JSONlista = new JSONArray(result);
            JSONObject jsonObjecto = JSONlista.getJSONObject(0);

            Historial unaHistoria=new Historial();
            unaHistoria.setImagen_evidencia(jsonObjecto.getString("imagen_evidencia"));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater factory = LayoutInflater.from(this);
            final View view = factory.inflate(R.layout.layout_ver_foto, null);
            builder.setView(view);

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dlg, int sumthin) {
                    dlg.dismiss();
                }
            });
            ImageView imageView=view.findViewById(R.id.imgHistorial);
            Glide.with(this).load(unaHistoria.getImagen_evidencia()).into(imageView);
            builder.show();
        }

    }
    public void listarSupervisados(String result) throws JSONException {
        ArrayList<Objeto> lsSuper= new ArrayList<Objeto>();
        JSONArray JSONlista =  new JSONArray(result);
        ltSupervisados=new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ActHistorial.this, android.R.layout.simple_selectable_list_item);
        for(int i=0; i< JSONlista.length();i++){
            JSONObject jsonObjecto=  JSONlista.getJSONObject(i);
            Supervisado supervisado=new Supervisado();
            supervisado.setId(jsonObjecto.getInt("id"));
            supervisado.setPersona__nombres(jsonObjecto.getString("persona__nombres")+" "+jsonObjecto.getString("persona__apellidos"));
            ltSupervisados.add(supervisado);
            arrayAdapter.add(jsonObjecto.getString("persona__nombres")+" "+jsonObjecto.getString("persona__apellidos"));
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActHistorial.this);
        builderSingle.setCancelable(false);
        builderSingle.setIcon(R.drawable.ic_person);
        builderSingle.setTitle("Seleccione un niño");

        builderSingle.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(ActHistorial.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Ha seleccionado a:");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        for (Supervisado s:ltSupervisados){
                            if (s.getPersona__nombres().equals(strName)){
                                supervisadoId=s.getId();
                                txtSeleccionadoSup.setText("Seleccionado : "+s.getPersona__nombres());
                            }
                        }
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }
}