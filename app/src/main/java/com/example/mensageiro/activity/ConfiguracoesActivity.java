package com.example.mensageiro.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.GenericLifecycleObserver;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mensageiro.R;
import com.example.mensageiro.config.ConfiguracaoFirebase;
import com.example.mensageiro.helper.Base64Custom;
import com.example.mensageiro.helper.Permissao;
import com.example.mensageiro.helper.UsuarioFirebase;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private String[] permissoesNecessarias = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private StorageReference storageReference;

    private ImageButton imageButtonCamera, imageButtonGaleria;
    private CircleImageView circleImageViewPerfil;
    private EditText editNome;


    private static final int SELECAO_CAMERA  = 100;
    private static final int SELECAO_GALERIA = 101;

    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        //configurações iniciais
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        idUsuario = UsuarioFirebase.getIdUsuario();


        //Validar permissões
        Permissao.validarPermissões(permissoesNecessarias, this, 1);

        //
        imageButtonCamera = findViewById(R.id.imageButtonCameraConfig);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleriaConfig);
        circleImageViewPerfil = findViewById(R.id.circleImageViewFotoPerfil);
        editNome = findViewById(R.id.editTextNomeConfig);


        //configurando toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.configuracoes);
        setSupportActionBar(toolbar);
        //habilitando botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //recuperar usuario
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        if (url != null) {
            Glide.with(ConfiguracoesActivity.this)
                    .load(url)
                    .into(circleImageViewPerfil);
        } else {
            circleImageViewPerfil.setImageResource(R.drawable.padrao);
        }

        editNome.setText(usuario.getDisplayName());

        /*
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
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                } else {
                    Toast.makeText(
                            ConfiguracoesActivity.this,
                            "Não conseguiu abrir camera",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

         */

    } //fim do onCreate

    //@RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //para recuperar as imagens da camera ou da galeria
        if(resultCode == RESULT_OK) {
            Bitmap img = null;

            try {
                //recupera imagem
                switch (requestCode) {
                    case SELECAO_CAMERA:
                        img = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        /*Uri localImagemSelecionada = data.getData();
                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), localImagemSelecionada);
                        img = ImageDecoder.decodeBitmap(source);

                         /*

                        Uri localImagemSelecionada = data.getData();
                        img = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;

                         */

                        Uri localImagemSelecionada = data.getData();

                        if (android.os.Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source imageDecoder = ImageDecoder.createSource(getContentResolver(), localImagemSelecionada);
                            img = ImageDecoder.decodeBitmap(imageDecoder);
                            //Log.i("SDK", ">= 29");
                        } else {
                            img = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                            //Log.i("SDK", "< 29");
                        }
                        break;


                    default:
                        break;
                }

                if(img != null) {
                    circleImageViewPerfil.setImageBitmap(img);

                    //recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //salvar imagem no firebase
                    //pode ser preciso definir como "final"
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(idUsuario + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(
                                    ConfiguracoesActivity.this,
                                    "Erro ao subir imagem",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(
                                    ConfiguracoesActivity.this,
                                    "Sucesso ao subir imagem",
                                    Toast.LENGTH_SHORT
                            ).show();


                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();

                                    atualizaFotoUsuario(url);
                                }
                            });
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(
                    ConfiguracoesActivity.this,
                    "O aplicativo não foi capaz de recuperar a imagem.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    } //fim onActivityResult



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //para verificar as permissões
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



    public void abrirGaleriaConfiguracoes(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if(i.resolveActivity(getPackageManager()) == null) {
            startActivityForResult(i, SELECAO_GALERIA);
        } else {
            Toast.makeText(
                    ConfiguracoesActivity.this,
                    "Não foi possível abrir a galeria",
                    Toast.LENGTH_SHORT
            ).show();
        }

    } //fim abrirGaleriaConfiguracoes



    public void abrirCameraConfiguracoes(View view) {

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

    } //fim abrirCameraConfiguracoes


    public void atualizaFotoUsuario(Uri url) {
        UsuarioFirebase.atualizarFotoUsuario(url);
    }
}