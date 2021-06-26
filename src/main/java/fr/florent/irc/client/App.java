package fr.florent.irc.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) throws IOException {
        Application.launch(args);


    }


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/view/client.fxml"));
        loader.load();

        Scene secondScene = new Scene(loader.getRoot(), 800, 600);
        Stage newWindow = new Stage();
        newWindow.setTitle("Client IRC");
        newWindow.setScene(secondScene);
        newWindow.show();
    }
}
