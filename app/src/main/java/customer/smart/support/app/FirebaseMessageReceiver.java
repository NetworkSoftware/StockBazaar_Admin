package customer.smart.support.app;

import static com.android.volley.Request.Method.GET;
import static customer.smart.support.app.Appconfig.mypreference;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import customer.smart.support.NaviActivity;
import customer.smart.support.R;
import customer.smart.support.client.stock.Product;
import customer.smart.support.client.stock.ProductRegister;
import customer.smart.support.order.MainActivityOrder;
import customer.smart.support.order.Order;


public class FirebaseMessageReceiver extends FirebaseMessagingService {
    Intent intent = null;
    private SharedPreferences sharedpreferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (remoteMessage.getData().size() > 0) {

            showNotification(remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("message"), remoteMessage.getData().get("metadata"));
        }


        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(), null);
        }

    }

    private RemoteViews getCustomDesign(String title, String message) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon, R.drawable.airking);
        return remoteViews;
    }

    public void showNotification(String title, String message, String metadata) {

        Intent intent = null;
        if (metadata != null) {
            try {
                JSONObject jsonObject = new JSONObject(metadata);
                String type = jsonObject.getString("type");
                String adminPhone = sharedpreferences.getString(Appconfig.AdminPhone, "");
                Log.e("xxxx", adminPhone);
                if (adminPhone.equalsIgnoreCase("9787665726") ||
                        adminPhone.equalsIgnoreCase("7010504536") ||
                        adminPhone.equalsIgnoreCase("9514414404") ||
                        adminPhone.equalsIgnoreCase("9787665854")) {
                    if (type.equalsIgnoreCase("service")
                            || type.equalsIgnoreCase("exchange")) {
                        intent = new Intent(this, NaviActivity.class);
                        sent(message, title, intent, null);
                    }else if("order".equalsIgnoreCase(type)){
                        intent = new Intent(this, MainActivityOrder.class);
                        sent(message, title, intent, null);
                    } else {
                        String id = jsonObject.getString("id");
                        fetchContacts(id, message, title);
                    }
                }
                return;
            } catch (Exception e) {
                Log.e("xxxx", e.toString());
            }
        }
        if (intent == null) {
            intent = new Intent(this, NaviActivity.class);
        }
        sent(message, title, intent, null);

    }


    public void sent(String message, String title, Intent intent, Bitmap bitmap) {

        String channel_id = "stock_bazaar_web_app_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.icon)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setStyle(bitmap == null ? new NotificationCompat.BigTextStyle()
                            .bigText(message) : new NotificationCompat.BigPictureStyle()
                            .bigLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.airking))
                            .bigPicture(bitmap));
        } else {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.airking);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Stock Bazaar", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());

    }

    private void fetchContacts(String id, final String message, final String title) {

        String url = Appconfig.FETCH_ITEM_BY_ID + "?id=" + id
                + "&type=product";
        JsonObjectRequest request = new JsonObjectRequest(GET, url, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject jsonObject = response.getJSONArray("data").getJSONObject(0);
                                Product productListBean = new Product();
                                productListBean.setId(jsonObject.getString("id"));
                                productListBean.setBrand(jsonObject.getString("brand"));
                                productListBean.setModel(jsonObject.getString("model"));
                                productListBean.setDescription(jsonObject.getString("description"));
                                productListBean.setSelectshop(jsonObject.getString("selectshop"));
                                productListBean.setStock_update(jsonObject.getString("stockupdate"));
                                if (!jsonObject.isNull("categoryName")) {
                                    productListBean.setCategoryName(jsonObject.getString("categoryName"));
                                }
                                if (!jsonObject.isNull("category")) {
                                    productListBean.setCategory(jsonObject.getString("category"));
                                }
                                productListBean.setPrice(jsonObject.getString("price"));
                                if (!jsonObject.isNull("quantity")) {
                                    productListBean.setQty(jsonObject.getString("quantity"));
                                }
                                if (!jsonObject.isNull("categoryTag")) {
                                    productListBean.setCategoryTag(jsonObject.getString("categoryTag"));
                                }
                                if (!jsonObject.isNull("bulkPrice")) {
                                    productListBean.setBulkPrice(jsonObject.getString("bulkPrice"));
                                }

                                if (!jsonObject.isNull("incategory")) {
                                    productListBean.setIncategory(jsonObject.getString("incategory"));
                                }
                                if (jsonObject.get("image") instanceof JSONArray) {
                                    ArrayList<String> samplesList = new ArrayList<>();
                                    for (int k = 0; k < jsonObject.getJSONArray("image").length(); k++) {
                                        String image = jsonObject.getJSONArray("image").getString(k);
                                        if (image != null && image.length() > 0) {
                                            samplesList.add(image);
                                        }
                                    }
                                    productListBean.setImage(new Gson().toJson(samplesList));
                                } else {
                                    productListBean.setImage(jsonObject.getString("image"));
                                }
                                intent = new Intent(getApplicationContext(),
                                        ProductRegister.class);
                                intent.putExtra("data", productListBean);
                                intent.putExtra("SHOPNAME", jsonObject.getString("shopName"));
                                intent.putExtra("from", "admin");
                            }
                        } catch (Exception e) {
                            Log.e("xxxxxxxxxxx", e.toString());
                        }
                        if (intent == null) {
                            intent = new Intent(getApplicationContext(), NaviActivity.class);
                        }
                        sent(message, title, intent, null);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (intent == null) {
                    intent = new Intent(getApplicationContext(), NaviActivity.class);
                }
                sent(message, title, intent, null);
            }
        });
        request.setRetryPolicy(Appconfig.getTimeOut());
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//        Log.e("xxxxxxxxxxxxx", "taskremoved,rootint");
        super.onTaskRemoved(rootIntent);

        //Log.d(TAG, "TASK REMOVED");
        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), FirebaseMessageReceiver.class),
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }
    //app part ready now let see how to send differnet users
    //like send to specific device
    //like specifi topic
}
