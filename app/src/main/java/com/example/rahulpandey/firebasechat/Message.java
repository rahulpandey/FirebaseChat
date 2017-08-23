package com.example.rahulpandey.firebasechat;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import java.util.Date;

@IgnoreExtraProperties public class Message {
  public String message;
  @PropertyName("sender") public User sender;
  public Date timeStamp;

  public Message() {
  }

  public Message(String message, User sender, Date timeStamp) {
    this.message = message;
    this.sender = sender;
    this.timeStamp = timeStamp;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public User getSender() {
    return sender;
  }

  public void setSender(User sender) {
    this.sender = sender;
  }

  public Date getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(Date timeStamp) {
    this.timeStamp = timeStamp;
  }
}
