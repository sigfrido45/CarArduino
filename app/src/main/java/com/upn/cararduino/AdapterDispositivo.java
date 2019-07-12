package com.upn.cararduino;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterDispositivo extends ArrayAdapter {
    int resource;
    Context contexto;
    List<Dispositivo> dispositivos;
    public AdapterDispositivo(Context context, int resource, List<Dispositivo>objects) {
        super(context, resource, objects);
        this.resource=resource;
        this.contexto=context;
        this.dispositivos=objects;
    }

    @Override
    public int getCount() {
        return dispositivos.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(contexto);

        convertView=inflater.inflate(resource,parent,false);
        TextView nombre = convertView.findViewById(R.id.nombre);

        TextView Mac = convertView.findViewById(R.id.mac);
        final Dispositivo dispositivo = dispositivos.get(position);
        nombre.setText(dispositivo.getNombre());
        Mac.setText(dispositivo.getMac());
        return  convertView;
    }
}
