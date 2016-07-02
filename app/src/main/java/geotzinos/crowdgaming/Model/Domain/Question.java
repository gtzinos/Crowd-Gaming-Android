package geotzinos.crowdgaming.Model.Domain;

import java.util.ArrayList;

/**
 * Created by George on 2016-05-29.
 */
public class Question {
    private long id;
    private String text;
    private double weight;
    private String creationDate;
    private double timeToAnswer;
    private ArrayList<Answer> answersList;

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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public double getTimeToAnswer() {
        return timeToAnswer;
    }

    public void setTimeToAnswer(double timeToAnswer) {
        this.timeToAnswer = timeToAnswer;
    }

    public ArrayList<Answer> getAnswersList() {
        return answersList;
    }

    public void setAnswersList(ArrayList<Answer> answersList) {
        this.answersList = answersList;
    }

    //Default constructor
    public Question() {
    }

    public Question(long id, String text, double weight, String creationDate) {
        this.id = id;
        this.text = text;
        this.weight = weight;
        this.creationDate = creationDate;
    }

    public Question(long id, String text, double weight, String creationDate, ArrayList<Answer> answersList) {
        this.id = id;
        this.text = text;
        this.weight = weight;
        this.creationDate = creationDate;
        this.answersList = answersList;
    }

}
