package customer.smart.support.client.category;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import customer.smart.support.R;
import customer.smart.support.app.AndroidMultiPartEntity;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;
import customer.smart.support.app.Imageutils;
import customer.smart.support.client.price.PercentagePriceAdapter;
import customer.smart.support.client.price.PercentagePriceBeen;
import customer.smart.support.client.stock.ImageClick;

import static customer.smart.support.app.Appconfig.CATEGORIES;

public class CategoriesRegister extends AppCompatActivity implements Imageutils.ImageAttachmentListener {

    TextInputEditText description, brand, tag;
    String studentId = null;
    TextView submit;
    Imageutils imageutils;
    private ProgressDialog pDialog;
    private ImageView profiletImage;
    private String imageUrl = "";
    private Categories categories = null;
    public MaterialButton addBulkPrice;
    ArrayList<PercentagePriceBeen> percentagePriceBeens = new ArrayList<>();
    PercentagePriceAdapter percentagePriceAdapter;
    private RecyclerView percentagepricelist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_register);

        imageutils = new Imageutils(this);

        getSupportActionBar().setTitle("Categories Register");

        profiletImage = (ImageView) findViewById(R.id.profiletImage);
        profiletImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(1);
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        description = findViewById(R.id.description);

        tag = findViewById(R.id.tag);
        brand = findViewById(R.id.brand);
        addBulkPrice = findViewById(R.id.addPrice);
        percentagepricelist = findViewById(R.id.pricelist);
        percentagePriceBeens = new ArrayList<>();
        addBulkPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBulkPercentagePrice();
            }
        });
        percentagePriceAdapter = new PercentagePriceAdapter(this, percentagePriceBeens, new ImageClick() {
            @Override
            public void onImageClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                percentagePriceBeens.remove(position);
                percentagePriceAdapter.notifyData(percentagePriceBeens);
            }
        }, true);
        final LinearLayoutManager sizeManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        percentagepricelist.setLayoutManager(sizeManager);
        percentagepricelist.setAdapter(percentagePriceAdapter);
        submit = (TextView) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryReg();
            }
        });

        try {
            categories = (Categories) getIntent().getSerializableExtra("data");
            description.setText(categories.title);
            brand.setText(categories.brand);
            tag.setText(categories.tag);
            studentId = categories.id;
            imageUrl = categories.image;

            GlideApp.with(CategoriesRegister.this)
                    .load(Appconfig.getResizedImage(categories.getImage(), true))
                    .placeholder(R.drawable.ic_add_a_photo_black_24dp)
                    .into(profiletImage);
            if (categories.getPercentage() == null || categories.getPercentage().equalsIgnoreCase("null")) {
                percentagePriceBeens = new ArrayList<>();
            } else {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Object listBeans = new Gson().fromJson(categories.getPercentage(),
                            Object.class);
                    percentagePriceBeens = mapper.convertValue(
                            listBeans,
                            new TypeReference<ArrayList<PercentagePriceBeen>>() {
                            }
                    );
                } catch (Exception e) {
                    Log.e("xxxxxxxxxx", e.toString());
                }
            }
            if (percentagePriceBeens == null) {
                percentagePriceBeens = new ArrayList<>();
            }
            percentagePriceAdapter.notifyData(percentagePriceBeens);

        } catch (Exception e) {
            Log.e("xxxxxxxxxxx", e.toString());

        }
    }

    private void showBulkPercentagePrice() {
        final RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(CategoriesRegister.this);
        LayoutInflater inflater = CategoriesRegister.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bottom_percentageprice_layout, null);

        final TextInputLayout qtyTxt = dialogView.findViewById(R.id.qtyLayoutTxt);
        final TextInputEditText pricePercentage = dialogView.findViewById(R.id.pricePercentage);
        final TextInputEditText priceRange = dialogView.findViewById(R.id.priceRange);

        final Button submit = dialogView.findViewById(R.id.submit);
        qtyTxt.setVisibility(View.VISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pricePercentage.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Enter Valid Size", Toast.LENGTH_LONG).show();
                    return;
                } else if (priceRange.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Enter Valid Price", Toast.LENGTH_LONG).show();
                    return;
                }
                percentagePriceBeens.add(new PercentagePriceBeen(pricePercentage.getText().toString(), priceRange.getText().toString()));
                percentagePriceAdapter.notifyData(percentagePriceBeens);
                mBottomSheetDialog.cancel();
            }
        });
        pricePercentage.requestFocus();
        priceRange.requestFocus();

        mBottomSheetDialog.setContentView(dialogView);
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RoundedBottomSheetDialog d = (RoundedBottomSheetDialog) dialog;
                        FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 0);
            }
        });
        mBottomSheetDialog.show();
    }

    private void categoryReg() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Updateing ...");
        showDialog();
        int method = Request.Method.POST;
        if (categories != null) {
            method = Request.Method.PUT;
        }
        String url = CATEGORIES;
        StringRequest strReq = new StringRequest(method,
                url, new Response.Listener<String>() {
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
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                if (categories != null) {
                    localHashMap.put("id", studentId);
                }
                localHashMap.put("image", imageUrl);
                localHashMap.put("title", description.getText().toString());
                localHashMap.put("brand", brand.getText().toString());
                localHashMap.put("tag", tag.getText().toString());
                localHashMap.put("percentage", new Gson().toJson(percentagePriceBeens));
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        String path = getCacheDir().getPath() + File.separator + "ImageAttach" + File.separator;
        File storedFile = imageutils.createImage(file, filename, path, false);
        pDialog.setMessage("Uploading...");
        showDialog();
        new UploadFileToServer().execute(Appconfig.compressImage(storedFile.getPath(), CategoriesRegister.this));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageutils.onActivityResult(requestCode, resultCode, data);

    }

    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
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
                JSONObject jsonObject = new JSONObject(result);
                if (!jsonObject.getBoolean("error")) {
                    GlideApp.with(getApplicationContext())
                            .load(filepath)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                            .placeholder(R.drawable.profile)
                            .into(profiletImage);
                    imageUrl = Appconfig.ip + "/images/" + imageutils.getfilename_from_path(filepath);
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
}
