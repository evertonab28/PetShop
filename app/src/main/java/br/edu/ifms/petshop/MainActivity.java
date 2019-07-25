package br.edu.ifms.petshop;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private ImageView gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gif = findViewById(R.id.ivGif);
        Glide.with(this).asGif().load(R.drawable.dog4).into(gif);

        inicializarBotoes();
    }

    private void inicializarBotoes() {
        View.OnClickListener listenerClientes = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClienteActivity.class);
                startActivity(intent);
            }
        };

        View.OnClickListener listenerAnimais = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnimalActivity.class);
                startActivity(intent);
            }
        };

        FloatingActionButton btnClientes = findViewById(R.id.btnClientes);
        btnClientes.setOnClickListener(listenerClientes);

        FloatingActionButton btnAnimais = findViewById(R.id.btnAnimais);
        btnAnimais.setOnClickListener(listenerAnimais);
    }
}
