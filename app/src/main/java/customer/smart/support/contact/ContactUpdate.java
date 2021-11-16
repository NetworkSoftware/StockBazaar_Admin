package customer.smart.support.contact;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import customer.smart.support.R;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;

/**
 * Created by user_1 on 11-07-2018.
 */

public class ContactUpdate extends AppCompatActivity {


    EditText phone,name,address,pincode,area;


    private ProgressDialog pDialog;


    String studentId = null;

    TextView submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_register);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);



        name = (EditText) findViewById(R.id.name);
        pincode = (EditText) findViewById(R.id.pincode);
        address = (EditText) findViewById(R.id.address);
        phone = (EditText) findViewById(R.id.phone);
        area = (EditText) findViewById(R.id.area);
        submit = (TextView) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().length() > 0 &&
                        pincode.getText().toString().length() > 0 &&
                        address.getText().toString().length() > 0 &&
                        phone.getText().toString().length() > 0&&
                        area.getText().toString().length()>0
                ) {
                    registerUser();

                }
            }
        });


        try {

            Contact contact = (Contact) getIntent().getSerializableExtra("object");
            name.setText(contact.name);
            pincode.setText(contact.pincode);
            phone.setText(contact.phone);
            address.setText(contact.address);
            area.setText(contact.area);
            studentId = contact.id;
            submit.setText("Update");

        } catch (Exception e) {
            Log.e("xxxxxxxxxxx", e.toString());
        }

    }

    private void registerUser() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Processing ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Appconfig.UPDATE_CONTACT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String msg = jsonObject.getString("message");
                    if (success) {
                        finish();
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("pincode", pincode.getText().toString());
                localHashMap.put("phone", phone.getText().toString());
                localHashMap.put("address", address.getText().toString());
                localHashMap.put("name", name.getText().toString());
                localHashMap.put("area", area.getText().toString());
                localHashMap.put("id", studentId);
                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }



}
