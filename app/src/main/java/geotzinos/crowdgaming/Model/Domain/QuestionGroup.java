package geotzinos.crowdgaming.Model.Domain;

import java.util.ArrayList;

/**
 * Created by George on 2016-05-29.
 */
public class QuestionGroup {
    private int id;
    private String name;
    private String latitude;
    private String longitude;
    private int radius;
    private String creationDate;
    private ArrayList<Question> questionsList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
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

    public QuestionGroup(int id, String name, String latitude, String longitude, int radius, String creationDate) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.creationDate = creationDate;
    }

    public QuestionGroup(int id, String name, String latitude, String longitude, int radius, String creationDate, ArrayList<Question> questionsList) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.creationDate = creationDate;
        this.questionsList = questionsList;
    }
}
