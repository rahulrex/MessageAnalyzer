package com.sbi.message.messageanalyzer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sbi.message.messageanalyzer.api.AESSecurity;
import com.sbi.message.messageanalyzer.database.model.SentItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "sent_msg_db";
    AESSecurity aesSecurity = new AESSecurity();
    public DatabaseHelper(Context context) {
        super(context, "database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create Msg table
        db.execSQL(SentItem.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + SentItem.TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public long insertMsg(SentItem sentItem) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(sentItem.TEAM_ID, AESSecurity.encrypt(sentItem.getTeamId().toString(),aesSecurity.getSecretKey()));
        values.put(sentItem.SENDER_NAME, AESSecurity.encrypt(sentItem.getSenderName() != null || !sentItem.getSenderName().isEmpty() ? sentItem.getSenderName() : "Null",aesSecurity.getSecretKey()));
        values.put(sentItem.SENDER_NUMBER, AESSecurity.encrypt(sentItem.getSenderNumber() != null || !sentItem.getSenderNumber().isEmpty() ? sentItem.getSenderNumber() : "Null",aesSecurity.getSecretKey()));
        values.put(sentItem.RECEIVER_NAME, AESSecurity.encrypt(sentItem.getReceiverName() != null || !sentItem.getReceiverName().isEmpty() ? sentItem.getReceiverName() : "Null",aesSecurity.getSecretKey()));
        values.put(sentItem.RECEIVER_NUMBER, AESSecurity.encrypt(sentItem.getReceiverNumber() != null || !sentItem.getReceiverNumber().isEmpty() ? sentItem.getReceiverNumber() : "Null",aesSecurity.getSecretKey()));
        values.put(sentItem.MSG_DESCRIPTION, AESSecurity.encrypt(sentItem.getMsgDescription() != null || !sentItem.getMsgDescription().isEmpty() ? sentItem.getMsgDescription() : "Null",aesSecurity.getSecretKey()));
        values.put(sentItem.SENT_TIME, AESSecurity.encrypt(sentItem.getSentTime() != null || !sentItem.getSentTime().isEmpty() ? sentItem.getSentTime() : "Null",aesSecurity.getSecretKey()));

        long id = db.insert(SentItem.TABLE_NAME, null, values);

        db.close();
        return id;
    }


    public List<SentItem> getAllSentMsg() {
        List<SentItem> sentItems = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + SentItem.TABLE_NAME + " ORDER BY " +
                SentItem.INSERT_TIME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SentItem sentItem = new SentItem();
                sentItem.setId(cursor.getInt(cursor.getColumnIndex(SentItem.MSG_ID)));
                sentItem.setTeamId(cursor.getString(cursor.getColumnIndex(SentItem.TEAM_ID)));
                sentItem.setSenderName(cursor.getString(cursor.getColumnIndex(SentItem.SENDER_NAME)));
                sentItem.setSenderNumber(cursor.getString(cursor.getColumnIndex(SentItem.SENDER_NUMBER)));
                sentItem.setReceiverName(cursor.getString(cursor.getColumnIndex(SentItem.RECEIVER_NAME)));
                sentItem.setReceiverNumber(cursor.getString(cursor.getColumnIndex(SentItem.RECEIVER_NUMBER)));
                sentItem.setMsgDescription(cursor.getString(cursor.getColumnIndex(SentItem.MSG_DESCRIPTION)));
                sentItem.setSentTime(cursor.getString(cursor.getColumnIndex(SentItem.SENT_TIME)));
                sentItem.setInsertTime(cursor.getString(cursor.getColumnIndex(SentItem.INSERT_TIME)));

                sentItems.add(sentItem);
            } while (cursor.moveToNext());
        }
        db.close();

        return sentItems;
    }

    public int getMsgCount() {
        String countQuery = "SELECT  * FROM " + SentItem.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void deleteAllMsg() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ SentItem.TABLE_NAME);
        db.close();
    }
}
