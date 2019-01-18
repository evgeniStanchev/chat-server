package com.egtinteractive.chat.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import com.egtinteractive.chat.server.logger.Logger;
import com.egtinteractive.chat.server.room.Room;
import com.egtinteractive.chat.server.server.Server;
import com.egtinteractive.chat.server.user.User;

public final class CommandHandler implements Handler {

  private final User user;
  private final Server server;
  private final Logger logger;

  public CommandHandler(final User user) {
    this.user = user;
    this.server = user.getServer();
    this.logger = user.getLogger();
  }

  @Override
  public void handleRequest(final BufferedReader reader, final String line) {
    switch (line) {
      case ("quit"):
        if (user.getRoom() != null) {
          leaveRoom();
        }
        quit();
        break;
      case ("help"):
        logger.showOptions();
        break;
      case ("create room"):
        try {
          createRoom(reader);
        } catch (final IOException e) {
          throw new RuntimeException(e);
        }
        break;
      case ("join room"):
        if (roomsExists()) {
          try {
            joinRoom(reader);
          } catch (final IOException e) {
            throw new RuntimeException(e);
          }
        }
        break;
      case ("remove room"):
        if (showMyRooms() != null) {
          try {
            removeRoom(reader);
          } catch (final IOException e) {
            throw new RuntimeException(e);
          }
        }
        break;
      case ("show rooms"):
        showServerRooms();
        break;
      case ("show room"):
        showCurrentRoom();
        break;
      case ("leave"):
        leaveRoom();
        break;
      case ("show users"):
        showUsers();
        break;
      default:
        sendMessage(line);
    }
  }

  private void showUsers() {
    final Room room = user.getRoom();
    if (room == null) {
      logger.writeMsg("You are not in a room right now!");
    } else {
      user.showUsersInMyRoom();
    }
  }

  private void quit() {
    user.quitServer();
    logger.writeMsg("exiting the chat server..");
  }

  private void createRoom(final BufferedReader reader) throws IOException {
    String name = null;
    logger.writeMsg("Type the name of the new room or 0 to cancel");
    while ((name = reader.readLine()) != null) {
      if ("0".equals(name)) {
        break;
      } else {
        final Room newRoom = server.addRoom(name);
        if (newRoom == null) {
          logger.writeMsg("You created a room: " + name);
          user.addMyRoom(server.getRoom(name));
          return;
        }
      }
      logger.writeMsg("A room with that name already exists. Try with another one:");
    }
    logger.writeMsg("Creating room was canceled.");
  }

  private void removeRoom(final BufferedReader reader) throws IOException {
    logger.writeMsg("Type the name of the room you want to remove or 0 to cancel");
    final String roomName = reader.readLine();
    try {
      server.getServerRooms().get(roomName).lockTheRoom();
      final Room room = server.getServerRooms().get(roomName);
      removeTheRoom(roomName, room);
    } catch (final NullPointerException e) {
      if ("0".equals(roomName)) {
        logger.writeMsg("Removing room was canceled.");
      } else {
        logger.writeMsg("You don't own a room with that name!");
      }
    }
  }

  private void removeTheRoom(final String roomName, final Room room) {
    server.removeRoom(roomName);
    notifyRemovingAndLeave(room);
  }

  private void notifyRemovingAndLeave(final Room room) {
    for (User currentUser : room.getUsers()) {
      if (!currentUser.equals(user) && currentUser.getRoom() == room) {
        currentUser.getLogger().writeMsg(
            "Room '" + room.getName() + "' was deleted from admin! You must enter another one!");
      }
      room.removeUser(currentUser);
      currentUser.setRoom(null);
    }
    logger.writeMsg("You deleted the room '" + room.getName() + "!'");
  }

  private boolean isCorrectRoom(final Room room) {
    if (room == null) {
      logger.writeMsg("There is no room with that name!");
      logger.showOptions();
      return false;
    }
    final Room userRoom = user.getRoom();
    if (userRoom != null) {
      if (user.getRoom() == room) {
        logger.writeMsg("You are in the same room!");
        return false;
      }
      leaveRoom();
    }
    return true;
  }

  private void joinRoom(final BufferedReader reader) throws IOException {
    showServerRooms();
    final Room room = server.getRoom(reader.readLine());
    if (isCorrectRoom(room) && !room.isLocked()) {
      joinInRoom(room);
    } else {
      user.getLogger()
          .writeMsg("This room was removed, while you were trying to enter it! Please try again.");
    }
  }

  private void joinInRoom(final Room room) {
    user.setRoom(server.getServerRooms().get(room.getName()));
    room.addUser(user.getName(), user);
    if (user.getRoom() == null || room.isLocked()) {
      user.setRoom(null);
      user.getLogger()
          .writeMsg("This room was removed, while you were trying to enter it! Please try again.");
    } else {
      notifyJoining(room);
    }
  }

  private void notifyJoining(Room room) {
    logger.writeMsg("Welcome to room:" + room.getName());
    for (User currentUser : room.getUsers()) {
      if (currentUser.isAlive() && !currentUser.equals(user)) {
        currentUser.getLogger().writeMsg(user.getName() + " joined the room.");
      }
    }
  }

  private void sendMessage(final String msg) {
    final Room room = user.getRoom();
    if (room != null) {
      for (User currentUser : room.getUsers()) {
        if (currentUser.isAlive() && currentUser.getRoom().equals(room)) {
          currentUser.getMessenger().addMessage(user, msg);
        }
      }
    } else {
      logger.writeMsg("You need to enter some room before you write messages!");
    }
  }

  private void leaveRoom() {
    final Room room = user.getRoom();
    if (room != null) {
      if (!room.isLocked() && user.isAlive()) {
        room.removeUser(user);
        user.setRoom(null);
        notifyLeaving(room);
      }
    } else {
      logger.writeMsg("You are not in a room right now!");
    }
  }


  private void notifyLeaving(final Room room) {
    logger.writeMsg("You left room:" + room.getName());
    for (User currentUser : room.getUsers()) {
      if (currentUser.isAlive() && !currentUser.equals(user)) {
        currentUser.getLogger().writeMsg(user.getName() + " left the room.");
      }
    }
  }

  private void showCurrentRoom() {
    if (user.getRoom() != null && !user.getRoom().isLocked()) {
      logger.writeMsg("Current room:" + user.getRoom().getName());
    } else {
      logger.writeMsg("You are not in a room right now!");
    }
  }

  private Map<String, Room> showMyRooms() {
    final Map<String, Room> myRooms = user.getMyRooms();
    if (myRooms.isEmpty()) {
      logger.writeMsg("You didn't create any room yet.");
      return null;
    } else {
      logger.writeMsg(System.lineSeparator());
      logger.writeMsg("Your rooms:" + System.lineSeparator() + "----------------");
      for (String name : myRooms.keySet()) {
        logger.writeMsg(name);
      }
      logger.writeMsg("----------------");
      return myRooms;
    }
  }

  private boolean roomsExists() {
    final Map<String, Room> serverRooms = server.getServerRooms();
    if (serverRooms.isEmpty()) {
      logger.writeMsg("There are no rooms right now. You must create at least one!");
      return false;
    }
    return true;
  }

  private void showServerRooms() {
    final Map<String, Room> serverRooms = server.getServerRooms();
    if (roomsExists()) {
      logger.writeMsg("Rooms on that server:" + System.lineSeparator() + "----------------");
      for (Room room : serverRooms.values()) {
        if (!room.isLocked()) {
          logger.writeMsg(room.getName());
        }
      }
      logger.writeMsg("----------------");
    }
  }
}
