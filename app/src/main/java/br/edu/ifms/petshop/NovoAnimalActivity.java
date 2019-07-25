package br.edu.ifms.petshop;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifms.petshop.util.Animal;
import br.edu.ifms.petshop.util.Cliente;
import br.edu.ifms.petshop.util.Clientes;
import br.edu.ifms.petshop.util.Raca;
import br.edu.ifms.petshop.util.Racas;

public class NovoAnimalActivity extends AppCompatActivity {

    private static String ip = "http://10.10.36.119:8080";

    private static final String TAG = NovoAnimalActivity.class.getSimpleName();

    Spinner spinnerCliente;
    Spinner spinnerRaca;
    EditText etNome;
    EditText etIdade;
    EditText etPeso;
    RadioGroup radioSexGroup;
    EditText etPorte;
    EditText etObservacao;
    Button btnSalvar;
    RadioButton radioSexButton;
    EditText etFoto;
    Racas racas;
    Clientes clientes;

    private ProgressDialog progress;

    protected Animal animal = new Animal();

    protected void dismissProgress() {
        progress.dismiss();
        progress = null;
    }

    protected void showProgress() {
        progress = new ProgressDialog(this);
        progress.setTitle("Carregando");
        progress.setMessage("Obtendo dados necessários.");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_animal);

        iniciarComponentes();
    }

    private void iniciarComponentes() {
        etNome = findViewById(R.id.etNome);
        etIdade = findViewById(R.id.etIdade);
        etPeso = findViewById(R.id.etPeso);
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        etPorte = findViewById(R.id.etPorte);
        etObservacao = findViewById(R.id.etObservacao);
        etFoto = findViewById(R.id.etFoto);

        iniciarSpinnerRaca();

        iniciarBtnSalvar();
    }

    private void iniciarBtnSalvar() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvar();
            }
        };
        btnSalvar = findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(onClickListener);
    }

    protected String getUrl() {
        return ip + "/WebAppPetShopAtualizado/webresources/petshop/animal/inserir";
    }

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

        String url = getUrl();

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, response);

                dismissProgress();

                Toast.makeText(NovoAnimalActivity.this, "Cadastrado!", Toast.LENGTH_LONG).show();

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
                Request.Method.POST,
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
                    body = NovoAnimalActivity.this.getParamsAnimal().toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return body.getBytes();
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    protected JSONObject getParamsAnimal() throws JSONException {
        JSONObject params = new JSONObject();
        JSONObject proprietario = new JSONObject();
        JSONObject raca = new JSONObject();

        params.put("nome", animal.getNome());
        params.put("idade", animal.getIdade());
        params.put("peso", animal.getPeso());
        params.put("sexo", animal.getSexo());
        params.put("porte", animal.getPorte());

        raca.put("id", animal.getRaca().getId());
        raca.put("raca", animal.getRaca().getRaca());

        params.put("raca", raca);

        proprietario.put("id", animal.getProprietario().getId());
        proprietario.put("cpf", animal.getProprietario().getCPF());
        proprietario.put("rg", animal.getProprietario().getRG());
        proprietario.put("email", animal.getProprietario().getEmail());
        proprietario.put("endereco", animal.getProprietario().getEndereco());
        proprietario.put("nome", animal.getProprietario().getNome());
        proprietario.put("telefone", animal.getProprietario().getTelefone());

        params.put("proprietario", proprietario);

        params.put("observacao", animal.getObservacao());
        params.put("url", animal.getUrl());

        return params;
    }

    private void iniciarSpinnerRaca() {
        spinnerRaca = findViewById(R.id.spinnerRaca);

        showProgress();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = ip + "/WebAppPetShopAtualizado/webresources/petshop/raca/list";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        racas = new Racas();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonRaca = response.getJSONObject(i);

                                Raca raca = new Raca();
                                raca.setId(jsonRaca.getInt("id"));
                                raca.setRaca(jsonRaca.getString("raca"));

                                racas.add(raca);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter adapter = new ArrayAdapter(
                                NovoAnimalActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                racas
                        );
                        spinnerRaca.setAdapter(adapter);

                        iniciarSpinnerRacaCompleto();
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

        queue.add(jsonArrayRequest);
    }

    protected void iniciarSpinnerRacaCompleto() {
        dismissProgress();
        iniciarSpinnerCliente();
    }

    private void iniciarSpinnerCliente() {
        spinnerCliente = findViewById(R.id.spinnerCliente);

        showProgress();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = ip + "/WebAppPetShopAtualizado/webresources/petshop/cliente/list";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        clientes = new Clientes();

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

                                clientes.add(cliente);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter adapter = new ArrayAdapter(
                                NovoAnimalActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                clientes
                        );
                        spinnerCliente.setAdapter(adapter);

                        iniciarSpinnerClienteCompleto();
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

        queue.add(jsonArrayRequest);
    }


    protected void iniciarSpinnerClienteCompleto() {
        dismissProgress();
    }

}
