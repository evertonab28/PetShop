package br.edu.ifms.petshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifms.petshop.util.Cliente;

public class EditarClienteActivity extends NovoClienteActivity {

    public static final String PARAM_CLIENTE = "cliente";
    protected static final String TAG = EditarClienteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        cliente = (Cliente) intent.getSerializableExtra(PARAM_CLIENTE);

        Log.i("EditarClienteActivity", cliente.toString());

        atualizarTitulo();
    }

    private void atualizarTitulo() {
        TextView tvTitulo = findViewById(R.id.tvTitulo);
        tvTitulo.setText("Atualizar cliente");
    }

    @Override
    protected String getUrl() {
        return "http://10.10.36.119:8080/WebAppPetShopAtualizado/webresources/petshop/cliente/get/" + cliente.getId();

    }

    private void carregarCliente() {
        showProgress();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                getUrl(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject jsonCliente = response.getJSONObject(i);

                                Cliente cliente = new Cliente();
                                cliente.setId(jsonCliente.getInt("id"));
                                cliente.setNome(jsonCliente.getString("nome"));
                                cliente.setRG(jsonCliente.getString("RG"));
                                cliente.setCPF(jsonCliente.getString("CPF"));
                                cliente.setTelefone(jsonCliente.getString("telefone"));
                                cliente.setEmail(jsonCliente.getString("email"));
                                cliente.setEndereco(jsonCliente.getString("endereco"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        etNome.setText(cliente.getNome());
                        etRg.setText(cliente.getRG());
                        etCpf.setText(cliente.getCPF());
                        etTelefone.setText(cliente.getTelefone());
                        etEmail.setText(cliente.getEmail());
                        etEndereco.setText(cliente.getEndereco());

                        dismissProgress();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        dismissProgress();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    @Override
    protected JSONObject getParamsCliente() throws JSONException {
        JSONObject params = super.getParamsCliente();
        params.put("_method", "PUT");
        return params;
    }
}
