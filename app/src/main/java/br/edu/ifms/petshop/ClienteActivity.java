package br.edu.ifms.petshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifms.petshop.util.Cliente;
import br.edu.ifms.petshop.util.ClienteAdapter;
import br.edu.ifms.petshop.util.Clientes;


public class ClienteActivity extends AppCompatActivity {

    private static String ip = "http://10.10.36.119:8080";

    final public static String STATE_CLIENTES = "STATE_CLIENTES";
    private static final int CLIENTE_CADASTRAR = 1;
    private static final int CLIENTE_EDITAR = 2;

    ListView listView;
    Clientes clientes;
    Bundle savedInstanceState;
    ClienteAdapter clienteAdapter;

    private ProgressDialog progress;

    protected void dismissProgress() {
        progress.dismiss();
        progress = null;
    }

    protected void showProgress() {
        progress = new ProgressDialog(this);
        progress.setTitle("Carregando");
        progress.setMessage("Obtendo dados necessários...");
        progress.setCancelable(false);
        progress.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        this.savedInstanceState = savedInstanceState;


        iniciarLista();
        iniciarBtnAddCliente();

    }

    private void iniciarBtnAddCliente() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClienteActivity.this, NovoClienteActivity.class);
                startActivityForResult(intent, CLIENTE_CADASTRAR);
            }
        };

        FloatingActionButton btnAddCliente = findViewById(R.id.btnAddCliente);
        btnAddCliente.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CLIENTE_CADASTRAR || requestCode == CLIENTE_EDITAR) {
            if (resultCode == RESULT_OK) {
                clientes = null;
                clienteAdapter.notifyDataSetChanged();
                iniciarDados();
            }
        }
    }

    private void iniciarLista() {
        listView = findViewById(R.id.listaClientes);

        iniciarDados();


    }

    private void iniciarDados() {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_CLIENTES)) {
                clientes = (Clientes) savedInstanceState.getSerializable(STATE_CLIENTES);
            }
        }

        carregarClientes();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_CLIENTES, clientes);
    }

    private void excluirClienteConfirmacao(final Cliente cliente) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmação")
                .setMessage("Deseja apagar esse cliente?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        excluirCliente(cliente);
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }


    private void excluirCliente(final Cliente cliente) {
        String url = ip + "/WebAppPetShopAtualizado/webresources/petshop/cliente/excluir/" + cliente.getId();

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                clientes.remove(cliente);
                clienteAdapter.notifyDataSetChanged();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
            }
        };

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                listener,
                errorListener
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                JSONObject data = new JSONObject();
                try {
                    data.put("_method", "DELETE");
                    data.put("id", cliente.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return data.toString().getBytes();
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);

        showProgress();
        queue.add(request);
    }

    private void carregarClientes() {
        if (clientes instanceof Clientes) {
            carregarClientesOk();
            return;
        }

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
                        carregarClientesOk();

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

        queue.add(jsonArrayRequest);

    }

    private void carregarClientesOk() {
        clienteAdapter = new ClienteAdapter(ClienteActivity.this, clientes);
        listView.setAdapter(clienteAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cliente cliente = clientes.get(i);

                Intent intent = new Intent(ClienteActivity.this, EditarClienteActivity.class);
                intent.putExtra(EditarClienteActivity.PARAM_CLIENTE, cliente);
                startActivityForResult(intent, CLIENTE_EDITAR);
            }
        };
        listView.setOnItemClickListener(itemClickListener);

        AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                excluirClienteConfirmacao(clientes.get(i));

                return true;
            }
        };
        listView.setOnItemLongClickListener(itemLongClickListener);

    }

}
