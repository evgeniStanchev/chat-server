package com.egtinteractive.chat.server.tests.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public final class Client {

  private final String name;
  private final OutputStream outputStream;
  private final Socket socket;
  private final PrintWriter out;
  private final InputStream inputStream;
  private final InputStreamReader reader;
  private final BufferedReader in;

  public Client(final String name, final List<Client> clients, final int port) {
    this.name = name;
    try {
      socket = new Socket("localhost", port);
      outputStream = socket.getOutputStream();
      out = new PrintWriter(outputStream, true);
      inputStream = socket.getInputStream();
      reader = new InputStreamReader(inputStream);
      in = new BufferedReader(reader);
      writeLine(this.name);
      clients.add(this);
    } catch (final IOException e) {
      shutdown();
      throw new RuntimeException(e);
    }
  }



  public String readLine() throws IOException {
    return in.readLine();
  }

  public void writeLine(final String line) {
    out.println(line);
  }

  public String getName() {
    return name;
  }

  public void shutdown() {
    try {
      in.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    try {
      reader.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    try {
      inputStream.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    out.close();
    try {
      outputStream.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    try {
      socket.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
