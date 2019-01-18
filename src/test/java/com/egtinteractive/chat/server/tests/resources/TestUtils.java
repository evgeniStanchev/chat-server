package com.egtinteractive.chat.server.tests.resources;

import static org.testng.Assert.assertEquals;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {

  public static final String CMD_CREATE_ROOM = "create room";
  public static final String CMD_REMOVE_ROOM = "remove room";
  public static final String CMD_SHOW_USERS = "show users";
  public static final String CMD_SHOW_ROOMS = "show rooms";
  public static final String CMD_JOIN_ROOM = "join room";
  public static final String CMD_SHOW_OPTIONS = "help";
  public static final String CMD_SHOW_ROOM = "show room";
  public static final String CMD_QUIT = "quit";
  public static final String CMD_LEAVE = "leave";

  public static final int OPTIONS_LINES_COUNT = 12;

  public static void joinRoom(final Client client, final String roomName,
      final List<Client> clients) throws IOException {
    client.writeLine(CMD_JOIN_ROOM);
    assertEquals(client.readLine(), "Rooms on that server:");
    assertEquals(client.readLine(), "----------------");
    assertEquals(client.readLine(), roomName);
    assertEquals(client.readLine(), "----------------");
    client.writeLine(roomName);
    assertEquals(client.readLine(), "Welcome to room:" + roomName);
    informUsersForJoining(client, clients);
  }

  private static void informUsersForJoining(final Client client, final List<Client> clients)
      throws IOException {
    for (Client currentClient : clients) {
      if (client != currentClient) {
        assertEquals(currentClient.readLine(), client.getName() + " joined the room.");
      }
    }
  }

  public static void initialize(final Client client) throws IOException {
    assertEquals(client.readLine(), "Write your name:");
    assertEquals(client.readLine(), "Hello, " + client.getName());
    for (int i = 0; i < OPTIONS_LINES_COUNT; i++) {
      client.readLine();
    }
  }

  public static void createRoom(final Client theCreator, final String roomName) throws IOException {
    theCreator.writeLine(CMD_CREATE_ROOM);
    assertEquals(theCreator.readLine(), "Type the name of the new room or 0 to cancel");
    theCreator.writeLine(roomName);
    assertEquals(theCreator.readLine(), "You created a room: " + roomName);
  }


  public static void removeRoom(final String roomName, final Client client) throws IOException {
    client.writeLine(CMD_REMOVE_ROOM);
    assertEquals(client.readLine(), "");
    assertEquals(client.readLine(), "");
    assertEquals(client.readLine(), "Your rooms:");
    assertEquals(client.readLine(), "----------------");
    assertEquals(client.readLine(), roomName);
    assertEquals(client.readLine(), "----------------");
    assertEquals(client.readLine(), "Type the name of the room you want to remove or 0 to cancel");
    client.writeLine(roomName);
    assertEquals(client.readLine(), "You deleted the room '" + roomName + "!'");
  }

  public static int getAvailablePort() {
    int port;
    while (true) {
      port = ThreadLocalRandom.current().nextInt(1000, 9999);
      try (final ServerSocket socket = new ServerSocket(port)) {
        break;
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
    return port;
  }
}
