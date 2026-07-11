package com.example.torddis.clasesGenerales;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class Dialog {

    public static void showDialog(String mensaje, Context context) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Mensaje")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            this.finalize();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder.show();
    }
}
