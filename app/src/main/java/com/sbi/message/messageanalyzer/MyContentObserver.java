package com.sbi.message.messageanalyzer;

import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.sbi.message.messageanalyzer.database.model.SMS;

import java.util.Date;

public class MyContentObserver extends ContentObserver {

    public static final int MESSAGE_TYPE_ALL = 0;
    public static final int MESSAGE_TYPE_DRAFT = 3;
    public static final int MESSAGE_TYPE_FAILED = 5;
    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_OUTBOX = 4;
    public static final int MESSAGE_TYPE_QUEUED = 6;
    public static final int MESSAGE_TYPE_SENT = 2;
    public static final String TYPE = "type";
    private SMSListener mSMSListener;
    public static long _id;
    private ContentObserver observer;

    public MyContentObserver(Handler handler) {
        super(handler);
        // TODO Auto-generated constructor stub
    }
    private void readFromOutgoingSMS(Context paramContext) {
        System.out.println("Inside read");
//        if (MainActivity.active) {
        if (true) {
            Cursor localCursor = paramContext.getContentResolver().query(
                    Uri.parse("content://sms"), null, null, null, null);
            long l = 0;
            int i;
            if (localCursor.moveToNext()) {
                l = localCursor.getLong(localCursor.getColumnIndex("_id"));
                String str1 = localCursor.getString(localCursor
                        .getColumnIndex("protocol"));
                i = localCursor.getInt(localCursor.getColumnIndex("type"));

                long sub_id = localCursor.getLong(localCursor.getColumnIndex("sub_id"));
                System.out.println("_id :"+l+" protocol: "+str1+" sub_id: "+sub_id);

//                System.out.println("IIII "+i);
//                if ((str1 != null) || (i != 6))
//                    localCursor.close();

                int k = localCursor.getColumnIndex("address");
                int j = localCursor.getColumnIndex("body");
                int subject = localCursor.getColumnIndex("subject");
                Date localDate = new Date(localCursor.getLong(localCursor
                        .getColumnIndex("date")));
                String str2 = localCursor.getString(k);
                String str3 = localCursor.getString(j);
                String creatorString = localCursor.getString(subject);
//                String[] splited = creatorString.split("\\s+");
//                String from = splited[1];
                localCursor.close();
                _id = l;
                System.out.println("creatorString :"+subject+" "+str2+" "+str3+" "+localDate);

                if (i == 2) {
//                    int j = localCursor.getColumnIndex("body");
//                    int k = localCursor.getColumnIndex("address");
//                    int creator = localCursor.getColumnIndex("creator");
//                    Date localDate = new Date(localCursor.getLong(localCursor
//                            .getColumnIndex("date")));
//                    String str2 = localCursor.getString(k);
//                    String str3 = localCursor.getString(j);
//                    String creatorString = localCursor.getString(creator);
//                    localCursor.close();
//                    _id = l;
//                    System.out.println("creatorString :"+creatorString+" "+str2+" "+str3+" "+localDate);
                    SMS localSMS = new SMS(creatorString, str2, str3, localDate);
                    this.mSMSListener.reportOutgoingSms(localSMS);
                    localCursor.close();
                }
            }
        }
    }

    public static boolean deleteSms(Context paramContext, long paramLong) {
        Uri localUri = ContentUris.withAppendedId(Uri.parse("content://sms"),
                paramLong);
        boolean bool = false;
        if (localUri != null) {
            try {
                int j = paramContext.getContentResolver().delete(localUri,
                        null, null);
                if (j == 1)
                    bool = true;
                else
                    bool = false;
            } catch (Exception localException) {
                localException.printStackTrace();
                bool = false;
            }
        }
        return bool;
    }

    private void registerContentObserver(final Context paramContext) {
        this.observer = new ContentObserver(null) {
            public void onChange(boolean paramAnonymousBoolean) {
                readFromOutgoingSMS(paramContext);
            }
        };
        paramContext.getContentResolver().registerContentObserver(
                Uri.parse("content://sms"), true, this.observer);
    }
    public void setSMSListener(SMSListener paramSMSListener) {
        this.mSMSListener = paramSMSListener;
    }

    public void start(Context paramContext) {
        registerContentObserver(paramContext);
        listenForIncomingSms(paramContext);
    }

    public void stop(Context paramContext) {
        paramContext.getContentResolver().unregisterContentObserver(
                this.observer);
    }

    private void listenForIncomingSms(Context paramContext) {
        //.....
    }

}