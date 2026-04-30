package com.example.myapplication29;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

public class CRUD_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void GetPost(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EditText edtID = findViewById(R.id.edtID);
                    URL url = new URL("https://jsonplaceholder.typicode.com/posts/" + edtID.getText());

                    HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                    conexao.setRequestMethod("GET");

                    int responseCode = conexao.getResponseCode();
                    if (responseCode == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        String resultado = response.toString();

                        Gson gson = new Gson();
                        Type tipoPost = new TypeToken<Post>() {
                        }.getType();
                        Post post = gson.fromJson(resultado, tipoPost);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView5 = findViewById(R.id.textView5);
                                TextView textView6 = findViewById(R.id.textView6);
                                textView5.setText(post.getTitle());
                                textView6.setText(post.getBody());
                                Log.d("JSON", resultado);
                            }
                        });

                    } else {
                        Log.e("API", "Erro de conexão: " + responseCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void PostPost(View view) {
        new Thread(() -> {

            Log.d("POST", "1");
            try {
                EditText edtTitulo = findViewById(R.id.edtTitulo);
                EditText edtCorpo = findViewById(R.id.edtTexto);

                Post novoPost = new Post();
                novoPost.setUserId(1);
                novoPost.setTitle(edtTitulo.getText().toString());
                novoPost.setBody(edtCorpo.getText().toString());

                Log.d("POST", "2");

                Gson gson = new Gson();
                String jsonPost = gson.toJson(novoPost);

                URL url = new URL("https://jsonplaceholder.typicode.com/posts");
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("POST");
                conexao.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conexao.setDoOutput(true);

                Log.d("POST", "3");

                OutputStream os = conexao.getOutputStream();
                os.write(jsonPost.getBytes("UTF-8"));
                os.close();

                Log.d("POST", "4");

                int responseCode = conexao.getResponseCode();

                Log.d("POST", "responedCode:" + responseCode);

                TextView txtResultado = findViewById(R.id.textView8);

                if (responseCode == 201) {
                    txtResultado.setText("Sucesso ao gravar o POST!");
                } else {
                    txtResultado.setText("Erro ao gravar o POST!");
                }
            } catch (Exception e) {
                Log.d("Erro", e.getMessage());
                TextView txtResultado = findViewById(R.id.textView8);
                txtResultado.setText("Erro: " + e.getMessage());
            }

        }).start();
    }
}
