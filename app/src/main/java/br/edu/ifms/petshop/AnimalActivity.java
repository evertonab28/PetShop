package br.edu.ifms.petshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifms.petshop.util.Animais;
import br.edu.ifms.petshop.util.Animal;
import br.edu.ifms.petshop.util.AnimalAdapter;
import br.edu.ifms.petshop.util.Cliente;
import br.edu.ifms.petshop.util.Clientes;
import br.edu.ifms.petshop.util.Raca;
import br.edu.ifms.petshop.util.Racas;

public class AnimalActivity extends AppCompatActivity {

    private static String ip = "http://10.10.36.119:8080";

    final public static String STATE_ANIMAIS = "STATE_ANIMAIS";
    final public static String STATE_RACAS = "STATE_RACAS";
    final public static String STATE_CLIENTES = "STATE_CLIENTES";
    private static final int ANIMAL_CADASTRAR = 1;
    private static final int ANIMAL_EDITAR = 2;

    private Boolean spinnerTouched = false;
    ListView listView;
    Animais animais;
    Clientes clientes;
    Racas racas;
    Bundle savedInstanceState;
    AnimalAdapter animalAdapter;
    AnimalAdapter animalAdapterProprietario;
    Spinner spinnerCliente;

    private ProgressDialog progress;

    protected void dismissProgress() {
        progress.dismiss();
        progress = null;
    }

    protected void showProgress() {
        progress = new ProgressDialog(this);
        progress.setTitle("Carregando");
        progress.setMessage("Obtendo dados...");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);

        this.savedInstanceState = savedInstanceState;

