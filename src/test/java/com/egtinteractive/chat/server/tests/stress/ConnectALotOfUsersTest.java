package com.egtinteractive.chat.server.tests.stress;

import static com.egtinteractive.chat.server.tests.resources.TestUtils.getAvailablePort;
import static com.egtinteractive.chat.server.tests.resources.TestUtils.initialize;
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

public class ConnectALotOfUsersTest {

  private static final int MIN_USER_COUNT = 100;
  private static final int MAX_USER_COUNT = 300;
  private static final int PORT = getAvailablePort();
  private static final int USER_COUNT =
      ThreadLocalRandom.current().nextInt(MIN_USER_COUNT, MAX_USER_COUNT);
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
  public void connectUsers() throws IOException {
    for (int i = 0; i < USER_COUNT; i++) {
      final Client client = new Client(UUID.randomUUID().toString(), clients, PORT);
      initialize(client);
    }
  }
}
