package fr.florent.irc.client.controleur;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnectionModal implements Initializable {

    interface IAction {
        boolean action(String host, String port, String name);
    }

    public TextField name;
    public TextField port;
    public TextField host;
    public Button btnConnection;


    public IAction action;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnConnection.setOnAction(e -> validateModal(e));

    }

    private void validateModal(ActionEvent e) {

        boolean close = false;

        if (action != null) {
            close = action.action(host.getText(), port.getText(), name.getText());
        }

        if (close) {
            Stage stage = getStage(e);
            stage.close();
        }

    }

    private Stage getStage(ActionEvent t) {
        Node source = (Node) t.getSource();
        return (Stage) source.getScene().getWindow();
    }

    public void setAction(IAction action) {
        this.action = action;
    }
}
