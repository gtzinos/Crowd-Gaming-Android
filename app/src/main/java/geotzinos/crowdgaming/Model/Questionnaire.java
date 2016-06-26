package geotzinos.crowdgaming.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 2016-05-29.
 */
public class Questionnaire {
    private int id;
    private String name;
    private String description;
    private String creationDate;
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
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.questionGroupsList = questionGroupsList;
    }
}
