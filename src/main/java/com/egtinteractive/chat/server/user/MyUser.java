package com.egtinteractive.chat.server.user;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.egtinteractive.chat.server.handler.CommandHandler;
import com.egtinteractive.chat.server.handler.Handler;
import com.egtinteractive.chat.server.logger.Logger;
import com.egtinteractive.chat.server.logger.MyLogger;
import com.egtinteractive.chat.server.room.Room;
import com.egtinteractive.chat.server.server.Server;
import com.egtinteractive.message.Messenger;
import com.egtinteractive.message.MyMessenger;

public final class MyUser implements Runnable, Closeable, User {

  private final Map<String, Room> myRooms = new ConcurrentHashMap<>();

  private volatile boolean isAlive = true;

  private final Socket socket;
  private final Server server;

  private volatile Room room;
  private volatile String name;
  private volatile Logger logger;
  private volatile Messenger messenger;

  private InputStream inputStream;
  private InputStreamReader inputStreamReader;
  private OutputStream outputStream;
  private BufferedReader bufferedReader;

  public MyUser(final Server server, final Socket socket) {
    this.server = server;
    this.socket = socket;
  }

  @Override
  public Server getServer() {
    return this.server;
  }

  @Override
  public Logger getLogger() {
    return this.logger;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Messenger getMessenger() {
    return this.messenger;
  }

  @Override
  public Room getRoom() {
    return this.room;
  }

  @Override
  public void setRoom(final Room room) {
    this.room = room;
  }

  @Override
  public void addMyRoom(final Room room) {
    myRooms.put(room.getName(), room);
  }

  @Override
  public Room removeMyRoom(final String name) {
    return myRooms.remove(name);
  }

  @Override
  public Map<String, Room> getMyRooms() {
    return this.myRooms;
  }

  @Override
  public void quitServer() {
    server.removeUser(this);
    Thread.currentThread().interrupt();
  }

  private void setName(final BufferedReader reader) throws IOException {
    while (true) {
      logger.writeMsg("Write your name:");
      final String name = reader.readLine();
      if (name.split(" ").length == 1) {
        if (server.addUser(name, this) == null) {
          this.name = name;
          logger.writeMsg("Hello, " + name);
          logger.showOptions();
          logger.writeMsg("You can see your options by typing 'help'!");
          break;
        } else {
          logger.writeMsg("This name is already used, type another one!");
        }
      } else {
        logger.writeMsg("This name is incorrect, type only one name/word");
      }
    }
  }

  @Override
  public void showUsersInMyRoom() {
    logger.writeMsg(
        System.lineSeparator() + "-------------------" + System.lineSeparator() + "Users in room '"
            + room.getName() + "':" + System.lineSeparator() + "-------------------");
    synchronized (room) {
      for (String username : room.getUserNames()) {
        logger.writeMsg(username);
      }
    }
  }


  @Override
  public void run() {
    try (final InputStream inputStream = socket.getInputStream();
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final OutputStream outputStream = socket.getOutputStream();
        final Messenger messenger = new MyMessenger(this);) {
      setResources(inputStream, inputStreamReader, bufferedReader, outputStream, messenger);
      setName(bufferedReader);
      new Thread(messenger).start();
      acceptCommands(bufferedReader);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    } finally {
      shutdown();
    }
  }

  private void acceptCommands(final BufferedReader bufferedReader) throws IOException {
    final Handler commandHandler = new CommandHandler(this);
    String line;
    while (!Thread.currentThread().isInterrupted() && (line = bufferedReader.readLine()) != null) {
      commandHandler.handleRequest(bufferedReader, line);
    }
  }

  private void setResources(final InputStream inputStream,
      final InputStreamReader inputStreamReader, final BufferedReader bufferedReader,
      final OutputStream outputStream, final Messenger messenger) {
    this.inputStream = inputStream;
    this.inputStreamReader = inputStreamReader;
    this.outputStream = outputStream;
    this.bufferedReader = bufferedReader;
    this.logger = new MyLogger(outputStream);
    this.messenger = messenger;
  }

  @Override
  public void close() {
    isAlive = false;
    shutdown();
  }

  private void shutdown() {
    try {
      server.removeUser(this);
    } catch (final Exception e) {
      e.printStackTrace();
    }
    try {
      inputStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    try {
      inputStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    try {
      inputStreamReader.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    try {
      bufferedReader.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    try {
      outputStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    try {
      messenger.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean isAlive() {
    return isAlive;
  }
}
