package com.sbi.message.messageanalyzer.database.model;

import java.util.Date;

public class SMS {
    public Date date;
    public String from;
    public String message;
    public String to;

    public SMS(String paramString1, String paramString2, String paramString3,Date paramDate) {
        this.from = paramString1;
        this.to = paramString2;
        this.message = paramString3;
        this.date = paramDate;
    }

    public String getTo() {
        return to;
    }

    public Date getDate() {
        return date;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SMS{" +
                "date=" + date +
                ", from='" + from + '\'' +
                ", message='" + message + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}