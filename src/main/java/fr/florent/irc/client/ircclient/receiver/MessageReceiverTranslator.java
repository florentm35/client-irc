package fr.florent.irc.client.ircclient.receiver;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageReceiverTranslator {

    private static final Logger LOGGER = Logger.getLogger(MessageReceiverTranslator.class.getName());

    private List<IMessageReceiver> receivers;

    public MessageReceiverTranslator() {
        this.receivers = new ArrayList<>();
    }

    public void onMessage(String message) {
        LOGGER.debug(message);
        for (IMessageReceiver receiver : receivers) {

            Pattern pattern = Pattern.compile(receiver.getPattern());

            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                receiver.message(matcher);
            }

        }
    }

    public void addMessageReceiver(IMessageReceiver receiver) {
        receivers.add(receiver);
    }

}
