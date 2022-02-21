package it.unipi.dii.dmml.moviebox.persistence;

import it.unipi.dii.dmml.moviebox.model.Film;
import it.unipi.dii.dmml.moviebox.model.User;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import static org.neo4j.driver.Values.parameters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Neo4jManager {

    Driver driver;

    public Neo4jManager(Driver driver) {
        this.driver = driver;
    }

    /**
     * Login with the given username and password
     * @param username Username of the target user
     * @param password Password of the target user
     * @return The object user if the login is done successfully, otherwise null
     */
    public User login(final String username, final String password) {
        User u = null;
        try (Session session = driver.session()) {
            u = session.readTransaction((TransactionWork<User>) tx -> {
                Result result = tx.run("MATCH (u:User) " +
                                "WHERE u.username = $username " +
                                "AND u.password = $password " +
                                "RETURN u.firstName AS firstName, u.lastName AS lastName, " +
                                "u.username AS username, u.password AS password, u.type AS type",
                        parameters("username", username, "password", password));
                User user = null;
                try {
                    Record r = result.next();
                    String firstName = r.get("firstName").asString();
                    String lastName = r.get("lastName").asString();
                    boolean type = r.get("type").asBoolean();
                    user = new User(username, password, firstName, lastName, type);
                } catch (NoSuchElementException ex) {
                    user = null;
                }
                return user;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    /**
     * Create a new User in graphDB
     * @param u new User
     * @return True if user creation was successful, otherwise null
     */
    public boolean addUser(User u) {
        boolean res = false;
        try (Session session = driver.session()) {
            res = session.writeTransaction((TransactionWork<Boolean>) tx -> {
                tx.run("CREATE (u:User {firstName: $firstName, lastName: $lastName, username: $username," +
                                "password: $password, type: $type})",
                        parameters("firstName", u.getFirstName(), "lastName", u.getLastName(), "username",
                                u.getUsername(), "password", u.getPassword(), "type", u.getType()));
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return res;
    }

    /**
     * Update a new User in graphDB
     * @param u User
     * @return True if user update was successful, otherwise null
     */
    public boolean updateUser(User u) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Boolean>) tx -> {
                tx.run("MATCH (u:User {username: $username}) " +
                                "SET u.firstName = $firstName, u.lastName = $lastName, u.password = $password",
                        parameters("username", u.getUsername(),
                                "firstName", u.getFirstName(),
                                "lastName", u.getLastName(),
                                "password", u.getPassword()));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Return a User by username
     * @param username Username of the user
     * @return The object user if the user is present in the database, otherwise null
     */
    public User getUserByUsername(String username) {
        {
            User u = null;
            try (Session session = driver.session()) {
                u = session.readTransaction((TransactionWork<User>) tx -> {
                    Result result = tx.run("MATCH (u:User) " +
                                    "WHERE u.username = $username " +
                                    "RETURN u.firstName AS firstName, u.lastName AS lastName, " +
                                    "u.username AS username, u.password AS password, u.type AS type",
                            parameters("username", username));
                    User user = null;
                    try {
                        Record r = result.next();
                        String password = r.get("password").asString();
                        String firstName = r.get("firstName").asString();
                        String lastName = r.get("lastName").asString();
                        boolean type = r.get("type").asBoolean();
                        user = new User(firstName, lastName, username, password, type);
                    } catch (NoSuchElementException ex) {
                        user = null;
                    }
                    return user;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return u;
        }
    }

    /**
     * Function that deletes a User from the GraphDB
     * @param u User to delete
     * @return True if the operation is done successfully, false otherwise
     */
    public boolean deleteUser(User u) {
        try(Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User) WHERE u.username = $username DETACH DELETE u",
                        parameters("username", u.getUsername()));
                return null;
            });
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create a new Film in graphDB
     * @param f new Film
     * @return True if user creation was successful, otherwise null
     */
    public boolean addFilm(Film f) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        boolean res = false;
        try (Session session = driver.session()) {
            res = session.writeTransaction((TransactionWork<Boolean>) tx -> {
                tx.run("MERGE (f:Film {tmdb_id: $id})" +
                                "ON CREATE " +
                                "SET " +
                                "f.title = $title , " +
                                "f.plot = $plot , " +
                                "f.genre = $genre, " +
                                "f.posterUrl = $posterUrl, " +
                                "f.language = $language, " +
                                "f.production_companies = $companies, " +
                                "f.production_country = $country, " +
                                "f.release_date = $date, " +
                                "f.casts = $casts " +
                                "ON MATCH " +
                                "SET " +
                                "f.title = $title , " +
                                "f.plot = $plot , " +
                                "f.genre = $genre, " +
                                "f.posterUrl = $posterUrl, " +
                                "f.language = $language, " +
                                "f.production_companies = $companies, " +
                                "f.production_country = $country, " +
                                "f.release_date = $date, " +
                                "f.casts = $casts ",
                        parameters("id", f.getTmdb_id(),
                                "title", f.getTitle(),
                                "plot", f.getPlot(),
                                "genre", f.getGenre(),
                                "posterUrl", f.getPosterUrl(),
                                "language", f.getLanguage(),
                                "companies", String.join(",", f.getProduction_companies()),
                                "country", f.getProduction_country(),
                                "date", df.format(f.getRelease_date()),
                                "casts", String.join(",",f.getCasts())));
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return res;
    }

    /**
     * Function that deletes a Film from the GraphDB
     * @param id Film_id to delete
     * @return True if the operation is done successfully, false otherwise
     */
    public boolean deleteFilm(String id) {
        try(Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (f:Film) WHERE f.tmdb_id = $id DETACH DELETE f",
                        parameters("id", id));
                return null;
            });
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Add a LIKES relationship between a User and a Film
     * @param user User object
     * @param film Film object
     */
    public void likeFilm (User user, Film film) {
        try (Session session = driver.session()){
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User {username:$user}), (f:Film {tmdb_id:$film_id}) " +
                                "MERGE (u)-[:LIKES]->(f) ",
                        parameters("user", user.getUsername(), "film_id", film.getTmdb_id()));
                return null;
            });
        }
    }

    /**
     * Remove the relationship of LIKES between a User and a Film
     * @param user User object
     * @param film Film object
     */
    public void unlikeFilm (User user, Film film) {
        try (Session session = driver.session()){
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (:User {username:$user})-[r:LIKES]-> (:Film {tmdb_id:$film_id}) " +
                                "DELETE r",
                        parameters("user", user.getUsername(), "film_id", film.getTmdb_id()));
                return null;
            });
        }
    }

    /**
     * Function that return true if exist a relation user-likes->film
     * @param user User object
     * @param film Film object
     */
    public boolean userLikeFilm (User user, Film film){
        boolean res = false;
        try(Session session = driver.session()){
            res = session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result r = tx.run("MATCH (:User{username:$user})-[r:LIKES]->(f:Film) " +
                        "WHERE (f.tmdb_id = $film_id) " +
                        "RETURN COUNT(*)",
                        parameters("user", user.getUsername(), "film_id", film.getTmdb_id()));
                Record record = r.next();
                if (record.get(0).asInt() == 0)
                    return false;
                else
                    return true;
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Function that return a list of films by parameters
     * @param keyword Keyword contained in the film title
     * @param limit Limit the number of films returned
     * @param skip Skip the first "n" films
     */
    public List<Film> getFilms (String keyword, int limit, int skip){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<Film> films;
        try (Session session = driver.session()) {
            films = session.writeTransaction((TransactionWork<List<Film>>) tx -> {
                Result result = tx.run("MATCH (f:Film) " +
                                "WHERE toLower(f.title) CONTAINS toLower($keyword) " +
                                "RETURN f.tmdb_id AS tmdb_id, " +
                                "f.title AS title, " +
                                "f.plot AS plot, " +
                                "f.genre AS genre, " +
                                "f.posterUrl AS posterUrl, " +
                                "f.casts AS casts, " +
                                "f.production_companies AS production_companies, " +
                                "f.release_date AS release_date, " +
                                "f.production_country AS production_country, " +
                                "f.language AS language ORDER BY title ASC " +
                                "SKIP $skip LIMIT $limit",
                        parameters("keyword", keyword, "limit", limit, "skip", skip));
                List<Film> filmList = new ArrayList<>();
                while(result.hasNext()) {
                    Record record = result.next();
                    Film snap = null;
                    try {
                        snap = new Film(record.get("tmdb_id").asString(),
                                record.get("title").asString(),
                                record.get("plot").asString(),
                                record.get("genre").asString(),
                                record.get("posterUrl").asString(),
                                Arrays.asList(record.get("casts").asString().split("\\s*,\\s*")),
                                Arrays.asList(record.get("production_companies").asString().split("\\s*,\\s*")),
                                df.parse(record.get("release_date").asString()),
                                record.get("production_country").asString(),
                                record.get("language").asString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    filmList.add(snap);
                }
                return filmList;
            });
        }
        return films;
    }

    /**
     * Function that return a list of liked by the user
     * @param u User
     * @param limit Limit the number of films returned
     * @param skip Skip the first "n" films
     */
    public List<Film> getSnapsOfLikedFilm (User u, int limit, int skip){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<Film> likeFilms;
        try (Session session = driver.session()) {
            likeFilms = session.writeTransaction((TransactionWork<List<Film>>) tx -> {
                Result result = tx.run("MATCH (u:User {username: $username})-[:LIKES]->(f:Film) " +
                                "RETURN f.tmdb_id AS tmdb_id, " +
                                "f.title AS title, " +
                                "f.plot AS plot, " +
                                "f.genre AS genre, " +
                                "f.posterUrl AS posterUrl, " +
                                "f.casts AS casts, " +
                                "f.production_companies AS production_companies, " +
                                "f.release_date AS release_date, " +
                                "f.production_country AS production_country, " +
                                "f.language AS language ORDER BY title ASC " +
                                "SKIP $skip LIMIT $limit",
                        parameters("username", u.getUsername(), "limit", limit, "skip", skip));
                List<Film> followedList = new ArrayList<>();
                while(result.hasNext()) {
                    Record record = result.next();
                    Film snap = null;
                    try {
                        snap = new Film(record.get("tmdb_id").asString(),
                                record.get("title").asString(),
                                record.get("plot").asString(),
                                record.get("genre").asString(),
                                record.get("posterUrl").asString(),
                                Arrays.asList(record.get("casts").asString().split("\\s*,\\s*")),
                                Arrays.asList(record.get("production_companies").asString().split("\\s*,\\s*")),
                                df.parse(record.get("release_date").asString()),
                                record.get("production_country").asString(),
                                record.get("language").asString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    followedList.add(snap);
                }
                return followedList;
            });
        }
        return likeFilms;
    }

    /**
     * Function that return a list of suggested film for the user
     * "The function suggest films retrieving all the genres liked by the user and searching the most
     * popular films by number of likes belonging to those genres."
     * @param u User
     * @param limit Limit the number of films returned
     * @param skip Skip the first "n" films
     */
    public List<Film> getSnapsOfSuggestedFilm (User u, int limit, int skip){
        List<Film> likeFilms;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try (Session session = driver.session()) {
            likeFilms = session.writeTransaction((TransactionWork<List<Film>>) tx -> {
                Result result = tx.run("MATCH (me:User {username: $username})-[:LIKES]->(f:Film), (target:Film)<-[l:LIKES]-() " +
                                "WHERE NOT EXISTS((me)-[:LIKES]->(target)) AND (target.genre = f.genre) " +
                                "RETURN target.tmdb_id AS tmdb_id, " +
                                "target.title AS title, " +
                                "target.plot AS plot, " +
                                "target.genre AS genre, " +
                                "target.posterUrl AS posterUrl, " +
                                "target.casts AS casts, " +
                                "target.production_companies AS production_companies, " +
                                "target.release_date AS release_date, " +
                                "target.production_country AS production_country, " +
                                "target.language AS language, " +
                                "COUNT(DISTINCT l) AS likes ORDER BY likes DESC, title ASC " +
                                "SKIP $skip LIMIT $limit",
                        parameters("username", u.getUsername(), "limit", limit, "skip", skip));
                List<Film> followedList = new ArrayList<>();
                while(result.hasNext()) {
                    Record record = result.next();
                    Film snap = null;
                    try {
                        snap = new Film(record.get("tmdb_id").asString(),
                                record.get("title").asString(),
                                record.get("plot").asString(),
                                record.get("genre").asString(),
                                record.get("posterUrl").asString(),
                                Arrays.asList(record.get("casts").asString().split("\\s*,\\s*")),
                                Arrays.asList(record.get("production_companies").asString().split("\\s*,\\s*")),
                                df.parse(record.get("release_date").asString()),
                                record.get("production_country").asString(),
                                record.get("language").asString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    followedList.add(snap);
                }
                return followedList;
            });
        }
        return likeFilms;
    }
}
