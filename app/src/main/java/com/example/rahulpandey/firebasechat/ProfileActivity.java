package com.example.rahulpandey.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ProfileActivity extends AppCompatActivity {

  private static final String KEY_FIREBASE_USER = "KEY_FIREBASE_USER";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    User user = getIntent().getParcelableExtra(KEY_FIREBASE_USER);
    getSupportActionBar().setTitle(user.userName);
    ImageView imageView = findViewById(R.id.profile_image);
    Glide.with(this)
        .asBitmap()
        .apply(RequestOptions.noAnimation().diskCacheStrategy(DiskCacheStrategy.DATA))
        .load(user.profilePhoto)
        .into(imageView);

  }

  public static void start(Context context, User user) {
    Intent starter = new Intent(context, ProfileActivity.class);
    starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    starter.putExtra(KEY_FIREBASE_USER, user);
    context.startActivity(starter);
  }
}
