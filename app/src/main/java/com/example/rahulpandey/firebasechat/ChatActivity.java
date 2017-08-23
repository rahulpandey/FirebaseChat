package com.example.rahulpandey.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
  private static final String WITH = "-with-";
  EditText messageView;
  User mSender;
  User mReceiver;
  private static final String KEY_SENDER_USER =
      "com.example.rahulpandey.firebasechat.KEY_SENDER_USER";
  public static final String KEY_RECEIVER_USER =
      "com.example.rahulpandey.firebasechat.KEY_RECEIVER_USER";
  MessageAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
    messageView = findViewById(R.id.message);
    mSender = getIntent().getParcelableExtra(KEY_SENDER_USER);
    mReceiver = getIntent().getParcelableExtra(KEY_RECEIVER_USER);

    RecyclerView mRecyclerView = findViewById(R.id.list_item);
    LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
    //layout.setStackFromEnd(false);
    mRecyclerView.setLayoutManager(layout);
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    adapter = new MessageAdapter(this, getGlide());
    mRecyclerView.setAdapter(adapter);

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    final String room_type_1 = mSender.uid + WITH + mReceiver.uid;
    final String room_type_2 = mReceiver.uid + WITH + mSender.uid;
    DatabaseReference messages1 = mDatabase.child("messages").child(room_type_1);
    DatabaseReference messages2 = mDatabase.child("messages").child(room_type_2);

    View actionSend = findViewById(R.id.action_send_message);
    actionSend.setOnClickListener(v -> sendMessage(messages1, messages2));

    DatabaseReference dateRef = messages1.orderByChild("timeStamp").getRef();

    ValueListener.with()
        .addChildValueListener(dateRef)
        .firebaseValue(data -> adapter.addMessage(data.getValue(Message.class)),
            error -> showError());
  }

  private void showError() {
    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
  }

  private void sendMessage(DatabaseReference messages1, DatabaseReference messages2) {
    String messageText = messageView.getText().toString();
    if (TextUtils.isEmpty(messageText)) return;
    Date time = Calendar.getInstance().getTime();
    Message messages = new Message(messageText, mSender, time);
    messages1.push().setValue(messages);
    messages2.push().setValue(messages);
    messageView.getText().clear();
  }

  public static void start(Context context, User sender, User receiver) {
    Intent starter = new Intent(context, ChatActivity.class);
    starter.putExtra(KEY_SENDER_USER, sender);
    starter.putExtra(KEY_RECEIVER_USER, receiver);
    context.startActivity(starter);
  }

  RequestBuilder<Bitmap> getGlide() {
    return getWith().asBitmap()
        .apply(RequestOptions.circleCropTransform()
            .override(80, 80)
            .diskCacheStrategy(DiskCacheStrategy.DATA))
        .transition(BitmapTransitionOptions.withCrossFade());
  }

  private RequestManager getWith() {
    return Glide.with(this);
  }

  private static class MessageAdapter extends RecyclerView.Adapter<ChatActivity.MessageViewHolder> {
    private final Context mContext;
    private List<Message> mMessages = new ArrayList<>();

    private RequestBuilder<Bitmap> requestBuilder;

    MessageAdapter(Context context, RequestBuilder<Bitmap> requestBuilder) {
      this.mContext = context;
      this.requestBuilder = requestBuilder;
    }

    @Override public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new MessageViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(R.layout.list_chat_message, parent, false));
    }

    void addMessage(Message message) {
      //  int start = mMessages.size();
      mMessages.add(message);
      notifyDataSetChanged();
    }

    @Override public void onBindViewHolder(MessageViewHolder holder, int position) {
      Message message = mMessages.get(getItemCount() - 1 - position);

      holder.nameView.setText(message.getSender().userName);
      holder.messageView.setText(message.getMessage());
      CharSequence date =
          DateUtils.getRelativeTimeSpanString(mContext, message.getTimeStamp().getTime());
      holder.dateView.setText(date);
      requestBuilder.load(message.getSender().profilePhoto).into(holder.profileImageView);
    }

    @Override public int getItemCount() {
      return mMessages.size();
    }
  }

  private static class MessageViewHolder extends RecyclerView.ViewHolder {
    final TextView nameView;
    final TextView messageView;
    final ImageView profileImageView;
    final TextView dateView;

    MessageViewHolder(View itemView) {
      super(itemView);
      nameView = itemView.findViewById(R.id.name);
      messageView = itemView.findViewById(R.id.message);
      profileImageView = itemView.findViewById(R.id.profile_image);
      dateView = itemView.findViewById(R.id.date);
    }
  }
}
