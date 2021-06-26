package fr.florent.irc.client.controleur;

import fr.florent.irc.client.App;
import fr.florent.irc.client.ircclient.IrcClient;
import fr.florent.irc.client.ircclient.receiver.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ClientControler implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ClientControler.class.getName());

    public VBox userList;
    public VBox messageList;
    public Button sendBtn;
    public TextField messageBox;
    public MenuItem connectionMenu;

    private IrcClient client;

    List<String> users = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sendBtn.setOnAction((e) -> sendMessageAction());
        messageBox.setOnAction((e) -> sendMessageAction());

        connectionMenu.setOnAction(e -> openCOnnectionModal());

    }

    public void addUser(List<String> users) {
        this.users.addAll(users);
        refreshUser();
    }

    public void addUser(String user) {
        users.add(user);
        refreshUser();
    }

    public void removeUser(String user) {
        this.users.remove(user);
        refreshUser();
    }

    public void clearUser() {
        users.clear();
        Platform.runLater(() -> userList.getChildren().clear());
    }

    private void refreshUser() {
        Platform.runLater(() -> userList.getChildren().clear());
        List<String> copy = new ArrayList<>(users);
        for (String user : copy) {
            Platform.runLater(() -> userList.getChildren().add(new Text(user)));
        }

    }


    private void openCOnnectionModal() {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/view/connexionModal.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException();
        }

        ConnectionModal modal = loader.getController();
        modal.setAction(this::connect);

        Scene secondScene = new Scene(loader.getRoot());
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Connexion");
        dialog.setScene(secondScene);
        dialog.show();

    }

    public boolean connect(String host, String port, String name) {

        this.client = IrcClient.getClient();
        client.connect(host, port, name);
        initReceiver();

        client.sendJoinChannel("#defis");

        return true;
    }

    private void initReceiver() {

        client.getTranslator().addMessageReceiver(new JoinChannelReceiver(this::onJoinChannel));
        client.getTranslator().addMessageReceiver(new MessageReceiver(this::onMessage));
        client.getTranslator().addMessageReceiver(new JoinReceiver(this::onJoin));
        client.getTranslator().addMessageReceiver(new QuitReceiver(this::onQuit));

    }

    private void onQuit(String user, String reason) {
        addMessage(String.format("%s as quit the channel(%s)", user, reason));
        removeUser(user);
    }

    private void onJoin(String user, String channel) {
        addMessage(String.format("%s as join the channel", user));
        addUser(user);
    }

    private void onMessage(String sender, String channel, String message) {
        addMessage(String.format("%s : %s", sender, message));
    }

    private void onJoinChannel(String user, String channel, String[] users) {
        addMessage(String.format("You join %s", channel));
        clearUser();
        addUser(Arrays.asList(users));
    }


    private void addMessage(String message) {
        Platform.runLater(() -> {
            Text textMessage = new Text(message);
            messageList.getChildren().add(textMessage);
        });

    }

    private void sendMessageAction() {

        if (this.client != null) {


            String text = messageBox.getText();
            if (text != null && !text.isEmpty()) {

                if (text.startsWith("/")) {
                    String command = text.split(" |\r\n")[0];
                    int index = text.indexOf(" ");
                    switch (command) {
                        case "/quit":
                        case "/q":
                            quitCommand(text, index);
                            break;
                        case "/join":
                        case "/j":
                            joinCommand(text, index);
                            break;
                        case "/leave":
                        case "/l":
                            leaveCommand(text, index);
                            break;
                        case "/msg":
                            privateMessageCommand(text, index);
                            break;
                        default:
                            addMessage(String.format("Invalid commande : %s", text));
                    }

                } else {
                    client.sendMessage(text);
                    addMessage(client.getUser() + " : " + text);
                }
            }
        } else {
            addMessage("Not connected");
        }
        messageBox.setText("");
    }

    private void leaveCommand(String text, int index) {
        if (index < 0) {
            client.sendLeaveChannel();
        } else {
            client.sendLeaveChannel(text.substring(index));
        }
    }

    private void privateMessageCommand(String text, int index) {
        String param = text.substring(index);
        int indexTarget = param.indexOf(" ", index);

        String user = param.substring(0, indexTarget);
        String message = param.substring(indexTarget);

        client.sendMessage(
                user, message);
        addMessage(String.format("%s -> %s : %s", client.getUser(), user, message));
    }

    private void joinCommand(String text, int index) {
        String channel = text.substring(index);
        client.sendLeaveChannel();
        client.sendJoinChannel(channel);
    }

    private void quitCommand(String text, int index) {
        if (index < 0) {
            client.sendQuit();
        } else {
            client.sendQuit(text.substring(index));
        }
        userList.getChildren().clear();
        messageList.getChildren().clear();
        addMessage("Connection closed");

        try {
            client.close();
            client = null;
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
