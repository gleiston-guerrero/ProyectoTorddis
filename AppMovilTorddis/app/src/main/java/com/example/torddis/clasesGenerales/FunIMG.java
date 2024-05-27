package com.example.torddis.clasesGenerales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class FunIMG {
    public static String bitmapBase(Bitmap bitmap){
        String fotoEnBase64="";
        final int maxSize = 800;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        Bitmap imageScaled = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageScaled.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            byte[] byteArray = byteArrayOutputStream.toByteArray();
            fotoEnBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return fotoEnBase64;
    }
}
