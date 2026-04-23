package com.example.firebase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
    }

    public void cadastrarUsuario(View v){
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtSenha = findViewById(R.id.edtSenha);

        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtSenha.getText().toString()).addOnCompleteListener(task->{
            if (task.isSuccessful()){
                Toast.makeText(this, "Usuario criado com sucesso", Toast.LENGTH_SHORT).show();
                Log.d("FIREBASE", "Usuario criado com sucesso");
            }else{
                Toast.makeText(this, "Erro ao cadastrar o usuário", Toast.LENGTH_SHORT).show();
                Log.e("FIREBASE","Erro ao Criar Usuário", task.getException());
            }
        });
    }

    public void logarUsuario(View v){
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtSenha = findViewById(R.id.edtSenha);

        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtSenha.getText().toString()).addOnCompleteListener(task->{
            if (task.isSuccessful()){
                Toast.makeText(this, "Logado com sucesso", Toast.LENGTH_SHORT).show();
                Log.d("FIREBASE", "Login bem-sucedido");
            }else{
                Toast.makeText(this, "Erro no Login", Toast.LENGTH_SHORT).show();
                Log.e("FIREBASE","Erro no login", task.getException());
            }
        });
    }
}