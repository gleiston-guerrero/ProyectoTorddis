package com.example.torddis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.example.torddis.clasesGenerales.Dialog;
import com.example.torddis.clasesGenerales.FunIMG;
import com.example.torddis.interfaces.APIBase;
import com.example.torddis.webService.Asynchtask;
import com.example.torddis.webService.WebService;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registrar_tutor extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, Asynchtask {
    TextInputEditText txtFechaNace;
    CircleImageView imgTutorReg;
    AlertDialog.Builder builder;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    JSONObject json_data;
    boolean imagenSelec=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_tutor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//mostrar flecha atras
        getSupportActionBar().setTitle("Registrar cuenta");

        imgTutorReg = (CircleImageView)findViewById(R.id.imgTutorReg);
        txtFechaNace=findViewById(R.id.txtFechaNace);
        findViewById(R.id.txtFechaNace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
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
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.datePickerDialog,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year+"-"+(month+1)+"-"+dayOfMonth;
        txtFechaNace.setText(date);
    }
    public void ocAbrirGaleria(View view){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imgTutorReg.setImageURI(imageUri);
            imagenSelec=true;
        }
    }

    public void ocRegistrarTutor(View view){

        TextInputLayout tilNombre = findViewById(R.id.tilNombre);
        TextInputLayout tilApellidos = findViewById(R.id.tilApellidos);
        TextInputLayout tilCorreo = findViewById(R.id.tilCorreo);
        TextInputLayout tilFechaNace = findViewById(R.id.tilFechaNace);
        TextInputLayout tilUsuarioTutor = findViewById(R.id.tilUsuarioTutor);
        TextInputLayout tilContrasena = findViewById(R.id.tilContrasena);
        TextInputLayout tilContrasenaRep = findViewById(R.id.tilContrasenaRep);

        if(
                tilNombre.getEditText().getText().toString().trim().isEmpty()||
                tilApellidos.getEditText().getText().toString().trim().isEmpty()||
                tilCorreo.getEditText().getText().toString().trim().isEmpty()||
                tilFechaNace.getEditText().getText().toString().trim().isEmpty()||
                tilUsuarioTutor.getEditText().getText().toString().trim().isEmpty()||
                tilContrasena.getEditText().getText().toString().trim().isEmpty()||
                tilContrasenaRep.getEditText().getText().toString().trim().isEmpty()
        ){
            Dialog.showDialog("Existen campos sin llenar", this);
        }else{
            if(tilContrasena.getEditText().getText().toString().equals(tilContrasenaRep.getEditText().getText().toString())){
                json_data = new JSONObject();
                try {
                    json_data.put("usuario", tilUsuarioTutor.getEditText().getText().toString());
                    json_data.put("clave", tilContrasena.getEditText().getText().toString());
                    json_data.put("correo", tilCorreo.getEditText().getText().toString());
                    json_data.put("persona__nombres", tilNombre.getEditText().getText().toString());
                    json_data.put("persona__apellidos", tilApellidos.getEditText().getText().toString());
                    json_data.put("persona__fecha_nacimiento", tilFechaNace.getEditText().getText().toString());
                    if (imagenSelec) {
                        String fotoEnBase64 = "";
                        fotoEnBase64 = FunIMG.bitmapBase(((BitmapDrawable) imgTutorReg.getDrawable()).getBitmap());
                        if (!fotoEnBase64.equals("")) {
                            json_data.put("persona__foto_perfil", "data:image/png;base64," + fotoEnBase64);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                WebService ws= new WebService(Registrar_tutor.this,"POST", APIBase.URLBASE+"persona/tutor/",json_data.toString(),Registrar_tutor.this);
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
                    .setMessage("Tutor registrado exitosamente")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        }
    }
}