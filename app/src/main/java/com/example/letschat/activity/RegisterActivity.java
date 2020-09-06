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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText userName,userEmail,password,confirmPassword;
    Button next;
    TextView backButton,registerButton;
    com.github.ybq.android.spinkit.SpinKitView spin_kit;

    String mUserName,mUserEmail,mPassword,mConfirmPassword,phoneNumber;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_register);

        userName = findViewById (R.id.userName);
        userEmail = findViewById (R.id.userEmail);
        password = findViewById (R.id.password);
        confirmPassword = findViewById (R.id.confirmPassword);

        registerButton = findViewById (R.id.registerButton);
        spin_kit = findViewById (R.id.spin_kit);
        backButton = findViewById (R.id.backButton);

//        phoneNumber = getIntent().getStringExtra("number");
//        phoneNumber = "+23123123311";

        auth = FirebaseAuth.getInstance(  );

        backButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (RegisterActivity.this, Login.class);
                startActivity (intent);
            }
        });

        registerButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                spin_kit.setVisibility (View.VISIBLE);
                mUserName = userName.getText ().toString ();
                mUserEmail = userEmail.getText ().toString ();
                mPassword = password.getText ().toString ();
                mConfirmPassword = confirmPassword.getText ().toString ();

                if (mUserName.equals (null)){
                    userName.setError ("Please, Enter your user name");
                    spin_kit.setVisibility (View.GONE);
                }
                else if (mUserName.contains (" ")) {
                    userName.setError ("Space not allow");
                    spin_kit.setVisibility (View.GONE);
                }
                else if (mUserEmail.equals (null)){
                    userEmail.setError ("Please, Enter your Email");
                    spin_kit.setVisibility (View.GONE);
                }
                else if (mPassword.equals (null)){
                    password.setError ("Please, Enter your password");
                    spin_kit.setVisibility (View.GONE);
                }
                else if (mConfirmPassword.equals (null)){
                    confirmPassword.setError ("Please, Enter your confirm password");
                    spin_kit.setVisibility (View.GONE);
                }
                else if (!mPassword.equals (mConfirmPassword)){
                    confirmPassword.setError ("Password not matched");
                    spin_kit.setVisibility (View.GONE);
                }else if (password.length ()<6){
                    password.setError ("Please, Enter 6 Character password");
                    confirmPassword.setError ("Please, Enter 6 Character password");
                    spin_kit.setVisibility (View.GONE);
                }
                else
                {
                    auth.createUserWithEmailAndPassword( mUserEmail,mPassword )
                            .addOnCompleteListener( new OnCompleteListener<AuthResult> () {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        spin_kit.setVisibility (View.GONE);
                                        FirebaseUser firebaseUser = auth.getCurrentUser();

                                        assert firebaseUser != null;
                                        String userid = firebaseUser.getUid();

                                        reference = FirebaseDatabase.getInstance().getReference("Users").child( userid );

                                        HashMap<String , String> hashMap = new HashMap <>(  );
                                        hashMap.put( "id",userid );
                                        hashMap.put( "username",mUserName );
                                        hashMap.put( "imageUrl","default" );
                                        hashMap.put( "user_email",mUserEmail );
                                        hashMap.put( "status","offline" );
                                        hashMap.put( "search",mUserName.toLowerCase());


                                        reference.setValue( hashMap ).addOnCompleteListener( new OnCompleteListener <Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task <Void> task) {
                                                if (task.isSuccessful()){
                                                    spin_kit.setVisibility (View.GONE);
                                                    Intent intent = new Intent( RegisterActivity.this, MainActivity.class );
                                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                                    startActivity( intent );
                                                    finish();

                                                }
                                            }
                                        } );
                                    }else {
                                        spin_kit.setVisibility (View.GONE);
//                                        Toast.makeText( RegisterActivity.this, mUserEmail+"\n"+mPassword, Toast.LENGTH_SHORT ).show();
                                        showDialog();
                                    }
                                }} );

                }
            }
        });
    }

    void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View viewLayout = inflater.inflate(R.layout.same_email_registar_error , null);
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