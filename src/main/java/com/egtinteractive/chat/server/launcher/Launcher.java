package com.egtinteractive.chat.server.launcher;

import com.egtinteractive.chat.server.server.MyServer;
import com.egtinteractive.chat.server.server.Server;

public final class Launcher {

  private static final int PORT = 8888;

  public static void main(String[] args) {
    try (final Server server = new MyServer(PORT)) {
      server.run();
    }
  }
}
