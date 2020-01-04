package com.sbi.message.messageanalyzer;

import com.sbi.message.messageanalyzer.database.model.SMS;

public interface SMSListener {
    public abstract void reportIncomingSms(SMS paramSMS);

    public abstract void reportOutgoingSms(SMS paramSMS);
}
