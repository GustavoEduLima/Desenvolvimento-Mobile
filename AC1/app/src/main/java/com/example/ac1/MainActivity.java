package com.example.ac1;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // --- Atributos da tela ---
    EditText edtTitulo, edtValor, edtData;

    Spinner spinnerFiltroCategoria, spinnerFormadepagamento;
    CheckBox checkPagamento;
    Button btnSalvar;
    ListView listViewDespesas;

    // --- Banco e listas ---
    BancoHelper databaseHelper;
    ArrayAdapter<String> adapter;

    ArrayList<String> listaDespesas;
    ArrayList<Integer> listaIds;

    // Filtro Categoria
    String[] Categoria = { "Alimentacao", "Transporte", "Lazer", "Saude", "Contas"};

    // Forma de pagamento
    String[] FormadePagamento = { "Dinheiro", "Pix", "Débito", "Crédito"};

    int idEditando = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // --- Referências dos componentes ---
            edtTitulo    = findViewById(R.id.edtTitulo);
            edtValor   = findViewById(R.id.edtValor);
            edtData     = findViewById(R.id.edtData);
            spinnerFiltroCategoria = findViewById(R.id.spinnerFiltroCategoria);
            spinnerFormadepagamento = findViewById(R.id.spinnerFormadepagamento);
            checkPagamento  = findViewById(R.id.checkPagamento);
            btnSalvar    = findViewById(R.id.btnSalvar);
            listViewDespesas = findViewById(R.id.listViewDespesas);

            databaseHelper = new BancoHelper(this);

            // --- Configurar Spinners ---
            // Spinner de Categorias
            String[] categoriaForm = {"Alimentacao", "Transporte", "Lazer", "Saude", "Contas"};
            ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, categoriaForm);
            adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFiltroCategoria.setAdapter(adapterCategoria);

            // Spinner de Forma de pagamento
            String[] FormadePagamentoForm = {"Dinheiro", "Pix", "Débito", "Crédito"};
            ArrayAdapter<String> adapterFormapagamento = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, FormadePagamentoForm);
            adapterFormapagamento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFormadepagamento.setAdapter(adapterFormapagamento);

            // Carregar lista inicial
            carregarDespesas("Todos");

            // --- Botão Salvar / Atualizar ---
            btnSalvar.setOnClickListener(v -> {
                String Descricao  = edtTitulo.getText().toString();
                String Categoria = spinnerFiltroCategoria.getSelectedItem().toString();
                String Valor = edtValor.getText().toString();
                String Data     = edtData.getText().toString();
                String FormaPagamento  = spinnerFormadepagamento.getSelectedItem().toString();
                int CheckPagamento     = checkPagamento.isChecked() ? 1 : 0;

                if (!Descricao.isEmpty() && !Valor.isEmpty() && !Data.isEmpty() && !Categoria.isEmpty() && !FormaPagamento.isEmpty()) {

                    if (idEditando == -1) {
                        // ---- INSERIR ----
                        long resultado = databaseHelper.inserirDespesas(
                                Descricao, Categoria, Data, Valor, FormaPagamento, CheckPagamento);
                        if (resultado != -1) {
                            Toast.makeText(this, "Despesa salva!", Toast.LENGTH_SHORT).show();
                            limparCampos();
                            carregarDespesas(spinnerFiltroCategoria.getSelectedItem().toString());
                        } else {
                            Toast.makeText(this, "Erro ao salvar!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // ---- ATUALIZAR ----
                        int resultado = databaseHelper.atualizarDespesas(
                                idEditando,  Descricao, Categoria, Data, Valor, FormaPagamento, CheckPagamento);
                        if (resultado > 0) {
                            Toast.makeText(this, "Despesa atualizada!", Toast.LENGTH_SHORT).show();
                            limparCampos();
                            carregarDespesas(spinnerFiltroCategoria.getSelectedItem().toString());
                        } else {
                            Toast.makeText(this, "Erro ao atualizar!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                }
            });

            // --- Clique curto: carregar para edição ---
            listViewDespesas.setOnItemClickListener((parent, view, position, id) -> {
                idEditando = listaIds.get(position);

                // Busca os dados da despesa selecionado pelo cursor
                Cursor cursor = databaseHelper.listarDespesas();
                if (cursor.moveToFirst()) {
                    do {
                        if (cursor.getInt(0) == idEditando) {
                            edtTitulo.setText(cursor.getString(1));
                            edtValor.setText(cursor.getString(2));
                            edtData.setText(cursor.getString(3));


                            // Ajustar spinner de gênero
                            String[] categoriaForm2 = {"Alimentacao", "Transporte", "Lazer", "Saude", "Contas"};
                            for (int i = 0; i < categoriaForm2.length; i++) {
                                if (categoriaForm2[i].equals(cursor.getString(5))) {
                                    spinnerFiltroCategoria.setSelection(i);
                                    break;
                                }
                            }
                            checkPagamento.setChecked(cursor.getInt(6) == 1);
                            break;
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();

                btnSalvar.setText("Atualizar");
            });

            // --- Clique longo: excluir ---
            listViewDespesas.setOnItemLongClickListener((adapterView, view1, pos, l) -> {
                int idDespesa = listaIds.get(pos);
                int deletado = databaseHelper.excluirDespesas(idDespesa);
                if (deletado > 0) {
                    Toast.makeText(this, "Despesa excluída!", Toast.LENGTH_SHORT).show();
                    limparCampos();
                    carregarDespesas(spinnerFiltroCategoria.getSelectedItem().toString());
                }
                return true;
            });

            // --- Filtro por categoria ---
            spinnerFiltroCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, android.view.View view,
                                           int position, long id) {
                    carregarDespesas(Categoria[position]);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // --- Método: carregar Despesas na lista ---
    private void carregarDespesas(String filtroCategoria) {
        Cursor cursor;
        if (filtroCategoria.equals("Todos")) {
            cursor = databaseHelper.listarDespesas();
        } else {
            cursor = databaseHelper.listarDespesasCategoria(filtroCategoria);
        }

        listaDespesas = new ArrayList<>();
        listaIds    = new ArrayList<>();



        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listaDespesas);
        listViewDespesas.setAdapter(adapter);
    }

    //  Metodo: limpar campos
    private void limparCampos() {
        edtTitulo.setText("");
        edtValor.setText("");
        edtData.setText("");
        spinnerFormadepagamento.setSelection(0);
        spinnerFiltroCategoria.setSelection(0);
        checkPagamento.setChecked(false);
        btnSalvar.setText("Salvar");
        idEditando = -1;
    }
}