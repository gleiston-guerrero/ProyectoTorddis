package com.example.torddis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.torddis.clasesGenerales.Dialog;
import com.example.torddis.interfaces.APIBase;
import com.example.torddis.models.Historial;
import com.example.torddis.models.Objeto;
import com.example.torddis.models.Supervisado;
import com.example.torddis.models.UsuarioLogeado;
import com.example.torddis.webService.Asynchtask;
import com.example.torddis.webService.WebService;
import com.google.android.material.textfield.TextInputEditText;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActReportes extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, Asynchtask {

    TextInputEditText txtFechaGraficos;
    TextView txtSeleccionadoSupR;
    List<Supervisado> ltSupervisados;
    int supervisadoId = 0, anio_seleccionado = 0, mes_seleccionado = 0, dia_seleccionado = 0;
    GraphView grafico_expresiones;
    GraphView grafico_sueno;
    GraphView grafico_objetos;
    String listado="s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_reportes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//mostrar flecha atras
        getSupportActionBar().setTitle("Reportes");
        txtFechaGraficos = findViewById(R.id.txtFechaGraficos);
        txtSeleccionadoSupR=findViewById(R.id.txtSeleccionadoSupR);
        findViewById(R.id.txtFechaGraficos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        grafico_expresiones = findViewById(R.id.grafico_expresiones);
        grafico_sueno = findViewById(R.id.grafico_sueno);
        grafico_objetos = findViewById(R.id.grafico_objetos);

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
        WebService ws= new WebService(ActReportes.this,"GET", APIBase.URLBASE + "persona/supervisado/?tutor_id=" + UsuarioLogeado.unTutor.getId(),this);
        ws.execute();
    }

    public void ocConsultarGraficos(View view){
       if(txtFechaGraficos.getText().toString().equals("") || supervisadoId==0){
           Dialog.showDialog("Faltan datos por seleccionar", this);
        } else {
           listado="r";
            WebService ws = new WebService(ActReportes.this, "GET", APIBase.URLBASE + "monitoreo/graficos/?supervisado_id="+supervisadoId+"&fecha="+txtFechaGraficos.getText().toString(), this);
            ws.execute();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year + "-" + (month+1) + "-" + dayOfMonth;
        this.anio_seleccionado = year;
        this.mes_seleccionado = month;
        this.dia_seleccionado = dayOfMonth;
        txtFechaGraficos.setText(date);
    }

    @Override
    public void processFinish(String result) throws JSONException {
        if(listado.equals("s")){
            listarSupervisados(result);
        }else{
            grafico_expresiones.removeAllSeries();
            grafico_sueno.removeAllSeries();
            grafico_objetos.removeAllSeries();
            ArrayList<Historial> lsHistorial= new ArrayList<Historial>();
            JSONArray JSONlista =  new JSONArray(result);
            // Gráfico de expresiones faciales
            JSONObject jsonExpresiones =  JSONlista.getJSONObject(0);
            BarGraphSeries<DataPoint> series_expresiones = new BarGraphSeries<>(new DataPoint[]{
                    new DataPoint(1,jsonExpresiones.getInt("enfadado")),
                    new DataPoint(2,jsonExpresiones.getInt("disgustado")),
                    new DataPoint(3,jsonExpresiones.getInt("temeroso")),
                    new DataPoint(4,jsonExpresiones.getInt("feliz")),
                    new DataPoint(5,jsonExpresiones.getInt("neutral")),
                    new DataPoint(6,jsonExpresiones.getInt("triste")),
                    new DataPoint(7,jsonExpresiones.getInt("sorprendido"))
            });
            series_expresiones.setTitle("Conteo de expresiones faciales");
            grafico_expresiones.addSeries(series_expresiones);
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(grafico_expresiones);
            staticLabelsFormatter.setHorizontalLabels(new String[] {"enf", "disg", "temer", "feliz", "neutral", "triste", "sorpr"});
            grafico_expresiones.setTitle("Conteo de expresiones faciales");
            grafico_expresiones.getLegendRenderer().setVisible(true);
            grafico_expresiones.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            series_expresiones.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int)data.getX()*255/4,(int)data.getY()*255/6,100);
                }
            });
            series_expresiones.setDrawValuesOnTop(true);
            series_expresiones.setAnimated(true);
            series_expresiones.setValuesOnTopColor(Color.MAGENTA);
            // Gráfico de sueño
            JSONObject jsonSueno =  JSONlista.getJSONObject(1);
            BarGraphSeries<DataPoint> serie_sueno = new BarGraphSeries<>(new DataPoint[]{
                    new DataPoint(1,jsonSueno.getInt("dia_1")),
                    new DataPoint(2,jsonSueno.getInt("dia_2")),
                    new DataPoint(3,jsonSueno.getInt("dia_3")),
                    new DataPoint(4,jsonSueno.getInt("dia_4")),
                    new DataPoint(5,jsonSueno.getInt("dia_5")),
                    new DataPoint(6,jsonSueno.getInt("dia_6")),
                    new DataPoint(7,jsonSueno.getInt("dia_7"))
            });
            grafico_sueno.addSeries(serie_sueno);
            grafico_sueno.setTitle("Conteo de sueño");
            StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(grafico_sueno);
            staticLabelsFormatter2.setHorizontalLabels(new String[] {"dia:1", "dia:2", "dia:3", "dia:4", "dia:5", "dia:6", "dia:7"});
            grafico_sueno.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);
            grafico_sueno.getLegendRenderer().setVisible(true);
            serie_sueno.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int)data.getX()*255/4,(int)data.getY()*255/6,100);
                }
            });
            serie_sueno.setTitle("Últimos 7 días, veces dormido");
            serie_sueno.setDrawValuesOnTop(true);
            serie_sueno.setAnimated(true);
            serie_sueno.setValuesOnTopColor(Color.MAGENTA);
            // Gráfico del uso de objetos
            JSONObject jsonObjetos =  JSONlista.getJSONObject(2);
            BarGraphSeries<DataPoint> serie_objetos = new BarGraphSeries<>(new DataPoint[]{
                    new DataPoint(1,jsonObjetos.getInt("dia_1")),
                    new DataPoint(2,jsonObjetos.getInt("dia_2")),
                    new DataPoint(3,jsonObjetos.getInt("dia_3")),
                    new DataPoint(4,jsonObjetos.getInt("dia_4")),
                    new DataPoint(5,jsonObjetos.getInt("dia_5")),
                    new DataPoint(6,jsonObjetos.getInt("dia_6")),
                    new DataPoint(7,jsonObjetos.getInt("dia_7"))
            });
            grafico_objetos.addSeries(serie_objetos);
            grafico_objetos.setTitle("Conteo de uso de objetos no permitidos por días");
            StaticLabelsFormatter staticLabelsFormatter3 = new StaticLabelsFormatter(grafico_objetos);
            staticLabelsFormatter3.setHorizontalLabels(new String[] {"dia:1", "dia:2", "dia:3", "dia:4", "dia:5", "dia:6", "dia:7"});
            grafico_objetos.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter3);
            grafico_objetos.getLegendRenderer().setVisible(true);
            serie_objetos.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int)data.getX()*255/4,(int)data.getY()*255/6,100);
                }
            });

            serie_objetos.setTitle("Últimos 7 días, uso de objetos sin permiso");
            serie_objetos.setDrawValuesOnTop(true);
            serie_objetos.setAnimated(true);
            serie_objetos.setValuesOnTopColor(Color.MAGENTA);
        }
    }

    public void listarSupervisados(String result) throws JSONException {
        ArrayList<Objeto> lsSuper= new ArrayList<Objeto>();
        JSONArray JSONlista =  new JSONArray(result);
        ltSupervisados=new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ActReportes.this, android.R.layout.simple_selectable_list_item);
        for(int i=0; i< JSONlista.length();i++){
            JSONObject jsonObjecto=  JSONlista.getJSONObject(i);
            Supervisado supervisado=new Supervisado();
            supervisado.setId(jsonObjecto.getInt("id"));
            supervisado.setPersona__nombres(jsonObjecto.getString("persona__nombres")+" "+jsonObjecto.getString("persona__apellidos"));
            ltSupervisados.add(supervisado);
            arrayAdapter.add(jsonObjecto.getString("persona__nombres")+" "+jsonObjecto.getString("persona__apellidos"));
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActReportes.this);
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
                AlertDialog.Builder builderInner = new AlertDialog.Builder(ActReportes.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Ha seleccionado a:");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        for (Supervisado s:ltSupervisados){
                            if (s.getPersona__nombres().equals(strName)){
                                supervisadoId=s.getId();
                                txtSeleccionadoSupR.setText("Seleccionado : "+s.getPersona__nombres());
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