package br.edu.ifms.petshop;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifms.petshop.util.Cliente;

public class NovoClienteActivity extends AppCompatActivity {

    private static String ip = "http://10.10.36.119:8080";
    public static final String TAG = NovoClienteActivity.class.getSimpleName();

    EditText etNome;
    EditText etRg;
    EditText etCpf;
    EditText etTelefone;
    EditText etEmail;
    EditText etEndereco;
    Button btnSalvar;

    private ProgressDialog progress;

    protected Cliente cliente = new Cliente();

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
        setContentView(R.layout.activity_novo_cliente);

        iniciarComponentes();
    }

    private void iniciarComponentes() {
        etNome = findViewById(R.id.etNome);
        etRg = findViewById(R.id.etIdade);
        etCpf = findViewById(R.id.etPeso);
        etTelefone = findViewById(R.id.etPorte);
        etEmail = findViewById(R.id.etRaca);
        etEndereco = findViewById(R.id.etEndereco);

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
        return ip + "/WebAppPetShopAtualizado/webresources/petshop/cliente/inserir";
    }

    protected void salvar() {
        showProgress();

        final String nome = etNome.getText().toString();
        final String rg = etRg.getText().toString();
        final String cpf = etCpf.getText().toString();
        final String telefone = etTelefone.getText().toString();
        final String email = etEmail.getText().toString();
        final String endereco = etEndereco.getText().toString();

        Log.i(TAG, nome);
        Log.i(TAG, rg);
        Log.i(TAG, cpf);
        Log.i(TAG, telefone);
        Log.i(TAG, email);
        Log.i(TAG, endereco);

        cliente.setNome(nome);
        cliente.setRG(rg);
        cliente.setCPF(cpf);
        cliente.setTelefone(telefone);
        cliente.setEmail(email);
        cliente.setEndereco(endereco);

        String url = getUrl();

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, response);

                dismissProgress();

                Toast.makeText(NovoClienteActivity.this, "Cadastrado!", Toast.LENGTH_LONG).show();

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
                    body = NovoClienteActivity.this.getParamsCliente().toString();
                    System.out.println("--------------body----------");
                    System.out.println(body);
                    //body = NovoAnimalActivity.this.gSonAnimal();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return body.getBytes();
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    protected JSONObject getParamsCliente() throws JSONException {
        JSONObject params = new JSONObject();

        params.put("nome", cliente.getNome());
        params.put("RG", cliente.getRG());
        params.put("CPF", cliente.getCPF());
        params.put("telefone", cliente.getTelefone());
        params.put("email", cliente.getEmail());
        params.put("endereco", cliente.getEndereco());

        return params;
    }

}
