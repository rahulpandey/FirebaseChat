package com.example.rahulpandey.firebasechat;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by rahul.pandey on 8/9/2017.
 */
@IgnoreExtraProperties public class User implements Parcelable {

  public String uid;
  public String userName;
  public String email;
  public String profilePhoto;

  public User() {
  }

  User(FirebaseUser user) {
    this.uid = user.getUid();
    this.userName = user.getDisplayName();
    this.email = user.getEmail();
    this.profilePhoto = user.getPhotoUrl().toString();
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getProfilePhoto() {
    return profilePhoto;
  }

  public void setProfilePhoto(String profilePhoto) {
    this.profilePhoto = profilePhoto;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.uid);
    dest.writeString(this.userName);
    dest.writeString(this.email);
    dest.writeString(this.profilePhoto);
  }

  private User(Parcel in) {
    this.uid = in.readString();
    this.userName = in.readString();
    this.email = in.readString();
    this.profilePhoto = in.readString();
  }

  public static final Creator<User> CREATOR = new Creator<User>() {
    @Override public User createFromParcel(Parcel source) {
      return new User(source);
    }

    @Override public User[] newArray(int size) {
      return new User[size];
    }
  };

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    User user = (User) o;

    return uid.equals(user.uid);
  }

  @Override public int hashCode() {
    return uid.hashCode();
  }
}
