package com.example.cs160_sp18.prog3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class CommentFeedActivity extends AppCompatActivity{

    private String username;
    private String landmarkName;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Comment> mComments = new ArrayList<Comment>();
    public HashMap<String, String> comments;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference databaseLike;
    CommentAdapter.onBtnClickListener listener;
    public Integer likeCount;
    boolean processLike = false;


    // UI elements
    EditText commentInputBox;
    RelativeLayout layout;
    Button sendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_feed);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        landmarkName = intent.getStringExtra("locationName");

        // sets the app bar's title
        setTitle(landmarkName + ": Posts");

        // hook up UI elements
        layout = (RelativeLayout) findViewById(R.id.comment_layout);
        commentInputBox = (EditText) layout.findViewById(R.id.comment_input_edit_text);
        sendButton = (Button) layout.findViewById(R.id.send_button);
        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // create an onclick for the send button
        setOnClickForSendButton();

        mComments = new ArrayList<Comment>();

        final DatabaseReference bearLandmark = database.getReference(landmarkName);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mComments = new ArrayList<Comment>();
                comments = (HashMap<String, String>) dataSnapshot.getValue();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String s = ds.getKey();
                    Date date = getDate(s);
                    String comment = ds.child("comment").getValue(String.class);
                    String user = ds.child("user").getValue(String.class);
                    String landmark = ds.child("landmark").getValue(String.class);
                    Comment c = new Comment(comment, user, landmark, date);
                    mComments.add(c);
                }

                setAdapterAndUpdateData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        bearLandmark.addValueEventListener(listener);
        setAdapterAndUpdateData();
    }

    public Date getDate(String s){
        Date date = null;
        try {
            date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(s);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;

    }

    private void setOnClickForSendButton() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String comment = commentInputBox.getText().toString();
                if (TextUtils.isEmpty(comment)) {
                    commentInputBox.requestFocus();
                } else {
                    commentInputBox.setText("");
                    postNewComment(comment);
                }
            }
        });
    }


    private void setAdapterAndUpdateData() {
            CommentAdapter.onBtnClickListener listener = new CommentAdapter.onBtnClickListener() {
            @Override
            public void onBtnClick(CommentAdapter.CommentViewHolder mHolder, Comment comment) {

                String tag = mHolder.likeButton.getTag().toString();
                if (tag == "liked") {
                    mHolder.likeButton.setImageResource(R.drawable.unlike);
                    mHolder.likeButton.setTag("unlike");

                } else {
                    mHolder.likeButton.setImageResource(R.drawable.liked);
                    mHolder.likeButton.setTag("liked");
                }
            }

            };


            mAdapter = new CommentAdapter(this, mComments, listener);
            mRecyclerView.setAdapter(mAdapter);
            if (mComments.size() == 0) {
                mRecyclerView.smoothScrollToPosition(0);
            } else {
                mRecyclerView.smoothScrollToPosition(mComments.size() - 1);
            }
    }





    private void postNewComment(String commentText) {
        Comment newComment = new Comment(commentText, username, landmarkName, new Date());
        mComments.add(newComment);

        DatabaseReference landmark  = database.getReference(landmarkName);
        String time = String.valueOf(new Date());
        DatabaseReference entry = landmark.child(time);
        entry.child("user").setValue(username);
        entry.child("comment").setValue(commentText);
        entry.child("landmark").setValue(landmarkName);


        setAdapterAndUpdateData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}



