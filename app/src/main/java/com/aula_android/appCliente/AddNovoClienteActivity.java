package com.aula_android.appCliente;

import android.app.AlertDialog;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNovoClienteActivity extends AppCompatActivity {
    private long idLinha;
    private EditText txtTitulo;
    private EditText txtEditora;
    private EditText txtISBN;
    private Button btnSalvar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_novo_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtTitulo = (EditText) findViewById(R.id.txtTitulo);
        txtEditora = (EditText) findViewById(R.id.txtEditora);
        txtISBN = (EditText) findViewById(R.id.txtISBN);

        Bundle extras = getIntent().getExtras();

        // Se há extras, usa os valores para preencher a tela
        if (extras != null){
            idLinha = extras.getLong("idLinha");
            txtTitulo.setText(extras.getString("titulo"));
            txtEditora.setText(extras.getString("editora"));
            txtISBN.setText(extras.getString("isbn"));
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
            if (txtTitulo.getText().length() != 0){
                AsyncTask<Object, Object, Object> salvaLivroTask = new AsyncTask<Object, Object, Object>(){
                    @Override
                    protected Object doInBackground(Object... params){
                        salvaLivro(); // Salva o livro na base de dados
                        return null;
                    } // end method doInBackground

                    @Override
                    protected void onPostExecute(Object result){
                        finish(); // Fecha a atividade
                    }
                };

                // Salva o livro no BD usando uma thread separada
                salvaLivroTask.execute();
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
    private void salvaLivro(){
        DBAdapter databaseConnector = new DBAdapter(this);
        try{
            databaseConnector.open();
            if (getIntent().getExtras() == null){
                databaseConnector.insereLivro(
                        txtTitulo.getText().toString(),
                        txtEditora.getText().toString(),
                        txtISBN.getText().toString());
            }
            else{
                databaseConnector.alteraTitulo(idLinha,
                        txtTitulo.getText().toString(),
                        txtEditora.getText().toString(),
                        txtISBN.getText().toString());
            }
            databaseConnector.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
