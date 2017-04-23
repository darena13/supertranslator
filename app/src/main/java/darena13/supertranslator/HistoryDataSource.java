package darena13.supertranslator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darena13 on 13.04.2017.
 */

public class HistoryDataSource {
    private static final String[] ALL_COLUMNS = {
            HistoryOpenHelper.COLUMN_ID,
            HistoryOpenHelper.COLUMN_DATE,
            HistoryOpenHelper.COLUMN_TEXT,
            HistoryOpenHelper.COLUMN_TRNS,
            HistoryOpenHelper.COLUMN_LANG,
            HistoryOpenHelper.COLUMN_FAV
    };
    private static final long FAV_FALSE = 0;
    private static final long FAV_TRUE = 1;

    private SQLiteDatabase database;
    private HistoryOpenHelper dbHelper;

    public HistoryDataSource(Context context) {
        dbHelper = new HistoryOpenHelper(context);
    }

    public void open() {
        try {
            database = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            database = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        dbHelper.close();
    }

    //добавить строчку в таблицу
    public void insertOrUpdateHistoryItem(String text, String trns, String lang) {
        //class is used to store a set of values
        long time = System.currentTimeMillis();
        ContentValues insertValues = new ContentValues();
        //добавляем к сету пары ключ+значение
        insertValues.put(HistoryOpenHelper.COLUMN_DATE, time);
        insertValues.put(HistoryOpenHelper.COLUMN_TEXT, text);
        insertValues.put(HistoryOpenHelper.COLUMN_TRNS, trns);
        insertValues.put(HistoryOpenHelper.COLUMN_LANG, lang);
        insertValues.put(HistoryOpenHelper.COLUMN_FAV, 0);
        //добавляем весь сет в базу и получаем id строки
        long insertId = database.insertWithOnConflict(HistoryOpenHelper.HISTORY_TABLE_NAME, null,
                insertValues, SQLiteDatabase.CONFLICT_IGNORE);
        //если при добавлении нашлась запись, совпадающая по полям Текст и Направление перевода, обновляем в ней Дату и Перевод
        if (insertId == -1) {
            ContentValues updateValues = new ContentValues();
            updateValues.put(HistoryOpenHelper.COLUMN_DATE, time);
            updateValues.put(HistoryOpenHelper.COLUMN_TRNS, trns);
            database.update(HistoryOpenHelper.HISTORY_TABLE_NAME, updateValues,
                    HistoryOpenHelper.COLUMN_TEXT + "=? AND " +
                            HistoryOpenHelper.COLUMN_LANG + "=?", new String[]{text, lang});
        }
    }

    //удалить запись из таблицы
    public void deleteHistoryItem(Translation translation) {
        long id = translation.getId();
        System.out.println("Item deleted with id: " + id);
        database.delete(HistoryOpenHelper.HISTORY_TABLE_NAME, HistoryOpenHelper.COLUMN_ID
                + " = " + id, null);
    }

    //получить список всех записей в таблице
    public List<Translation> getAllItems() {
        List<Translation> translations = new ArrayList<Translation>();
        //запрос в БД, к результата мкоторого прилагается курсор
        Cursor cursor = database.query(HistoryOpenHelper.HISTORY_TABLE_NAME,
                ALL_COLUMNS, null, null, null, null, HistoryOpenHelper.COLUMN_DATE + " DESC");
        //перемещаем курсор на первую запись ответа на запрос к БД
        cursor.moveToFirst();
        //проходимся по ответу курсором и добавляем записи в список с переводами
        while (!cursor.isAfterLast()) {
            Translation translation = cursorToTranslation(cursor);
            translations.add(translation);
            cursor.moveToNext();
        }
        //освобождаем курсор
        cursor.close();
        return translations;
    }

    //заполняем поля объекта значениями из таблицы, на строчку таблицы указывает курсор
    public static Translation cursorToTranslation(Cursor cursor) {
        Translation translation = new Translation();
        translation.setId(cursor.getLong(0));
        translation.setDate(cursor.getLong(1));
        translation.setText(cursor.getString(2));
        translation.setTranslation(cursor.getString(3));
        translation.setLanguage(cursor.getString(4));
        translation.setFavorite(cursor.getLong(5));
        return translation;
    }

    //переключить состояние "в Избранном"
    public void toggleFavorites(Translation translation) {
        long id = translation.getId();
        ContentValues values = new ContentValues();
        values.put(HistoryOpenHelper.COLUMN_FAV,
                translation.getFavorite() == FAV_FALSE ? FAV_TRUE : FAV_FALSE);
        database.update(HistoryOpenHelper.HISTORY_TABLE_NAME, values,
                HistoryOpenHelper.COLUMN_ID + " = " + id, null);
    }
}
