package com.example.torddis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.torddis.clasesGenerales.Dialog;
import com.example.torddis.clasesGenerales.FunIMG;
import com.example.torddis.interfaces.APIBase;
import com.example.torddis.models.UsuarioLogeado;
import com.example.torddis.webService.Asynchtask;
import com.example.torddis.webService.WebService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActEditarTutor extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, Asynchtask  {
    TextInputEditText txtFechaNaceE;
    CircleImageView imgTutorRegE;

    SharedPreferences session;
    SharedPreferences.Editor editorSession;


    AlertDialog.Builder builder;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    JSONObject json_data;
    boolean imagenSelec=false;
    int idTutor=0, anio_seleccionado = 0, mes_seleccionado = 0, dia_seleccionado = 0;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_editar_tutor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//mostrar flecha atras
        getSupportActionBar().setTitle("Modificar cuenta");
        idTutor = UsuarioLogeado.unTutor.getId();

        session=this.getSharedPreferences("sesiones", Context.MODE_PRIVATE);
        editorSession= session.edit();

        imgTutorRegE = (CircleImageView)findViewById(R.id.imgTutorRegE);
        txtFechaNaceE=findViewById(R.id.txtFechaNaceE);
        TextInputLayout tilNombre = findViewById(R.id.tilNombreE);
        TextInputLayout tilApellidos = findViewById(R.id.tilApellidosE);
        TextInputLayout tilCorreo = findViewById(R.id.tilCorreoE);
        TextInputLayout tilFechaNace = findViewById(R.id.tilFechaNaceE);
        TextInputLayout tilUsuarioTutor = findViewById(R.id.tilUsuarioTutorE);
        TextInputLayout tilContrasena = findViewById(R.id.tilContrasenaE);
        TextInputLayout tilContrasenaRep = findViewById(R.id.tilContrasenaRepE);

        tilNombre.getEditText().setText(UsuarioLogeado.unTutor.getPersona__nombres());
        tilApellidos.getEditText().setText(UsuarioLogeado.unTutor.getPersona__apellidos());
        tilCorreo.getEditText().setText(UsuarioLogeado.unTutor.getCorreo());
        tilUsuarioTutor.getEditText().setText(UsuarioLogeado.unTutor.getUsuario());
        txtFechaNaceE.setText(UsuarioLogeado.unTutor.getPersona__fecha_nacimiento());

        LocalDate date = LocalDate.parse(UsuarioLogeado.unTutor.getPersona__fecha_nacimiento());
        if(!UsuarioLogeado.unTutor.getFoto_perfil().isEmpty()){
            Glide.with(this).load(UsuarioLogeado.unTutor.getFoto_perfil()).into(imgTutorRegE);
        }
        this.dia_seleccionado=date.getDayOfMonth();
        this.mes_seleccionado=date.getMonth().getValue()-1;
        this.anio_seleccionado=date.getYear();

        findViewById(R.id.txtFechaNaceE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.datePickerDialog,
                this,
                this.anio_seleccionado,
                this.mes_seleccionado,
                this.dia_seleccionado
        );
        datePickerDialog.show();
    }
    public void ocAbrirGaleriaTutE(View view){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imgTutorRegE.setImageURI(imageUri);
            imagenSelec=true;
        }
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.anio_seleccionado=year;
        this.mes_seleccionado=month;
        this.dia_seleccionado=dayOfMonth;
        String date = year+"-"+(month+1)+"-"+dayOfMonth;
        txtFechaNaceE.setText(date);
    }
    public void ocEditarTutor(View view){
        TextInputLayout tilNombre = findViewById(R.id.tilNombreE);
        TextInputLayout tilApellidos = findViewById(R.id.tilApellidosE);
        TextInputLayout tilCorreo = findViewById(R.id.tilCorreoE);
        TextInputLayout tilFechaNace = findViewById(R.id.tilFechaNaceE);
        TextInputLayout tilUsuarioTutor = findViewById(R.id.tilUsuarioTutorE);
        TextInputLayout tilContrasena = findViewById(R.id.tilContrasenaE);
        TextInputLayout tilContrasenaRep = findViewById(R.id.tilContrasenaRepE);

        if(
                tilNombre.getEditText().getText().toString().trim().isEmpty()||
                        tilApellidos.getEditText().getText().toString().trim().isEmpty()||
                        tilCorreo.getEditText().getText().toString().trim().isEmpty()||
                        tilFechaNace.getEditText().getText().toString().trim().isEmpty()||
                        tilUsuarioTutor.getEditText().getText().toString().trim().isEmpty()
        ){
            Dialog.showDialog("Existen campos sin llenar", this);
        }else{
            if(tilContrasena.getEditText().getText().toString().equals(tilContrasenaRep.getEditText().getText().toString())){
                json_data = new JSONObject();
                try {
                    json_data.put("id", idTutor);
                    json_data.put("usuario", tilUsuarioTutor.getEditText().getText().toString());
                    if(!(tilContrasena.getEditText().getText().toString().trim().isEmpty()||
                    tilContrasenaRep.getEditText().getText().toString().trim().isEmpty()))
                    {
                        json_data.put("clave", tilContrasena.getEditText().getText().toString());
                    }
                    json_data.put("correo", tilCorreo.getEditText().getText().toString());
                    json_data.put("persona__nombres", tilNombre.getEditText().getText().toString());
                    json_data.put("persona__apellidos", tilApellidos.getEditText().getText().toString());
                    json_data.put("persona__fecha_nacimiento", tilFechaNace.getEditText().getText().toString());

                    if (imagenSelec) {
                        String fotoEnBase64 = "";
                        fotoEnBase64 = FunIMG.bitmapBase(((BitmapDrawable) imgTutorRegE.getDrawable()).getBitmap());
                        if (!fotoEnBase64.equals("")) {
                            json_data.put("persona__foto_perfil", "data:image/png;base64," + fotoEnBase64);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                WebService ws= new WebService(this,"PUT", APIBase.URLBASE+"persona/tutor/",json_data.toString(),this);
                ws.execute();
            }else{
                Dialog.showDialog("Las contraseñas no coinciden", this);
            }
        }
    }
    @Override
    public void processFinish(String result) throws JSONException {
        json_data = new JSONObject(result);
        String mensaje=json_data.getString("tutores");
        if(mensaje.equals("usuario repetido")){
            Dialog.showDialog("El usuario ya se encuentra registrado por otra persona", this);
        }else if(mensaje.equals("error")){
            Dialog.showDialog("Error al crear la cuenta de tutor, por favor intente nuevamente", this);
        }else if (mensaje.equals("guardado")){
            builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Mensaje")
                    .setMessage("Cambios registrados exitosamente, reinicie la aplicación para aplicar cambios")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editorSession.putBoolean("sesion_guardada",false);
                            editorSession.remove("sesion");
                            editorSession.apply();
                            finishAffinity();
                        }
                    });
            builder.show();
        }
    }

}