package com.example.mensageiro.model;

import com.example.mensageiro.config.ConfiguracaoFirebase;
import com.example.mensageiro.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String foto;

    public Usuario() {}

    public void salvar() {
        DatabaseReference db = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = db.child("usuarios").child(getId());

        usuario.setValue(this);
    }

    public void atualizar() {
        String idUsuario = UsuarioFirebase.getIdUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuariosRef = database.child("usuarios")
                .child(idUsuario);

        Map<String, Object> dadosUsuario = converterParaMap();

        usuariosRef.updateChildren(dadosUsuario);

    }

    @Exclude
    public Map<String, Object> converterParaMap() {
        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("foto", getFoto());

        return usuarioMap;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
