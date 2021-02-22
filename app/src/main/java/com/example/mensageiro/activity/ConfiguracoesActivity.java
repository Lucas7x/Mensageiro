package com.example.mensageiro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.mensageiro.R;

public class ConfiguracoesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        //configurando toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.configuracoes);
        setSupportActionBar(toolbar);
        //habilitando botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    } //fim do onCreate
}