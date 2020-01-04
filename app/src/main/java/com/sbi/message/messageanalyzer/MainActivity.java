package com.sbi.message.messageanalyzer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sbi.message.messageanalyzer.api.URLs;
import com.sbi.message.messageanalyzer.api.VolleySingleton;
import com.sbi.message.messageanalyzer.database.DatabaseHelper;
import com.sbi.message.messageanalyzer.database.model.SentItem;
import com.sbi.message.messageanalyzer.database.model.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_READ_SMS_PERMISSION = 0;
    public final static String READ_SMS_PERMISSION_NOT_GRANTED = "Please allow to access your SMS from setting";
    static boolean active = false;
    public static String USERNAME = "103232656";
    public static String PASSWORD = "ZFQRMMSN";
    DatabaseHelper databaseHelper = new DatabaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startService(new Intent(this, ServiceClass.class));

        Button btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "All messages has been sent", Toast.LENGTH_SHORT).show();
            }
        });


        // Check for Permission
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not given
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)){
                // User Denied the request
            }else{
                // Pop up for requesting again
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_SMS},REQUEST_READ_SMS_PERMISSION);
            }
        }


        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            // notify user you are online
            Toast.makeText(getApplicationContext(), "You're Online", Toast.LENGTH_SHORT).show();
            try{
                int count = databaseHelper.getMsgCount();
                if(count > 0){
                    List<SentItem> allSentMsg = databaseHelper.getAllSentMsg();
                    int counter = 0;
                    Iterator iterator = allSentMsg.iterator();
                    while(iterator.hasNext()) {
                        counter++;
                        if(counter >= 60){
                            Thread.sleep(600);
                        }else{
                            call((SentItem) iterator.next());
                        }
                    }
                    databaseHelper.deleteAllMsg();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), "You're Offline, Please Connect to Internet", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]) {

        switch (requestCode){
            case REQUEST_READ_SMS_PERMISSION:
            {
                if(grantResults.length > 0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Got the permission",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this,"Denied  the permission",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void call(SentItem sentItem){

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
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}
