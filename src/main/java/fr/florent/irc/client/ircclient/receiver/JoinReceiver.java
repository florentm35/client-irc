package fr.florent.irc.client.ircclient.receiver;

import java.util.regex.Matcher;

public class JoinReceiver implements IMessageReceiver {

    public interface IJoin {
        void onMessage(String user, String channel);
    }

    private IJoin action;

    public JoinReceiver(IJoin action) {
        this.action = action;
    }

    @Override
    public void message(Matcher match) {
        //:Florent JOIN #defis
        String user = match.group(1);
        String channel = match.group(2);

        action.onMessage(user, channel);
    }

    @Override
    public String getPattern() {
        return "^:([^\\s]+) JOIN ([^\\s]+)$";
    }
}
