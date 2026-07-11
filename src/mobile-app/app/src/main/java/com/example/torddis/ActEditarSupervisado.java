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

import com.bumptech.glide.Glide;
import com.example.torddis.clasesGenerales.Dialog;
import com.example.torddis.clasesGenerales.FunIMG;
import com.example.torddis.interfaces.APIBase;
import com.example.torddis.interfaces.imgTemporal;
import com.example.torddis.models.UsuarioLogeado;
import com.example.torddis.webService.Asynchtask;
import com.example.torddis.webService.WebService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActEditarSupervisado extends AppCompatActivity implements Asynchtask, DatePickerDialog.OnDateSetListener {
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    CircleImageView imgUsuarioSupE;
    AlertDialog.Builder builder;
    JSONObject json_data;
    TextInputEditText txtFechaNaceSupE;
    boolean imagenSelec=false;
    int idSupervisado=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_editar_supervisado);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//mostrar flecha atras
        getSupportActionBar().setTitle("Datos del niño");

        idSupervisado = getIntent().getExtras().getInt("idSupervisado");

        TextInputLayout tilNombresSupE=findViewById(R.id.tilNombresSupE);
        TextInputLayout tilApellidosSupE=findViewById(R.id.tilApellidosSupE);
        txtFechaNaceSupE=findViewById(R.id.txtFechaNaceSupE);
        imgUsuarioSupE = (CircleImageView)findViewById(R.id.imgUsuarioSupE);

        tilNombresSupE.getEditText().setText(getIntent().getExtras().getString("persona__nombres"));
        tilApellidosSupE.getEditText().setText(getIntent().getExtras().getString("persona__apellidos"));
        txtFechaNaceSupE.setText(getIntent().getExtras().getString("persona__fecha_nacimiento"));
        if(!imgTemporal.IMGSUPERVISADO.isEmpty()){
            Glide.with(this).load(imgTemporal.IMGSUPERVISADO).into(imgUsuarioSupE);
        }

        findViewById(R.id.txtFechaNaceSupE).setOnClickListener(new View.OnClickListener() {
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
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    public void ocAbrirGaleriaSupE(View view){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imgUsuarioSupE.setImageURI(imageUri);
            imagenSelec=true;
        }
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year+"-"+(month+1)+"-"+dayOfMonth;
        txtFechaNaceSupE.setText(date);
    }
    public void ocEditarSupervisado(View view){

        TextInputLayout tilNombresSup = findViewById(R.id.tilNombresSupE);
        TextInputLayout tilApellidosSup = findViewById(R.id.tilApellidosSupE);
        TextInputLayout tilFechaNaceSup = findViewById(R.id.tilFechaNaceSupE);
        CircleImageView imgUsuarioSup=findViewById(R.id.imgUsuarioSupE);
        if(
                tilApellidosSup.getEditText().getText().toString().trim().isEmpty() ||
                        tilNombresSup.getEditText().getText().toString().trim().isEmpty() ||
                        tilFechaNaceSup.getEditText().getText().toString().trim().isEmpty()
        ){
            Dialog.showDialog("Existen campos sin llenar", this);
        }else {

            json_data = new JSONObject();
            try {
                json_data.put("id", idSupervisado);
                json_data.put("tutor_id", UsuarioLogeado.unTutor.getId());
                json_data.put("persona__nombres", tilNombresSup.getEditText().getText().toString());
                json_data.put("persona__apellidos", tilApellidosSup.getEditText().getText().toString());
                json_data.put("persona__fecha_nacimiento", tilFechaNaceSup.getEditText().getText().toString());

                if (imagenSelec) {
                    String fotoEnBase64="";
                    fotoEnBase64 = FunIMG.bitmapBase(((BitmapDrawable) imgUsuarioSupE.getDrawable()).getBitmap());
                    if (!fotoEnBase64.equals("")) {
                        json_data.put("persona__foto_perfil", "data:image/png;base64," + fotoEnBase64);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            WebService ws = new WebService(ActEditarSupervisado.this, "PUT", APIBase.URLBASE + "persona/supervisado/", json_data.toString(), ActEditarSupervisado.this);
            ws.execute();
        }
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
        json_data = new JSONObject(result);

        String mensaje=json_data.getString("supervisados");
        if(mensaje.equals("guardado")){
            builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Mensaje")
                    .setMessage("Datos del niño modificados exitosamente")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        }else{
            Dialog.showDialog(mensaje, this);
        }
    }
}