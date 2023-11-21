package customer.smart.support.marketingstaff;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import customer.smart.support.R;
import customer.smart.support.app.AndroidMultiPartEntity;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;
import customer.smart.support.app.Imageutils;
import customer.smart.support.app.LocationTrack;
import customer.smart.support.attachment.ActivityMediaOnline;
import customer.smart.support.attachment.AttachmentBaseAdapter;
import customer.smart.support.attachment.Base;
import customer.smart.support.attachment.BaseClick;
import customer.smart.support.spares.AddImageAdapter;
import customer.smart.support.spares.ImageClick;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static customer.smart.support.app.Appconfig.MARKETINGSTAFF;
import static customer.smart.support.app.Appconfig.mypreference;

/**
 * Created by user_1 on 11-07-2018.
 */

public class MarketStaffRegister extends AppCompatActivity implements Imageutils.ImageAttachmentListener{

    private final static int ALL_PERMISSIONS_RESULT = 101;
    TextInputEditText staffName, staffcontact;
    TextInputEditText name, contact, geotags;
    Imageutils imageutils;
    TextInputEditText  brand, model, description, imeiNo, price;
    CardView itemsAdd;
    SharedPreferences sharedpreferences;
    AddImageAdapter maddImageAdapter;
    ImgAddImageAdapter naddImageAdapter;
    TextView submit;
    LocationTrack locationTrack;
    private ProgressDialog pDialog;
    private ImageView signview;
    private String signUrl = "";
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    private RecyclerView imagelist, imageAttachementlist;
    private ArrayList<String> samplesList = new ArrayList<>();
    private ArrayList<String> attachementList = new ArrayList<>();
    private String imageUrl = "";
    private String imageUrlAttachement = "";
    int uploadOrder=1;
    String studentId = null;

    private MarketStaff mark = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_register);

        imageutils = new Imageutils(this);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Marketing");

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        locationTrack = new LocationTrack(MarketStaffRegister.this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        itemsAdd = findViewById(R.id.itemsAdd);


        signview =  findViewById(R.id.signview);
        ImageView refreshBtn = (ImageView) findViewById(R.id.refreshBtn);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationTrack = new LocationTrack(MarketStaffRegister.this);
                if (locationTrack.canGetLocation()) {

                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();
                    geotags.setText(Double.toString(latitude) + "," + Double.toString(longitude));

                } else {
                    locationTrack.showSettingsAlert();
                }

            }
        });

        ImageView btnGps = (ImageView) findViewById(R.id.btnGps);
        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String strUri = "http://maps.google.com/maps?q=loc:" + Double.parseDouble(
                            geotags.getText().toString().split(",")[0]) + "," + Double.parseDouble(
                            geotags.getText().toString().split(",")[1]) + " (" + name.getText().toString() + ")";
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("xxxxxxxxxxxx", e.toString());
                }
            }
        });

        ImageView image_wallpaper = findViewById(R.id.image_wallpaper);
        image_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadOrder=1;
                imageutils.imagepicker(1);

            }
        });

        ImageView image_Attachement = findViewById(R.id.image_Attachement);
        image_Attachement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadOrder=2;
                imageutils.imagepicker(1);

            }
        });

        samplesList = new ArrayList<>();
        imagelist = findViewById(R.id.imagelist);
        naddImageAdapter = new ImgAddImageAdapter(this, samplesList, new ImageClick() {
            @Override
            public void onImageClick(int position) {
                Intent localIntent = new Intent(MarketStaffRegister.this, ActivityMediaOnline.class);
                localIntent.putExtra("filePath", samplesList.get(position));
                localIntent.putExtra("isImage", true);
                startActivity(localIntent);
            }

            @Override
            public void onDeleteClick(int position) {
                samplesList.remove(position);
                naddImageAdapter.notifyData(samplesList);
            }
        });
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imagelist.setLayoutManager(addManager1);
        imagelist.setAdapter(naddImageAdapter);


        attachementList = new ArrayList<>();
        imageAttachementlist = findViewById(R.id.imageAttachementlist);
        maddImageAdapter = new AddImageAdapter(this, attachementList, new ImageClick() {
            @Override
            public void onImageClick(int position) {
                Intent localIntent = new Intent(MarketStaffRegister.this, ActivityMediaOnline.class);
                localIntent.putExtra("filePath", attachementList.get(position));
                localIntent.putExtra("isImage", true);
                startActivity(localIntent);
            }

            @Override
            public void onDeleteClick(int position) {
                attachementList.remove(position);
                maddImageAdapter.notifyData(attachementList);
            }
        });
        final LinearLayoutManager addManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageAttachementlist.setLayoutManager(addManager);
        imageAttachementlist.setAdapter(maddImageAdapter);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        name = findViewById(R.id.name);
        contact = findViewById(R.id.contact);
        geotags = findViewById(R.id.geotags);
        staffName = findViewById(R.id.nameStaff);
        staffcontact = findViewById(R.id.staffcontact);

        staffName.setText(sharedpreferences.getString("name",""));
        staffcontact.setText(sharedpreferences.getString("user",""));
        staffName.setFocusable(false);
        staffcontact.setFocusable(false);
        // Edit Text
        imeiNo = findViewById(R.id.imeiNo);
        brand = findViewById(R.id.brand);
        price = findViewById(R.id.price);
        model = findViewById(R.id.model);
        description = findViewById(R.id.description);

