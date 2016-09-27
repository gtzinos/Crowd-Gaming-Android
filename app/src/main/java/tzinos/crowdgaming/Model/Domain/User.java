package tzinos.crowdgaming.Model.Domain;

import java.io.Serializable;

/**
 * Created by George on 2016-05-29.
 */
public class User implements Serializable {
    private String name;
    private String surname;
    private String email;
    private String apiTaken;

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApiTaken() {
        return apiTaken;
    }

    public void setApiTaken(String apiTaken) {
        this.apiTaken = apiTaken;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Default constructor
    public User() {
    }

    //Constructor
    public User(String name, String surname, String email, String apiTaken) {
        this(name, surname, apiTaken);
        this.email = email;
    }

    //Constructor
    public User(String name, String surname, String apiTaken) {
        this.name = name;
        this.surname = surname;
        this.apiTaken = apiTaken;
    }
}
