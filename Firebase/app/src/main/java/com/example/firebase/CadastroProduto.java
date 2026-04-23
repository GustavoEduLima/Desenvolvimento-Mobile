package com.example.firebase;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class CadastroProduto extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db;
    private EditText edtNome, edtEstoque;
    private RecyclerView recyclerProdutos;
    private List<Produto> listaProdutos = new ArrayList<>();
    private ProdutoAdapter adapter;

    private Produto produtoEditando = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_produto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        edtNome = findViewById(R.id.edtNome);
        edtEstoque = findViewById(R.id.edtEstoque);
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProdutoAdapter(listaProdutos);
        recyclerProdutos.setAdapter(adapter);

        findViewById(R.id.btnSalvar).setOnClickListener(v -> salvarProduto());

        carregarProdutos();
    }
    private void salvarProduto() {
        String nome = edtNome.getText().toString();
        int estoque = Integer.parseInt(edtEstoque.getText().toString());

        if (produtoEditando == null) {
            // Criar novo
            Produto produto = new Produto(null, nome, estoque);
            db.collection("produtos")
                    .add(produto)
                    .addOnSuccessListener(doc -> {
                        produto.setId(doc.getId());
                        Toast.makeText(this, "Produto salvo!", Toast.LENGTH_SHORT).show();
                        limparCampos();
                        carregarProdutos();
                    });
        } else {
            // Atualizar existente
            produtoEditando.setNome(nome);
            produtoEditando.setEstoque(estoque);

            db.collection("produtos").document(produtoEditando.getId())
                    .set(produtoEditando)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Produto atualizado!", Toast.LENGTH_SHORT).show();
                        limparCampos();
                        carregarProdutos();
                    });
        }
    }
    private void limparCampos() {
        edtNome.setText("");
        edtEstoque.setText("");
        produtoEditando = null;
        ((Button) findViewById(R.id.btnSalvar)).setText("Salvar Produto");
    }

    private void carregarProdutos() {
        db.collection("produtos")
                .get()
                .addOnSuccessListener(query -> {
                    listaProdutos.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Produto p = doc.toObject(Produto.class);
                        p.setId(doc.getId());
                        listaProdutos.add(p);
                    }
                    adapter.notifyDataSetChanged();
                });

        adapter.setOnItemClickListener(produto -> {
            edtNome.setText(produto.getNome());
            edtEstoque.setText(String.valueOf(produto.getEstoque()));
            produtoEditando = produto;
            ((Button) findViewById(R.id.btnSalvar)).setText("Atualizar Produto");
        });

    }

    public void deletarProduto(String id) {
        db.collection("produtos").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> carregarProdutos());
    }

    public void atualizarProduto(Produto produto) {
        db.collection("produtos").document(produto.getId())
                .set(produto)
                .addOnSuccessListener(aVoid -> carregarProdutos());
    }
}