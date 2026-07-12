package com.example.torddis.adapterRcVw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.torddis.R;
import com.example.torddis.interfaces.APIBase;
import com.example.torddis.models.Distraccion;
import com.example.torddis.models.UsuarioLogeado;
import com.example.torddis.webService.Asynchtask;
import com.example.torddis.webService.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdapterDistracciones extends RecyclerView.Adapter<AdapterDistracciones.ViewHolder>  {

    private List<Distraccion> ltDistracciones;
    private Context Ctx;
    private boolean estadoSw=true;
    public AdapterDistracciones(Context mCtx, List<Distraccion> distracciones) {
        this.ltDistracciones = distracciones;
        Ctx=mCtx;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(Ctx);
        View view = inflater.inflate(R.layout.card_tiposdistrac_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Distraccion unaDistracion = ltDistracciones.get(position);
        holder.item_distraccion.setText(unaDistracion.getNombre());
        holder.swtDistrac.setChecked(unaDistracion.isHabilitado());
        holder.swtDistrac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                estadoSw=isChecked;
                JSONObject json_data = new JSONObject();
                try {
                    json_data.put("tipo_dist_id", unaDistracion.getId());
                    json_data.put("tutor_id", UsuarioLogeado.unTutor.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(isChecked){
                    WebService ws= new WebService(Ctx,"POST", APIBase.URLBASE+"monitoreo/tipos-distraccion/?tutor_id="+ UsuarioLogeado.unTutor.getId(),json_data.toString(), (Asynchtask) Ctx);
                    ws.execute();
                }else{
                    WebService ws= new WebService(Ctx,"DELETE", APIBase.URLBASE+"monitoreo/tipos-distraccion/?tutor_id="+ UsuarioLogeado.unTutor.getId()+"&tipo_dist_id="+unaDistracion.getId(), (Asynchtask) Ctx);
                    ws.execute();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ltDistracciones.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_distraccion;
        Switch swtDistrac;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_distraccion=itemView.findViewById(R.id.item_distraccion);
            swtDistrac=itemView.findViewById(R.id.swtDistrac);
        }
    }
}
