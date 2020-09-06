package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.letschat.fragment.ChatsFragment;
import com.example.letschat.fragment.ProfileFragment;
import com.example.letschat.fragment.UsersFragment;
import com.example.letschat.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.luseen.spacenavigation.SpaceOnLongClickListener;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView userName;
    de.hdodenhof.circleimageview.CircleImageView profileImage;

    com.luseen.spacenavigation.SpaceNavigationView bottomNavigationView;
    FrameLayout fragmentContiner;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    UsersFragment usersFragment;
    ChatsFragment chatsFragment;
    ProfileFragment profileFragment;

    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setTitle("");

        userName = findViewById (R.id.user_name);
        profileImage = findViewById (R.id.profileImage);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child( firebaseUser.getUid() );

        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                String name = user.getUsername ();

                userName.setText( name );
                if (user.getImageUrl().equals( "default" )){
                    profileImage.setImageResource( R.drawable.ic_user );
                }else {
                    Glide.with( getApplicationContext() ).load( user.getImageUrl() ).into( profileImage );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );




        bottomNavigationView = findViewById (R.id.space);
        fragmentContiner = findViewById (R.id.fragmentContiner);

        bottomNavigationView.initWithSaveInstanceState (savedInstanceState);
        bottomNavigationView.addSpaceItem (new SpaceItem ("", R.drawable.ic_friends));
        bottomNavigationView.addSpaceItem (new SpaceItem ("", R.drawable.ic_profile));

        chatsFragment = new ChatsFragment ();
        profileFragment = new ProfileFragment ();
        usersFragment = new UsersFragment ();

        ((AppCompatActivity) MainActivity.this).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContiner, usersFragment).commit();

        bottomNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                InitialFragment(chatsFragment);
                Toast.makeText(MainActivity.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {
                    case 0:
                        InitialFragment(usersFragment);
                        Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                        return;

                    case 1:
                        InitialFragment(profileFragment);
                        Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                        return;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
            }
        });

    }

    public void  InitialFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContiner,fragment);
        fragmentTransaction.commit();
    }
}