package com.example.cs160_sp18.prog3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;



// Adapter for the recycler view in CommentFeedActivity. You do not need to modify this file
public class CommentAdapter extends RecyclerView.Adapter {

    public interface onBtnClickListener {

        void onBtnClick(CommentViewHolder mHolder, Comment comment);

    }

    public void setOnBtnClickListener(onBtnClickListener listener){
        this.mListener = listener;
    }


    private Context mContext;
    private final ArrayList<Comment> mComments;
    onBtnClickListener mListener;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    private boolean processLike = false;

    public CommentAdapter(Context context, ArrayList<Comment> comments, onBtnClickListener listen) {
        this.mContext = context;
        this.mComments = comments;
        this.mListener = listen;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // here,  we specify what kind of view each cell should have. In our case, all of them will have a view
        // made from comment_cell_layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_cell_layout, parent, false);
        return new CommentViewHolder(view);
    }


    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // here, we the comment that should be displayed at index `position` in our recylcer view
        // everytime the recycler view is refreshed, this method is called getItemCount() times (because
        // it needs to recreate every cell).

        Log.e("onBindView", "Method");
        final CommentViewHolder mHolder = (CommentViewHolder) holder;
        final Comment comment = mComments.get(position);
        mHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLike(comment);
                addCount(comment, true);

                if(mListener != null){
                    mListener.onBtnClick(mHolder, comment);
                }

                }
            });

        mHolder.bind(comment);

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mComments.size();
    }



    public class CommentViewHolder extends RecyclerView.ViewHolder  {
        // each data item is just a string in this case
        View mView;
        public RelativeLayout mCommentBubbleLayout;
        public TextView mUsernameTextView, mDateTextView, mCommentTextView, likeCount;
        ImageButton likeButton;
        FirebaseAuth mAuth;
        boolean processLike;
        final public Integer count = 0;

        public CommentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mCommentBubbleLayout = mView.findViewById(R.id.comment_cell_layout);
            mUsernameTextView = mCommentBubbleLayout.findViewById(R.id.username_text_view);
            mDateTextView = mCommentBubbleLayout.findViewById(R.id.date_text_view);
            mCommentTextView = mCommentBubbleLayout.findViewById(R.id.comment_text_view);
            likeCount = (TextView) mView.findViewById(R.id.numLikes);
            this.likeButton = (ImageButton) mView.findViewById(R.id.like);
            likeButton.setTag("unlike");
            mAuth = FirebaseAuth.getInstance();
//            final boolean processLike = false;

        }

        public void bind(final Comment comment) {
            mUsernameTextView.setText(comment.username);
            mDateTextView.setText("posted " + comment.elapsedTimeString() + " ago");
            mCommentTextView.setText(comment.text);
            final String id = comment.getPostid();
            final String landmark = comment.getLandmark();
        }
    }

    public void processLike(Comment comment){
        final String id = comment.getPostid();
        final String landmark = comment.getLandmark();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("likes").child(landmark).child(id);
        final DatabaseReference idRef = FirebaseDatabase.getInstance().getReference("likes").child(landmark);

        processLike = true;
        Log.d("FIRST processLike = ", Boolean.toString(processLike));

        idRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (processLike) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (snapshot.hasChild(id + "," + user.getUid())) {
                        idRef.child(id + "," + user.getUid()).removeValue();
                        Log.d("user  ", "removed");
                        processLike = false;
                        Log.d("processLike = ", Boolean.toString(processLike));
                    } else {
                        idRef.child(id + "," + user.getUid()).child(id).setValue("random text");
                        processLike = true;
                        Log.d("user  ", "added");
                        Log.d("processLike = ", Boolean.toString(processLike));
                    }

                }
                Log.d("LAST processLike = ", Boolean.toString(processLike));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void addCount(Comment clickedPost, final boolean bool){
        final String id = clickedPost.getPostid();
        final String landmark = clickedPost.getLandmark();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference likes = database.getReference("likes").child(landmark).child(id + "," + user.getUid()).child("numLikes");

        likes.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
//                DatabaseReference likeCount = database.getReference("likes").child(id + "," + landmark).child("numLikes");

                Integer curr = mutableData.getValue(Integer.class);

                if (curr == null && bool == true) {
                    mutableData.setValue(1);
                    Log.e("count set to", "1");

                } else {
                    if(bool == true && curr != null){
                        mutableData.setValue(curr + 1);
                        Log.e("new count", "set");

                    }else if (bool == false && curr != null) {
                        mutableData.setValue(curr - 1);
                        Log.e("new count", "set");
                    }
                }

//                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                String currUser = user.getUid();
//
//                likes.child(currUser).setValue(true);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                Log.e("tag", "transaction completed");

            }
        });
    }



    public void numLikes(final TextView likes, Comment comment){
        final String id = comment.getPostid();
        final String landmark = comment.getLandmark();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(landmark).child(id).child("numLikes");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Integer value = dataSnapshot.getValue(Integer.class);
                Log.e("numLikes", Integer.toString(value));
                likes.setText(value + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
