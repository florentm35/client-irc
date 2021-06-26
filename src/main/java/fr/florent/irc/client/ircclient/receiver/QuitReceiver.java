package fr.florent.irc.client.ircclient.receiver;

import java.util.regex.Matcher;

public class QuitReceiver implements IMessageReceiver {

    public interface IQuit {
        void onMessage(String user, String reason);
    }

    private IQuit action;

    public QuitReceiver(IQuit action) {
        this.action = action;
    }

    @Override
    public void message(Matcher match) {
        //:Florent QUIT :Connection reset by peer
        String user = match.group(1);
        String reason = match.group(2);

        action.onMessage(user, reason);
    }

    @Override
    public String getPattern() {
        return "^:([^\\s]+) QUIT :(.*)$";
    }
}
