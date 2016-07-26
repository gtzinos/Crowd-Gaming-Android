package geotzinos.crowdgaming.Model.Mapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

import geotzinos.crowdgaming.Core.Database.Database;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;

/**
 * Created by George on 2016-06-29.
 */
public class QuestionnaireMapper {
    private Database database;

    public QuestionnaireMapper(Database database) {
        this.database = database;
    }

    /*
        Insert questionnaire into database
    */
    public boolean InsertQuestionnaire(Questionnaire questionnaire) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", questionnaire.getName());
        contentValues.put("description", questionnaire.getDescription());
        contentValues.put("creation_date", questionnaire.getCreationDate());
        contentValues.put("time_left", questionnaire.getTime_left());
        contentValues.put("time_left_to_end", questionnaire.getTime_left_to_end());
        contentValues.put("total_questions", questionnaire.getTotal_questions());
        contentValues.put("answered_questions", questionnaire.getAnswered_questions());
        contentValues.put("allow_multiple_groups_play_through", questionnaire.getAllow_multiple_groups_playthrough());

        database.getWritableDatabase().insert("questionnaires", null, contentValues);
        Effect.Log("Class QuestionnaireMapper", "Questionnaire inserted successfully.");
        return true;
    }

    /*
        Get questionnaires from database
    */
    public ArrayList<Questionnaire> GetQuestionnaires() {
        String[] columns = {"name", "description", "creation_date", "time_left", "time_left_to_end", "total_questions", "answered_questions", "allow_multiple_groups_play_through"};
        Cursor cursor = database.getWritableDatabase().query("questionnaires", columns, null, null, null, null, null);
        ArrayList<Questionnaire> questionnairesList = new ArrayList<Questionnaire>();

        while (cursor.moveToNext()) {
            Long id = cursor.getLong(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            String creation_date = cursor.getString(3);
            int time_left = cursor.getInt(4);
            int time_left_to_end = cursor.getInt(5);
            int total_questions = cursor.getInt(6);
            int answered_questions = cursor.getInt(7);
            int allow_multiple_groups_play_through = cursor.getInt(8);
            boolean is_completed = cursor.getInt(9) == 0 ? false : true;

            Questionnaire questionnaire = new Questionnaire(id,name, description, creation_date, time_left, time_left_to_end, total_questions, answered_questions, allow_multiple_groups_play_through, is_completed);
            questionnairesList.add(questionnaire);
        }

        Effect.Log("Class QuestionnaireMapper", "GetQuestionnaires completed successfully.");
        return questionnairesList;
    }
}


