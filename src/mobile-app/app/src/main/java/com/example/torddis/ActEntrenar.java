package com.example.torddis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.torddis.adapterRcVw.AdapterDistracciones;
import com.example.torddis.clasesGenerales.Dialog;
import com.example.torddis.interfaces.APIBase;
import com.example.torddis.models.Distraccion;
import com.example.torddis.models.UsuarioLogeado;
import com.example.torddis.webService.Asynchtask;
import com.example.torddis.webService.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimerTask;

public class ActEntrenar extends AppCompatActivity  implements Asynchtask {
    WebView webvideo;
    ProgressDialog progDailog;
    String direccion_ruta = "";
    int idSupervisado = 0;
    boolean direccion_correcta = true;
    boolean borrarContenidoView = false;
    boolean guardarCamara = true;
    AlertDialog alertDialogPersonalizado;
    boolean entrenar = false;
    JSONObject json_data;
    Subtarea subtarea;
    TextView txtPrsEntrenamiento;
    android.app.AlertDialog.Builder builder;

    int idCamara = 0;
    String vinculado = "POST";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_entrenar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//mostrar flecha atras
        getSupportActionBar().setTitle("Entrenamiento facial");
        txtPrsEntrenamiento=findViewById(R.id.txtPrsEntrenamiento);
        subtarea=new Subtarea();
        webvideo = (WebView) findViewById(R.id.webvideoE);

        webvideo.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                progDailog.dismiss();
                if (direccion_correcta) {
                    if (borrarContenidoView) {
                        borrarContenidoView = false;
                        Dialog.showDialog("Dirección de transmisión no encontrada", getBaseContext());
                    }
                } else {
                    borrarContenidoView = true;
                    webvideo.loadUrl("about:blank");
                }
            }

            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                direccion_correcta = false;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                direccion_correcta = true;
            }
        });
        idSupervisado = getIntent().getExtras().getInt("idSupervisado");

        this.obtenerCamaras();
    }

    public void ocVincularDispositivoE(View view) {
        this.crearLayoutDialog();
        alertDialogPersonalizado.show();
    }
    private void guardarCamara(int idCamara, String ruta) throws JSONException {

        guardarCamara = true;
        if (vinculado.equals("POST")) {
            entrenar=false;
            json_data = new JSONObject();
            json_data.put("direccion_ruta", ruta);
            json_data.put("tutor_id", UsuarioLogeado.unTutor.getId());
            WebService ws = new WebService(ActEntrenar.this, "POST", APIBase.URLBASE + "monitoreo/camara/", json_data.toString(), this);
            ws.execute();
        } else {
            entrenar=false;
            json_data = new JSONObject();
            json_data.put("id", idCamara);
            json_data.put("direccion_ruta", ruta);
            json_data.put("tutor_id", UsuarioLogeado.unTutor.getId());
            WebService ws = new WebService(ActEntrenar.this, "PUT", APIBase.URLBASE + "monitoreo/camara/", json_data.toString(), this);
            ws.execute();
        }
    }
    public void crearLayoutDialog() {
        LinearLayout layout = new LinearLayout(ActEntrenar.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText editTextNombre = new EditText(ActEntrenar.this);
        editTextNombre.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
        editTextNombre.setText(direccion_ruta);
        editTextNombre.setHint("Ingresar dirección IP la cámara");
        layout.addView(editTextNombre);

        final AlertDialog.Builder builder = new AlertDialog.Builder(ActEntrenar.this);
        builder
                .setTitle("IP Cámara")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ActEntrenar.this.guardarCamara(idCamara, editTextNombre.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        alertDialogPersonalizado = builder.create();
        alertDialogPersonalizado.setView(layout);
    }
    public void verTransmision() {
        String newUA = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
        webvideo.getSettings().setUserAgentString(newUA);
        webvideo.getSettings().setJavaScriptEnabled(true);
        webvideo.getSettings().setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            webvideo.getSettings().setPluginState(WebSettings.PluginState.ON);
        }
        webvideo.loadUrl( APIBase.URLBASE+"monitoreo/stream-entrenamiento/");
        progDailog = new ProgressDialog(ActEntrenar.this);
        progDailog.setMessage("Cargando transmisión");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
    }


    public void ocEntrenar(View view) throws JSONException {
        subtarea.run();
        entrenar=true;

        guardarCamara = false;
        json_data = new JSONObject();
        json_data.put("supervisado_id", idSupervisado);
        json_data.put("tutor_id", UsuarioLogeado.unTutor.getId());
        WebService ws = new WebService(ActEntrenar.this, "PUT", APIBase.URLBASE + "monitoreo/entrenamiento-facial/",json_data.toString(), this);
        ws.execute();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void obtenerCamaras() {

        guardarCamara = false;
        entrenar = false;
        WebService ws = new WebService(ActEntrenar.this, "GET", APIBase.URLBASE + "monitoreo/camara/?tutor_id=" + UsuarioLogeado.unTutor.getId(), this);
        ws.execute();
    }
    @Override
    public void processFinish(String result) throws JSONException {
        if (guardarCamara) {
            JSONObject jsonObjecto = new JSONObject(result);
            Dialog.showDialog(jsonObjecto.getString("camara"), this);
            direccion_ruta = json_data.getString("direccion_ruta");
            this.obtenerCamaras();
        }

        try {
            JSONObject jsonResult=  new JSONObject(result);
            if (jsonResult.has("fin_entrenamiento")){
                if(!jsonResult.getBoolean("fin_entrenamiento")){
                    txtPrsEntrenamiento.setText(jsonResult.getString("estado"));
                    subtarea.run();
                }else{
                    subtarea.cancel();
                    builder=new android.app.AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setTitle("Mensaje")
                            .setMessage("Entrenamiento finalizado")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                    builder.show();
                }
            }
        }catch (Exception e){

        }


        if (entrenar) {
            verTransmision();
            progDailog.dismiss();
        } else {
            JSONArray JSONlista = new JSONArray(result);
            System.out.println(result);
            if (JSONlista.length() == 0) {
                vinculado = "POST";
                JSONObject jsonObjecto = JSONlista.getJSONObject(0);
                idCamara = jsonObjecto.getInt("id");
                direccion_ruta = jsonObjecto.getString("direccion_ruta");
            } else {
                vinculado = "PUT";
                JSONObject jsonObjecto = JSONlista.getJSONObject(0);
                idCamara = jsonObjecto.getInt("id");
                direccion_ruta = jsonObjecto.getString("direccion_ruta");
            }
        }
    }
    public class Subtarea extends TimerTask {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            WebService ws = new WebService(ActEntrenar.this, "GET", APIBase.URLBASE + "monitoreo/video/?estado_entrenamiento", ActEntrenar.this);
            ws.mensaje=false;
            ws.execute();
        }

    }
}
