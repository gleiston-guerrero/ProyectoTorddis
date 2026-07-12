package com.example.torddis.adapterRcVw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.torddis.ActObjetos;
import com.example.torddis.R;
import com.example.torddis.clasesGenerales.Dialog;
import com.example.torddis.interfaces.APIBase;
import com.example.torddis.models.Objeto;
import com.example.torddis.models.UsuarioLogeado;
import com.example.torddis.webService.Asynchtask;
import com.example.torddis.webService.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterObjeto extends RecyclerView.Adapter<AdapterObjeto.ViewHolder> {

    private List<Objeto> ltObjetos;
    private List<Objeto> ltObjetosCp;
    private Context Ctx;

    public AdapterObjeto(Context mCtx,List<Objeto> objetos) {
        this.ltObjetos = objetos;
        ltObjetosCp=new ArrayList<>();
        ltObjetosCp.addAll(ltObjetos);
        Ctx=mCtx;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(Ctx);
        View view = inflater.inflate(R.layout.card_objetos_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Objeto unObjeto = ltObjetos.get(position);
        int auxPosition=position;
        holder.txtObjeto.setText(unObjeto.getNombre());
        holder.swtObjeto.setChecked(unObjeto.getHabilitado());
        holder.swtObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json_data = new JSONObject();
                try {
                    json_data.put("objeto_id", unObjeto.getId());
                    json_data.put("tutor_id", UsuarioLogeado.unTutor.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ltObjetos.get(auxPosition).getHabilitado()) {
                    ltObjetos.get(auxPosition).setHabilitado(false);
                    WebService ws= new WebService(Ctx,"DELETE", APIBase.URLBASE+"monitoreo/permisos-objeto/?tutor_id="+ UsuarioLogeado.unTutor.getId()+"&objeto_id="+unObjeto.getId(), (Asynchtask) Ctx);
                    ws.execute();
                } else {
                    ltObjetos.get(auxPosition).setHabilitado(true);
                    WebService ws= new WebService(Ctx,"POST", APIBase.URLBASE+"monitoreo/permisos-objeto/",json_data.toString(), (Asynchtask) Ctx);
                    ws.execute();
                }
            }
        });
        Glide.with(Ctx).load(unObjeto.getFoto_objeto()).into(holder.imvObjeto);
    }

    public void filtrado(String txtBuscar){
        int longitud=txtBuscar.length();
        if(longitud==0){
            ltObjetos.clear();
            ltObjetos.addAll(ltObjetosCp);
        }else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Objeto> collecion=ltObjetos.stream()
                        .filter(i -> i.getNombre().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                ltObjetos.clear();
                ltObjetos.addAll(collecion);
            }else{
                for (Objeto o:ltObjetosCp){
                    if (o.getNombre().toLowerCase().contains((txtBuscar.toLowerCase()))){
                        ltObjetos.add(o);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return ltObjetos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtObjeto;
        ImageView imvObjeto;
        Switch swtObjeto;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtObjeto=itemView.findViewById(R.id.item_objeto);
            imvObjeto=itemView.findViewById(R.id.item_image_objeto);
            swtObjeto=itemView.findViewById(R.id.item_objeto_habilitado);
        }
    }
}
