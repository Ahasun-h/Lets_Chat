package com.example.letschat.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.letschat.adapter.ChatingAdapter;
import com.example.letschat.adapter.UserAdapter;
import com.example.letschat.model.Chatlist;
import com.example.letschat.model.User;
import com.example.letschat.notification.Token;
import com.example.letschat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    RecyclerView recylerView;

    ChatingAdapter chatingAdapter;
    List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    SwipeRefreshLayout SwipeRefresh;

    List <Chatlist> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_chats, container, false );

        SwipeRefresh = view.findViewById(R.id.SwipeRefresh);

        recylerView = view.findViewById( R.id.recycler_view );
        recylerView.setHasFixedSize( true );
        recylerView.setLayoutManager( new LinearLayoutManager( getContext() ) );

        userList = new ArrayList <Chatlist>(  );
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        fetchMessageList();

        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMessageList();
                SwipeRefresh.setRefreshing(true);
            }
        });

        updateToken( FirebaseInstanceId.getInstance().getToken(  ) );
        return view;
    }

    private void fetchMessageList() {
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child( fuser.getUid() );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    userList.add( chatlist );
                }
                chatList();
                SwipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
    }

    public void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token( token );
        reference.child( fuser.getUid() ).setValue( token1 );
    }

    private void chatList() {

        mUsers = new ArrayList <>(  );
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist : userList){
                        if (user.getId().equals( chatlist.getId() )){
                            mUsers.add( user );
                        }
                    }
                }
                chatingAdapter = new ChatingAdapter ( getContext(),mUsers,true );
                recylerView.setAdapter( chatingAdapter );
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

}
