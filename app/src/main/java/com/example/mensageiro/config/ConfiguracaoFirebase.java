package com.example.mensageiro.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {
    private static DatabaseReference db;
    private static FirebaseAuth auth;

    //retorna instância do FirebaseDatabase
    public static DatabaseReference getFirebaseDatabase() {
        if (db == null) {
            db = FirebaseDatabase.getInstance().getReference();
        }

        return db;
    }

    //retorna instância do FirebaseAuth
    public static FirebaseAuth getFirebaseAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }

}
