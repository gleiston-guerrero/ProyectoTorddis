package com.example.torddis.adapterRcVw;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.torddis.ActEditarSupervisado;
import com.example.torddis.ActEntrenar;
import com.example.torddis.R;
import com.example.torddis.interfaces.imgTemporal;
import com.example.torddis.models.Supervisado;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSupervisado extends RecyclerView.Adapter<AdapterSupervisado.ViewHolder>  {

    private List<Supervisado> ltSupervisados;
    private Context Ctx;

    public AdapterSupervisado(Context mCtx, List<Supervisado> supervisados) {
        this.ltSupervisados = supervisados;
        Ctx=mCtx;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(Ctx);
        View view = inflater.inflate(R.layout.card_supervisados_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Supervisado unSupervisado = ltSupervisados.get(position);
        holder.item_supervisado.setText("Nombres : "+unSupervisado.getPersona__nombres()+" "+unSupervisado.getPersona__apellidos());
        holder.item_edad_sup.setText("Edad : "+unSupervisado.getPersona__edad());
        holder.btnEntrenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ctx, ActEntrenar.class);
                intent.putExtra("idSupervisado", unSupervisado.getId());
                Ctx.startActivity(intent);
            }
        });
        holder.btnModificarSup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ctx, ActEditarSupervisado.class);
                intent.putExtra("idSupervisado", unSupervisado.getId());
                intent.putExtra("persona__nombres", unSupervisado.getPersona__nombres());
                intent.putExtra("persona__apellidos", unSupervisado.getPersona__apellidos());
                intent.putExtra("persona__fecha_nacimiento", unSupervisado.getPersona__fecha_nacimiento());
                imgTemporal.IMGSUPERVISADO="";
                imgTemporal.IMGSUPERVISADO=unSupervisado.getPersona__foto_perfil();
                Ctx.startActivity(intent);
            }
        });
        Glide.with(Ctx).load(unSupervisado.getPersona__foto_perfil()).into(holder.item_image_Supervisado);
    }

    @Override
    public int getItemCount() {
        return ltSupervisados.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView item_image_Supervisado;
        TextView item_supervisado;
        TextView item_edad_sup;
        Button btnEntrenar;
        Button btnModificarSup;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_image_Supervisado=itemView.findViewById(R.id.item_image_Supervisado);
            item_supervisado=itemView.findViewById(R.id.item_distraccion);
            item_edad_sup=itemView.findViewById(R.id.item_edad_sup);
            btnEntrenar=itemView.findViewById(R.id.btnEntrenar);
            btnModificarSup=itemView.findViewById(R.id.btnModificarSup);
        }
    }
}
