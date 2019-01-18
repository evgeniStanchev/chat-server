package com.egtinteractive.chat.server.room;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.egtinteractive.chat.server.user.User;

public final class MyRoom implements Room {


  private volatile boolean isLocked;
  private final String name;
  private final Map<String, User> usersMap = new ConcurrentHashMap<>();
  private final Map<String, User> usersMapView = Collections.unmodifiableMap(usersMap);

  public MyRoom(final String name) {
    this.name = name;
  }

  @Override
  public Collection<User> getUsers() {
    return usersMapView.values();
  }

  @Override
  public Set<String> getUserNames() {
    return usersMapView.keySet();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void addUser(final String name, final User user) {
    usersMap.putIfAbsent(name, user);
  }

  @Override
  public void removeUser(final User user) {
    usersMap.remove(user.getName());
  }

  @Override
  public boolean isLocked() {
    return isLocked;
  }

  @Override
  public void lockTheRoom() {
    isLocked = true;
  }
}
