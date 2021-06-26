package fr.florent.irc.client.ircclient.receiver;

import java.util.regex.Matcher;

public interface IMessageReceiver {
    void message(Matcher match);
    public String getPattern();
}
