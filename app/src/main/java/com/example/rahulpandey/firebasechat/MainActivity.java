package com.example.rahulpandey.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
    implements GoogleApiClient.OnConnectionFailedListener {

  private static final int RC_SIGN_IN = 1001;
  private static final String TAG = "MainActivity";
  private GoogleApiClient mGoogleApiClient;
  private FirebaseAuth mAuth;
  private View mSignButton;
  private View mActivityIndicator;
  private DatabaseReference mDatabase;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mDatabase = FirebaseDatabase.getInstance().getReference();
    mAuth = FirebaseAuth.getInstance();
    mSignButton = findViewById(R.id.action_sign_in);
    mActivityIndicator = findViewById(R.id.activity_indicator);
    GoogleSignInOptions gso =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
            getString(R.string.default_web_client_id)).requestEmail().build();
    mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .build();
    mSignButton.setOnClickListener(v -> signIn());
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser currentUser = mAuth.getCurrentUser();
    updateUI(currentUser);
  }

  public static void start(Context context) {
    Intent starter = new Intent(context, MainActivity.class);
    starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    context.startActivity(starter);
  }

  private void signIn() {
    showProgress(true);
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  private void showProgress(boolean show) {
    mSignButton.setVisibility(show ? View.GONE : View.VISIBLE);
    mActivityIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override public void onStart() {
    super.onStart();
  }

  @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.e(TAG, connectionResult.getErrorMessage());
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        // Google Sign In was successful, authenticate with Firebase
        GoogleSignInAccount account = result.getSignInAccount();
        firebaseAuthWithGoogle(account);
      } else {
        // Google Sign In failed, update UI appropriately
        // ...
        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
      showProgress(false);
      if (task.isSuccessful()) {
        // Sign in success, update UI with the signed-in user's information
        Log.d(TAG, "signInWithCredential:success");
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
      } else {
        // If sign in fails, display a message to the user.
        Log.w(TAG, "signInWithCredential:failure", task.getException());
        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        updateUI(null);
      }
    });
  }

  private void updateUI(FirebaseUser currentUser) {
    if (currentUser != null) {
      User user = new User(currentUser);
      writeNewUser(user);
      UserListActivity.start(this, user);
    }
  }

  private void writeNewUser(User user) {
    mDatabase.child("users").child(user.uid).setValue(user);
  }
}
