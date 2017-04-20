package darena13.supertranslator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darena13 on 13.04.2017.
 */

public class HistoryDataSource {
    private SQLiteDatabase database;
    private HistoryOpenHelper dbHelper;
    private String[] allColumns = { HistoryOpenHelper.COLUMN_ID,
                                    HistoryOpenHelper.COLUMN_DATE,
                                    HistoryOpenHelper.COLUMN_TEXT,
                                    HistoryOpenHelper.COLUMN_TRNS,
                                    HistoryOpenHelper.COLUMN_LANG,
                                    HistoryOpenHelper.COLUMN_FAV };
    private static final long FAV_FALSE = 0;
    private static final long FAV_TRUE = 1;

    public HistoryDataSource(Context context) {
        dbHelper = new HistoryOpenHelper(context);
    }

    public void open() {
        try {
        database = dbHelper.getWritableDatabase();
        }
        catch (SQLiteException ex){
            database = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        dbHelper.close();
    }

    //добавить строчку в таблицу
    public HistoryItem createHistoryItem(long date, String text, String trns, String lang, long fav) {
        //class is used to store a set of values
        ContentValues values = new ContentValues();
        //добавляем к сету пары ключ+значение
        values.put(HistoryOpenHelper.COLUMN_DATE, date);
        values.put(HistoryOpenHelper.COLUMN_TEXT, text);
        values.put(HistoryOpenHelper.COLUMN_TRNS, trns);
        values.put(HistoryOpenHelper.COLUMN_LANG, lang);
        values.put(HistoryOpenHelper.COLUMN_FAV, fav);
        //удаляем старые записи воизбежание дублирования - ОШИБКА
        database.delete(HistoryOpenHelper.HISTORY_TABLE_NAME, HistoryOpenHelper.COLUMN_TEXT
                + " = '" + text + "'", null);
        //добавляем весь сет в базу и получаем id строки
        long insertId = database.insert(HistoryOpenHelper.HISTORY_TABLE_NAME, null,
                values);
        //получаем курсор для добавленной строки
        Cursor cursor = database.query(HistoryOpenHelper.HISTORY_TABLE_NAME,
                allColumns, HistoryOpenHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        //перемещаем курсор на первую строчку ответа на запрос
        cursor.moveToFirst();
        //создаем новый объект из строчки, на которую указывает курсор
        HistoryItem newHistoryItem = cursorToHistoryItem(cursor);
        //освобождаем курсор
        cursor.close();
        //возвращаем получившийся объект
        return newHistoryItem;
    }

    //удалить запись из таблицы
    public void deleteHistoryItem(HistoryItem historyItem) {
        long id = historyItem.getId();
        System.out.println("Item deleted with id: " + id);
        database.delete(HistoryOpenHelper.HISTORY_TABLE_NAME, HistoryOpenHelper.COLUMN_ID
                + " = " + id, null);
    }

    //получить список всех записей в таблице
    public List<HistoryItem> getAllItems() {
        List<HistoryItem> historyItems = new ArrayList<HistoryItem>();

        Cursor cursor = database.query(HistoryOpenHelper.HISTORY_TABLE_NAME,
                allColumns, null, null, null, null, HistoryOpenHelper.COLUMN_DATE + " DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HistoryItem historyItem = cursorToHistoryItem(cursor);
            historyItems.add(historyItem);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return historyItems;
    }

    //получить список всех записей в таблице, добавленных в избранное
    public ArrayList<HistoryItem> getAllFavorites() {
        ArrayList<HistoryItem> historyItems = new ArrayList<HistoryItem>();

        Cursor cursor = database.query(HistoryOpenHelper.HISTORY_TABLE_NAME,
                allColumns, HistoryOpenHelper.COLUMN_FAV + " = " + FAV_TRUE , null, null, null, HistoryOpenHelper.COLUMN_DATE + " DESC");


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HistoryItem historyItem = cursorToHistoryItem(cursor);
            historyItems.add(historyItem);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return historyItems;
    }

    //заполняем поля объекта значениями из таблицы, на строчку таблицы указывает курсор
    private HistoryItem cursorToHistoryItem(Cursor cursor) {
        HistoryItem historyItem = new HistoryItem();
        historyItem.setId(cursor.getLong(0));
        historyItem.setDate(cursor.getLong(1));
        historyItem.setText(cursor.getString(2));
        historyItem.setTranslation(cursor.getString(3));
        historyItem.setLanguage(cursor.getString(4));
        historyItem.setFavorite(cursor.getLong(5));
        return historyItem;
    }

    //добавить в favorites
    public void addToFavorites(HistoryItem historyItem) {
        long id = historyItem.getId();
        ContentValues values = new ContentValues();
        values.put(HistoryOpenHelper.COLUMN_FAV, FAV_TRUE);
        database.update(HistoryOpenHelper.HISTORY_TABLE_NAME, values,
                HistoryOpenHelper.COLUMN_ID + " = " + id, null);
    }

    //убрать из favorites
    public void removeFromFavorites(HistoryItem historyItem) {
        long id = historyItem.getId();
        ContentValues values = new ContentValues();
        values.put(HistoryOpenHelper.COLUMN_FAV, FAV_FALSE);
        database.update(HistoryOpenHelper.HISTORY_TABLE_NAME, values,
                HistoryOpenHelper.COLUMN_ID + " = " + id, null);
    }
}
