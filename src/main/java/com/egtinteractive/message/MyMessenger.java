package com.egtinteractive.message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.egtinteractive.chat.server.user.User;

public class MyMessenger implements Messenger {

  private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
  private final User user;

  public MyMessenger(final User user) {
    this.user = user;
  }

  @Override
  public void addMessage(final User creator, final String text) {
    final Message newMessage = new Message(user, creator, text);
    try {
      queue.put(newMessage);
    } catch (final InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        final Message msg = queue.take();
        msg.sendMessage();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void close() {
    Thread.currentThread().interrupt();
  }
}
