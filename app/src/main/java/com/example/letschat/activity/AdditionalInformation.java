package com.example.letschat.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.Calendar;
import java.util.HashMap;

public class AdditionalInformation extends AppCompatActivity {

    EditText about;
    TextView birthDate,registerButton,backButton;
    com.github.ybq.android.spinkit.SpinKitView spin_kit;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    String UserName,UserEmail,Password,phoneNumber,About,BirthDate;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_additional_information);

        about = findViewById (R.id.about);
        birthDate = findViewById (R.id.birthDate);
        registerButton = findViewById (R.id.registerButton);
        backButton = findViewById (R.id.backButton);

        spin_kit = findViewById (R.id.spin_kit);

        phoneNumber = getIntent ().getStringExtra ("Phone_Number");
        UserName = getIntent ().getStringExtra ("User_Name");
        UserEmail = getIntent ().getStringExtra ("Email");
        Password = getIntent ().getStringExtra ("Password");

        auth = FirebaseAuth.getInstance(  );

        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCalendar = Calendar.getInstance();
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                int month = mCalendar.get(Calendar.MONTH);
                int year = mCalendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AdditionalInformation.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        day,month,year);

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable (Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                BirthDate = dayOfMonth+"/"+month+"/"+year;
                birthDate.setText(BirthDate);
            }
        };


        registerButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                spin_kit.setVisibility (View.VISIBLE);
                About = about.getText ().toString ();

                if (About.equals (null))
                {
                    about.setError ("Please,write something about you");
                    spin_kit.setVisibility (View.GONE);
                }
                else if (BirthDate.equals (null))
                {
                    birthDate.setError ("Please,Select your birth date");
                    spin_kit.setVisibility (View.GONE);
                }
                else
                {
                    auth.createUserWithEmailAndPassword( UserEmail,Password )
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
                                        hashMap.put( "username",UserName );
                                        hashMap.put( "imageUrl","default" );
                                        hashMap.put( "user_email",UserEmail );
                                        hashMap.put( "phone_number",phoneNumber );
                                        hashMap.put( "about",About );
                                        hashMap.put( "birth_date",BirthDate );
                                        hashMap.put( "status","offline" );
                                        hashMap.put( "search",UserName.toLowerCase());


                                        reference.setValue( hashMap ).addOnCompleteListener( new OnCompleteListener <Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task <Void> task) {
                                                if (task.isSuccessful()){
                                                    spin_kit.setVisibility (View.GONE);
                                                    Intent intent = new Intent( AdditionalInformation.this,MainActivity.class );
                                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                                    startActivity( intent );
                                                    finish();

                                                }
                                            }
                                        } );
                                    }else {
                                        spin_kit.setVisibility (View.GONE);
                                        Toast.makeText( AdditionalInformation.this, UserEmail+"\n"+Password, Toast.LENGTH_SHORT ).show();
                                    }
                                }
                            } );

                }
            }
        });
    }
}