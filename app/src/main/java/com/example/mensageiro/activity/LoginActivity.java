package com.example.mensageiro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mensageiro.R;
import com.example.mensageiro.config.ConfiguracaoFirebase;
import com.example.mensageiro.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    
    private TextInputEditText editEmail, editSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        editEmail =  findViewById(R.id.editEmailLogin);
        editSenha = findViewById(R.id.editSenhaLogin);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null) {
            abrirTelaPrincipal();
        }
    }

    public void logarUsuario(Usuario u) {
        autenticacao.signInWithEmailAndPassword(
                u.getEmail(),
                u.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    abrirTelaPrincipal();
                } else {
                    String excessao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        excessao = "Usuário não está cadastrado.";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excessao = "E-mail e senha não correspondem a um usuário cadastrado.";
                    } catch (Exception e) {
                        excessao = "Erro ao autenticar usuário: " + e.getMessage();
                    }

                    Toast.makeText(LoginActivity.this,
                            excessao,
                            Toast.LENGTH_SHORT
                    ).show();
                }

            }
        });
    }

    public void validarLoginUsuario(View view) {
        //recuperar textos dos campos
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        if (!email.isEmpty() && !senha.isEmpty()) {

            Usuario u = new Usuario();
            u.setEmail(email);
            u.setSenha(senha);

            logarUsuario(u);

        } else {
            Toast.makeText(LoginActivity.this,
                    "Preencha todos os campos!",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    public void abrirTelaCadastro(View view) {
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    public void abrirTelaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}