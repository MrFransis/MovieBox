package it.unipi.dii.dmml.moviebox.controller;

import it.unipi.dii.dmml.moviebox.model.Session;
import it.unipi.dii.dmml.moviebox.model.User;
import it.unipi.dii.dmml.moviebox.persistence.Neo4jDriver;
import it.unipi.dii.dmml.moviebox.persistence.Neo4jManager;
import it.unipi.dii.dmml.moviebox.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    private Neo4jManager neoMan;

    @FXML private TextField firstNameTf;
    @FXML private TextField lastNameTf;
    @FXML private Button loginButton;
    @FXML private PasswordField passwordTf;
    @FXML private Button signUPButton;
    @FXML private TextField usernameTf;
    @FXML private Label errorTf;

    public void initialize () {
        neoMan = new Neo4jManager(Neo4jDriver.getInstance().openConnection());
    }

    @FXML
    void checkForm(ActionEvent event) {
        String username = usernameTf.getText();
        if (neoMan.getUserByUsername(username) != null || passwordTf.getText().isEmpty() || firstNameTf.getText().isEmpty() || lastNameTf.getText().isEmpty()) {
            usernameTf.setText("");
            errorTf.setText("Username already used or missing data!");
            System.out.println("Username already used or missing data!");
            return;
        }

        User newUser = new User(username,  passwordTf.getText(), firstNameTf.getText(),
                lastNameTf.getText(),false);

        if (!neoMan.addUser(newUser)) {
            Utils.error();
            return;
        }
        Session.getInstance().setLoggedUser(newUser);
        Utils.changeScene("/it/unipi/dii/dmml/moviebox/layout/browser.fxml", event);
    }

    /**
     * Load login form
     * @param event
     */
    @FXML
    void loadLogin(ActionEvent event) {
        Utils.changeScene("/it/unipi/dii/dmml/moviebox/layout/login.fxml", event);
    }
}
