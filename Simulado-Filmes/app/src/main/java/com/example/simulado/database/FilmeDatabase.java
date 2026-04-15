package com.example.simulado.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.simulado.model.Filme;
import java.util.ArrayList;
import java.util.List;

public class FilmeDatabase extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "filmes.db";
    private static final int VERSAO = 1;

    // Nomes da tabela e colunas
    public static final String TABELA = "filmes";
    public static final String COL_ID      = "id";
    public static final String COL_TITULO  = "titulo";
    public static final String COL_DIRETOR = "diretor";
    public static final String COL_ANO     = "ano";
    public static final String COL_NOTA    = "nota";
    public static final String COL_GENERO  = "genero";
    public static final String COL_CINEMA  = "viu_cinema";

    public FilmeDatabase(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + COL_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITULO  + " TEXT NOT NULL, "
                + COL_DIRETOR + " TEXT, "
                + COL_ANO     + " INTEGER, "
                + COL_NOTA    + " REAL, "
                + COL_GENERO  + " TEXT, "
                + COL_CINEMA  + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    // ── CREATE ──────────────────────────────────────────────
    public long inserir(Filme f) {
        try {
            ContentValues cv = toContentValues(f);
            return getWritableDatabase().insert(TABELA, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // ── UPDATE ──────────────────────────────────────────────
    public int atualizar(Filme f) {
        ContentValues cv = toContentValues(f);
        return getWritableDatabase().update(
                TABELA, cv, COL_ID + "=?",
                new String[]{String.valueOf(f.getId())});
    }

    // ── DELETE ──────────────────────────────────────────────
    public void deletar(int id) {
        getWritableDatabase().delete(
                TABELA, COL_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    // ── READ — todos, com filtros opcionais ─────────────────
    public List<Filme> listar(String filtroGenero,
                              boolean apenasNoCinema,
                              String ordenacao) {
        List<Filme> lista = new ArrayList<>();

        StringBuilder where = new StringBuilder("1=1");
        List<String> args = new ArrayList<>();

        if (filtroGenero != null && !filtroGenero.equals("Todos")) {
            where.append(" AND ").append(COL_GENERO).append("=?");
            args.add(filtroGenero);
        }
        if (apenasNoCinema) {
            where.append(" AND ").append(COL_CINEMA).append("=1");
        }

        String order = COL_TITULO; // padrão
        if ("nota".equals(ordenacao))  order = COL_NOTA  + " DESC";
        if ("ano".equals(ordenacao))   order = COL_ANO   + " DESC";

        Cursor c = getReadableDatabase().query(
                TABELA, null,
                where.toString(),
                args.toArray(new String[0]),
                null, null, order);

        while (c.moveToNext()) {
            lista.add(cursorToFilme(c));
        }
        c.close();
        return lista;
    }

    // ── Helpers ─────────────────────────────────────────────
    private ContentValues toContentValues(Filme f) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TITULO,  f.getTitulo());
        cv.put(COL_DIRETOR, f.getDiretor());
        cv.put(COL_ANO,     f.getAno());
        cv.put(COL_NOTA,    f.getNota());
        cv.put(COL_GENERO,  f.getGenero());
        cv.put(COL_CINEMA,  f.isViuNoCinema() ? 1 : 0);
        return cv;
    }

    private Filme cursorToFilme(Cursor c) {
        return new Filme(
                c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_TITULO)),
                c.getString(c.getColumnIndexOrThrow(COL_DIRETOR)),
                c.getInt(c.getColumnIndexOrThrow(COL_ANO)),
                c.getFloat(c.getColumnIndexOrThrow(COL_NOTA)),
                c.getString(c.getColumnIndexOrThrow(COL_GENERO)),
                c.getInt(c.getColumnIndexOrThrow(COL_CINEMA)) == 1
        );
    }
}
