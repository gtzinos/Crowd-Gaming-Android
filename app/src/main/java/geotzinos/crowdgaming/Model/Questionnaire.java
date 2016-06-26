package geotzinos.crowdgaming.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 2016-05-29.
 */
public class Questionnaire implements Serializable {
    private int id;
    private String name;
    private String description;
    private String creationDate;
    private int time_left;
    private int time_left_to_end;
    private int total_questions;
    private int answered_questions;
    private int allow_multiple_groups_playthrough;
    private List<QuestionGroup> questionGroupsList;

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

    public List<QuestionGroup> getQuestionGroupsList() {
        return questionGroupsList;
    }

    public void setQuestionGroupsList(List<QuestionGroup> questionGroupsList) {
        this.questionGroupsList = questionGroupsList;
    }

    //Default constructor
    public Questionnaire() {
    }

    public Questionnaire(String name, String description, String creationDate) {
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.questionGroupsList = new ArrayList<QuestionGroup>();
    }

    public Questionnaire(String name, String description, String creationDate, List<QuestionGroup> questionGroupsList) {
        this(name, description, creationDate);
        this.questionGroupsList = questionGroupsList;
    }

    public Questionnaire(String name, String description, String creationDate, int time_left, int time_left_to_end
            , int total_questions, int answered_questions, int allow_multiple_groups_playthrough) {
        this(name, description, creationDate);
        this.time_left = time_left;
        this.time_left_to_end = time_left_to_end;
        this.total_questions = total_questions;
        this.answered_questions = answered_questions;
        this.allow_multiple_groups_playthrough = allow_multiple_groups_playthrough;
    }

    public Questionnaire(String name, String description, String creationDate, List<QuestionGroup> questionGroupsList
            , int time_left, int time_left_to_end, int total_questions, int answered_questions, int allow_multiple_groups_playthrough) {
        this(name, description, creationDate, time_left, time_left_to_end, total_questions, answered_questions, allow_multiple_groups_playthrough);
        this.questionGroupsList = questionGroupsList;
    }
}
