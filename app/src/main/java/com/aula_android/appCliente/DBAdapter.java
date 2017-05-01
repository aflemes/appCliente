package com.aula_android.appCliente;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NOME = "nome";
    public static final String KEY_CIDADE = "cidade";
    public static final String KEY_TELEFONE = "telefone";
    public static final String KEY_VENDAS = "vendas";
    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "dbcliente";
    private static final String DATABASE_TABLE = "clientes";
    private static final int DATABASE_VERSION = 1;

    private static final String CRIA_DATABASE = "create table clientes " +
            "(_id integer primary key autoincrement, " +
            " nome text not null," +
            " cidade text not null," +
            " telefone text not null," +
            " vendas text not null);" ;
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context); //classe interna que herda de SQLiteOpenHelper
    }

    //classe interna que manipula o banco
    //SQLiteOpenHelper é uma classe abstrata.
    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            try{
                db.execSQL(CRIA_DATABASE);
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){
            Log.w(TAG, "Atualizando a base de dados a partir da versao " + oldVersion
                    + " para " + newVersion + ",isso irá destruir todos os dados antigos");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    // *******************************************************************************
    //--- abre a base de dados ---
    public DBAdapter open() throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //--- fecha a base de dados ---
    public void close(){
        DBHelper.close();
    }

    //---insere um Livro na base da dados ---
    public long insereCliente(String nome, String cidade, String telefone, String vendas){
        ContentValues dados = new ContentValues();
        dados.put(KEY_NOME, nome);
        dados.put(KEY_CIDADE, cidade);
        dados.put(KEY_TELEFONE, telefone);
        dados.put(KEY_VENDAS, vendas);
        return db.insert(DATABASE_TABLE, null, dados);
    }

    //--- exclui um livro---
    public boolean excluiCliente(long idLinha){
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + idLinha, null) > 0;
    }

    //--- devolve todos os livros---
    public Cursor getTodosClientes(){
        String colunas[] ={KEY_ROWID,KEY_NOME,KEY_CIDADE,KEY_TELEFONE,KEY_VENDAS};
        return db.query(DATABASE_TABLE,colunas, null, null, null, null, null);
    }

    //--- recupera uma linha (livro) ---
    public Cursor getCliente(long idLinha) throws SQLException{

        String colunas[] ={KEY_ROWID,KEY_NOME,KEY_CIDADE,KEY_TELEFONE,KEY_VENDAS};
        String linhaAcessada = KEY_ROWID + "=" + idLinha;
        Cursor mCursor = db.query(DATABASE_TABLE, colunas,linhaAcessada,null,null,null,null,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //--- atualiza um titulo---
    public boolean alteraCliente(long idLinha, String nome, String cidade,String telefone, String vendas){
        ContentValues dados = new ContentValues();
        String linhaAcessada = KEY_ROWID + "=" + idLinha;

        dados.put(KEY_NOME, nome);
        dados.put(KEY_CIDADE, cidade);
        dados.put(KEY_TELEFONE, telefone);
        dados.put(KEY_VENDAS, vendas);

        return db.update(DATABASE_TABLE, dados, linhaAcessada, null)>0;
    }
}