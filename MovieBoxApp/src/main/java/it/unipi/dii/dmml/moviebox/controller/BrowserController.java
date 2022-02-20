package it.unipi.dii.dmml.moviebox.controller;

import it.unipi.dii.dmml.moviebox.model.Film;
import it.unipi.dii.dmml.moviebox.model.Session;
import it.unipi.dii.dmml.moviebox.model.User;
import it.unipi.dii.dmml.moviebox.persistence.Neo4jDriver;
import it.unipi.dii.dmml.moviebox.persistence.Neo4jManager;
import it.unipi.dii.dmml.moviebox.utils.Utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrowserController {

    private Neo4jManager neoMan;
    private User user;
    private int page;
    private boolean filmsrs;

    @FXML private ComboBox<String> chooseType;
    @FXML private TextField keywordTf;
    @FXML private Button insertBt;
    @FXML private GridPane cardsGrid;
    @FXML private Button backBt;
    @FXML private Button forwardBt;
    @FXML private Button profileButton;
    @FXML private Button logoutButton;


    public void initialize() {
        user = Session.getInstance().getLoggedUser();
        neoMan = new Neo4jManager(Neo4jDriver.getInstance().openConnection());
        insertBt.setOnMouseClicked(mouseEvent -> clickOnBackBtn(mouseEvent));
        profileButton.setOnMouseClicked(mouseEvent -> profilePage(mouseEvent));
        logoutButton.setOnMouseClicked(mouseEvent -> logout(mouseEvent));
        forwardBt.setOnMouseClicked(mouseEvent -> goForward());
        backBt.setOnMouseClicked(mouseEvent -> goBack());
        backBt.setDisable(true);
        forwardBt.setDisable(true);
        filmsrs = false;
        if(!user.getType()){
            insertBt.setVisible(false);
        }
        loadComboBox();
    }

    private void logout(MouseEvent event) {
        Utils.changeScene("/it/unipi/dii/dmml/moviebox/layout/login.fxml", event);
    }

    private void clickOnBackBtn(MouseEvent mouseEvent) {
        Utils.changeScene("/it/unipi/dii/dmml/moviebox/layout/insertFilm.fxml", mouseEvent);
    }

    @FXML
    void showOption() {
        page = 0;
        backBt.setDisable(true);
        forwardBt.setDisable(true);
    }

    @FXML
    void startResearch() {
        forwardBt.setDisable(false);
        backBt.setDisable(true);
        page = 0;
        filmsrs = false;
        handleResearch();
    }

    @FXML
    void startFilmSearch() {
        forwardBt.setDisable(false);
        backBt.setDisable(true);
        page = 0;
        filmsrs = true;
        handleFilmResearch();
    }

    private void handleResearch(){
        List<Film> filmsList;
        filmsrs = false;
        if(chooseType.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Select an option!");
            alert.showAndWait();
        }else{
            switch (chooseType.getValue()) {
                case "My Films" -> {
                    System.out.println("Load my films");
                    filmsList = neoMan.getSnapsOfLikedFilm(user, 4, 4*page);
                    System.out.println(filmsList);
                    fillFilm(filmsList);
                }
                case "Suggestions" -> {
                    System.out.println("Load suggest films");
                    filmsList = neoMan.getSnapsOfSuggestedFilm(user, 4, 4*page);
                    System.out.println(filmsList);
                    fillFilm(filmsList);
                }
            }
        }
    }

    private void handleFilmResearch(){
        List<Film> filmsList;
        System.out.println(page);
        filmsList = neoMan.getFilms(keywordTf.getText(), 4, 4*page);
        fillFilm(filmsList);
    }

    void profilePage(MouseEvent event) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField firstName = new TextField(Session.getInstance().getLoggedUser().getFirstName());
        firstName.setPromptText("First Name");
        TextField lastName = new TextField(Session.getInstance().getLoggedUser().getLastName());
        lastName.setPromptText("Last Name");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        dialogPane.setContent(new VBox(8, firstName, lastName, password));
        Platform.runLater(firstName::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                String pass;
                if(password.getText().isEmpty())
                    pass = Session.getInstance().getLoggedUser().getPassword();
                else
                    pass = password.getText();
                return new User(Session.getInstance().getLoggedUser().getUsername(),
                        pass,
                        firstName.getText(),
                        lastName.getText(),
                        Session.getInstance().getLoggedUser().getType());
            }
            return null;
        });
        Optional<User> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((User u) -> {
            if (!neoMan.updateUser(u)) {
                Utils.error();
                return;
            }
            Session.getInstance().setLoggedUser(u);
        });
    }

    private void loadComboBox(){
        List<String> suggestionList = new ArrayList<>();
        suggestionList.add("My Films");
        suggestionList.add("Suggestions");
        ObservableList<String> observableListSuggestion = FXCollections.observableList(suggestionList);
        chooseType.getItems().clear();
        chooseType.setItems(observableListSuggestion);
    }

    private void fillFilm(List<Film> filmsList){
        setGridFilm();
        forwardBt.setDisable(false);
        if (filmsList.size() < 4)
            forwardBt.setDisable(true);
        int row = 0;
        for (Film f : filmsList) {
            Pane card = loadFilmCard(f);
            cardsGrid.add(card, 0, row);
            row++;
        }
    }

    private void setGridFilm() {
        cleanGrid();
        cardsGrid.setAlignment(Pos.CENTER);
        cardsGrid.setVgap(0);
        //cardsGrid.setPadding(new Insets(1,1,1,1));
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setPercentWidth(100);
        cardsGrid.getColumnConstraints().add(constraints);
    }

    private void cleanGrid() {
        cardsGrid.getColumnConstraints().clear();
        while (cardsGrid.getChildren().size() > 0) {
            cardsGrid.getChildren().remove(0);
        }
    }

    private void goForward () {
        page++;
        backBt.setDisable(false);
        if (filmsrs)
            handleFilmResearch();
        else
            handleResearch();
    }

    private void goBack () {
        page--;
        if (page <= 0) {
            page = 0;
            backBt.setDisable(true);
        }
        forwardBt.setDisable(false);
        if (filmsrs)
            handleFilmResearch();
        else
            handleResearch();
    }

    private Pane loadFilmCard(Film film){
        Pane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unipi/dii/dmml/moviebox/layout/filmCard.fxml"));
            pane = loader.load();
            FilmCardController ctrl = loader.getController();
            ctrl.setParameters(film);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return pane;
    }
}
