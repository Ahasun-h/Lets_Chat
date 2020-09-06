package com.example.letschat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.letschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPVerification extends AppCompatActivity {

    com.chaos.view.PinView firstPinView;
    TextView nextButton,backButton;
    com.github.ybq.android.spinkit.SpinKitView spin_kit;

    private String mAuthVerificationId;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    String number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.otp_verification);

        firstPinView = findViewById (R.id.firstPinView);
        nextButton = findViewById (R.id.nextButton);
        backButton = findViewById (R.id.backButton);
        spin_kit = findViewById (R.id.spin_kit);
        spin_kit.setVisibility (View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        number = getIntent().getStringExtra("number");
        mAuthVerificationId = getIntent().getStringExtra("AuthCredentials");

        nextButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spin_kit.setVisibility (View.VISIBLE);
                String otp = firstPinView.getText().toString();


                if (otp.equals( "" ))
                {
                    firstPinView.setError( "Enter Your 6 digit Code" );
                    spin_kit.setVisibility (View.GONE);
                }
                else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId, otp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        } );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTPVerification.this, new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            spin_kit.setVisibility (View.GONE);
                            sendUserToHome();
                        } else {
                            spin_kit.setVisibility (View.GONE);
                            Toast.makeText( OTPVerification.this, "Enter Wrong Code!", Toast.LENGTH_SHORT ).show();
                            Intent intent = new Intent( OTPVerification.this,NumberVerification.class );
                            startActivity( intent );
                            finish();
                        }
                    }
                });
    }

    public void sendUserToHome() {
        Intent intent = new Intent(OTPVerification.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("number",number);
        startActivity(intent);
        finish();
    }
}


