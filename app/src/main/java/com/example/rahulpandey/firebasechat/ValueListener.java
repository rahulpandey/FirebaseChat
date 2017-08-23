package com.example.rahulpandey.firebasechat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ValueListener implements ValueEventListener, ChildEventListener {
  private Consumer<DataSnapshot> dataSnap = null;
  private Consumer<DatabaseError> errorConsumer = null;

  ValueListener addSingleValueListener(DatabaseReference database) {
    database.addListenerForSingleValueEvent(this);
    return this;
  }

  void firebaseValue(Consumer<DataSnapshot> dataSnap, Consumer<DatabaseError> errorConsumer) {
    this.dataSnap = dataSnap;
    this.errorConsumer = errorConsumer;
  }

  ValueListener addChildValueListener(DatabaseReference database) {
    database.addChildEventListener(this);
    return this;
  }

  static synchronized ValueListener with() {
    return new ValueListener();
  }

  @Override public void onDataChange(DataSnapshot dataSnapshot) {
    dataSnap.accept(dataSnapshot);
  }

  @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
    dataSnap.accept(dataSnapshot);

  }

  @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {

  }

  @Override public void onChildRemoved(DataSnapshot dataSnapshot) {

  }

  @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {

  }

  @Override public void onCancelled(DatabaseError databaseError) {
    errorConsumer.accept(databaseError);
  }
}
