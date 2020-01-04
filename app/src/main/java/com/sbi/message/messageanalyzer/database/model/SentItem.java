package com.sbi.message.messageanalyzer.database.model;

public class SentItem {

    public static final String TABLE_NAME = "sent_message";


    public static final String MSG_ID = "msg_id";
    public static final String TEAM_ID = "teamId";
    public static final String SENDER_NAME = "sender_name";
    public static final String SENDER_NUMBER = "sender_number";
    public static final String RECEIVER_NAME = "receiver_name";
    public static final String RECEIVER_NUMBER = "receiver_number";
    public static final String SENT_TIME = "sent_time";
    public static final String INSERT_TIME = "insert_time";
    public static final String MSG_DESCRIPTION = "msg_description";

    private int id;
    private String teamId;
    private String senderName;
    private String senderNumber;
    private String receiverName;
    private String receiverNumber;
    private String msgDescription;
    private String sentTime;
    private String insertTime;

    public SentItem() {
    }

    public SentItem(int id,String teamId, String senderName, String senderNumber, String receiverName, String receiverNumber, String msgDescription, String sentTime , String insertTime) {
        this.id = id;
        this.teamId = teamId;
        this.senderName = senderName;
        this.senderNumber = senderNumber;
        this.receiverName = receiverName;
        this.receiverNumber = receiverNumber;
        this.msgDescription = msgDescription;
        this.sentTime = sentTime;
        this.insertTime = insertTime;
    }

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TEAM_ID + " TEXT,"
                    + SENDER_NAME + " TEXT,"
                    + SENDER_NUMBER + " TEXT,"
                    + RECEIVER_NAME + " TEXT,"
                    + RECEIVER_NUMBER + " TEXT,"
                    + MSG_DESCRIPTION + " TEXT,"
                    + SENT_TIME + " TEXT,"
                    + INSERT_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public  String getMsgId() {
        return this.teamId;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderNumber() {
        return this.senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getReceiverName() {
        return this.receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverNumber() {
        return this.receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getSentTime() {
        return this.sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getMsgDescription() {
        return this.msgDescription;
    }

    public void setMsgDescription(String msgDescription) {
        this.msgDescription = msgDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    @Override
    public String toString() {
        return "SentItem{" +
                "id=" + id +
                ", teamId='" + teamId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderNumber='" + senderNumber + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", receiverNumber='" + receiverNumber + '\'' +
                ", msgDescription='" + msgDescription + '\'' +
                ", sentTime='" + sentTime + '\'' +
                ", insertTime='" + insertTime + '\'' +
                '}';
    }


}
