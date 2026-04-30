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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText editText = findViewById(R.id.editCEP);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EditText editText = findViewById(R.id.editCEP);
                                URL url = new URL("http://viacep.com.br/ws/" + editText.getText() + "/json");
                                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();


                                if (conexao.getResponseCode() != 200)
                                    throw new RuntimeException("HTTP error code : " + conexao.getResponseCode());

                                Log.d("JSON", "JSON - antes do buffer");

                                BufferedReader resposta = new BufferedReader(new InputStreamReader((conexao.getInputStream())));
                                String aux, jsonEmString = "";
                                while ((aux = resposta.readLine()) != null) {
                                    jsonEmString += aux;
                                }

                                String finalJsonEmString = jsonEmString;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView textView = findViewById(R.id.txvEndereco);
                                        textView.setText(finalJsonEmString);

                                        Log.d("JSON", "JSON - antes do GSON");

                                        Gson gson = new Gson();
                                        Endereco endereco = gson.fromJson(finalJsonEmString, Endereco.class);

                                        EditText editTextText4 = findViewById(R.id.editTextText4);
                                        editTextText4.setText(endereco.getUf());

                                        Log.d("JSON", "JSON - final");
                                    }
                                });
                            } catch (Exception e) {
                                Log.d("JSON", "JSON - erro: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
    });
}

}