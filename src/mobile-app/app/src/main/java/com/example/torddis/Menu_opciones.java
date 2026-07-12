package com.example.torddis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.torddis.clasesGenerales.Dialog;
import com.example.torddis.models.Tutor;
import com.example.torddis.models.UsuarioLogeado;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class Menu_opciones extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    NavigationView navView;
    TextView txtUsuarioLog;
    CircleImageView imgUsuarioLog;
    AlertDialog.Builder builder;
    SharedPreferences session;
    SharedPreferences.Editor editorSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_opciones);
        getSupportActionBar().hide();
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        navView.getHeaderView(0).findViewById(R.id.txtUsuarioLog);
        txtUsuarioLog = navView.getHeaderView(0).findViewById(R.id.txtUsuarioLog);
        imgUsuarioLog = navView.getHeaderView(0).findViewById(R.id.imgUsuarioLog);
        session=this.getSharedPreferences("sesiones", Context.MODE_PRIVATE);
        editorSession= session.edit();
        this.obtenerDatos();
        txtUsuarioLog.setText(UsuarioLogeado.unTutor.getPersona__nombres() + " " + UsuarioLogeado.unTutor.getPersona__apellidos());
        Glide.with(this.getApplicationContext()).load(UsuarioLogeado.unTutor.getFoto_perfil()).into(imgUsuarioLog);
    }
    public void obtenerDatos() {
        String datosSession=session.getString("sesion","");
        try {
            JSONObject json_data = new JSONObject(datosSession);
            Tutor unTutor=new Tutor();
            unTutor.setId(json_data.getInt("id"));
            unTutor.setUsuario(json_data.getString("usuario"));
            unTutor.setCorreo(json_data.getString("correo"));
            unTutor.setFoto_perfil(json_data.getString("foto_perfil"));
            unTutor.setPersona__nombres(json_data.getString("persona__nombres"));
            unTutor.setPersona__apellidos(json_data.getString("persona__apellidos"));
            unTutor.setPersona__fecha_nacimiento(json_data.getString("persona__fecha_nacimiento"));
            UsuarioLogeado.unTutor=unTutor;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void ocSupervisados(View view){
        Intent intent = new Intent(getApplicationContext(), ActSupervisados.class);
        startActivity(intent);
    }
    public void ocObjeto(View view){
        Intent intent = new Intent(getApplicationContext(), ActObjetos.class);
        startActivity(intent);
    }
    public void ocMonitoreo(View view){
        Intent intent = new Intent(getApplicationContext(), ActMonitoreo.class);
        startActivity(intent);
    }
    public void ocHistorial(View view){
        Intent intent = new Intent(getApplicationContext(), ActHistorial.class);
        startActivity(intent);
    }
    public void ocReportes(View view){
        Intent intent = new Intent(getApplicationContext(), ActReportes.class);
        startActivity(intent);
    }
    public void ocCerrarSesion(View view){
        builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Mensaje")
                .setMessage("¿Desea cerrar la sesíon?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editorSession.putBoolean("sesion_guardada",false);
                        editorSession.remove("sesion");
                        editorSession.apply();
                        finish();
                    }
                });
        builder.show();
    }
    public void onOptionsItemSelected(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.OpcEditarPerfil:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(getApplicationContext(), ActEditarTutor.class);
                startActivity(intent);
                return true;
            case R.id.OpcCerrarSesion:
                builder=new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("Mensaje")
                        .setMessage("¿Desea cerrar la sesíon?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editorSession.putBoolean("sesion_guardada",false);
                                editorSession.remove("sesion");
                                editorSession.apply();
                                finish();
                            }
                        });
                builder.show();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}