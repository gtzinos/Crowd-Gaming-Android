package geotzinos.crowdgaming.Model.Domain;

/**
 * Created by George on 2016-05-29.
 */
public class Answer {
    private int id;
    private String text;
    private String creationDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Answer(int id, String text, String creationDate) {
        this.id = id;
        this.text = text;
        this.creationDate = creationDate;
    }

}
