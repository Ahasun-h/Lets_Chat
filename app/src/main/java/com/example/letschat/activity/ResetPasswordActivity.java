package com.example.letschat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.letschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    Button btn_reset;
    TextInputEditText send_email;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_reset_password );

        btn_reset = findViewById( R.id.btn_reset );
        send_email = findViewById( R.id.send_email );

        firebaseAuth = FirebaseAuth.getInstance();

        btn_reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = send_email.getText().toString();

                if (email.equals("")){
                    Toast.makeText( ResetPasswordActivity.this, "Please,Enter your email...", Toast.LENGTH_SHORT ).show();
                }else {
                    firebaseAuth.sendPasswordResetEmail( email ).addOnCompleteListener( new OnCompleteListener <Void>() {
                        @Override
                        public void onComplete(@NonNull Task <Void> task) {
                           if (task.isSuccessful()){
                               Toast.makeText( ResetPasswordActivity.this, "Please check you Email", Toast.LENGTH_SHORT ).show();
                               startActivity( new Intent( ResetPasswordActivity.this,Login.class ) );
                               finish();
                           }else {
                               String error = task.getException().getMessage();
                               Toast.makeText( ResetPasswordActivity.this, error, Toast.LENGTH_SHORT ).show();
                           }
                        }
                    } );
                }

            }
        } );

    }
}
