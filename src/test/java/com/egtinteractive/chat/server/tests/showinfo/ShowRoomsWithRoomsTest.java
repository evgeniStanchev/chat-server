package com.egtinteractive.chat.server.tests.showinfo;

import static com.egtinteractive.chat.server.tests.resources.TestUtils.CMD_SHOW_ROOMS;
import static com.egtinteractive.chat.server.tests.resources.TestUtils.createRoom;
import static com.egtinteractive.chat.server.tests.resources.TestUtils.getAvailablePort;
import static com.egtinteractive.chat.server.tests.resources.TestUtils.initialize;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.egtinteractive.chat.server.server.MyServer;
import com.egtinteractive.chat.server.server.Server;
import com.egtinteractive.chat.server.tests.resources.Client;
import com.egtinteractive.chat.server.tests.resources.ServerRunner;

public class ShowRoomsWithRoomsTest {

  private static final int MIN_ROOM_COUNT = 5;
  private static final int MAX_ROOM_COUNT = 10;
  private static final int PORT = getAvailablePort();
  private static final int ROOM_COUNT =
      ThreadLocalRandom.current().nextInt(MIN_ROOM_COUNT, MAX_ROOM_COUNT);

  private final List<String> roomNames = new ArrayList<>();
  private final Server server = new MyServer(PORT);
  private final ServerRunner runner = new ServerRunner(server);
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final List<Client> clients = new ArrayList<>();

  @BeforeTest
  public void startTheServer() {
    executor.execute(runner);
    for (int i = 0; i < ROOM_COUNT; i++) {
      roomNames.add(UUID.randomUUID().toString());
    }
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
  public void showRoomsWithoutRoomsTest() throws IOException {
    final String userName = UUID.randomUUID().toString();
    final Client client = new Client(userName, clients, PORT);
    initialize(client);
    for (int i = 0; i < ROOM_COUNT; i++) {
      createRoom(client, roomNames.get(i));
    }
    client.writeLine(CMD_SHOW_ROOMS);
    assertEquals(client.readLine(), "Rooms on that server:");
    assertEquals(client.readLine(), "----------------");
    for (int i = 0; i < ROOM_COUNT; i++) {
      assertTrue(roomNames.contains(client.readLine()));
    }
  }


}
