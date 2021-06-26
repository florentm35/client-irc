package fr.florent.irc.client.ircclient;

import fr.florent.irc.client.ircclient.receiver.MessageReceiverTranslator;
import fr.florent.irc.client.ircclient.receiver.PingReceiver;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IrcClient {

    private static final Logger LOGGER = Logger.getLogger(IrcClient.class.getName());

    private static IrcClient client = null;

    private Socket socket;

    private String user;

    private String channel;

    PrintWriter writer;

    BufferedReader reader;

    ExecutorService executor;

    private MessageReceiverTranslator translator;

    private IrcClient() {
        translator = new MessageReceiverTranslator();
        translator.addMessageReceiver(new PingReceiver());
    }

    public static IrcClient getClient() {
        if (client == null) {
            client = new IrcClient();
        }
        return client;
    }


    private synchronized void writeMessage(String message) {
        LOGGER.debug(message);
        writer.println(message);
    }

    public void close() throws IOException, InterruptedException {
        client = null;
        executor.shutdownNow();
        executor.awaitTermination(1, TimeUnit.SECONDS);
        writer.close();
        reader.close();
        socket.close();
    }

    public void connect(String host, String port, String name) {
        try {
            socket = new Socket(host, Integer.parseInt(port));
            OutputStream output = socket.getOutputStream();

            InputStream input = socket.getInputStream();

            executor = Executors.newSingleThreadExecutor();

            executor.execute(() ->
            {
                try {
                    reader(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


            this.writer = new PrintWriter(output, true);

            writeMessage("NICK " + name);
            writeMessage("USER " + name + " * * " + name);

            this.user = name;

        } catch (Exception e) {

        }
    }


    public void reader(InputStream input) throws IOException {
        reader = new BufferedReader(new InputStreamReader(input));
        String line;

        while ((line = reader.readLine()) != null) {
            translator.onMessage(line);
        }
    }

    public void sendMessage(String message) {
        sendMessage(channel, message);
    }

    public void sendMessage(String target, String message) {
        writeMessage(String.format("PRIVMSG %s :%s", target, message));
    }

    public void sendLeaveChannel() {
        sendLeaveChannel(channel);
    }

    public void sendLeaveChannel(String channel) {
        writeMessage("PART " + channel);
        this.channel = channel;
    }

    public void sendJoinChannel(String channel) {
        writeMessage("JOIN " + channel);
        this.channel = channel;
    }

    public void sendPong(String deamon) {
        writeMessage(String.format("PONG %s", deamon));
    }

    public void sendQuit() {
        sendQuit("Connection closed");
    }

    public void sendQuit(String message) {
        writeMessage(String.format("QUIT %s", message));
    }

    public MessageReceiverTranslator getTranslator() {
        return translator;
    }

    public String getUser() {
        return this.user;
    }

}
