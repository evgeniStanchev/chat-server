package com.egtinteractive.chat.server.room;

import java.util.Collection;
import java.util.Set;
import com.egtinteractive.chat.server.user.User;

public interface Room {

  Set<String> getUserNames();

  Collection<User> getUsers();

  String getName();

  void removeUser(User currentUser);

  void addUser(String name, User user);

  boolean isLocked();


  public void lockTheRoom();



}
