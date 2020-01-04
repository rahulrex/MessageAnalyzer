package com.sbi.message.messageanalyzer.api;

public class URLs {

    private static final String ROOT_URL = "http://apisandbox.bank.sbi:12345/";

    public static final String URL_INSERT_DATA= ROOT_URL + "insertData";
    public static final String URL_GET_DATA_BY_TEAM = ROOT_URL + "getDataByTeamID";

    //http://apisandbox.bank.sbi:12345/getDataByDate/Team3/12-09-2009
    public static final String URL_GET_DATA_BY_DATE = ROOT_URL + "getDataByDate";
    //http://apisandbox.bank.sbi:12345/getDataBySenderName/Team1/SBIINK
    public static final String URL_GET_DATA_BY_SENDER = ROOT_URL + "getDataBySenderName";



    // {"TEAM_ID" : "Team3",
    //"MOBILE_NO" : "1234567845",
    //"DATE" : "12-03-2019",
    //"TIME" : "18:19",
    //"SENDER_NAME" : "SBIATM"
    //}
}