        iniciarLista();
        iniciarBtnAdd();

    }

    private void iniciarBtnAdd() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AnimalActivity.this, NovoAnimalActivity.class);
                startActivityForResult(intent, ANIMAL_CADASTRAR);
            }
        };

        FloatingActionButton btnAdd = findViewById(R.id.btnAddAnimal);
        btnAdd.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ANIMAL_CADASTRAR || requestCode == ANIMAL_EDITAR) {
            if (resultCode == RESULT_OK) {
                animais = null;
                animalAdapter.notifyDataSetChanged();
                iniciarDados();
            }
        }
    }

    private void iniciarLista() {
        listView = findViewById(R.id.listaAnimais);

        iniciarDados();

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Animal animal = animais.get(i);

                Intent intent = new Intent(AnimalActivity.this, EditarAnimalActivity.class);
                intent.putExtra(EditarAnimalActivity.PARAM_ANIMAL, animal);
                startActivityForResult(intent, ANIMAL_EDITAR);

            }
        };
        listView.setOnItemClickListener(itemClickListener);

        AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                excluirAnimalConfirmacao(animais.get(i));

                return true;
            }
        };
        listView.setOnItemLongClickListener(itemLongClickListener);

        listenerSpinner();
    }

    private void listenerSpinner() {
        spinnerCliente = findViewById(R.id.spinnerCliente);
        spinnerCliente.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("Real touch felt.");
                spinnerTouched = true;
                return false;
            }
        });

        spinnerCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (spinnerTouched) {

                    showProgress();
                    final Cliente cliente = (Cliente) spinnerCliente.getSelectedItem();

                    Animais animaisPorProprietario = new Animais();

                    for (int i = 0; i < animais.size(); i++) {
                        Animal an = animais.get(i);

                        if (an.getProprietario().equals(cliente)) {
                            animaisPorProprietario.add(an);
                        }
                    }

                    animalAdapterProprietario = new AnimalAdapter(AnimalActivity.this, animaisPorProprietario);
                    listView.setAdapter(animalAdapterProprietario);
                    dismissProgress();

                }
                spinnerTouched = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void iniciarDados() {
        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey(STATE_RACAS)) {
                racas = (Racas) savedInstanceState.getSerializable(STATE_RACAS);
            }

            if (savedInstanceState.containsKey(STATE_ANIMAIS)) {
                animais = (Animais) savedInstanceState.getSerializable(STATE_ANIMAIS);
            }

            if (savedInstanceState.containsKey(STATE_CLIENTES)) {
                clientes = (Clientes) savedInstanceState.getSerializable(STATE_CLIENTES);
            }
        }

        carregarRacas();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_RACAS, racas);
        outState.putSerializable(STATE_CLIENTES, clientes);
        outState.putSerializable(STATE_ANIMAIS, animais);
    }

    private void excluirAnimalConfirmacao(final Animal animal) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmação")
                .setMessage("Deseja apagar este animal?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //excluirAnimal(animal);
                        excluirAnimalUpdate(animal);
                        dismissProgress();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    //EXCLUI O ANIMAL ATUALIZANDO O LISTVIEW
    private void excluirAnimalUpdate(final Animal animal){
        String url = ip + "/WebAppPetShopAtualizado/webresources/petshop/animal/excluir/" + animal.getId();

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgress();
                animais.remove(animal);
                dismissProgress();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
            }
        };

        StringRequest stringRequest= new StringRequest(
                Request.Method.GET,
                url,
                listener,
                errorListener
        ){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                JSONObject data = new JSONObject();
                try {
                    data.put("_method", "DELETE");
                    data.put("id", animal.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return data.toString().getBytes();
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        showProgress();
        queue.add(stringRequest);

    }

    //EXCLUIR SEM ATUALIZAR A LISTA
    private void excluirAnimal(final Animal animal) {
        String url = ip + "/WebAppPetShopAtualizado/webresources/petshop/animal/excluir/" + animal.getId();

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showProgress();
                animais.remove(animal);
                dismissProgress();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
            }
        };

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
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
                    data.put("id", animal.getId());
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

    private void carregarRacas() {

        if (racas instanceof Racas) {
            carregarRacasOk();
            return;
        }

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

                        carregarRacasOk();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();

                    }
                }
        );

        queue.add(jsonArrayRequest);

    }

    private void carregarRacasOk() {
        carregarClientes();
    }

    private void carregarClientes() {
        if (clientes instanceof Clientes) {
            carregarClientesOk();
            return;
        }

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
                        dismissProgress();
                        carregarClientesOk();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        error.printStackTrace();

                    }
                }
        );
        showProgress();
        queue.add(jsonArrayRequest);
    }

    private void carregarClientesOk() {
        carregarAnimais();
    }

    private void carregarAnimais() {
        if (animais instanceof Animais) {
            carregarAnimaisOk();
            return;
        }

        showProgress();

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = ip + "/WebAppPetShopAtualizado/webresources/petshop/animal/list";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        animais = new Animais();

                        try {
                            for (int i = 0; i < response.length(); i++) {

                                Gson gson = new Gson();
                                JSONObject jsonAnimal = response.getJSONObject(i);

                                Animal animal = new Animal();

                                animal.setId(jsonAnimal.getInt("id"));
                                animal.setNome(jsonAnimal.getString("nome"));
                                animal.setIdade(jsonAnimal.getInt("idade"));
                                animal.setPeso(jsonAnimal.getDouble("peso"));
                                animal.setSexo(jsonAnimal.getString("sexo"));
                                animal.setPorte(jsonAnimal.getString("porte"));
                                animal.setUrl(jsonAnimal.getString("url"));

                                Raca raca = gson.fromJson(jsonAnimal.getJSONObject("raca").toString(), Raca.class);
                                animal.setRaca(raca);

                                Cliente proprietario = gson.fromJson(jsonAnimal.getJSONObject("proprietario").toString(), Cliente.class);
                                animal.setProprietario(proprietario);

                                animais.add(animal);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        carregarAnimaisOk();
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

    private void carregarAnimaisOk() {
        iniciarSpinnerCliente();
    }

    private void iniciarSpinnerCliente() {
        spinnerCliente = findViewById(R.id.spinnerCliente);


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
                                AnimalActivity.this,
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

    private void iniciarSpinnerClienteCompleto() {

        animalAdapter = new AnimalAdapter(AnimalActivity.this, animais);
        listView.setAdapter(animalAdapter);
    }
}
