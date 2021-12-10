package customer.smart.support;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.client.stock.MainActivityProduct;

import static customer.smart.support.app.Appconfig.mypreference;
import static customer.smart.support.client.shop.MainActivityShop.SHOPID;
import static customer.smart.support.client.shop.MainActivityShop.SHOPNAME;

public class StartActivity extends AppCompatActivity {
    ProgressDialog dialog;
    EditText password, phoneNo;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        password = findViewById(R.id.password);
        phoneNo = findViewById(R.id.mobile);
        if (sharedpreferences.contains("user")) {
            phoneNo.setText(sharedpreferences.getString("user", ""));
            password.requestFocus();
        }
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNo.getText().toString().length() > 0 &&
                        password.getText().toString().length() > 0) {
                    checkLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Enert valid username", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void checkLogin() {
        String tag_string_req = "req_register";
        dialog.setMessage("Login ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Appconfig.STAFF_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    String msg = jObj.getString("message");
                    if (success == 1) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("user", phoneNo.getText().toString());
                        if (!jObj.isNull("isClient") && jObj.getBoolean("isClient")) {
                            editor.commit();
                            Intent io = new Intent(StartActivity.this, MainActivityProduct.class);
                            io.putExtra(SHOPID, jObj.getJSONObject("data").getString("id"));
                            io.putExtra(SHOPNAME, jObj.getJSONObject("data").getString("shopname"));
                            io.putExtra("from", "client");
                            io.putExtra("EXTRA_CAT", jObj.getJSONObject("data").getString("category"));
                            startActivity(io);
                            finish();
                        } else {
                            editor.putString("role", jObj.getString("role"));
                            editor.putString("data", jObj.getString("data"));
                            editor.commit();
                            Intent io = new Intent(StartActivity.this, NaviActivity.class);
                            startActivity(io);
                            finish();
                        }
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e("xxxxxxxxxx", e.toString());
                }
                hideDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),
                        "Slow network found.Try again later", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("email", phoneNo.getText().toString());
                localHashMap.put("password", password.getText().toString());
                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!dialog.isShowing())
            dialog.show();
    }

    private void hideDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }

}
