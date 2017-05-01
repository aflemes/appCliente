package com.aula_android.appCliente;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    private ListView clientesListView;
    public static final String LINHA_ID = "idLinha";
    private CursorAdapter clientesAdapter; // Adaptador para a ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        clientesListView = (ListView) findViewById(R.id.listView);
        clientesListView.setOnItemClickListener(viewClientesListener);

        // mapeia cada coluna da tabela com um componente da tela
        String[] origem = new String[]{"nome","cidade","telefone"};
        int[] destino = new int[] { R.id.txtNome, R.id.txtCidade, R.id.txtTelefone};
        int flags = 0;

        clientesAdapter = new SimpleCursorAdapter(MainActivity.this,R.layout.activity_view_clientes,null,origem,destino,flags);
        clientesListView.setAdapter(clientesAdapter);

     /*   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        //sempre que executar onResume, irá fazer uma busca no banco de dados
        //e vai atualizar a tela de exibição dos livros cadastrados
        super.onResume();
        new ObtemClientes().execute();
    }
    ////////////////////////////////////////////////////////////
    // Quando precisamos dos resultados de uma operação do BD na thread da
    // interface gráfica, vamos usar AsyncTask para efetuar a operação em
    // uma thread e receber os resultados na thread da interface gráfica
    private class ObtemClientes extends AsyncTask<Object, Object, Cursor> {
        DBAdapter conexaoDB = new DBAdapter(MainActivity.this);
        @Override
        protected Cursor doInBackground(Object... params){
            conexaoDB.open(); //abre a base de dados
            return conexaoDB.getTodosClientes(); //retorna todos os livros
        }
        // usa o cursor retornado pelo doInBackground
        @Override
        protected void onPostExecute(Cursor result){
            clientesAdapter.changeCursor(result); //altera o cursor para um novo cursor
            conexaoDB.close();
        }
    }
///////////////////////////////////////////////////////////
    //Quando o usuário clica em uma linha da consulta, uma nova intenção
    //é executada, para exibir os dados do livro selecionado
    AdapterView.OnItemClickListener viewClientesListener = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent, View view, int posicao,long id){
            Intent viewLivros = new Intent(getApplicationContext(), ViewClienteActivity.class);
            viewLivros.putExtra(LINHA_ID, id);
            startActivity(viewLivros);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //Cria uma intenção para executar o cadastramento de um novo livro
        Intent addNovoCliente = new Intent(getApplicationContext(), AddNovoClienteActivity.class);
        startActivity(addNovoCliente);
        return super.onOptionsItemSelected(item);
    }
}
