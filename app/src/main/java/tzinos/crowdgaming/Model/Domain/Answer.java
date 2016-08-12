package tzinos.crowdgaming.Model.Domain;

import java.io.Serializable;

/**
 * Created by George on 2016-05-29.
 */
public class Answer implements Serializable {
    private long id;
    private String text;
    private String creationDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    //Default constructor
    public Answer() {
    }

    public Answer(long id, String text, String creationDate) {
        this.id = id;
        this.text = text;
        this.creationDate = creationDate;
    }

}
