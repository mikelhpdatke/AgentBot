package tompython.agentbot;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.content.Context.NOTIFICATION_SERVICE;
import static java.lang.StrictMath.max;

/**
 * Created by luong on 3/18/2018.
 */

public class FetchData extends AsyncTask<String, String, String> {
    public static int LASTCOUNT;
    Activity contextParent;
    Adapter adapter;
    RecyclerView recyclerView;
    String ip_server;
    List<String> list = null;
    public FetchData(Activity contextParent, Adapter adapter, RecyclerView recyclerView, String ip_server) {
        this.contextParent = contextParent;
        this.adapter = adapter;
        this.recyclerView = recyclerView;
        this.ip_server = ip_server;
        LASTCOUNT = 0;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e("Debug_Tom", Calendar.getInstance().getTime().toString() + ":" +"Running FetchData classs..");

    }

    protected String doInBackground(String... strings) {
        Log.e("Debug_Tom", String.valueOf(ItemFragmentDashboard.is_run));
        while (ItemFragmentDashboard.is_run == true) {
            Log.e("FetchData", String.valueOf(isCancelled()));
            if (isCancelled()){
                Log.e("FetchData", "Da huy backround..");
                return null;
            }
            Log.e("Background::","Fetching data from server..");
            Log.e("Debug_Tom", String.valueOf(ItemFragmentSetting.collection.count()) + "WTF");
            //LogActivity.addString(Calendar.getInstance().getTime().toString() + ":" +"Fetching data from server..");
            try {
                if (ItemFragmentSetting.collection.count() != ItemFragmentSetting.foundIP) {
                    list = new ArrayList<>();
                    for (Document document : ItemFragmentSetting.collection.find(
                            Filters.gt("id", max(0, ItemFragmentSetting.collection.count() - 10)))) {
                                list.add(document.getString("ip"));
                    }
                    ItemFragmentSetting.foundIP = list.size();
                    publishProgress();
                }
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(String... values) {
        Log.e("Dang xu ly giao dien..", "Giao dien fetch data");
        super.onProgressUpdate(values);
        ItemFragmentDashboard.list = list;
        adapter.setList(list);
        for (String i : adapter.list) {
            Log.e("Check list::::", i);
        }
        adapter.notifyDataSetChanged();

        // Push notification..

            //if (s.length() > 0) {
                Intent intent = new Intent(contextParent, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                PendingIntent pIntent = PendingIntent.getActivity(contextParent, (int) System.currentTimeMillis(), intent, 0);

                // Build notification
                // Actions are just fake
                Notification noti = null;

                    noti = new Notification.Builder(contextParent)
                            .setContentTitle("Thiết bị truy cập địa chỉ lạ!!")
                            .setContentText("Bấm để xem chi tiết")
                            .setSmallIcon(R.drawable.ic_action_bug)
                            .setContentIntent(pIntent)
                            .build();


                noti.defaults |= Notification.DEFAULT_SOUND;

                NotificationManager notificationManager = (NotificationManager) contextParent.getSystemService(NOTIFICATION_SERVICE);
                // hide the notification after its selected
                noti.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(0, noti);
                //LASTCOUNT = list.size();


    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //Toast.makeText(contextParent, "Okie, Finished", Toast.LENGTH_SHORT).show();
        Toasty.success(contextParent, "Fetching data has been completed!" , Toast.LENGTH_SHORT, true).show();
    }
}