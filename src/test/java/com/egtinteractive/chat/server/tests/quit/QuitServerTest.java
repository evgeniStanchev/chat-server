package com.egtinteractive.chat.server.tests.quit;

import static com.egtinteractive.chat.server.tests.resources.TestUtils.CMD_QUIT;
import static com.egtinteractive.chat.server.tests.resources.TestUtils.getAvailablePort;
import static com.egtinteractive.chat.server.tests.resources.TestUtils.initialize;
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

public class QuitServerTest {

  private final static int PORT = getAvailablePort();

  private final String userName = UUID.randomUUID().toString();
  private final Server server = new MyServer(PORT);
  private final ServerRunner runner = new ServerRunner(server);
  private final ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
  private final List<Client> clients = new ArrayList<>();


  @BeforeTest
  public void startTheServer() {
    serverExecutor.execute(runner);
  }

  @AfterTest
  public void shutdown() {
    for (Client client : clients) {
      client.shutdown();
    }
    try {
      serverExecutor.shutdownNow();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void quitServerTest() throws IOException {
    final Client client = new Client(userName, clients, PORT);
    initialize(client);
    client.writeLine(CMD_QUIT);
    assertEquals(client.readLine(), "exiting the chat server..");
  }
}
