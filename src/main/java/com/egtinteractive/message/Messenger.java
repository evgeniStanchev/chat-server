package com.egtinteractive.message;

import java.io.Closeable;
import com.egtinteractive.chat.server.user.User;

public interface Messenger extends Runnable, Closeable {

  @Override
  void run();

  @Override
  void close();

  void addMessage(final User creator, final String text);

}
