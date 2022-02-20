module it.unipi.dii.dmml.moviebox {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.neo4j.driver;

    opens it.unipi.dii.dmml.moviebox to javafx.fxml;
    exports it.unipi.dii.dmml.moviebox;
    opens it.unipi.dii.dmml.moviebox.controller to javafx.fxml;
    exports it.unipi.dii.dmml.moviebox.controller;
    opens it.unipi.dii.dmml.moviebox.persistence to javafx.fxml;
    exports it.unipi.dii.dmml.moviebox.persistence;
    opens it.unipi.dii.dmml.moviebox.model to javafx.fxml;
    exports it.unipi.dii.dmml.moviebox.model;
    exports it.unipi.dii.dmml.moviebox.classification;
    opens it.unipi.dii.dmml.moviebox.classification to javafx.fxml;
}