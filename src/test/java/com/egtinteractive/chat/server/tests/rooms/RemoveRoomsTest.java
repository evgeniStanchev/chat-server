package com.egtinteractive.chat.server.tests.rooms;

import static com.egtinteractive.chat.server.tests.resources.TestUtils.CMD_JOIN_ROOM;
import static com.egtinteractive.chat.server.tests.resources.TestUtils.createRoom;
import static com.egtinteractive.chat.server.tests.resources.TestUtils.getAvailablePort;
import static com.egtinteractive.chat.server.tests.resources.TestUtils.initialize;
import static com.egtinteractive.chat.server.tests.resources.TestUtils.removeRoom;
import static org.testng.Assert.assertEquals;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.egtinteractive.chat.server.server.MyServer;
import com.egtinteractive.chat.server.server.Server;
import com.egtinteractive.chat.server.tests.resources.Client;
import com.egtinteractive.chat.server.tests.resources.ServerRunner;

public class RemoveRoomsTest {

  private final static int PORT = getAvailablePort();

  private final Server server = new MyServer(PORT);
  private final ServerRunner runner = new ServerRunner(server);
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final List<Client> clients = new ArrayList<>();

  @BeforeTest
  public void startTheServer() {
    executor.execute(runner);
  }

  @AfterTest
  public void shutdown() {
    for (Client client : clients) {
      client.shutdown();
    }
    try {
      executor.shutdownNow();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void removeRoomTest() throws IOException {
    final String roomName = UUID.randomUUID().toString();
    final String userName = UUID.randomUUID().toString();
    final Client client = new Client(userName, clients, PORT);
    initialize(client);
    createRoom(client, roomName);
    removeRoom(roomName, client);
  }

  @Test
  public void incorrectRemoveRoomTest() throws IOException {
    final String userName = UUID.randomUUID().toString();
    final String roomName = UUID.randomUUID().toString();
    final Client client = new Client(userName, clients, PORT);
    initialize(client);
    createRoom(client, roomName);
    removeRoom(roomName, client);
  }

  @Test
  public void removeRoomWhileInTheRoom() throws IOException {
    final String userName = UUID.randomUUID().toString();
    final String roomName = UUID.randomUUID().toString();
    final Client client = new Client(userName, clients, PORT);
    initialize(client);
    createRoom(client, roomName);
    client.writeLine(CMD_JOIN_ROOM);
    assertEquals(client.readLine(), "Rooms on that server:");
    assertEquals(client.readLine(), "----------------");
    assertEquals(client.readLine(), roomName);
    assertEquals(client.readLine(), "----------------");
    client.writeLine(roomName);
    assertEquals(client.readLine(), "Welcome to room:" + roomName);
    removeRoom(roomName, client);
    client.writeLine(UUID.randomUUID().toString());
    assertEquals(client.readLine(), "You need to enter some room before you write messages!");
  }

}
