package fr.florent.irc.client.ircclient.receiver;

import java.util.regex.Matcher;

public class MessageReceiver implements IMessageReceiver {

    public interface IMessage {
        void onMessage(String sender, String channel, String message);
    }

    private IMessage action;

    public MessageReceiver(IMessage action) {
        this.action = action;
    }

    @Override
    public void message(Matcher match) {
        //:Florent PRIVMSG #defis :test
        String sender = match.group(1);
        String channel = match.group(2);
        String message = match.group(3);

        action.onMessage(sender, channel, message);
    }

    @Override
    public String getPattern() {
        return "^:([^\\s]+) PRIVMSG ([^\\s]+) :(.*)$";
    }
}
