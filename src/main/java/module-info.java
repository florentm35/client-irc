module editor {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.base;
    requires log4j;

    exports fr.florent.irc.client.controleur;

    opens fr.florent.irc.client;
}