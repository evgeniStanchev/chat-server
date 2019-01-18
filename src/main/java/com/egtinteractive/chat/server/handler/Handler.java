package com.egtinteractive.chat.server.handler;

import java.io.BufferedReader;

public interface Handler {

  void handleRequest(BufferedReader reader, String line);

}
