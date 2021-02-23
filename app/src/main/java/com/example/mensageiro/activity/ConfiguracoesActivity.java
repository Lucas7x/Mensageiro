package com.example.mensageiro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mensageiro.R;
import com.example.mensageiro.helper.Permissao;

public class ConfiguracoesActivity extends AppCompatActivity {

    private String[] permissoesNecessarias = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton imageButtonCamera, imageButtonGaleria;
    private Button buttonTeste;

    private static final int SELECAO_CAMERA  = 100;
    private static final int SELECAO_GALERIA = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        //Validar permissões
        Permissao.validarPermissões(permissoesNecessarias, this, 1);

        //
        imageButtonCamera = findViewById(R.id.imageButtonCameraConfig);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleriaConfig);
        buttonTeste = findViewById(R.id.buttonTeste);

        //configurando toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.configuracoes);
        setSupportActionBar(toolbar);
        //habilitando botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(i.resolveActivity(getPackageManager()) == null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                } else {
                    Toast.makeText(
                            ConfiguracoesActivity.this,
                            "Não conseguiu abrir camera",
                            Toast.LENGTH_SHORT
                    ).show();
                }



            }
        });



        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        ConfiguracoesActivity.this,
                        "Clicou no botao",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });



        buttonTeste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    } //fim do onCreate

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults) {
            if(permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    } //fim do método onRequestPermissionsResult


    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o aplicativo é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    } //fim do método alertaValidacaoPermissao

    /*
    public void abrirGaleriaConfiguracoes(View view) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(i.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(i, SELECAO_CAMERA);
        }

    } //fim abrirGaleriaConfiguracoes

    public void abrirCameraConfiguracoes(View view) {

    } //fim abrirCameraConfiguracoes

     */
}