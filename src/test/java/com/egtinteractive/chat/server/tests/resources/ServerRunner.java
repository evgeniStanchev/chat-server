package com.egtinteractive.chat.server.tests.resources;

import java.io.Closeable;
import com.egtinteractive.chat.server.server.Server;

public class ServerRunner implements Runnable, Closeable {

  private final Server server;

  public ServerRunner(final Server server) {
    this.server = server;
  }

  @Override
  public void run() {
    server.run();
  }

  @Override
  public void close() {
    try {
      server.close();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

}
