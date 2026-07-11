package com.example.torddis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

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

public class ActMonitoreo extends AppCompatActivity implements Asynchtask {

    WebView webvideo;
    AlertDialog alertDialogPersonalizado;
    Switch swtTransmision;
    ProgressDialog progDailog;
    String vinculado = "POST";
    String direccion_ruta = "";
    int idCamara = 0;
    boolean direccion_correcta = true;
    boolean borrarContenidoView = false;
    boolean guardarCamara = true;
    boolean listarDistracciones = false;
    JSONObject json_data;
    RecyclerView rcvDistraccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_monitoreo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//mostrar flecha atras
        getSupportActionBar().setTitle("Monitoreo");

        swtTransmision = findViewById(R.id.swtTransmision);
        webvideo = (WebView) findViewById(R.id.webvideo);

        rcvDistraccion = (RecyclerView) findViewById(R.id.rcvDistraccion);
        rcvDistraccion.setHasFixedSize(true);
        rcvDistraccion.setLayoutManager(new LinearLayoutManager(this));
        rcvDistraccion.setItemAnimator(new DefaultItemAnimator());

        webvideo.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                progDailog.dismiss();
                if (direccion_correcta) {
                    if (borrarContenidoView) {
                        borrarContenidoView = false;
                        Dialog.showDialog("Dirección de transmisión no encontrada", getBaseContext());
                        swtTransmision.setChecked(false);
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

        swtTransmision.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ActMonitoreo.this.verTransmision();
                } else {
                    ActMonitoreo.this.cerrarTransmision();
                }
            }
        });
        this.obtenerCamaras();
    }

    public void verTransmision() {
        String newUA = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
        webvideo.getSettings().setUserAgentString(newUA);
        webvideo.getSettings().setJavaScriptEnabled(true);
        webvideo.getSettings().setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            webvideo.getSettings().setPluginState(WebSettings.PluginState.ON);
        }
        webvideo.loadUrl(APIBase.URLBASE+"monitoreo/stream-monitoreo/");
        progDailog = new ProgressDialog(ActMonitoreo.this);
        progDailog.setMessage("Cargando transmisión");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
    }

    public void cerrarTransmision() {
        webvideo.stopLoading();
    }

    private void obtenerCamaras() {
        guardarCamara = false;
        listarDistracciones = false;
        WebService ws = new WebService(ActMonitoreo.this, "GET", APIBase.URLBASE + "monitoreo/camara/?tutor_id=" + UsuarioLogeado.unTutor.getId(), this);
        ws.execute();
    }

    private void obtenerDistracciones() {
        guardarCamara = false;
        listarDistracciones = true;
        WebService ws = new WebService(ActMonitoreo.this, "GET", APIBase.URLBASE + "monitoreo/tipos-distraccion/?tutor_id=" + UsuarioLogeado.unTutor.getId(), this);
        ws.execute();
    }

    private void guardarCamara(int idCamara, String ruta) throws JSONException {
        if (vinculado.equals("POST")) {
            guardarCamara = true;
            listarDistracciones = false;
            json_data = new JSONObject();
            json_data.put("direccion_ruta", ruta);
            json_data.put("tutor_id", UsuarioLogeado.unTutor.getId());
            WebService ws = new WebService(ActMonitoreo.this, "POST", APIBase.URLBASE + "monitoreo/camara/", json_data.toString(), this);
            ws.execute();
        } else {
            guardarCamara = true;
            listarDistracciones = false;
            json_data = new JSONObject();
            json_data.put("id", idCamara);
            json_data.put("direccion_ruta", ruta);
            json_data.put("tutor_id", UsuarioLogeado.unTutor.getId());
            WebService ws = new WebService(ActMonitoreo.this, "PUT", APIBase.URLBASE + "monitoreo/camara/", json_data.toString(), this);
            ws.execute();
        }
    }

    public void ocVincularDispositivo(View view) {
        this.crearLayoutDialog();
        alertDialogPersonalizado.show();
    }

    public void crearLayoutDialog() {
        LinearLayout layout = new LinearLayout(ActMonitoreo.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText editTextNombre = new EditText(ActMonitoreo.this);
        editTextNombre.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
        editTextNombre.setText(direccion_ruta);
        editTextNombre.setHint("Ingresar dirección IP la cámara");
        layout.addView(editTextNombre);

        final AlertDialog.Builder builder = new AlertDialog.Builder(ActMonitoreo.this);
        builder
                .setTitle("IP Cámara")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ActMonitoreo.this.guardarCamara(idCamara, editTextNombre.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        alertDialogPersonalizado = builder.create();
        alertDialogPersonalizado.setView(layout);
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

    @Override
    public void processFinish(String result) throws JSONException {
        try {
            JSONObject jsonObjecto1=  new JSONObject(result);
            if(jsonObjecto1.has("monitoreo")){
                Dialog.showDialog(jsonObjecto1.getString("monitoreo"), this);
            }
        }catch (Exception e){
        }

        if (guardarCamara) {
            JSONObject jsonObjecto = new JSONObject(result);
            Dialog.showDialog(jsonObjecto.getString("camara"), this);
            direccion_ruta = json_data.getString("direccion_ruta");
            this.obtenerDistracciones();
        } else if (listarDistracciones) {
            ArrayList<Distraccion> lsDistraccionws = new ArrayList<Distraccion>();
            JSONArray JSONlista = new JSONArray(result);
            for (int i = 0; i < JSONlista.length(); i++) {
                JSONObject jsonObjecto = JSONlista.getJSONObject(i);
                Distraccion unaDistraccion = new Distraccion();
                unaDistraccion.setId(jsonObjecto.getInt("id"));
                unaDistraccion.setNombre(jsonObjecto.getString("nombre"));
                unaDistraccion.setHabilitado(jsonObjecto.getBoolean("habilitado"));
                lsDistraccionws.add(unaDistraccion);
            }
            AdapterDistracciones adapterDistracciones = new AdapterDistracciones(this, lsDistraccionws);
            rcvDistraccion.setAdapter(adapterDistracciones);
        } else {
            JSONArray JSONlista = new JSONArray(result);
            if (JSONlista.length() == 0) {
                vinculado = "POST";
            } else {
                vinculado = "PUT";
                JSONObject jsonObjecto = JSONlista.getJSONObject(0);
                idCamara = jsonObjecto.getInt("id");
                direccion_ruta = jsonObjecto.getString("direccion_ruta");
            }
            this.obtenerDistracciones();
        }
    }
}