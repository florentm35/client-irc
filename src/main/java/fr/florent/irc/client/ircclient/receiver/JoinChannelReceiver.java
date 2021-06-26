package fr.florent.irc.client.ircclient.receiver;

import java.util.regex.Matcher;

public class JoinChannelReceiver implements IMessageReceiver {

    public interface IJoinChannel {
        void onMessage(String user, String channel, String[] userList);
    }

    private IJoinChannel action;

    public JoinChannelReceiver(IJoinChannel action) {
        this.action = action;
    }


    @Override
    public void message(Matcher match) {
        String user = match.group(1);
        String channel = match.group(2);
        String[] users = match.group(3).split(" ");

        action.onMessage(user, channel, users);
    }

    @Override
    public String getPattern() {
        return "^:[^\\s]+ [^\\s]+ ([^\\s]*) = ([^\\s]*) :(.*)$";
    }


}
