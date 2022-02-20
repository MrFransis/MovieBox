package it.unipi.dii.dmml.moviebox.controller;

import it.unipi.dii.dmml.moviebox.model.Session;
import it.unipi.dii.dmml.moviebox.model.User;
import it.unipi.dii.dmml.moviebox.persistence.Neo4jDriver;
import it.unipi.dii.dmml.moviebox.persistence.Neo4jManager;
import it.unipi.dii.dmml.moviebox.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private Neo4jManager neoMan;

    @FXML private Button loginButton;
    @FXML private PasswordField passwordTf;
    @FXML private Button registerButton;
    @FXML private TextField usernameTf;
    @FXML private Label errorTf;

    public void initialize () {
        neoMan = new Neo4jManager(Neo4jDriver.getInstance().openConnection());
    }

    @FXML
    void checkCredential(ActionEvent event) {
        String username = usernameTf.getText();
        String password = passwordTf.getText();

        User u = neoMan.login(username, password);

        if (u == null) {
            usernameTf.setText("");
            passwordTf.setText("");
            errorTf.setText("Username or password not valid.");
            System.out.println("Username or password not valid.");
        } else {
            Session.getInstance().setLoggedUser(u);
            Utils.changeScene("/it/unipi/dii/dmml/moviebox/layout/browser.fxml", event);
        }
    }

    /**
     * Load register form
     * @param event
     */
    @FXML
    void loadRegisterForm(ActionEvent event) {
        Utils.changeScene("/it/unipi/dii/dmml/moviebox/layout/register.fxml", event);
    }

}
