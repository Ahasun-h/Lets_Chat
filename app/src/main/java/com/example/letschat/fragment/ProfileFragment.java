package com.example.letschat.fragment;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.letschat.activity.StartScreen;
import com.example.letschat.model.User;
import com.example.letschat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    CircleImageView profile_image;
    TextView username,userEmail;
    ImageButton btn_photo;

    public Context context;


    com.google.android.material.floatingactionbutton.FloatingActionButton logoutButton;

    DatabaseReference reference;
    FirebaseUser fuser;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate( R.layout.fragment_profile, container, false );

        profile_image = view.findViewById( R.id.profile_image );
        username = view.findViewById( R.id.username );
        userEmail = view.findViewById( R.id.userEmail );
        btn_photo = view.findViewById( R.id.btn_photo );
        logoutButton = view.findViewById( R.id.logoutButton );

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child( fuser.getUid() );

        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                username.setText(user.getUsername());
                userEmail.setText (user.getUser_email ());

                if (user.getImageUrl().equals( "default" )){
                    profile_image.setImageResource( R.drawable.ic_user );
                }else {
                   // Glide.with( getActivity () ).load( user.getImageUrl() ).into( profile_image );
                    if (getContext() != null){
                        Glide.with(view.getContext()).load(user.getImageUrl()).into(profile_image);
                    }else {

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        btn_photo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        } );


        logoutButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance(  ).signOut();
                startActivity( new Intent( getContext (), StartScreen.class ).setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ));
                getActivity().finish();
            }
        });

        return view;
    }

    private void openImage() {

        Intent intent = new Intent(  );
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( intent, IMAGE_REQUEST );
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension( contentResolver.getType( uri ) );
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog( getContext() );
        pd.setMessage( "uploading..." );
        pd.show();

        if (imageUri != null){
            final StorageReference fileReference = storageReference.child( System.currentTimeMillis()+"." +getFileExtension( imageUri ));

            uploadTask = fileReference.putFile( imageUri );
            uploadTask.continueWithTask( new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();

                }
            } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                   if(task.isSuccessful()){
                      Uri downloadUri = task.getResult();
                      String mUri = downloadUri.toString();

                      reference = FirebaseDatabase.getInstance().getReference("Users").child( fuser.getUid() );

                       HashMap<String, Object> map = new HashMap <>(  );
                       map.put( "imageUrl", mUri);
                       reference.updateChildren( map );

                       pd.dismiss();
                   }else {
                       pd.dismiss();
                       Toast.makeText( getContext(), "Upload Fail!", Toast.LENGTH_SHORT ).show();
                   }
                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( getContext(), e.getMessage(), Toast.LENGTH_SHORT ).show();
                    pd.dismiss();
                }
            } );
        }else {
            Toast.makeText( getContext(), "No Image Selected !", Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Glide.with( getContext() ).load( imageUri ).into( profile_image );
                Toast.makeText( getContext(), "Upload in process", Toast.LENGTH_SHORT ).show();
            }else {
                uploadImage();
            }
        }
    }

}
