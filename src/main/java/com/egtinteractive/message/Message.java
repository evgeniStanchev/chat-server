package com.egtinteractive.message;

import com.egtinteractive.chat.server.user.User;

public final class Message {

  private final User user;
  private final User creator;
  private final String text;

  public Message(final User user, final User creator, final String text) {
    this.user = user;
    this.creator = creator;
    this.text = text;
  }

  public void sendMessage() {
    if (user != creator) {
      user.getLogger().writeMsg(creator.getName() + ":" + text);
    }
  }

}
