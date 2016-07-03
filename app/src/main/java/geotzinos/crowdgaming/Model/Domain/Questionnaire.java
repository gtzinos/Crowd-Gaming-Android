package geotzinos.crowdgaming.Model.Domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by George on 2016-05-29.
 */
public class Questionnaire implements Serializable {
    private long id;
    private String name;
    private String description;
    private String creationDate;
    private int time_left;
    private double time_left_to_end;
    private int total_questions;
    private int answered_questions;
    private int allow_multiple_groups_playthrough;
    private String is_completed;
    private ArrayList<QuestionGroup> questionGroupsList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTime_left() {
        return time_left;
    }

    public void setTime_left(int time_left) {
        this.time_left = time_left;
    }

    public double getTime_left_to_end() {
        return time_left_to_end;
    }

    public void setTime_left_to_end(double time_left_to_end) {
        this.time_left_to_end = time_left_to_end;
    }

    public int getAnswered_questions() {
        return answered_questions;
    }

    public void setAnswered_questions(int answered_questions) {
        this.answered_questions = answered_questions;
    }

    public int getTotal_questions() {
        return total_questions;
    }

    public void setTotal_questions(int total_questions) {
        this.total_questions = total_questions;
    }

    public int getAllow_multiple_groups_playthrough() {
        return allow_multiple_groups_playthrough;
    }

    public void setAllow_multiple_groups_playthrough(int allow_multiple_groups_playthrough) {
        this.allow_multiple_groups_playthrough = allow_multiple_groups_playthrough;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getIs_completed() {
        return is_completed;
    }

    public void setIs_completed(String is_completed) {
        this.is_completed = is_completed;
    }

    public ArrayList<QuestionGroup> getQuestionGroupsList() {
        return questionGroupsList;
    }

    public void setQuestionGroupsList(ArrayList<QuestionGroup> questionGroupsList) {
        this.questionGroupsList = questionGroupsList;
    }

    //Default constructor
    public Questionnaire() {
    }

    public Questionnaire(long id, String name, String description, String creationDate, String is_completed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.is_completed = is_completed;
        this.questionGroupsList = new ArrayList<QuestionGroup>();
    }

    public Questionnaire(long id, String name, String description, String creationDate, String is_completed, ArrayList<QuestionGroup> questionGroupsList) {
        this(id, name, description, creationDate, is_completed);
        this.questionGroupsList = questionGroupsList;
    }

    public Questionnaire(long id, String name, String description, String creationDate, int time_left, double time_left_to_end
            , int total_questions, int answered_questions, int allow_multiple_groups_playthrough, String is_completed) {
        this(id, name, description, creationDate, is_completed);
        this.time_left = time_left;
        this.time_left_to_end = time_left_to_end;
        this.total_questions = total_questions;
        this.answered_questions = answered_questions;
        this.allow_multiple_groups_playthrough = allow_multiple_groups_playthrough;
    }

    public Questionnaire(long id, String name, String description, String creationDate, ArrayList<QuestionGroup> questionGroupsList
            , int time_left, double time_left_to_end, int total_questions, int answered_questions, int allow_multiple_groups_playthrough, String is_completed) {
        this(id, name, description, creationDate, time_left, time_left_to_end, total_questions, answered_questions, allow_multiple_groups_playthrough, is_completed);
        this.questionGroupsList = questionGroupsList;
    }
}
