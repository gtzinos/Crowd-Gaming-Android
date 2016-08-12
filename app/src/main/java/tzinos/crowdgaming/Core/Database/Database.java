package tzinos.crowdgaming.Core.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tzinos.crowdgaming.General.Config;
import tzinos.crowdgaming.General.Effect;

/**
 * Created by George on 2016-06-28.
 */
public class Database extends SQLiteOpenHelper {
    public static final int database_version = 1;

    public Database(Context context) {
        super(context, Config.LOCAL_DATABASE_NAME, null, database_version);
        Effect.Log("CLASS DATABASE", "Database created.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CreateTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            CreateTables(db);
            Effect.Log("CLASS DATABASE", "Database upgraded.");
        } else {
            Effect.Log("CLASS DATABASE", "This is not a newer version.");
        }
    }

    /*
        Get database connection instance
    */
    public SQLiteDatabase getInstance() {
        return getWritableDatabase();
    }

    /*
        Drop database tables
    */
    public void DropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS Questionnaire");
        Effect.Log("CLASS DATABASE", "Database dropped.");
    }

    /*
        Create database tables
    */
    public void CreateTables(SQLiteDatabase db) {
        DropTables(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS Questionnaire(" +
                "id int not null," +
                "name varchar(255) not null," +
                "description varchar(255) not null," +
                "create_date timestamp not null," +
                "time_left int not null," +
                "time_left_to_end int not null," +
                "total_questions int not null," +
                "answered_questions int not null," +
                "allow_multiple_groups_playthrough tinyint(1) not null," +
                "PRIMARY KEY (`id`))");
        Effect.Log("CLASS DATABASE", "Tables created.");
    }
}
