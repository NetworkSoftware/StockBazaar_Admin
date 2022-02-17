package customer.smart.support.offer;

import static customer.smart.support.app.Appconfig.FETCHOFFERPRODUCTID;
import static customer.smart.support.app.Appconfig.mypreference;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import customer.smart.support.R;
import customer.smart.support.app.AndroidMultiPartEntity;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;
import customer.smart.support.app.Imageutils;
import customer.smart.support.attachment.ActivityMediaOnline;
import customer.smart.support.attachment.AttachmentBaseAdapter;
import customer.smart.support.attachment.Base;
import customer.smart.support.attachment.BaseClick;

/**
 * Created by user_1 on 11-07-2018.
 */

public class OfferRegister extends AppCompatActivity implements BaseClick, Imageutils.ImageAttachmentListener {

    private final String[] CATEGORY = new String[]{
            "Customer", "Dealer", "All",
    };
    private final ArrayList<Base> bases = new ArrayList<>();
    EditText name, price;
    EditText startDate;
    EditText endDate;
    EditText minQuantity;
    EditText maxQuantity;
    SharedPreferences sharedpreferences;
    Imageutils imageutils;
    MaterialBetterSpinner category;
    AutoCompleteTextView productId;
        private TextView submit;
    private ProgressDialog pDialog;
private ImageView image;
    private String imageUrl = null;
    private String[] PRODUCTID = new String[]{
            "Loading",
    };
    private RecyclerView baseList;
    private AttachmentBaseAdapter attachmentBaseAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_register);

        imageutils = new Imageutils(this);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        category = findViewById(R.id.category);
        ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        category.setAdapter(titleAdapter);
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        //

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, PRODUCTID);
        productId.setThreshold(2);
        productId.setAdapter(adapter);
        image = findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageutils.imagepicker(1);
                imageutils.setImageAttachmentListener(new Imageutils.ImageAttachmentListener() {
                    @Override
                    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
                        String path = getCacheDir().getPath() + File.separator + "ImageAttach" + File.separator;
                        File storedFile = imageutils.createImage(file, filename, path, false);
                        pDialog.setMessage("Uploading...");
                        showDialog();
                        new UploadFileToServer().execute(Appconfig.compressImage(storedFile.getPath(), OfferRegister.this));
                    }
                });
            }
        });

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        minQuantity = findViewById(R.id.minQuantity);
        maxQuantity = findViewById(R.id.maxQuantity);


        bases.add(new Base("", "true"));
        baseList = findViewById(R.id.attachmentList);
        attachmentBaseAdapter = new AttachmentBaseAdapter(this, bases, this);
        baseList.setLayoutManager(new GridLayoutManager(this, 3));
        baseList.setAdapter(attachmentBaseAdapter);


        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().length() > 0 &&
                        startDate.getText().toString().length() > 0 &&
                        category.getText().toString().length() > 0 &&
                        endDate.getText().toString().length() > 0
                ) {

                   /* if (imageUrl == null) {
                        Toast.makeText(getApplicationContext(), "Select a Image", Toast.LENGTH_SHORT).show();
                    } else {*/

                    Offer shop = new Offer(name.getText().toString(),
                            imageUrl, startDate.getText().toString(),
                            category.getText().toString(),
                            endDate.getText().toString(), minQuantity.getText().toString(), maxQuantity.getText().toString());
                    pDialog.show();
                    new UploadDataToServer().execute();
                }

            }
        });


        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(OfferRegister.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    int latestmonth = monthOfYear + 1;
                                    String monthConverted = "" + latestmonth;
                                    if (latestmonth < 10) {
                                        monthConverted = "0" + monthConverted;
                                    }


                                    String dateConverted = "" + dayOfMonth;
                                    if (dayOfMonth < 10) {
                                        dateConverted = "0" + dayOfMonth;
                                    }
                                    startDate.setText(year + "-" + monthConverted + "-" + dateConverted);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            }
        });


        endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(OfferRegister.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    int latestmonth = monthOfYear + 1;
                                    String monthConverted = "" + latestmonth;
                                    if (latestmonth < 10) {
                                        monthConverted = "0" + monthConverted;
                                    }


                                    String dateConverted = "" + dayOfMonth;
                                    if (dayOfMonth < 10) {
                                        dateConverted = "0" + dayOfMonth;
                                    }
                                    endDate.setText(year + "-" + monthConverted + "-" + dateConverted);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            }
        });
        getAllStocks();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageutils.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBaseClick(final int position) {
        if (position == 0) {
            imageutils.imagepicker(1);
            imageutils.setImageAttachmentListener(new Imageutils.ImageAttachmentListener() {
                @Override
                public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
                    String path = getCacheDir() + File.separator + "ImageAttach" + File.separator;
                    Base base = new Base();
                    base.setUrl(imageutils.getPath(uri));
                    base.setIsImage("false");
                    if (filename != null) {
                        base.setIsImage("true");
                        imageutils.createImage(file, filename, path, false);
                    }
                    pDialog.setMessage("Uploading...");
                    showDialog();
                    new UploadFileToServerArray().execute(imageutils.getPath(uri));

                }
            });
        } else {
            Intent localIntent = new Intent(OfferRegister.this, ActivityMediaOnline.class);
            localIntent.putExtra("filePath", bases.get(position).getUrl());
            localIntent.putExtra("isImage", Boolean.parseBoolean(bases.get(position).getIsImage()));
            startActivity(localIntent);
        }

    }

    @Override
    public void onDeleteClick(int position) {
        bases.remove(position);
        attachmentBaseAdapter.notifyData(bases);
    }

    private void getAllStocks() {
        String tag_string_req = "req_register";
        //  pDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                FETCHOFFERPRODUCTID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        //  JSONArray jsonArray = jObj.getJSONArray("productId");
                        JSONArray productId = jObj.getJSONArray("productId");
                        PRODUCTID = new String[productId.length()];
                        for (int i = 0; i < productId.length(); i++) {
                            PRODUCTID[i] = productId.getString(i);
                        }

                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(OfferRegister.this,
                        android.R.layout.simple_dropdown_item_1line, PRODUCTID);
                productId.setAdapter(stateAdapter);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private class UploadDataToServer extends AsyncTask<String, Integer, String> {
        public long totalSize = 0;

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
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Appconfig.CREATE_OFFER);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                entity.addPart("price", new StringBody(price.getText().toString()));
                entity.addPart("name", new StringBody(name.getText().toString()));
                entity.addPart("image", new StringBody(imageUrl));
                entity.addPart("endDate", new StringBody(endDate.getText().toString()));
                entity.addPart("startDate", new StringBody(startDate.getText().toString()));
                entity.addPart("isDealer", new StringBody(category.getText().toString()));
                entity.addPart("productId", new StringBody(productId.getText().toString()));
                entity.addPart("minQuantity", new StringBody(minQuantity.getText().toString()));
                entity.addPart("maxQuantity", new StringBody(maxQuantity.getText().toString()));
                for (int i = 0; i < bases.size(); i++) {
                    entity.addPart("image1[]", new StringBody(bases.get(i).getUrl()));
                }

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
            hideDialog();
            Log.e("Response from server: ", result);
            try {
                JSONObject jObj = new JSONObject(result.split("0000")[1]);
                int success = jObj.getInt("success");
                String msg = jObj.getString("message");
                if (success == 1) {
                    finish();
                    //  sendNotification(name.getText().toString(), category.getText().toString());
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
            // showing the server response in an alert dialog
            //showAlert(result);


            super.onPostExecute(result);
        }

    }

    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
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
                entity.addPart("model", new StringBody("offer_" + name.getText().toString()));
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
                    GlideApp.with(getApplicationContext())
                            .load(filepath)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                            .placeholder(R.drawable.profile)
                            .into(image);
                    String model = jsonObject.getString("model");
                    imageUrl = Appconfig.ip_img + "uploads/" + model + "/" + imageutils.getfilename_from_path(filepath);
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

    private class UploadFileToServerArray extends AsyncTask<String, Integer, String> {
        public long totalSize = 0;
        String filepath;

        @Override
        protected void onPreExecute() {

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
                entity.addPart("model", new StringBody("offer_" + name.getText().toString()));
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
            hideDialog();
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (!jsonObject.getBoolean("error")) {
                    String model = jsonObject.getString("model");
                    String imageUrl = Appconfig.ip_img + "uploads/" + model + "/" + imageutils.getfilename_from_path(filepath);
                    Base base = new Base();
                    base.setUrl(imageUrl);
                    base.setIsImage("true");
                    bases.add(base);
                    attachmentBaseAdapter.notifyDataItem(bases, bases.size() + 1);
                }
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
            }

            // showing the server response in an alert dialog
            //showAlert(result);


            super.onPostExecute(result);
        }

    }

}