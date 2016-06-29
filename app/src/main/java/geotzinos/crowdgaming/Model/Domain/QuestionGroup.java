package geotzinos.crowdgaming.Model.Domain;

import java.util.List;

/**
 * Created by George on 2016-05-29.
 */
public class QuestionGroup {
    private String name;
    private double latitude;
    private double longitude;
    private int radius;
    private String creationDate;
    private List<Question> questionsList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
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

    public List<Question> getQuestionsList() {
        return questionsList;
    }

    public void setQuestionsList(List<Question> questionsList) {
        this.questionsList = questionsList;
    }

    //Default constructor
    public QuestionGroup() {
    }

    public QuestionGroup(String name, double latitude, double longitude, int radius, String creationDate) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.creationDate = creationDate;
    }

    public QuestionGroup(String name, double latitude, double longitude, int radius, String creationDate, List<Question> questionsList) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.creationDate = creationDate;
        this.questionsList = questionsList;
    }
}
