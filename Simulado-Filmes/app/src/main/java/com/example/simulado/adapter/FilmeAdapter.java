package com.example.simulado.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;
import com.example.simulado.R;
import com.example.simulado.model.Filme;
import java.util.List;

public class FilmeAdapter extends BaseAdapter {

    private final Context context;
    private List<Filme> filmes;

    public FilmeAdapter(Context context, List<Filme> filmes) {
        this.context = context;
        this.filmes  = filmes;
    }

    public void atualizar(List<Filme> novos) {
        this.filmes = novos;
        notifyDataSetChanged();
    }

    @Override public int getCount()             { return filmes.size(); }
    @Override public Filme getItem(int pos)     { return filmes.get(pos); }
    @Override public long getItemId(int pos)    { return filmes.get(pos).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_filme, parent, false);
        }

        Filme f = filmes.get(position);

        TextView tvTitulo = convertView.findViewById(R.id.tvTitulo);
        TextView tvInfo   = convertView.findViewById(R.id.tvInfo);
        TextView tvNota   = convertView.findViewById(R.id.tvNota);

        tvTitulo.setText(f.getTitulo());
        tvInfo.setText(f.getGenero() + " • " + f.getAno()
                + (f.isViuNoCinema() ? " 🎬" : ""));


        StringBuilder estrelas = new StringBuilder();
        int n = Math.round(f.getNota());
        for (int i = 0; i < 5; i++) estrelas.append(i < n ? "★" : "☆");
        tvNota.setText(estrelas.toString());

        return convertView;
    }
}