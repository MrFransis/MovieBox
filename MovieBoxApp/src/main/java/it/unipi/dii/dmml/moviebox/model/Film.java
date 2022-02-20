package it.unipi.dii.dmml.moviebox.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Film {
    private String tmdb_id;
    private String title;
    private String plot;
    private String genre;
    private String posterUrl;
    private List<String> casts;
    private List<String> production_companies;
    private Date release_date;
    private String production_country;
    private String language;

    public Film(String tmdb_id, String title, String plot, String genre, String posterUrl, List<String> casts, List<String> production_companies, Date date, String production_country, String language) {
        this.tmdb_id = tmdb_id;
        this.title = title;
        this.plot = plot;
        this.genre = genre;
        this.posterUrl = posterUrl;
        this.casts = casts;
        this.production_companies = production_companies;
        this.release_date = date;
        this.production_country = production_country;
        this.language = language;
    }

    public String getTmdb_id() { return tmdb_id; }

    public String getTitle() {
        return title;
    }

    public String getPlot() {
        return plot;
    }

    public String getGenre() {
        return genre;
    }

    public String getPosterUrl() { return posterUrl; }

    public List<String> getCasts() { return casts; }

    public List<String> getCasts(int limit) { return casts.stream().limit(limit).collect(Collectors.toList()); }

    public List<String> getProduction_companies() { return production_companies; }

    public List<String> getProduction_companies(int limit) { return production_companies.stream().limit(limit).collect(Collectors.toList()); }

    public Date getRelease_date() { return release_date; }

    public String getProduction_country() { return production_country; }

    public String getLanguage() { return language; }

    public void setImdb_id(String imdb_id) {
        this.tmdb_id = imdb_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setPostUrl(String postUrl) {
        this.posterUrl = postUrl;
    }

    public void setCasts(List<String> casts) { this.casts = casts; }

    public void setProduction_companies(List<String> production_companies) { this.production_companies = production_companies; }

    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
    }

    public void setProduction_country(String production_country) {
        this.production_country = production_country;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "Film{" +
                "title='" + title + '\'' +
                ", plot='" + plot + '\'' +
                ", genre='" + genre + '\'' +
                ", imdb_id='" + tmdb_id + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", casts='" + casts + '\'' +
                ", production_companies='" + production_companies + '\'' +
                ", release_date='" + release_date + '\'' +
                ", production_country='" + production_country + '\'' +
                ", language='" + language + '\'' +
                '}'+ '\n';
    }
}
