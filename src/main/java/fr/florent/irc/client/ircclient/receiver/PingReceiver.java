package fr.florent.irc.client.ircclient.receiver;

import fr.florent.irc.client.ircclient.IrcClient;

import java.util.regex.Matcher;

public class PingReceiver implements IMessageReceiver {


    @Override
    public void message(Matcher match) {
        String deamon = match.group(1);
        IrcClient.getClient().sendPong(deamon);
    }

    @Override
    public String getPattern() {
        return "^PING (.*)$";
    }


}
