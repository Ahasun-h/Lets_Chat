package com.example.letschat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.letschat.adapter.MessageAdapter;
import com.example.letschat.model.Chat;
import com.example.letschat.model.User;
import com.example.letschat.network.APIService;
import com.example.letschat.network.Client;
import com.example.letschat.notification.Data;
import com.example.letschat.notification.MyResponce;
import com.example.letschat.notification.Sender;
import com.example.letschat.notification.Token;
import com.example.letschat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView user_name;
    ImageButton btn_send;
    EditText btn_text;
    RecyclerView recyler_view;
    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    String userid;
    ValueEventListener seenListener;

    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_message );

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        toolbar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( MessageActivity.this, StartScreen.class ).setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ));
                finish ();
            }
        } );


        apiService = Client.getClient( "https://fcm.googleapis.com" ).create( APIService.class );

        profile_image = findViewById( R.id.profile_image );
        user_name = findViewById( R.id.user_name );

        btn_send = findViewById( R.id.btn_send );
        btn_text = findViewById( R.id.btn_text );

        recyler_view = findViewById( R.id.recyler_view );
        recyler_view.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getApplicationContext() );
        linearLayoutManager.setStackFromEnd( true );
        recyler_view.setLayoutManager( linearLayoutManager );

        userid = getIntent().getStringExtra( "userid" );
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = btn_text.getText().toString();
                if (!msg.equals( "" )){

                    sendMessage(fuser.getUid(),userid,msg);
                }else {
                    Toast.makeText( MessageActivity.this, "", Toast.LENGTH_SHORT ).show();
                }
                btn_text.setText( "" );
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("Users").child( userid );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user_name.setText( user.getUsername() );
                if (user.getImageUrl().equals( "default" )){
                    profile_image.setImageResource( R.drawable.ic_user );
                }else {
                    Glide.with( getApplicationContext() ).load( user.getImageUrl() ).into( profile_image );
                }

                readMessage( fuser.getUid(),userid,user.getImageUrl() );
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
        seenMessage( userid );
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals( fuser.getUid() ) && chat.getSender().equals( userid )){
                        HashMap<String, Object> hashMap = new HashMap <>(  );
                        hashMap.put( "isseen",true );
                        snapshot.getRef().updateChildren( hashMap );
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
    }

    public void sendMessage(String sender , final String receiver, String message ){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap <>(  );
        hashMap.put( "sender" , sender);
        hashMap.put( "receiver",receiver );
        hashMap.put( "message",message );
        hashMap.put( "isseen",false );
        reference.child( "Chats" ).push().setValue( hashMap );

        final DatabaseReference  chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child( fuser.getUid() )
                .child( userid );

        chatRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child( "id" ).setValue( userid );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child( fuser.getUid() );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (notify){
                    sendNotification(receiver,user.getUsername(),msg);
                }

                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    private void sendNotification(String receiver, final String username,final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo( receiver );
        query.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data( fuser.getUid(),R.drawable.notification,username+" : "+message,"New Message",userid);

                    Sender sender = new Sender( data,token.getToken() );

                    apiService.sendNotification( sender )
                            .enqueue( new Callback <MyResponce>() {
                                @Override
                                public void onResponse(Call <MyResponce> call, Response <MyResponce> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText( MessageActivity.this, "Failed!", Toast.LENGTH_SHORT ).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call <MyResponce> call, Throwable t) {

                                }
                            } );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    public void readMessage(final String myid, final String userid, final String imageurl){

        mChat = new ArrayList <>(  );

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()  ){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals( myid ) && chat.getSender().equals( userid ) ||
                            chat.getReceiver().equals( userid ) && chat.getSender().equals( myid )){

                        mChat.add( chat );
                    }

                    messageAdapter = new MessageAdapter( MessageActivity.this,mChat,imageurl );
                    recyler_view.setAdapter( messageAdapter );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child( fuser.getUid() );

        HashMap<String ,Object> hashMap = new HashMap <>(  );
        hashMap.put( "status",status );

        reference.updateChildren( hashMap );
    }

    @Override
    protected void onResume() {
        super.onResume();
        status( "online" );
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener( seenListener );
        status( "offline" );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish ();
    }

}
