package geotzinos.crowdgaming.Model.Domain;

import java.util.ArrayList;

/**
 * Created by George on 2016-05-29.
 */
public class QuestionGroup {
    private long id;
    private String name;
    private String latitude;
    private String longitude;
    private String radius;
    private String creationDate;
    private ArrayList<Question> questionsList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public ArrayList<Question> getQuestionsList() {
        return questionsList;
    }

    public void setQuestionsList(ArrayList<Question> questionsList) {
        this.questionsList = questionsList;
    }

    //Default constructor
    public QuestionGroup() {
    }

    public QuestionGroup(long id, String name, String latitude, String longitude, String radius, String creationDate) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.creationDate = creationDate;
    }

    public QuestionGroup(long id, String name, String latitude, String longitude, String radius, String creationDate, ArrayList<Question> questionsList) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.creationDate = creationDate;
        this.questionsList = questionsList;
    }
}
