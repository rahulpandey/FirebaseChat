package com.example.rahulpandey.firebasechat;


public interface Consumer<T> {
  void accept(T data);
}
