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
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView clientesListView;
    public static final String LINHA_ID = "idLinha";
    private CursorAdapter clientesAdapter; // Adaptador para a ListView
    private Button btnBuscar;
    private String varAux;
    private EditText txtFiltro;
    private long idLinha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        clientesListView = (ListView) findViewById(R.id.listView);
        clientesListView.setOnItemClickListener(viewClientesListener);

        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(buscarClienteButtonClicked);

        // mapeia cada coluna da tabela com um componente da tela
        String[] origem = new String[]{"nome","cidade","vendas","telefone"};
        int[] destino = new int[] { R.id.txtNome, R.id.txtCidade, R.id.txtVendas,R.id.txtTelefone};
        int flags = 0;

        clientesAdapter = new SimpleCursorAdapter(MainActivity.this,R.layout.activity_view_clientes,null,origem,destino,flags);
        clientesListView.setAdapter(clientesAdapter);

    }

    View.OnClickListener buscarClienteButtonClicked = new View.OnClickListener(){
        public void onClick(View v){
            hideSoftKeyboard();
            onResume();
        }
    };
    @Override
    protected void onResume(){
        //sempre que executar onResume, irá fazer uma busca no banco de dados
        //e vai atualizar a tela de exibição dos livros cadastrados
        super.onResume();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        txtFiltro = (EditText) findViewById(R.id.filtro);
        varAux = txtFiltro.getText().toString();

        switch (spinner.getSelectedItem().toString()){
            case "Cidade":
                new getClientesByCity().execute();
                break;
            case "Nome":
                new getClientesByName().execute();
                break;
            case "Todos":
                new ObtemClientes().execute();
                break;
        }
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
    private class getClientesByName extends AsyncTask<Object, Object, Cursor> {
        DBAdapter conexaoDB = new DBAdapter(MainActivity.this);
        @Override
        protected Cursor doInBackground(Object... params){
            conexaoDB.open(); //abre a base de dados
            return conexaoDB.getTodosClientesByName(varAux); //retorna todos os livros
        }
        // usa o cursor retornado pelo doInBackground
        @Override
        protected void onPostExecute(Cursor result){
            clientesAdapter.changeCursor(result); //altera o cursor para um novo cursor
            conexaoDB.close();
        }
    }

    private class getClientesByCity extends AsyncTask<Object, Object, Cursor> {
        DBAdapter conexaoDB = new DBAdapter(MainActivity.this);
        @Override
        protected Cursor doInBackground(Object... params){
            conexaoDB.open(); //abre a base de dados
            return conexaoDB.getTodosClientesByCity(varAux); //retorna todos os livros
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
            Intent viewCliente = new Intent(getApplicationContext(), ViewClienteActivity.class);
            viewCliente.putExtra(LINHA_ID, id);
            startActivity(viewCliente);
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
        switch (item.getItemId()) {
            case R.id.addLivroItem:
                Intent addNovoCliente = new Intent(getApplicationContext(), AddNovoClienteActivity.class);
                startActivity(addNovoCliente);
            break;
            case R.id.removeCliente:
                deleteTodosCliente();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void deleteTodosCliente(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(R.string.confirmaTitulo);
        builder.setMessage(R.string.confirmaMensagemSelecao);

        // provide an OK button that simply dismisses the dialog
        builder.setPositiveButton(R.string.botao_delete,
            new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int button){
                    final DBAdapter conexaoDB = new DBAdapter(MainActivity.this);

                    ListView viewTemp = (ListView) findViewById(R.id.listView);
                    ArrayList<Integer> removeItens = new ArrayList<>();

                    for (int i=0;i < viewTemp.getChildCount();i++){
                        removeItens.add(viewTemp.getAdapter().getView(i, null, viewTemp).getId());
                        Log.d("item",removeItens.get(i).toString());
                    }

                    try{
                        conexaoDB.open();
                        Log.d("param","param");
                        conexaoDB.excluiTodosCliente(removeItens);
                        conexaoDB.close();
                    }
                    catch(SQLException e){
                        e.printStackTrace();
                    }
                }
            }); // finaliza o  método setPositiveButton

        builder.setNegativeButton(R.string.botao_cancel, null);
        builder.show();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
