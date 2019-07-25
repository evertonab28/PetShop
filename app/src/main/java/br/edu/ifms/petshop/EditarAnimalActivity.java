package br.edu.ifms.petshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifms.petshop.util.Animal;
import br.edu.ifms.petshop.util.Cliente;
import br.edu.ifms.petshop.util.Raca;

public class EditarAnimalActivity extends NovoAnimalActivity {

    private static String ip = "http://10.10.36.119:8080";
    public static final String PARAM_ANIMAL = "animal";
    protected static final String TAG = EditarAnimalActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        animal = (Animal) intent.getSerializableExtra(PARAM_ANIMAL);

        Log.i("EditarAnimalActivityOLD", animal.toString());

        atualizarTitulo();
    }

    private void atualizarTitulo() {
        TextView tvTitulo = findViewById(R.id.tvTitulo);
        tvTitulo.setText("Atualizar animal");
    }

    @Override
    protected void iniciarSpinnerClienteCompleto() {
        super.iniciarSpinnerClienteCompleto();

        carregarAnimal();
    }

    @Override
    protected String getUrl() {
        return ip + "/WebAppPetShopAtualizado/webresources/petshop/animal/get/" + animal.getId();
    }

    @Override
    protected void salvar() {

        showProgress();

        final String nome = etNome.getText().toString();

        final String idadeTxt = etIdade.getText().toString();
        final Integer idade = Integer.parseInt(idadeTxt);

        final String pesoTxt = etPeso.getText().toString();
        final Double peso = Double.parseDouble(pesoTxt);

        final String porte = etPorte.getText().toString();

        int selectedId = radioSexGroup.getCheckedRadioButtonId();
        radioSexButton = (RadioButton) findViewById(selectedId);

        final String sexo = (String) radioSexButton.getText();

        final Raca raca = (Raca) spinnerRaca.getSelectedItem();

        final Cliente cliente = (Cliente) spinnerCliente.getSelectedItem();

        final String obs = etObservacao.getText().toString();

        final String foto = etFoto.getText().toString();

        Log.i(TAG, nome);
        Log.i(TAG, idadeTxt);
        Log.i(TAG, pesoTxt);
        Log.i(TAG, sexo);
        Log.i(TAG, raca.getRaca());
        Log.i(TAG, String.valueOf(raca.getId()));
        Log.i(TAG, cliente.getNome());
        Log.i(TAG, cliente.getTelefone());
        Log.i(TAG, obs);
        Log.i(TAG, foto);

        animal.setNome(nome);
        animal.setIdade(idade);
        animal.setPeso(peso);
        animal.setPorte(porte);
        animal.setSexo(sexo);
        animal.setRaca(raca);
        animal.setProprietario(cliente);
        animal.setObservacao(obs);
        animal.setUrl(foto);

        String url = ip + "/WebAppPetShopAtualizado/webresources/petshop/animal/alterar";

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, response);

                dismissProgress();

                Toast.makeText(EditarAnimalActivity.this, "Cadastrado!", Toast.LENGTH_LONG).show();

                setResult(RESULT_OK);
                finish();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        StringRequest request = new StringRequest(
                Request.Method.PUT,
                url,
                listener,
                errorListener
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {

                String body = "";
                try {
                    body = EditarAnimalActivity.this.getParamsAnimal().toString();
                    System.out.println("----GET BODY------");
                    System.out.println(body);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return body.getBytes();
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void carregarAnimal() {
        showProgress();

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse: OK ANIMAL");
                Gson g = new Gson();

                try {
                    animal.setId(response.getInt("id"));
                    animal.setNome(response.getString("nome"));
                    animal.setIdade(response.getInt("idade"));
                    animal.setObservacao(response.getString("observacao"));
                    animal.setPeso(response.getDouble("peso"));
                    animal.setPorte(response.getString("porte"));
                    animal.setSexo(response.getString("sexo"));
                    animal.setUrl(response.getString("url"));

                    Cliente proprietario = (Cliente) g.fromJson(response.getJSONObject("proprietario").toString(), Cliente.class);
                    animal.setProprietario(proprietario);

                    Raca raca = g.fromJson(response.getJSONObject("raca").toString(), Raca.class);
                    animal.setRaca(raca);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Cliente cli = animal.getProprietario();

                ArrayAdapter<Cliente> adapterCliente = (ArrayAdapter<Cliente>) spinnerCliente.getAdapter();
                int posCli = adapterCliente.getPosition(cli);
                spinnerCliente.setSelection(posCli);

                ArrayAdapter<Raca> adapterRaca = (ArrayAdapter<Raca>) spinnerRaca.getAdapter();
                int posRaca = adapterRaca.getPosition(animal.getRaca());
                System.out.println("POSICAO");
                String ad = String.valueOf(adapterRaca.getItem(2));
                System.out.println(ad);
                System.out.println(posRaca);
                spinnerRaca.setSelection(posRaca);

                if(animal.getSexo().equals("Fêmea")){
                    radioSexGroup.check(R.id.radioFemale);
                } else if(animal.getSexo().equals("Macho")){
                    radioSexGroup.check(R.id.radioMale);
                }

                etNome.setText(animal.getNome());
                etIdade.setText(animal.getIdade().toString());
                etPeso.setText(animal.getPeso().toString());
                etPorte.setText(animal.getPorte());
                etObservacao.setText(animal.getObservacao());
                etFoto.setText(animal.getUrl());

                dismissProgress();

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                getUrl(),
                null,
                listener,
                errorListener
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            System.out.println(getParamsAnimal());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        queue.add(request);
    }

    @Override
    protected JSONObject getParamsAnimal() throws JSONException {
        JSONObject params = super.getParamsAnimal();
        params.put("id", animal.getId());
        params.put("_method", "PUT");
        return params;
    }
}
