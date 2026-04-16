package com.example.ac1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "despesas.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "Despesas";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DESCRICAO = "descricao";
    private static final String COLUMN_CATEGORIA = "categoria";
    private static final String COLUMN_VALOR = "valor";
    private static final String COLUMN_DATA = "data";
    private static final String COLUMN_FORMADEPAGAMENTO = "formaPagamento";
    private static final String COLUMN_CHECKPAGAMENTO = "checkPagamento";

    public BancoHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID     + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DESCRICAO + " TEXT, "
                + COLUMN_CATEGORIA + " TEXT, "
                + COLUMN_VALOR    + " TEXT, "
                + COLUMN_DATA  + " TEXT, "
                + COLUMN_FORMADEPAGAMENTO + " TEXT, "
                + COLUMN_CHECKPAGAMENTO + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // CRUD - Inserção
    public long inserirDespesas(String descricao, String categoria, String valor,
                             String data, String formaPagamento, int checkPagamento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRICAO,  descricao);
        values.put(COLUMN_CATEGORIA, categoria);
        values.put(COLUMN_VALOR,     valor);
        values.put(COLUMN_DATA,    data);
        values.put(COLUMN_FORMADEPAGAMENTO,  formaPagamento);
        values.put(COLUMN_CHECKPAGAMENTO,  checkPagamento);
        return db.insert(TABLE_NAME, null, values);
    }

    // CRUD - Consulta (todos)
    public Cursor listarDespesas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // CRUD - Consulta filtrada por gênero
    public Cursor listarDespesasCategoria(String categoria) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CATEGORIA + "=?",
                new String[]{categoria});
    }

    // CRUD - Atualização
    public int atualizarDespesas(int id, String descricao, String categoria, String valor,
                                 String data, String formaPagamento, int checkPagamento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRICAO,  descricao);
        values.put(COLUMN_CATEGORIA, categoria);
        values.put(COLUMN_VALOR,     valor);
        values.put(COLUMN_DATA,    data);
        values.put(COLUMN_FORMADEPAGAMENTO,  formaPagamento);
        values.put(COLUMN_CHECKPAGAMENTO,  checkPagamento);
        return db.update(TABLE_NAME, values,
                COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // CRUD - Exclusão
    public int excluirDespesas(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

}
