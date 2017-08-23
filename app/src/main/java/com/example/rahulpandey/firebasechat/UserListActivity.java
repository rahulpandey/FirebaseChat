package com.example.rahulpandey.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
  public static final String TAG = "UserListActivity";
  private UserListAdapter userListAdapter;
  private static final String KEY_FIREBASE_USER = "KEY_FIREBASE_USER";
  private User mCurrentUser;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_list);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    mCurrentUser = getIntent().getParcelableExtra(KEY_FIREBASE_USER);
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    userListAdapter = new UserListAdapter(getGlide());
    userListAdapter.setOnItemClickListener(user -> ChatActivity.start(this, mCurrentUser, user));
    RecyclerView recyclerView = findViewById(R.id.user_list);
    recyclerView.setHasFixedSize(true);
    recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    recyclerView.setAdapter(userListAdapter);
    ValueListener.with()
        .addSingleValueListener(mDatabase.child("users"))
        .firebaseValue(this::displayData, this::displayError);
  }

  private void displayError(DatabaseError databaseError) {
    Log.d(TAG, databaseError.toString());
  }

  private void displayData(DataSnapshot data) {
    Log.d(TAG, data.toString());
    for (DataSnapshot dataSnapshot : data.getChildren()) {
      User user = dataSnapshot.getValue(User.class);

      if (!mCurrentUser.equals(user)) {
        userListAdapter.add(user);
      }
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_users, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_logout) {
      FirebaseAuth.getInstance().signOut();

      MainActivity.start(this);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  RequestBuilder<Bitmap> getGlide() {
    return Glide.with(this)
        .asBitmap()
        .apply(RequestOptions.circleCropTransform()
            .override(80, 80)
            .diskCacheStrategy(DiskCacheStrategy.DATA))
        .transition(BitmapTransitionOptions.withCrossFade());
  }

  private static class UserListAdapter
      extends RecyclerView.Adapter<UserListActivity.UserViewHolder> {
    List<User> users = new ArrayList<>();
    RequestBuilder<Bitmap> requestBuilder;
    Consumer<User> userConsumer;

    UserListAdapter(RequestBuilder<Bitmap> requestBuilder) {
      this.requestBuilder = requestBuilder;
    }

    @Override
    public UserListActivity.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new UserViewHolder(
          LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false),
          userConsumer);
    }

    @Override public void onBindViewHolder(UserListActivity.UserViewHolder holder, int position) {
      User user = users.get(position);
      holder.user = user;
      holder.nameView.setText(user.userName);
      requestBuilder.load(user.profilePhoto).into(holder.profileImageView);
    }

    @Override public int getItemCount() {
      return users.size();
    }

    void add(User user) {
      users.add(user);
      notifyDataSetChanged();
    }

    void setOnItemClickListener(Consumer<User> userConsumer) {
      this.userConsumer = userConsumer;
    }
  }

  private static class UserViewHolder extends RecyclerView.ViewHolder {
    final TextView nameView;
    final ImageView profileImageView;
    User user;
    final Consumer<User> userConsumer;

    UserViewHolder(View itemView, Consumer<User> userConsumer) {
      super(itemView);
      nameView = itemView.findViewById(R.id.name);
      profileImageView = itemView.findViewById(R.id.profile_image);
      this.userConsumer = userConsumer;
      itemView.setOnClickListener(v -> userConsumer.accept(user));
    }
  }

  public static void start(Context context, User currentUser) {
    Intent starter = new Intent(context, UserListActivity.class);
    starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    starter.putExtra(KEY_FIREBASE_USER, currentUser);
    context.startActivity(starter);
  }
}
