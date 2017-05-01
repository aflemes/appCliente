package com.aula_android.appCliente;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewClienteActivity extends AppCompatActivity {
    private long idLinha;
    private TextView lblNome;
    private TextView lblCidade;
    private TextView lblTelefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_clientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lblNome = (TextView) findViewById(R.id.lblNome);
        lblCidade = (TextView) findViewById(R.id.lblCidade);
        lblTelefone = (TextView) findViewById(R.id.lblTelefone);

        Bundle extras = getIntent().getExtras();
        idLinha = extras.getLong(MainActivity.LINHA_ID);

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
    @Override
    protected void onResume(){
        super.onResume();
        new CarregaLivroTask().execute(idLinha);
    }
    // Executa a consulta em uma thead separada
    private class CarregaLivroTask extends AsyncTask<Long, Object, Cursor> {
        DBAdapter databaseConnector = new DBAdapter(ViewClienteActivity.this);

        @Override
        protected Cursor doInBackground(Long... params){
            databaseConnector.open();
            return databaseConnector.getCliente(params[0]);
        }
        // Usa o Cursor retornado do método doInBackground
        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            result.moveToFirst();

            int nomeIndex = result.getColumnIndex("nome");
            int cidadeIndex = result.getColumnIndex("cidade");
            int telefoneIndex = result.getColumnIndex("telefone");

            lblNome.setText(result.getString(nomeIndex));
            lblCidade.setText(result.getString(cidadeIndex));
            lblTelefone.setText(result.getString(telefoneIndex));
            result.close();
            databaseConnector.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_consulta_clientes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.editItem:
                Intent addEditCliente = new Intent(this, AddNovoClienteActivity.class);

                addEditCliente.putExtra(MainActivity.LINHA_ID, idLinha);
                addEditCliente.putExtra("nome", lblNome.getText());
                addEditCliente.putExtra("cidade", lblCidade.getText());
                addEditCliente.putExtra("telefone", lblTelefone.getText());

                startActivity(addEditCliente);
                return true;
            case R.id.deleteItem:
                deleteCliente();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteCliente(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewClienteActivity.this);

        builder.setTitle(R.string.confirmaTitulo);
        builder.setMessage(R.string.confirmaMensagem);

        // provide an OK button that simply dismisses the dialog
        builder.setPositiveButton(R.string.botao_delete,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int button){
                        final DBAdapter conexaoDB = new DBAdapter(ViewClienteActivity.this);


                        AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>(){
                            @Override
                            protected Object doInBackground(Long... params){
                                try{
                                    conexaoDB.open();
                                    conexaoDB.excluiCliente(params[0]);
                                    conexaoDB.close();
                                }
                                catch(SQLException e){
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object result){
                                finish();
                            }
                        };


                        deleteTask.execute(new Long[] { idLinha });
                    }
                }); // finaliza o  método setPositiveButton

        builder.setNegativeButton(R.string.botao_cancel, null);
        builder.show();
    }
}
