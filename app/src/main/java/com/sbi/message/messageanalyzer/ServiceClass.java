package com.sbi.message.messageanalyzer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.sbi.message.messageanalyzer.api.URLs;
import com.sbi.message.messageanalyzer.api.VolleySingleton;
import com.sbi.message.messageanalyzer.database.DatabaseHelper;
import com.sbi.message.messageanalyzer.database.model.SMS;
import com.sbi.message.messageanalyzer.database.model.SentItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.sbi.message.messageanalyzer.MainActivity.PASSWORD;
import static com.sbi.message.messageanalyzer.MainActivity.USERNAME;

public class ServiceClass extends Service implements SMSListener {

    private static final int NOTIF_ID = 101 ;
    private static final String NOTIF_CHANNEL_ID = "Notification_service";
    public static boolean sendSms = true;
    private final IBinder mBinder = new LocalBinder();
    public static MyContentObserver mSMSObserver;
    private Context ctx;
    public static SMS param_SMS;
    int myID = -1;

    public static final String SBIINK = "SBIINK";
    public static final String SBIATM = "SBIATM";

    DatabaseHelper databaseHelper = new DatabaseHelper(getBaseContext());

    String channelId = "";

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId,  String channelName){
        NotificationChannel chan =  new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_NONE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        myID = startId;
        sendSms = true;
        Context localContext = getApplicationContext();
        ctx = localContext;

        startForeground();

        mSMSObserver = new MyContentObserver(null);
        mSMSObserver.setSMSListener(this);
        mSMSObserver.start(localContext);

        return Service.START_STICKY;
    }


    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("my_service", "My Background Service");
        }else{
            channelId = "";
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId );
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(101, notification);
//        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
//                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
//                .setOngoing(true)
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText("Service is running background")
//                .setContentIntent(pendingIntent)
//                .build());
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                "appData", 0);

       // if (prefs.getBoolean(CommonStrings.KEY_PREFS_TOGGLE, false)) {

            super.onDestroy();
            Log.e("OnDestroy", "Stopping Service");
            Context localContext = getApplicationContext();
            mSMSObserver.stop(localContext);

            try {
                stopSelf(myID);
                Log.e("Stopping self", "Stopping Service");
            } catch (Exception e) {
                e.printStackTrace();
            }
        //}
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.e("OnBinder", "OnBinder");
        return null;
    }

    @Override
    public void reportIncomingSms(SMS paramSMS) {
        // TODO Auto-generated method stub

    }

    public void reportOutgoingSms(SMS paramSMS) {
//        if (MainActivity.active) {
        if (true) {
            Log.e("OUT GOING SMS DETECTED", "OUT GOING SMS DETECTED");
            sendSms = true;
            param_SMS = paramSMS;

            Date date = paramSMS.getDate();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
            String strDate= dateFormatter.format(date);

            SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss");
            String strTime= timeFormatter.format(date);

System.out.println("outgoing SMS "+paramSMS.toString());

            String senderNum = paramSMS.getFrom();

            // DO ANY THING, Out going Msg detected...
            SentItem sentItem = new SentItem();
            sentItem.setTeamId("Team3");
            sentItem.setSenderNumber("SBIATM");
            sentItem.setReceiverNumber(paramSMS.getTo());
            sentItem.setSentTime(strDate+","+strTime);

            final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                // notify user you are online
                //Toast.makeText(getApplicationContext(), "You're Online", Toast.LENGTH_SHORT).show();
                callApi(sentItem);
            } else {
                //Toast.makeText(getApplicationContext(), "You're Offline, Please Connect to Internet", Toast.LENGTH_SHORT).show();
                databaseHelper.insertMsg(sentItem);
            }

        }
    }

    public void callApi(SentItem sentItem){
        try{
            JSONObject jsonBodyObj = new JSONObject();
            String dateTime[] = sentItem.getSentTime().split(",");
            jsonBodyObj.put("TEAM_ID", "Team3");
            jsonBodyObj.put("MOBILE_NO", sentItem.getReceiverNumber());
            jsonBodyObj.put("DATE", dateTime[0]);
            jsonBodyObj.put("TIME", dateTime[1]);
            jsonBodyObj.put("SENDER_NAME", "SBIATM");


            final String requestBody = jsonBodyObj.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_INSERT_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(" Response",String.valueOf(response));
                }
            }, new Response.ErrorListener() {
                @Override    public void onErrorResponse(VolleyError error) {
                    Log.e("Error: ", error.getMessage());
                }
            }){
                @Override    public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    String credentials = USERNAME+":"+PASSWORD;
                    String auth = "Basic "
                            + Base64.encodeToString(credentials.getBytes(),
                            Base64.NO_PADDING);
                    headers.put("Authorization", auth);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
                @Override    public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (Exception uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody, "utf-8");
                        return null;
                    }
                }
            };

            //make the request to your server as indicated in your request url
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public ServiceClass getService() {
            return ServiceClass.this;
        }
    }



}
