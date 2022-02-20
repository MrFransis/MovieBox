package it.unipi.dii.dmml.moviebox.controller;

import it.unipi.dii.dmml.moviebox.model.Film;
import it.unipi.dii.dmml.moviebox.classification.ClassifierDriver;
import it.unipi.dii.dmml.moviebox.persistence.Neo4jDriver;
import it.unipi.dii.dmml.moviebox.persistence.Neo4jManager;
import it.unipi.dii.dmml.moviebox.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertFilmController {

    private Neo4jManager neoMan;
    ClassifierDriver cd;
    @FXML private TextField titleTF;
    @FXML private TextField idTF;
    @FXML private TextField urlTF;
    @FXML private TextField languageTF;
    @FXML private TextField castsTF;
    @FXML private DatePicker dateTF;
    @FXML private TextField plotTF;
    @FXML private TextField companiesTF;
    @FXML private TextField countryTF;
    @FXML private Label errorTF;
    @FXML private ComboBox<String> chooseGenre;
    @FXML private Button classifyBT;
    @FXML private Button insertBT;
    @FXML private Button backButton;
    @FXML private Button removeBT;
    @FXML private TextField removeTF;

    public void initialize() {
        neoMan = new Neo4jManager(Neo4jDriver.getInstance().openConnection());
        cd = new ClassifierDriver();
        backButton.setOnMouseClicked(mouseEvent -> clickOnBackBtn(mouseEvent));
        classifyBT.setOnMouseClicked(mouseEvent -> {
            try {
                clickOnClassifyBtn(mouseEvent);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        removeBT.setOnMouseClicked(mouseEvent -> clickOnRemoveBtn(mouseEvent));
        insertBT.setOnMouseClicked(mouseEvent -> {
            try {
                clickOnInsertIcon(mouseEvent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        loadComboBox();
    }

    private void loadComboBox(){
        List<String> suggestionList = new ArrayList<>();
        suggestionList.add("Animation");
        suggestionList.add("Comedy");
        suggestionList.add("Action");
        suggestionList.add("Drama");
        suggestionList.add("Crime");
        suggestionList.add("Horror");
        suggestionList.add("Documentary");
        ObservableList<String> observableListSuggestion = FXCollections.observableList(suggestionList);
        chooseGenre.getItems().clear();
        chooseGenre.setItems(observableListSuggestion);
    }

    private void clickOnClassifyBtn(MouseEvent mouseEvent) throws IOException, URISyntaxException {
        if(plotTF.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Enter a plot!");
            alert.showAndWait();
            return;
        }
        classifyBT.setDisable(true);
        FileWriter myWriter = new FileWriter(getClass().getResource("/it/unipi/dii/dmml/moviebox/data/plot.txt").toURI().getPath());
        myWriter.write(plotTF.getText());
        myWriter.close();
        ClassifierDriver cd = new ClassifierDriver();
        String genre = cd.getMovieGenre(plotTF.getText());
        if (genre == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Service unavailable!");
            alert.showAndWait();
        }
        chooseGenre.setValue(genre);
        classifyBT.setDisable(false);
    }

    private void clickOnInsertIcon(MouseEvent mouseEvent) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        errorTF.setText("");
        if (titleTF.getText().equals("") ||
                idTF.getText().equals("") ||
                urlTF.getText().equals("") ||
                plotTF.getText().equals("") ||
                castsTF.getText().equals("") ||
                companiesTF.getText().equals("") ||
                dateTF.getValue() == null ||
                countryTF.getText().equals("") ||
                languageTF.getText().equals("") ||
                (chooseGenre.getValue() == null || chooseGenre.getValue().equals("Genre"))) {
            errorTF.setText("Missing fields!");
            return;
        }else{
            neoMan.addFilm(new Film(idTF.getText(),
                    titleTF.getText(),
                    plotTF.getText(),
                    chooseGenre.getValue(),
                    urlTF.getText(),
                    Arrays.asList(castsTF.getText().split("\\s*,\\s*")),
                    Arrays.asList(companiesTF.getText().split("\\s*,\\s*")),
                    df.parse(dateTF.getValue().toString()),
                    countryTF.getText(),
                    languageTF.getText()));
        }
        idTF.setText("");
        titleTF.setText("");
        plotTF.setText("");
        urlTF.setText("");
        castsTF.setText("");
        companiesTF.setText("");
        dateTF.setValue(null);
        countryTF.setText("");
        languageTF.setText("");
        chooseGenre.getSelectionModel().clearSelection();
    }

    private void clickOnRemoveBtn(MouseEvent mouseEvent) {
        if (!removeTF.getText().equals("")){
            neoMan.deleteFilm(removeTF.getText());
            removeTF.setText("");
        }
    }

    private void clickOnBackBtn(MouseEvent mouseEvent) {
        Utils.changeScene("/it/unipi/dii/dmml/moviebox/layout/browser.fxml", mouseEvent);
    }
}
