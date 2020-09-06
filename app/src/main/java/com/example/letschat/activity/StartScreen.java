package com.example.letschat.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.letschat.MainActivity;
import com.example.letschat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartScreen extends AppCompatActivity {

    FirebaseUser firebaseUser;

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (firebaseUser != null){
//
//            Intent intent = new Intent( SplashScreen.this, MainActivity.class );
//            startActivity( intent );
//            finish();
//
//        }
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_start_screen);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){

            Intent intent = new Intent( StartScreen.this, MainActivity.class );
            startActivity( intent );
            finish();

        }else {
            Intent intent = new Intent( StartScreen.this, Login.class );
            startActivity( intent );
            finish();
        }
    }
}