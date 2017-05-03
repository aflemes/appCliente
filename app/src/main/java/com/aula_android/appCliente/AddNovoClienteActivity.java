package com.aula_android.appCliente;

import android.app.AlertDialog;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNovoClienteActivity extends AppCompatActivity {
    private long idLinha;
    private EditText txtNome;
    private EditText txtCidade;
    private EditText txtTelefone;
    private EditText txtVendas;
    private Button btnSalvar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_novo_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Adicionar novo cliente");

        txtNome = (EditText) findViewById(R.id.txtNome);
        txtCidade = (EditText) findViewById(R.id.txtCidade);
        txtTelefone = (EditText) findViewById(R.id.txtTelefone);

        txtTelefone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("BR"));

        txtVendas = (EditText) findViewById(R.id.txtVendas);

        Bundle extras = getIntent().getExtras();

        // Se há extras, usa os valores para preencher a tela
        if (extras != null){
            idLinha = extras.getLong("idLinha");
            txtNome.setText(extras.getString("nome"));
            txtCidade.setText(extras.getString("cidade"));
            txtTelefone.setText(extras.getString("telefone"));
            txtVendas.setText(extras.getString("vendas"));
        }

        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(salvarLivroButtonClicked);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }
    View.OnClickListener salvarLivroButtonClicked = new View.OnClickListener(){
        public void onClick(View v){
            if (txtNome.getText().length() != 0){
                AsyncTask<Object, Object, Object> salvaClienteTask = new AsyncTask<Object, Object, Object>(){
                    @Override
                    protected Object doInBackground(Object... params){
                        salvaCliente(); // Salva o livro na base de dados
                        return null;
                    } // end method doInBackground

                    @Override
                    protected void onPostExecute(Object result){
                        finish(); // Fecha a atividade
                    }
                };

                // Salva o livro no BD usando uma thread separada
                salvaClienteTask.execute();
            } // end if
            else {
                // Cria uma caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNovoClienteActivity.this);
                builder.setTitle(R.string.tituloErro);
                builder.setMessage(R.string.mensagemErro);
                builder.setPositiveButton(R.string.botaoErro, null);
                builder.show();
            }
        }
    };

    // Salva o livro na base de dados
    private void salvaCliente(){
        DBAdapter databaseConnector = new DBAdapter(this);
        try{
            databaseConnector.open();
            if (getIntent().getExtras() == null){
                databaseConnector.insereCliente(
                        txtNome.getText().toString(),
                        txtCidade.getText().toString(),
                        txtTelefone.getText().toString(),
                        txtVendas.getText().toString());
            }
            else{
                databaseConnector.alteraCliente(idLinha,
                        txtNome.getText().toString(),
                        txtCidade.getText().toString(),
                        txtTelefone.getText().toString(),
                        txtVendas.getText().toString());
            }
            databaseConnector.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
