    package com.example.simulado;

    import android.app.AlertDialog;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.CheckBox;
    import android.widget.EditText;
    import android.widget.ListView;
    import android.widget.RatingBar;
    import android.widget.Spinner;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import com.example.simulado.adapter.FilmeAdapter;
    import com.example.simulado.database.FilmeDatabase;
    import com.example.simulado.model.Filme;

    public class MainActivity extends AppCompatActivity {

        // Views do formulário
        private EditText etTitulo, etDiretor, etAno;
        private RatingBar ratingBar;
        private Spinner spinnerGenero;
        private CheckBox checkCinema;
        private Button btnSalvar;

        // Views de filtro/lista
        private Spinner spinnerFiltro, spinnerOrdem;
        private CheckBox checkFiltroCinema;
        private ListView listView;

        // Dados
        private FilmeDatabase db;
        private FilmeAdapter adapter;
        private int idEditando = -1; // -1 = novo filme

        private final String[] GENEROS = {"Ação", "Drama", "Comédia",
                "Ficção Científica", "Terror", "Animação", "Documentário"};
        private final String[] GENEROS_FILTRO; // "Todos" + GENEROS
        private final String[] ORDENS = {"Título", "Nota", "Ano"};

        public MainActivity() {
            GENEROS_FILTRO = new String[GENEROS.length + 1];
            GENEROS_FILTRO[0] = "Todos";
            System.arraycopy(GENEROS, 0, GENEROS_FILTRO, 1, GENEROS.length);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            db = new FilmeDatabase(this);
            vincularViews();
            configurarSpinners();
            configurarBotaoSalvar();
            configurarLista();
            atualizarLista();
        }

        private void vincularViews() {
            etTitulo         = findViewById(R.id.etTitulo);
            etDiretor        = findViewById(R.id.etDiretor);
            etAno            = findViewById(R.id.etAno);
            ratingBar        = findViewById(R.id.ratingBar);
            spinnerGenero    = findViewById(R.id.spinnerGenero);
            checkCinema      = findViewById(R.id.checkCinema);
            btnSalvar        = findViewById(R.id.btnSalvar);
            spinnerFiltro    = findViewById(R.id.spinnerFiltro);
            spinnerOrdem     = findViewById(R.id.spinnerOrdem);
            checkFiltroCinema= findViewById(R.id.checkFiltroCinema);
            listView         = findViewById(R.id.listViewFilmes);
        }

        private void configurarSpinners() {
            spinnerGenero.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, GENEROS));

            spinnerFiltro.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, GENEROS_FILTRO));

            spinnerOrdem.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, ORDENS));

            // Recarrega lista ao mudar filtro ou ordem
            AdapterView.OnItemSelectedListener reload = new AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(AdapterView<?> p, View v, int i, long l) { atualizarLista(); }
                @Override public void onNothingSelected(AdapterView<?> p) {}
            };
            spinnerFiltro.setOnItemSelectedListener(reload);
            spinnerOrdem.setOnItemSelectedListener(reload);
            checkFiltroCinema.setOnCheckedChangeListener((b, c) -> atualizarLista());
        }

        private void configurarBotaoSalvar() {
            btnSalvar.setOnClickListener(v -> {
                String titulo = etTitulo.getText().toString().trim();
                if (titulo.isEmpty()) {
                    Toast.makeText(this, "Título obrigatório", Toast.LENGTH_SHORT).show();
                    return;
                }

                String diretor = etDiretor.getText().toString().trim();
                String anoStr  = etAno.getText().toString().trim();
                int ano        = anoStr.isEmpty() ? 0 : Integer.parseInt(anoStr);
                float nota     = ratingBar.getRating();
                String genero  = spinnerGenero.getSelectedItem().toString();
                boolean cinema = checkCinema.isChecked();

                if (idEditando == -1) {
                    // NOVO
                    db.inserir(new Filme(titulo, diretor, ano, nota, genero, cinema));
                    Toast.makeText(this, "Filme salvo!", Toast.LENGTH_SHORT).show();
                } else {
                    // EDIÇÃO
                    db.atualizar(new Filme(idEditando, titulo, diretor, ano, nota, genero, cinema));
                    Toast.makeText(this, "Filme atualizado!", Toast.LENGTH_SHORT).show();
                    idEditando = -1;
                    btnSalvar.setText("Salvar");
                }

                limparFormulario();
                atualizarLista();
            });
        }

        private void configurarLista() {
            adapter = new FilmeAdapter(this, db.listar(null, false, null));
            listView.setAdapter(adapter);

            // Clique curto — carrega para edição
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Filme f = adapter.getItem(position);
                idEditando = f.getId();

                etTitulo.setText(f.getTitulo());
                etDiretor.setText(f.getDiretor());
                etAno.setText(String.valueOf(f.getAno()));
                ratingBar.setRating(f.getNota());
                checkCinema.setChecked(f.isViuNoCinema());
                btnSalvar.setText("Atualizar");

                // Seleciona o gênero correto no spinner
                for (int i = 0; i < GENEROS.length; i++) {
                    if (GENEROS[i].equals(f.getGenero())) {
                        spinnerGenero.setSelection(i);
                        break;
                    }
                }
            });

            // Clique longo — confirma exclusão
            listView.setOnItemLongClickListener((parent, view, position, id) -> {
                Filme f = adapter.getItem(position);
                new AlertDialog.Builder(this)
                        .setTitle("Excluir")
                        .setMessage("Deseja excluir \"" + f.getTitulo() + "\"?")
                        .setPositiveButton("Excluir", (d, w) -> {
                            db.deletar(f.getId());
                            atualizarLista();
                            Toast.makeText(this, "Excluído", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true; // consome o evento
            });
        }

        private void atualizarLista() {
            String generoFiltro = spinnerFiltro.getSelectedItem().toString();
            boolean cinema      = checkFiltroCinema.isChecked();
            String ordemStr     = spinnerOrdem.getSelectedItem().toString();

            String ordem = null;
            if ("Nota".equals(ordemStr)) ordem = "nota";
            if ("Ano".equals(ordemStr))  ordem = "ano";

            adapter.atualizar(db.listar(generoFiltro, cinema, ordem));
        }

        private void limparFormulario() {
            etTitulo.setText("");
            etDiretor.setText("");
            etAno.setText("");
            ratingBar.setRating(3);
            spinnerGenero.setSelection(0);
            checkCinema.setChecked(false);
        }
    }