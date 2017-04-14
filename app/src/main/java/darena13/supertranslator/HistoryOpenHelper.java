package darena13.supertranslator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by darena13 on 13.04.2017.
 */

public class HistoryOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "super_translator.db";
    private static final String HISTORY_TABLE_NAME = "history";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_TRNS = "translate";
    private static final String COLUMN_LANG = "language";
    private static final String COLUMN_FAV = "favorite";

    private static final String HISTORY_TABLE_CREATE =
            "CREATE TABLE " + HISTORY_TABLE_NAME + " (" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_DATE + " integer not null, " +
                    COLUMN_TEXT + " text not null, " +
                    COLUMN_TRNS + " text not null, " +
                    COLUMN_LANG + " text not null, " +
                    COLUMN_FAV + " integer not null);";

    private static final String DATE_INDEX_CREATE =
            "CREATE INDEX date_index ON " + HISTORY_TABLE_NAME + " (" + COLUMN_DATE + ");";

    private static final String TEXT_INDEX_CREATE =
            "CREATE INDEX text_index ON " + HISTORY_TABLE_NAME + " (" + COLUMN_TEXT + ");";

    HistoryOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HISTORY_TABLE_CREATE);
        db.execSQL(DATE_INDEX_CREATE);
        db.execSQL(TEXT_INDEX_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(HistoryOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE_NAME);
        onCreate(db);
    }
}
