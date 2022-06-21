package customer.smart.support;

import static customer.smart.support.app.Appconfig.mypreference;
import static customer.smart.support.client.shop.MainActivityShop.SHOPID;
import static customer.smart.support.client.shop.MainActivityShop.SHOPNAME;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.poovam.pinedittextfield.PinField;
import com.poovam.pinedittextfield.SquarePinField;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.client.stock.MainActivityProduct;

public class StartActivity extends AppCompatActivity {
    ProgressDialog dialog;
    EditText password, phoneNo;
    SharedPreferences sharedpreferences;
    SquarePinField otp_view;
    TextView requestOtp;
    private Button loginPassword, loginOtp;

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
        otp_view = findViewById(R.id.otp_view);
        loginOtp = findViewById(R.id.loginOtp);
        requestOtp = findViewById(R.id.requestOtp);
        loginPassword = findViewById(R.id.loginPassword);
        phoneNo = findViewById(R.id.mobile);
        if (sharedpreferences.contains("user")) {
            phoneNo.setText(sharedpreferences.getString("user", ""));
            password.requestFocus();
        }
        password.setVisibility(View.GONE);
        otp_view.setVisibility(View.GONE);

        requestOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("PASSWORD LOGIN".equalsIgnoreCase(requestOtp.getText().toString())) {
                    password.setVisibility(View.VISIBLE);
                    otp_view.setVisibility(View.GONE);
                    requestOtp.setText("LOGIN OTP");
                    loginOtp.setText("OTP LOGIN");
                } else {
                    password.setVisibility(View.GONE);
                    otp_view.setVisibility(View.VISIBLE);
                    requestOtp.setText("PASSWORD LOGIN");
                    loginOtp.setText("VERIFY OTP");
                }
            }
        });
        loginOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNo.getText().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Enter valid phoneNo", Toast.LENGTH_SHORT).show();
                } else if ("Verify Otp".equalsIgnoreCase(loginOtp.getText().toString())) {
                    if (otp_view.getText().length() <= 0) {
                        Toast.makeText(getApplicationContext(), "Enter valid OTP", Toast.LENGTH_SHORT).show();
                    } else {
                        verifyOtp(otp_view.getText().toString());
                    }

                } else {
                    otp_view.setVisibility(View.VISIBLE);
                    loginOtp.setText("Verify Otp");
                    requestOtp.setText("PASSWORD LOGIN");
                    password.setVisibility(View.GONE);
                    getOtp();
                    otp_view.setFocusable(true);
                }
            }
        });
        loginPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNo.getText().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Enter valid phoneNo", Toast.LENGTH_SHORT).show();
                }else if ("SUBMIT".equalsIgnoreCase(loginPassword.getText().toString())) {
                    if (password.getText().toString().length() > 0) {
                        passwordLogin();
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter valid Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    otp_view.setVisibility(View.GONE);
                    loginPassword.setText("SUBMIT");
                    requestOtp.setText("LOGIN OTP");
                    password.setVisibility(View.VISIBLE);
                }
            }
        });

        otp_view.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText) {
                //  Toast.makeText(StartActivity.this, enteredText, Toast.LENGTH_SHORT).show();
                return true; // Return false to keep the keyboard open else return true to close the keyboard
            }
        });

    }

    private void passwordLogin() {
        String tag_string_req = "req_register";
        dialog.setMessage("Login ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Appconfig.STAFF_LOGIN, new Response.Listener<String>() {
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
                        editor.putBoolean(Appconfig.isLogin, true);
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
                            editor.putString("name", jObj.getString("name"));

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


    private void getOtp() {
        String tag_string_req = "req_register";
        dialog.setMessage("OTP ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Appconfig.OTP, new Response.Listener<String>() {
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
                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void verifyOtp(final String otp) {
        String tag_string_req = "req_register";
        dialog.setMessage("Login ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Appconfig.OTPLOGIN, new Response.Listener<String>() {
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
                        editor.putBoolean(Appconfig.isLogin, true);
                        if (!jObj.isNull("isClient") && jObj.getBoolean("isClient")) {
                            editor.putBoolean(Appconfig.isClient, true);
                            editor.putString(Appconfig.shopId, jObj.getJSONObject("data").getString("id"));
                            editor.putString(Appconfig.shopName, jObj.getJSONObject("data").getString("shopname"));
                            editor.putString(Appconfig.category,jObj.getJSONObject("data").getString("category"));
                            editor.commit();
                            Intent io = new Intent(StartActivity.this, MainActivityProduct.class);
                            io.putExtra(SHOPID, jObj.getJSONObject("data").getString("id"));
                            io.putExtra(SHOPNAME, jObj.getJSONObject("data").getString("shopname"));
                            io.putExtra("from", "client");
                            io.putExtra("EXTRA_CAT", jObj.getJSONObject("data").getString("category"));
                            startActivity(io);
                        } else {
                            editor.putBoolean(Appconfig.isClient, false);
                            editor.putString("role", jObj.getString("role"));
                            editor.putString("data", jObj.getString("data"));
                            editor.putString("name", jObj.getString("name"));

                            editor.commit();
                            Intent io = new Intent(StartActivity.this, NaviActivity.class);
                            startActivity(io);
                        }
                        finish();
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
                localHashMap.put("otp", otp);
                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
