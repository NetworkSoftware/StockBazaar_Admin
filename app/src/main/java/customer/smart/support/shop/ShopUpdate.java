package customer.smart.support.shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import customer.smart.support.R;
import customer.smart.support.app.AndroidMultiPartEntity;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;
import customer.smart.support.app.Imageutils;
import de.hdodenhof.circleimageview.CircleImageView;


import static customer.smart.support.app.Appconfig.mypreference;

/**
 * Created by user_1 on 11-07-2018.
 */

public class ShopUpdate extends AppCompatActivity implements Imageutils.ImageAttachmentListener
        , GoogleApiClient.OnConnectionFailedListener {


    String[] areas = {
            "Ganthipuram",
            "Ariyalur",
            "Karur",
            "Nagapattinam",
            "Perambalur",
            "Pudukkottai",
            "Thanjavur",
            "Tiruchirappalli",
            "Tiruvarur",
            "Dharmapuri",
            "Coimbatore",
            "Erode",
            "Krishnagiri",
            "Namakkal",
            "The Nilgiris",
            "Salem",
            "Tiruppur",
            "Dindigul",
            "Kanyakumari",
            "Madurai",
            "Ramanathapuram",
            "Sivaganga",
            "Theni",
            "Thoothukudi",
            "Tirunelveli",
            "Virudhunagar",
            "Chennai",
            "Cuddalore",
            "Kanchipuram",
            "Tiruvallur",
            "Tiruvannamalai",
            "Vellore",
            "Viluppuram",
            "Kallakurichi",
    };
    private String[] DISTRICTLIST = new String[]{
            "Ariyalur",
            "Chengalpattu",
            "Chennai",
            "Coimbatore",
            "Cuddalore",
            "Dharmapuri",
            "Dindigul",
            "Erode",
            "Kallakurichi",
            "Kanchipuram",
            "Kanyakumari",
            "Karur",
            "Krishnagiri",
            "Madurai",
            "Nagapattinam",
            "Namakkal",
            "Nilgiris",
            "Perambalur",
            "Pudukkottai",
            "Ramanathapuram",
            "Ranipet",
            "Salem",
            "Sivaganga",
            "Tenkasi",
            "Thanjavur",
            "Theni",
            "Thoothukudi (Tuticorin)",
            "Tiruchirappalli",
            "Tirunelveli",
            "Tirupathur",
            "Tiruppur",
            "Tiruvallur",
            "Tiruvannamalai",
            "Tiruvarur",
            "Vellore",
            "Viluppuram",
            "Virudhunagar"
    };


    private String[] BUSINESSTYPE = new String[]{
            "New Mobiles, Old Mobiles, Service","Old Mobiles, Service","Old Mobiles",
    };
    EditText pincode;
    EditText name;
    EditText contact;
    EditText shopname;
    EditText password;
    EditText confirmPass;
    MaterialBetterSpinner district;
    String latLon = "0,0";
    private TextView submit;
    private ProgressDialog pDialog;
    SharedPreferences sharedpreferences;
    Imageutils imageutils;
    private CircleImageView profiletImage;
    private String imageUrl = null;
    AutoCompleteTextView actv;
    private Shop shop;
    String shopId = null;
    MaterialBetterSpinner type;

    private String[] TYPE = new String[]{
            "Yes","No",
    };
    private int PLACE_PICKER_REQUEST = 101;
    CheckBox newMobiles, oldMobiles, service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_register);

        imageutils = new Imageutils(this);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        profiletImage = (CircleImageView) findViewById(R.id.profiletImage);
        profiletImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(1);
            }
        });

        name = (EditText) findViewById(R.id.name);
        contact = (EditText) findViewById(R.id.contact);
        shopname = (EditText) findViewById(R.id.shopName);
        district =  findViewById(R.id.district);
        password = (EditText) findViewById(R.id.password);
        confirmPass = (EditText) findViewById(R.id.confirmPass);
        newMobiles = findViewById(R.id.newMobiles);
        oldMobiles = findViewById(R.id.oldMobiles);
        service = findViewById(R.id.service);

        pincode = (EditText) findViewById(R.id.pincode);
        type = (MaterialBetterSpinner) findViewById(R.id.type);

        ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TYPE);
        type.setAdapter(titleAdapter);
        type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        ArrayAdapter<String> disAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, DISTRICTLIST);
        district.setAdapter(disAdapter);
        district.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, areas);
        //Getting the instance of AutoCompleteTextView
        actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        actv.setThreshold(0);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);


        submit = (TextView) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().length() > 0 &&
                        contact.getText().toString().length() > 0 &&
                        shopname.getText().toString().length() > 0 &&
                        pincode.getText().toString().length() > 0 &&
                        getBusinessType().length() > 0 &&
                        password.getText().toString().length() > 0 &&
                        type.getText().toString().length() > 0 &&
                        confirmPass.getText().toString().length() > 0
                        && actv.getText().toString().length() > 0
                        && latLon.length() > 0) {

                    if (!password.getText().toString().equalsIgnoreCase(confirmPass.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Password & Confirm password not matched", Toast.LENGTH_SHORT).show();
                    } else if (contact.getText().toString().length() != 10 || contact.getText().toString().matches(".*[a-zA-Z]+.*")) {
                        Toast.makeText(getApplicationContext(), "Enter a valid Contact", Toast.LENGTH_SHORT).show();
                    } /*else if (imageUrl == null) {
                        Toast.makeText(getApplicationContext(), "Select a Image", Toast.LENGTH_SHORT).show();
                    } */else {
                        Shop shop = new Shop(name.getText().toString(),
                                contact.getText().toString(),
                                shopname.getText().toString(),
                                password.getText().toString(),
                                confirmPass.getText().toString(), imageUrl, actv.getText().toString(),
                                district.getText().toString(), latLon,
                                pincode.getText().toString(),
                                type.getText().toString(),
                                getBusinessType()
                        );

                        registerUser(shop);
                    }
                }
            }
        });


        try {
            shop = (Shop) getIntent().getSerializableExtra("object");
            shopId = shop.id;
            name.setText(shop.name);
            contact.setText(shop.contact);
            shopname.setText(shop.shopname);
            password.setText(shop.password);
            confirmPass.setText(shop.confirmPass);
            pincode.setText(shop.pincode);
            oldMobiles.setChecked(shop.businesstype.toLowerCase().contains("old mobiles"));
            newMobiles.setChecked(shop.businesstype.toLowerCase().contains("new mobiles"));
            service.setChecked(shop.businesstype.toLowerCase().contains("service"));
            imageUrl = shop.getImage();
            actv.setText(shop.getArea());
            district.setText(shop.address);
            type.setText(shop.type);
            latLon = shop.latlon;
            GlideApp.with(getApplicationContext())
                    .load(imageUrl)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .placeholder(R.drawable.profile)
                    .into(profiletImage);

            submit.setText("Update");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No Shop found", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void registerUser(final Shop shop) {
        String tag_string_req = "req_register";
        pDialog.setMessage("Processing ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Appconfig.UPDATE_SHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.length()));
                    int success = jObj.getInt("success");
                    String msg = jObj.getString("message");
                    if (success == 1) {
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
                localHashMap.put("id", shopId);
                localHashMap.put("shopname", shopname.getText().toString());
                localHashMap.put("password", password.getText().toString());
                localHashMap.put("name", name.getText().toString());
                localHashMap.put("mobile", contact.getText().toString());
                localHashMap.put("imgurl", imageUrl==null ? "dummy":imageUrl);
                localHashMap.put("area", actv.getText().toString());
                localHashMap.put("address", district.getText().toString());
                localHashMap.put("latLon", latLon);
                localHashMap.put("dealerPhone","9514414404");
                localHashMap.put("pincode", pincode.getText().toString());
                localHashMap.put("businesstype", getBusinessType());
                localHashMap.put("isDealer", type.getText().toString());
                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        String path = getCacheDir() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file, filename, path, false);
        pDialog.setMessage("Uploading...");
        showDialog();
        new UploadFileToServer().execute(imageutils.getPath(uri));
    }

    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
        String filepath;
        public long totalSize = 0;

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pDialog.setMessage("Uploading..." + (String.valueOf(progress[0])));
        }

        @Override
        protected String doInBackground(String... params) {
            filepath = params[0];
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Appconfig.URL_IMAGE_UPLOAD);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filepath);
                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;

                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Response from server: ", result);
            try {
                JSONObject jsonObject = new JSONObject(result.toString());
                if (!jsonObject.getBoolean("error")) {
                    GlideApp.with(getApplicationContext())
                            .load(filepath)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                            .placeholder(R.drawable.profile)
                            .into(profiletImage);
                    imageUrl = Appconfig.ip_img + "uploads/" + imageutils.getfilename_from_path(filepath);
                } else {
                    imageUrl = null;
                }
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
            }
            hideDialog();
            // showing the server response in an alert dialog
            //showAlert(result);


            super.onPostExecute(result);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), connectionResult.getErrorMessage()
                + "", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                StringBuilder stBuilder = new StringBuilder();

                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String addressVal = String.format("%s", place.getAddress());
                latLon = latitude + "," + longitude;
                shopname.setText(placename);
                district.setText(addressVal);

            }
        } else {
            imageutils.onActivityResult(requestCode, resultCode, data);

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private String getBusinessType() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(newMobiles.isChecked() ? "New Mobiles," : "");
        stringBuffer.append(oldMobiles.isChecked() ? "Old Mobiles," : "");
        stringBuffer.append(service.isChecked() ? "Service," : "");
        return stringBuffer.toString();
    }

}
