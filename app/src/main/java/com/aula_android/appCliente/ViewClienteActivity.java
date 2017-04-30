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
    private TextView lblTitulo;
    private TextView lblEditora;
    private TextView lblISBN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_clientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lblTitulo = (TextView) findViewById(R.id.lblTitulo);
        lblEditora = (TextView) findViewById(R.id.lblEditora);
        lblISBN = (TextView) findViewById(R.id.lblISBN);

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
            return databaseConnector.getLivro(params[0]);
        }
        // Usa o Cursor retornado do método doInBackground
        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            result.moveToFirst();

            int tituloIndex = result.getColumnIndex("titulo");
            int editoraIndex = result.getColumnIndex("editora");
            int isbnIndex = result.getColumnIndex("isbn");

            lblTitulo.setText(result.getString(tituloIndex));
            lblEditora.setText(result.getString(editoraIndex));
            lblISBN.setText(result.getString(isbnIndex));
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
                Intent addEditLivro = new Intent(this, AddNovoClienteActivity.class);

                addEditLivro.putExtra(MainActivity.LINHA_ID, idLinha);
                addEditLivro.putExtra("titulo", lblTitulo.getText());
                addEditLivro.putExtra("editora", lblEditora.getText());
                addEditLivro.putExtra("isbn", lblISBN.getText());

                startActivity(addEditLivro);
                return true;
            case R.id.deleteItem:
                deleteLivro();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteLivro(){

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
                                    conexaoDB.excluiTitulo(params[0]);
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
