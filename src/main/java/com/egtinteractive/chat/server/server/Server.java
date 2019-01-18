package com.egtinteractive.chat.server.server;

import java.io.Closeable;
import java.util.Map;
import com.egtinteractive.chat.server.room.Room;
import com.egtinteractive.chat.server.user.User;

public interface Server extends Closeable {

  User addUser(String name, User user);

  Map<String, Room> getServerRooms();

  Room getRoom(final String name);

  Room addRoom(final String name);

  void removeRoom(final String roomName);

  void run();

  void removeUser(User user);

  void close();

}
