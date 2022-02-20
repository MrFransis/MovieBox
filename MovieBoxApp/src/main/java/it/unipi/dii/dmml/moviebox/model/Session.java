package it.unipi.dii.dmml.moviebox.model;

public class Session {
    private static Session instance = null;
    private User loggedUser;

    public static Session getInstance() {
        if(instance==null)
            instance = new Session();
        return instance;
    }

    private Session () {
    }

    public static void resetInstance() {
        instance = null;
    }

    public void setLoggedUser(User u) {
        instance.loggedUser = u;
    }
    public User getLoggedUser() {
        return loggedUser;
    }
}
