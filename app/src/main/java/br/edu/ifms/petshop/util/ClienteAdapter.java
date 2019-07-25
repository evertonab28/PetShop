package br.edu.ifms.petshop.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import br.edu.ifms.petshop.R;

public class ClienteAdapter extends BaseAdapter {

    private Activity activity;
    private Clientes clientes;

    public ClienteAdapter(Activity activity, Clientes clientes) {
        this.activity = activity;
        this.clientes = clientes;
    }

    @Override
    public int getCount() { return clientes.size(); }

    @Override
    public Object getItem(int i) { return clientes.get(i); }

    @Override
    public long getItemId(int i) { return 0; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Cliente cliente = (Cliente) getItem(i);

        View v = activity.getLayoutInflater().inflate(R.layout.cliente, viewGroup, false);

        TextView tvClienteModelo = v.findViewById(R.id.tvClienteModelo);
        tvClienteModelo.setText(cliente.getNome());

        return v;
    }
}
