package com.example.torddis.adapterRcVw;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.torddis.R;
import com.example.torddis.interfaces.APIBase;
import com.example.torddis.models.Historial;
import com.example.torddis.webService.Asynchtask;
import com.example.torddis.webService.WebService;

import java.time.LocalDateTime;
import java.util.List;

public class AdapterHistorial extends RecyclerView.Adapter<AdapterHistorial.ViewHolder>{

    private List<Historial> ltHistorial;
    private Context Ctx;
    public AdapterHistorial(Context mCtx, List<Historial> historial) {
        this.ltHistorial = historial;
        Ctx=mCtx;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(Ctx);
        View view = inflater.inflate(R.layout.card_historial_layout, null);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Historial unaHistoria= ltHistorial.get(position);
        LocalDateTime dateTime = LocalDateTime.parse(unaHistoria.getFecha_hora().substring(0,16));
        if(unaHistoria.getTipo_distraccion__id() == 2){
            holder.item_observacion.setText("Se reconoció la expresión facial: " + unaHistoria.getObservacion());
        }else{
            holder.item_observacion.setText(unaHistoria.getObservacion());
        }
        holder.item_fecha.setText("Fecha: "+dateTime.getYear()+"-"+dateTime.getMonthValue()+"-"+dateTime.getDayOfMonth());
        holder.item_hora.setText("Hora: "+dateTime.getHour()+":"+dateTime.getMinute()+":"+dateTime.getSecond());
        holder.btnVerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebService ws= new WebService(Ctx,"GET", APIBase.URLBASE+"monitoreo/historial/?historial_id="+unaHistoria.getId(), (Asynchtask) Ctx);
                ws.execute();
            }
        });
    }
    @Override
    public int getItemCount() {
        return ltHistorial.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_observacion;
        TextView item_fecha;
        TextView item_hora;
        Button btnVerFoto;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_observacion=itemView.findViewById(R.id.item_observacion);
            item_fecha=itemView.findViewById(R.id.item_fecha);
            item_hora=itemView.findViewById(R.id.item_hora);
            btnVerFoto=itemView.findViewById(R.id.btnVerFoto);
        }
    }
}
