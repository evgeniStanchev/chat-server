package com.egtinteractive.chat.server.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.egtinteractive.chat.server.room.MyRoom;
import com.egtinteractive.chat.server.room.Room;
import com.egtinteractive.chat.server.user.MyUser;
import com.egtinteractive.chat.server.user.User;

public final class MyServer implements Server {

  private final int port;
  private final Map<String, Room> roomsMap = new ConcurrentHashMap<>();
  private final Map<String, Room> roomsMapView = Collections.unmodifiableMap(roomsMap);
  private final Map<String, User> usersMap = new ConcurrentHashMap<>();

  private final ExecutorService userExecutor = Executors.newCachedThreadPool();

  private ServerSocket serverSocket;

  public MyServer(final int port) {
    this.port = port;
  }

  @Override
  public void removeUser(final User user) {
    usersMap.remove(user.getName());
  }

  @Override
  public User addUser(final String name, final User user) {
    return usersMap.put(name, user);
  }

  @Override
  public Room getRoom(final String name) {
    return roomsMap.get(name);
  }

  @Override
  public Map<String, Room> getServerRooms() {
    return roomsMapView;
  }

  public void run() {
    try (final ServerSocket serverSocket = new ServerSocket(port);) {
      this.serverSocket = serverSocket;
      System.out.println("Server has been initialized on port: " + port);
      while (true) {
        System.out.println("Waiting for next user...");
        final Socket clientSocket = serverSocket.accept();
        createUser(clientSocket);
      }
    } catch (final IOException e) {
      shutdown();
      throw new RuntimeException(e);
    } finally {
      shutdown();
    }
  }

  private void createUser(final Socket clientSocket) {
    userExecutor.execute(new MyUser(this, clientSocket));
    System.out.println(clientSocket + " has been connected!");
  }

  @Override
  public void removeRoom(final String roomName) {
    roomsMap.remove(roomName);
  }

  @Override
  public Room addRoom(final String name) {
    final Room newRoom = new MyRoom(name);
    final Room room = roomsMap.putIfAbsent(name, newRoom);
    return room;
  }


  @Override
  public void close() {
    shutdown();
    System.out.println("Server was closed!");
  }

  public void shutdown() {
    try {
      userExecutor.shutdownNow();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    try {
      serverSocket.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