//        bases.add(new Base("", "true"));
//        baseList = (RecyclerView) findViewById(R.id.attachmentList);
//        attachmentBaseAdapter = new AttachmentBaseAdapter(this, bases, this);
//        baseList.setLayoutManager(new GridLayoutManager(this, 3));
//        baseList.setAdapter(attachmentBaseAdapter);


        signview = (ImageView) findViewById(R.id.signview);
        signview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketStaffRegister.this, MainActivitySign.class);
                startActivityForResult(intent, 100);
            }
        });


        submit=findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().length() > 0 &&
                        contact.getText().toString().length() > 0 &&
                        geotags.getText().toString().length() > 0 &&
                        samplesList.size() > 0 &&
                        attachementList.size() > 0 &&
                        model.getText().toString().length() > 0 &&
                        price.getText().toString().length() > 0 &&
                        brand.getText().toString().length() > 0 &&
                        staffName.getText().toString().length() > 0 &&
                        staffcontact.getText().toString().length() > 0 &&
                        description.getText().toString().length() > 0
                ) {
                    registerUser();

                }
            }
        });

        try {

            MarketStaff cmobile = (MarketStaff) getIntent().getSerializableExtra("data");
            imeiNo.setText(cmobile.imeiNo);
            brand.setText(cmobile.brand);
            price.setText(cmobile.price);
            description.setText(cmobile.description);
            model.setText(cmobile.model);
            studentId = cmobile.id;
            staffName.setText(cmobile.staffName);
            staffcontact.setText(cmobile.staffContact);
            contact.setText(cmobile.customerContact);
            name.setText(cmobile.customerName);
            geotags.setText(cmobile.geotag);


            imageUrl = cmobile.image;


            if (imageUrl == null) {
                imageUrl = "";
            } else {
                samplesList = new Gson().fromJson(imageUrl, (Type) List.class);
            }

            imageUrlAttachement = cmobile.attachement;


            if (imageUrlAttachement == null) {
                imageUrlAttachement = "";
            } else {
                attachementList = new Gson().fromJson(imageUrlAttachement, (Type) List.class);
            }

            maddImageAdapter.notifyData(attachementList);
            naddImageAdapter.notifyData(samplesList);

            GlideApp.with(getApplicationContext())
                    .load(cmobile.sign)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .placeholder(R.drawable.profile)
                    .into(signview);
            signUrl = cmobile.sign;
        } catch (Exception e) {
            Log.e("xxxxxxxxxxx", e.toString());
        }


    }




    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void registerUser() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Processing ...");
        showDialog();
        int url;
        url = Request.Method.POST;
        if (mark != null) {
            url = Request.Method.PUT;
        }
        StringRequest strReq = new StringRequest(url,
                MARKETINGSTAFF, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
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
                Toast.makeText(getApplicationContext(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                if (studentId != null) {
                    localHashMap.put("id", studentId);
                }
                localHashMap.put("customerName", name.getText().toString());
                localHashMap.put("model", model.getText().toString());
                localHashMap.put("customerContact", contact.getText().toString());
                localHashMap.put("geotag", geotags.getText().toString());
                localHashMap.put("brand", brand.getText().toString());
                localHashMap.put("description", description.getText().toString());
                localHashMap.put("imeiNo", imeiNo.getText().toString());
                localHashMap.put("price", price.getText().toString());
                localHashMap.put("staffName", staffName.getText().toString());
                localHashMap.put("staffContact", staffcontact.getText().toString());
                localHashMap.put("sign", signUrl);
                localHashMap.put("image", new Gson().toJson(samplesList));
                localHashMap.put("attachement", new Gson().toJson(attachementList));
                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void deleteUser() {
        String tag_string_req = "req_register";
        StringRequest strReq = new StringRequest(Request.Method.DELETE,
                Appconfig.MARKETINGSTAFF + "?id=" + studentId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
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
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        String path = getCacheDir().getPath() + File.separator + "ImageAttach" + File.separator;
        String storedPath = imageutils.createImage(file, filename, path, false);
        pDialog.setMessage("Uploading...");
        showDialog();
        new MarketStaffRegister.UploadFileToServerimg().execute(Appconfig.compressImage(storedPath, MarketStaffRegister.this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                String url = data.getStringExtra("url");
                showDialog();
                new UploadSignToServer().execute(url);
            }
        }else {
            imageutils.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MarketStaffRegister.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private class UploadFileToServerimg extends AsyncTask<String, Integer, String> {
        public long totalSize = 0;
        String filepath;

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pDialog.setMessage("Uploading..." + (progress[0]));
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
            HttpPost httppost = new HttpPost(Appconfig.URL_IMAGE_UPLOAD_LATEST);
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
                entity.addPart("model", new StringBody(staffName.getText().toString()));
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
                JSONObject jsonObject = new JSONObject(result);
                if (!jsonObject.getBoolean("error")) {
                    String model = jsonObject.getString("model");
                    String imageUrl = Appconfig.ip_img + "uploads/" + model + "/" + imageutils.getfilename_from_path(filepath);

                    if(uploadOrder==1){
                        samplesList.add(imageUrl);
                        naddImageAdapter.notifyData(samplesList);
                    }else {
                        attachementList.add(imageUrl);
                        maddImageAdapter.notifyData(attachementList);
                    }

                } else {
                    imageUrl = null;
                }
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (Error | Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
            }
            hideDialog();
            // showing the server response in an alert dialog
            //showAlert(result);


            super.onPostExecute(result);
        }

    }


    private class UploadSignToServer extends AsyncTask<String, Integer, String> {
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
            HttpPost httppost = new HttpPost(Appconfig.URL_IMAGE_UPLOAD_LATEST);
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
                entity.addPart("model", new StringBody("sign"));
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
                            .into(signview);
                    String model = jsonObject.getString("model");
                    signUrl = Appconfig.ip_img + "uploads/" + model + "/" + imageutils.getfilename_from_path(filepath);
                } else {
                    signUrl = null;
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



}
