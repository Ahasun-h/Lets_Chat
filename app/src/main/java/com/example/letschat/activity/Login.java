package com.example.letschat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.letschat.MainActivity;
import com.example.letschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    TextView registerAccount,forgot_password,loginButton;
    EditText email,password;
    String Email,Password;
    com.github.ybq.android.spinkit.SpinKitView spin_kit;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);

        registerAccount = findViewById (R.id.registerAccount);
        forgot_password = findViewById (R.id.forgot_password);

        email = findViewById (R.id.email);
        password = findViewById (R.id.password);

        loginButton = findViewById (R.id.loginButton);
        spin_kit = findViewById (R.id.spin_kit);

        auth = FirebaseAuth.getInstance(  );


        loginButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                spin_kit.setVisibility (View.VISIBLE);
                Email = email.getText ().toString ();
                Password = password.getText ().toString ();


                if (Email.equals (null))
                {
                    email.setError ("Enter your email !");
                    spin_kit.setVisibility (View.GONE);
                }else if (Password.equals (null))
                {
                    password.setError ("Enter your email !");
                    spin_kit.setVisibility (View.GONE);
                }else {
                    auth.signInWithEmailAndPassword( Email,Password )
                            .addOnCompleteListener( new OnCompleteListener<AuthResult> () {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        spin_kit.setVisibility (View.GONE);
                                        Intent intent = new Intent( Login.this, MainActivity.class );
                                        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                        startActivity( intent );
                                        finish();
                                    }else {
                                        spin_kit.setVisibility (View.GONE);
                                        //Toast.makeText( Login.this, "Your email or password is incorrect", Toast.LENGTH_SHORT ).show();
                                        showDialog();
                                    }

                                }
                            } );
                }

            }
        });

        registerAccount.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Login.this,RegisterActivity.class);
                startActivity (intent);
                finish ();
            }
        });

        forgot_password.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Login.this,ResetPasswordActivity.class);
                startActivity (intent);
            }
        });

    }

    void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View viewLayout = inflater.inflate(R.layout.login_faild , null);
        dialog.setView(viewLayout);
        Button tryAgingButton = (Button) viewLayout.findViewById(R.id.tryAgingButton);
        final AlertDialog  alertDialog=dialog.create();
        tryAgingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}