package com.example.letschat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.letschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;


public class NumberVerification extends AppCompatActivity {


    EditText number;
    TextView send;
    CountryCodePicker ccp;
    com.github.ybq.android.spinkit.SpinKitView spin_kit;

    String complete_phone_number;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_number_verification);

        number = findViewById (R.id.phoneNumber);
        ccp = findViewById (R.id.ccp);
        send = findViewById (R.id.send);
        spin_kit = findViewById (R.id.spin_kit);




        send.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                complete_phone_number  = "+"+ccp.getFullNumber () + number.getText ().toString ();
                String phoneNumber = number.getText ().toString ();
                spin_kit.setVisibility (View.VISIBLE);

                if (phoneNumber.equals( "" ))
                {
                    number.setError( "Enter Your Phone Number" );

                    spin_kit.setVisibility( View.GONE );
                }
                else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            complete_phone_number,
                            120,
                            TimeUnit.SECONDS,
                            NumberVerification.this,
                            mCallbacks
                    );
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                spin_kit.setVisibility(View.GONE);
                Log.e("", "onVerificationFailed: "+e.getLocalizedMessage() );
            }


            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                spin_kit.setVisibility( View.GONE );

                                Intent otpIntent = new Intent(NumberVerification.this, OTPVerification.class);
                                otpIntent.putExtra("AuthCredentials", s);
                                otpIntent.putExtra("number", complete_phone_number);
                                otpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                otpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(otpIntent);
                                finish();
                            }
                        },
                        10000);
            }
        };

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser != null){
            sendUserToHome();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(NumberVerification.this, new OnCompleteListener <AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task <AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendUserToHome();
                            // ...
                        } else {

                        }
                        spin_kit.setVisibility(View.GONE);
                    }
                });
    }

    private void sendUserToHome() {
        Intent homeIntent = new Intent(NumberVerification.this, OTPVerification.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();

    }
}