package com.egtinteractive.chat.server.user;

import java.util.Map;
import com.egtinteractive.chat.server.logger.Logger;
import com.egtinteractive.chat.server.room.Room;
import com.egtinteractive.chat.server.server.Server;
import com.egtinteractive.message.Messenger;

public interface User {

  Map<String, Room> getMyRooms();

  Server getServer();

  Logger getLogger();

  String getName();

  Messenger getMessenger();

  Room getRoom();

  Room removeMyRoom(String name);

  void showUsersInMyRoom();

  void addMyRoom(Room room);

  void setRoom(Room room);

  void run();

  void close();

  void quitServer();

  boolean isAlive();

}
