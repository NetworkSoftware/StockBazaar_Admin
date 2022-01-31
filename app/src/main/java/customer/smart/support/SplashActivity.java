package customer.smart.support;

import static customer.smart.support.client.shop.MainActivityShop.SHOPID;
import static customer.smart.support.client.shop.MainActivityShop.SHOPNAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import customer.smart.support.app.Appconfig;
import customer.smart.support.client.stock.MainActivityProduct;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedpreferences = getSharedPreferences(Appconfig.mypreference,
                Context.MODE_PRIVATE);

        Log.d("TOken ", "" + FirebaseInstanceId.getInstance().getToken());
        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");


        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                } finally {
                    if (!(sharedpreferences.contains(Appconfig.isLogin)
                            && sharedpreferences.getBoolean(Appconfig.isLogin, false))) {
                        startActivity(new Intent(SplashActivity.this, StartActivity.class));
                        finish();
                    } else {
                        if (sharedpreferences.getBoolean(Appconfig.isClient, false)) {
                            Intent io = new Intent(SplashActivity.this, MainActivityProduct.class);
                            io.putExtra(SHOPID, sharedpreferences.getString(Appconfig.shopId, ""));
                            io.putExtra(SHOPNAME, sharedpreferences.getString(Appconfig.shopName, ""));
                            io.putExtra("from", "client");
                            io.putExtra("EXTRA_CAT", sharedpreferences.getString(Appconfig.category, ""));
                            startActivity(io);
                            finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this, NaviActivity.class));
                            finish();
                        }
                    }

                }
            }
        };
        logoTimer.start();

    }
}
