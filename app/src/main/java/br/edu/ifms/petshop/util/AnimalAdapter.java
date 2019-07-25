package br.edu.ifms.petshop.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

import br.edu.ifms.petshop.R;

public class AnimalAdapter extends BaseAdapter {

    private Activity activity;
    private Animais animais;

    public AnimalAdapter(Activity activity, Animais animais) {
        this.activity = activity;
        this.animais = animais;
    }

    public void refresh(Animais animais){
        this.animais.clear();
        this.animais.addAll(animais);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return animais.size(); }

    @Override
    public Object getItem(int i) { return animais.get(i); }

    @Override
    public long getItemId(int i) { return 0; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Animal animal = (Animal) getItem(i);

        View v = activity.getLayoutInflater().inflate(R.layout.animal, viewGroup, false);

        TextView tvProprietario = v.findViewById(R.id.tvProprietário);
        tvProprietario.setText(animal.getProprietario().getNome());

        TextView tvRaca = v.findViewById(R.id.tvRaca);
        tvRaca.setText(animal.getRaca().getRaca());

        TextView tvAnimal = v.findViewById(R.id.tvAnimal);
        tvAnimal.setText(animal.getNome());

        ImageView ivFoto = v.findViewById(R.id.ivFoto);

        // Imagem local
        // ivFoto.setImageResource(carro.getFoto());

        // Imagem da internet
        try {
            String url = URLDecoder.decode(animal.getUrl(), "UTF-8");
            new ImageAsyncTask(ivFoto).execute(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return v;
    }
}
