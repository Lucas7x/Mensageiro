package com.example.mensageiro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mensageiro.R;
import com.example.mensageiro.config.ConfiguracaoFirebase;
import com.example.mensageiro.helper.Base64Custom;
import com.example.mensageiro.helper.UsuarioFirebase;
import com.example.mensageiro.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText editNome, editEmail, editSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editNome = findViewById(R.id.editNomeCadastro);
        editEmail = findViewById(R.id.editEmailCadastro);
        editSenha = findViewById(R.id.editSenhaCadastro);
    }

    public void cadastrarUsuario(Usuario u) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                u.getEmail(),
                u.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    try {
                        String idUsuario = Base64Custom.codificar(u.getEmail());

                        u.setId(idUsuario);
                        u.salvar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            "Usuário cadastrado com sucesso.",
                            Toast.LENGTH_SHORT
                    ).show();

                    UsuarioFirebase.atualizarNomeUsuario(u.getNome());

                    finish();

                }else {
                    String excessao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excessao = "Digite uma senha mais forte.";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excessao = "Digite um e-mail válido.";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excessao = "Esta conta já foi cadastrada.";
                    }catch (Exception e) {
                        excessao = "Erro ao cadastrar usuário: " + e.getMessage();
                    }

                    Toast.makeText(CadastroActivity.this,
                            excessao,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    } //fim do método cadastrarUsuario

    public void validarCadastroUsuario(View view) {
        //recuperar textos dos campos
        String nome = editNome.getText().toString();
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        if (!nome.isEmpty() && !email.isEmpty() && !senha.isEmpty()) {
            Usuario u = new Usuario();
            u.setNome(nome);
            u.setEmail(email);
            u.setSenha(senha);
            cadastrarUsuario(u);

        } else {
            Toast.makeText(CadastroActivity.this,
                    "Preencha todos os campos!",
                    Toast.LENGTH_SHORT
            ).show();
        }

    } //fim validar cadastro usuario

}