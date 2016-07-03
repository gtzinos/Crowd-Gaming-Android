package geotzinos.crowdgaming.Model.Domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by George on 2016-05-29.
 */
public class Question implements Serializable {
    private long id;
    private String text;
    private double multiplier;
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

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double weight) {
        this.multiplier = weight;
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

    public Question(long id, String text, double multiplier, String creationDate) {
        this.id = id;
        this.text = text;
        this.multiplier = multiplier;
        this.creationDate = creationDate;
        this.answersList = new ArrayList<Answer>();
    }

    public Question(long id, String text, double weight, String creationDate, ArrayList<Answer> answersList) {
        this(id, text, weight, creationDate);
        this.answersList = answersList;
    }

}
