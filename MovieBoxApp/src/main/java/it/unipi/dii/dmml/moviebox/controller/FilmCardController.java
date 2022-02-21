package it.unipi.dii.dmml.moviebox.controller;

import it.unipi.dii.dmml.moviebox.model.Film;
import it.unipi.dii.dmml.moviebox.model.Session;
import it.unipi.dii.dmml.moviebox.model.User;
import it.unipi.dii.dmml.moviebox.persistence.Neo4jDriver;
import it.unipi.dii.dmml.moviebox.persistence.Neo4jManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class FilmCardController implements Initializable {

    private Neo4jManager neoMan;
    private Film film;
    private User user;

    @FXML private ImageView imgIV;
    @FXML private Text titleTF;
    @FXML private Text overviewTF;
    @FXML private ScrollPane overviewSP;
    @FXML private Text genreTF;
    @FXML private Text dateTF;
    @FXML private Text languageTF;
    @FXML private Text castsTF;
    @FXML private Text companiesTF;
    @FXML private Text countryTF;
    @FXML private Button likebtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user = Session.getInstance().getLoggedUser();
        neoMan = new Neo4jManager(Neo4jDriver.getInstance().openConnection());
        overviewSP.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        likebtn.setOnMouseClicked(mouseEvent -> clickLike(mouseEvent));
    }

    public void setParameters(Film film) {
        this.film = film;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Image image = null;
        image = new Image("https://image.tmdb.org/t/p/w92/" + film.getPosterUrl());
        if (image.isError()){
            URL url = getClass().getResource("/it/unipi/dii/dmml/moviebox/img/"+ ThreadLocalRandom.current().nextInt(1, 11) +".jpg");
            image = new Image(String.valueOf(url));
        }

        imgIV.setImage(image);
        titleTF.setText(film.getTitle());
        overviewTF.setText(film.getPlot());
        genreTF.setText(film.getGenre());
        dateTF.setText(df.format(film.getRelease_date()));
        languageTF.setText(film.getLanguage());
        castsTF.setText(String.join(", ", film.getCasts(3)));
        companiesTF.setText(String.join(", ", film.getProduction_companies(2)));
        countryTF.setText(film.getProduction_country());

        if(neoMan.userLikeFilm(user, film))
            likebtn.setText("Unlike");
        else
            likebtn.setText("Like");
    }

    /**
     * Add User-LIKES-Films relationship to database
     * @param mouseEvent
     */
    private void clickLike (MouseEvent mouseEvent){
        if(Objects.equals(likebtn.getText(), "Like")){
            neoMan.likeFilm(user, film);
            likebtn.setText("UnLike");
        }else{
            neoMan.unlikeFilm(user, film);
            likebtn.setText("Like");
        }
    }
}
