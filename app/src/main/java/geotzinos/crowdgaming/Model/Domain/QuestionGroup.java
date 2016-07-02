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
    private long total_questions;
    private long answered_questions;
    private long allowed_repeats;
    private long current_repeats;
    private String time_left;
    private String time_to_complete;
    private long priority;
    private String is_completed;

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

    public long getTotal_questions() {
        return total_questions;
    }

    public void setTotal_questions(long total_questions) {
        this.total_questions = total_questions;
    }

    public long getAnswered_questions() {
        return answered_questions;
    }

    public void setAnswered_questions(long answered_questions) {
        this.answered_questions = answered_questions;
    }

    public long getAllowed_repeats() {
        return allowed_repeats;
    }

    public void setAllowed_repeats(long allowed_repeats) {
        this.allowed_repeats = allowed_repeats;
    }

    public long getCurrent_repeats() {
        return current_repeats;
    }

    public void setCurrent_repeats(long current_repeats) {
        this.current_repeats = current_repeats;
    }

    public String getTime_left() {
        return time_left;
    }

    public void setTime_left(String time_left) {
        this.time_left = time_left;
    }

    public String getTime_to_complete() {
        return time_to_complete;
    }

    public void setTime_to_complete(String time_to_complete) {
        this.time_to_complete = time_to_complete;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public String getIs_completed() {
        return is_completed;
    }

    public void setIs_completed(String is_completed) {
        this.is_completed = is_completed;
    }

    public ArrayList<Question> getQuestionsList() {
        return questionsList;
    }

    public void setQuestionsList(ArrayList<Question> questionsList) {
        this.questionsList = questionsList;
    }

    public QuestionGroup(long id, String name, String latitude, String longitude, String radius, String creationDate, long total_questions, long answered_questions, long allowed_repeats, long current_repeats, String time_left, String time_to_complete, long priority, String is_completed) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.creationDate = creationDate;
        this.total_questions = total_questions;
        this.answered_questions = answered_questions;
        this.allowed_repeats = allowed_repeats;
        this.current_repeats = current_repeats;
        this.time_left = time_left;
        this.time_to_complete = time_to_complete;
        this.priority = priority;
        this.is_completed = is_completed;
        this.questionsList = new ArrayList<Question>();
    }

    public QuestionGroup(long id, String name, String latitude, String longitude, String radius, String creationDate, long total_questions, long answered_questions, long allowed_repeats, long current_repeats, String time_left, String time_to_complete, long priority, String is_completed, ArrayList<Question> questionsList) {
        this(id, name, latitude, longitude, radius, creationDate, total_questions, answered_questions, allowed_repeats, current_repeats, time_left, time_to_complete, priority, is_completed);
        this.questionsList = questionsList;
    }
}
