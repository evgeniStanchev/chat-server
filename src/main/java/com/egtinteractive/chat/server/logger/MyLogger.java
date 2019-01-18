package com.egtinteractive.chat.server.logger;

import java.io.IOException;
import java.io.OutputStream;

public final class MyLogger implements Logger {

  private final OutputStream outputStream;

  public MyLogger(final OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  @Override
  public void writeMsg(final String msg) {
    try {
      outputStream.write((msg + System.lineSeparator()).getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void showOptions() {
    StringBuffer sb = new StringBuffer();
    sb.append("COMMANDS:").append(System.lineSeparator())
        .append("'create room'--> creates a new room.            ").append(System.lineSeparator())
        .append("'show rooms' --> show all rooms.                ").append(System.lineSeparator())
        .append("'join room'  --> join some room.                ").append(System.lineSeparator())
        .append("'remove room'--> remove some room.              ").append(System.lineSeparator())
        .append("'leave'      --> leave the curent room.         ").append(System.lineSeparator())
        .append("'show room'  --> show the current room.         ").append(System.lineSeparator())
        .append("'show users' --> show users in the current room.").append(System.lineSeparator())
        .append("'help'       --> show the options.              ").append(System.lineSeparator())
        .append("'quit'       --> quit the server.               ").append(System.lineSeparator());
    writeMsg(sb.toString());
  }

}
