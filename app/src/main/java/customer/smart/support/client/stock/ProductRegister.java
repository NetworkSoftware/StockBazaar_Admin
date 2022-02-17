package customer.smart.support.client.stock;

import static customer.smart.support.app.Appconfig.SHOP;
import static customer.smart.support.app.Appconfig.STOCK;
import static customer.smart.support.app.Appconfig.mypreference;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import customer.smart.support.R;
import customer.smart.support.StartActivity;
import customer.smart.support.app.AndroidMultiPartEntity;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.Imageutils;
import customer.smart.support.attachment.ActivityMediaOnline;
import customer.smart.support.client.bulkPrice.BulkPriceBeen;

public class ProductRegister extends AppCompatActivity
        implements Imageutils.ImageAttachmentListener, ImageClick {

    private final String[] STOCKUPDATE = new String[]{
            "In Stock", "Currently Unavailable",
    };

    String[] SHOPNAME = new String[]{"Loading"};
    String[] BRAND = new String[]{"Loading"};
    String[] CATEGORY = new String[]{"Loading"};
    TextInputLayout brandTxt, modelTxt,
            descriptionTxt;
    TextInputEditText model,
            description, selectshop;
    AddImageAdapter maddImageAdapter;
    MaterialBetterSpinner stock_update;
    MaterialBetterSpinner brand;
    String studentId = null;
    TextView submit;
    Imageutils imageutils;
    CardView itemsAdd;
    MaterialBetterSpinner selectcategories;
    SharedPreferences sharedpreferences;
    ArrayList<BulkPriceBeen> bulkPriceBeens = new ArrayList<>();
    CheckBox isNotify;
    TextView qtyOne;
    TextInputEditText price;
    TextView qtyThree;
    TextInputEditText priceThree;
    TextView qtyFive;
    TextInputEditText priceFive;
    private Map<String, String> nameIdMap = new HashMap<>();
    private Map<String, String> idNameMap = new HashMap<>();
    private Map<String, String> idBrandMap = new HashMap<>();
    private ProgressDialog pDialog;
    private RecyclerView imagelist;
    private ArrayList<String> samplesList = new ArrayList<>();
    private String imageUrl = "";
    private Product product = null;
    private String shopname = "STOCK BAZAAR";
    private String shopid = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_stock_register);
        imageutils = new Imageutils(this);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        qtyOne = findViewById(R.id.qtyOne);
        qtyThree = findViewById(R.id.qtyThree);
        priceThree = findViewById(R.id.priceThree);
        priceFive = findViewById(R.id.priceFive);
        isNotify = findViewById(R.id.isNotify);
        qtyFive = findViewById(R.id.qtyFive);
        itemsAdd = findViewById(R.id.itemsAdd);
        ImageView image_wallpaper = findViewById(R.id.image_wallpaper);
        image_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (brand.getText().toString().length() > 0) {
                    imageutils.imagepicker(1);
                } else {
                    Toast.makeText(getApplicationContext(), "Please select brand", Toast.LENGTH_SHORT).show();
                }
            }
        });
        samplesList = new ArrayList<>();
        imagelist = findViewById(R.id.imagelist);
        maddImageAdapter = new AddImageAdapter(this, samplesList, this);
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imagelist.setLayoutManager(addManager1);
        imagelist.setAdapter(maddImageAdapter);
        stock_update = findViewById(R.id.stock_update);
        selectshop = findViewById(R.id.selectShop);
        brandTxt = findViewById(R.id.brandTxt);
        modelTxt = findViewById(R.id.modelTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        brand = findViewById(R.id.brand);
        model = findViewById(R.id.model);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);

        String type = getIntent().getStringExtra("SHOPNAME");
        shopid = getIntent().getStringExtra("SHOPID");
        shopname = type;
        selectshop.setText(type);
        selectshop.setFocusableInTouchMode(false);

        selectcategories = findViewById(R.id.selectcategories);
        if ("MOBILE SPARES".equalsIgnoreCase(selectcategories.getText().toString())) {
            brand.setHint("Select Items");
        } else {
            brand.setHint("Select Brand");
        }


        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        selectcategories.setAdapter(categoryAdapter);
        selectcategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BRAND = idBrandMap.containsKey(selectcategories.getText().toString()) ?
                        idBrandMap.get(selectcategories.getText().toString()).split(",") :
                        idBrandMap.get(SHOPNAME[0]).split(",");

                if ("MOBILE SPARES".equalsIgnoreCase(selectcategories.getText().toString())) {
                    brand.setHint("Select Item");
                } else {
                    brand.setHint("Select Brand");
                }
                ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(ProductRegister.this,
                        android.R.layout.simple_dropdown_item_1line, BRAND);
                brand.setAdapter(brandAdapter);
                brand.setText("");
            }
        });

        //brand
        brand = findViewById(R.id.brand);
        //stock update
        ArrayAdapter<String> stockAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, STOCKUPDATE);
        stock_update.setAdapter(stockAdapter);
        stock_update.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        bulkPriceBeens = new ArrayList<>();
        submit = findViewById(R.id.submit);
        submit.setText("SUBMIT");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (brand.getText().toString().length() <= 0) {
                    brand.setError("Select the Brand");
                } else if (price.getText().toString().length() <= 0) {
                    price.setError("Enter the Price");
                } else if (selectshop.getText().toString().length() <= 0) {
                    selectshop.setError("Enter the Shop Name");
                } else if (selectcategories.getText().toString().length() <= 0) {
                    selectcategories.setError("Enter the Category");
                } else if (model.getText().toString().length() <= 0) {
                    model.setError("Enter the Model");
                } else if (description.getText().toString().length() <= 0) {
                    description.setError("Enter the Description");
                } else if (stock_update.getText().toString().length() <= 0) {
                    stock_update.setError("Select the Sold or Not");
                } else if (price.getText().toString().length() <= 0) {
                    price.setError("Please enter the price");
                } else if (stock_update.getText().toString().length() <= 0) {
                    stock_update.setError("Select the Sold or Not");
                } else if (samplesList.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Upload the Images!", Toast.LENGTH_SHORT).show();
                } else {
                    bulkPriceBeens = new ArrayList<>();
                    if (priceThree.getText().length() > 0) {
                        bulkPriceBeens.add(new BulkPriceBeen("3", priceThree.getText().toString()));
                    }
                    if (priceFive.getText().length() > 0) {
                        bulkPriceBeens.add(new BulkPriceBeen("5", priceFive.getText().toString()));
                    }
                    if (bulkPriceBeens.size() > 0) {
                        bulkPriceBeens.add(new BulkPriceBeen("1", price.getText().toString()));
                    }
                    stockRegister();

                }

            }
        });

        if (sharedpreferences.getString(
                "role", "admin").equalsIgnoreCase("sadmin")) {
            isNotify.setVisibility(View.VISIBLE);
        } else {
            isNotify.setVisibility(View.GONE);
        }

        try {
            product = (Product) getIntent().getSerializableExtra("data");
            studentId = product.id;
            brand.setText(product.brand);
            model.setText(product.model);
            price.setText(product.price);
            description.setText(product.description);
            if (product.shopName != null) {
                selectshop.setText(product.shopName);
            } else {
                selectshop.setText(type);
            }
            String categoryIntend = getIntent().getStringExtra("CATEGORY");
            if (categoryIntend != null) {
                selectcategories.setText(categoryIntend);
            } else {
                selectcategories.setText(product.categoryName);
            }

            stock_update.setText(product.stock_update);
            imageUrl = product.image;
            if (imageUrl == null) {
                imageUrl = "";
            } else {
                samplesList = new Gson().fromJson(imageUrl, (Type) List.class);
            }
            maddImageAdapter.notifyData(samplesList);

            if (product.getBulkPrice() == null || product.getBulkPrice().equalsIgnoreCase("null")) {
                bulkPriceBeens = new ArrayList<>();
            } else {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Object listBeans = new Gson().fromJson(product.getBulkPrice(),
                            Object.class);
                    bulkPriceBeens = mapper.convertValue(
                            listBeans,
                            new TypeReference<ArrayList<BulkPriceBeen>>() {
                            }
                    );
                } catch (Exception e) {
                    Log.e("xxxxxxxxxx", e.toString());
                }
            }
            if (bulkPriceBeens == null) {
                bulkPriceBeens = new ArrayList<>();
            }

            for (int i = 0; i < bulkPriceBeens.size(); i++) {
                BulkPriceBeen bulkPriceBeen = bulkPriceBeens.get(i);
                int qty = Integer.parseInt(bulkPriceBeen.getQuantity());
                if (qty >= 5) {
                    priceFive.setText(bulkPriceBeen.getQty_price());
                } else if (qty > 1) {
                    priceThree.setText(bulkPriceBeen.getQty_price());
                } else if (qty == 1) {
                    price.setText(bulkPriceBeen.getQty_price());
                }
            }

        } catch (Exception e) {
            Log.e("xxxxxxxxxxx", e.toString());
        }
        getAllCategories(shopid);
    }

    private void stockRegister() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Updateing ...");
        showDialog();
        int method = Request.Method.POST;
        if (product != null) {
            method = Request.Method.PUT;
        }
        String url = STOCK;
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
                        onBackPressed();
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
                if (product != null) {
                    localHashMap.put("id", studentId);
                }
                localHashMap.put("brand", brand.getText().toString());
                localHashMap.put("model", model.getText().toString());
                localHashMap.put("price", price.getText().toString());
                localHashMap.put("bulkPrice", new Gson().toJson(bulkPriceBeens));
                localHashMap.put("description", description.getText().toString());
                localHashMap.put("selectshop", shopid);
                String[] selectedNames = selectcategories.getText().toString().split(",");
                StringBuilder selectedIds = new StringBuilder();
                for (int i = 0; i < selectedNames.length; i++) {
                    selectedIds.append(nameIdMap.get(selectedNames[i]));
                    if (i != selectedNames.length - 1) {
                        selectedIds.append(",");
                    }
                }
                localHashMap.put("category", selectedIds.toString());
                localHashMap.put("stockupdate", stock_update.getText().toString());
                localHashMap.put("mobile", sharedpreferences.getString(Appconfig.AdminPhone, ""));
                localHashMap.put("image", new Gson().toJson(samplesList));
                localHashMap.put("isNoti", isNotify.isChecked() ? "true" : "false");
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void getAllCategories(String shopname) {
        String tag_string_req = "req_register";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                SHOP + "?shopname=" + shopname, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("category");
                        nameIdMap = new HashMap<>();
                        idNameMap = new HashMap<>();
                        idBrandMap = new HashMap<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            nameIdMap.put(jsonObject.getString("title"),
                                    jsonObject.getString("id"));
                            idNameMap.put(jsonObject.getString("id"),
                                    jsonObject.getString("title"));
                            idBrandMap.put(jsonObject.getString("title"),
                                    jsonObject.getString("brand"));
                        }
                        JSONArray dataArray = jObj.getJSONArray("data");
                        for (int l = 0; l < dataArray.length(); l++) {
                            JSONObject jsonObject = dataArray.getJSONObject(l);
                            String[] selectedIds = jsonObject.getString("category").split(",");
                            StringBuilder selectedNames = new StringBuilder();
                            for (int i = 0; i < selectedIds.length; i++) {
                                selectedNames.append(idNameMap.get(selectedIds[i]));
                                if (i != selectedIds.length - 1) {
                                    selectedNames.append(",");
                                }
                            }
                            SHOPNAME = selectedNames.toString().split(",");
                            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(ProductRegister.this,
                                    android.R.layout.simple_dropdown_item_1line, SHOPNAME);
                            selectcategories.setAdapter(categoryAdapter);

                            BRAND = idBrandMap.get(SHOPNAME[0]).split(",");
                            ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(ProductRegister.this,
                                    android.R.layout.simple_dropdown_item_1line, BRAND);
                            brand.setAdapter(brandAdapter);
                        }
                    }

                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
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

    private void deleteUser() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.DELETE,
                STOCK + "?id=" + studentId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String msg = jsonObject.getString("message");
                    if (success) {
                        onBackPressed();
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
                localHashMap.put("id", studentId);
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
        if (requestCode != 123) {
            imageutils.request_permission_result(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        String path = getCacheDir().getPath() + File.separator + "ImageAttach" + File.separator;
        File storedFile = imageutils.createImage(file, filename, path, false);
        pDialog.setMessage("Uploading...");
        showDialog();
        new UploadFileToServer().execute(Appconfig.compressImage(storedFile.getPath(), ProductRegister.this));
    }

    @Override
    public void onImageClick(int position) {
        Intent localIntent = new Intent(ProductRegister.this, ActivityMediaOnline.class);
        localIntent.putExtra("filePath", samplesList.get(position));
        localIntent.putExtra("isImage", true);
        startActivity(localIntent);
    }


    @Override
    public void onDeleteClick(int position) {
        samplesList.remove(position);
        maddImageAdapter.notifyData(samplesList);
    }

    @Override
    public void itemEditClick(int position) {

    }

    @Override
    public void itemDownload(String position) {
        DownloadImage(position);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageutils.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog diaBox = AskOption();
                diaBox.show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.ic_delete_black_24dp)

                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        dialog.dismiss();
                        deleteUser();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("from") != null
                && getIntent().getStringExtra("from").equalsIgnoreCase("admin")) {
            Intent intent = new Intent(ProductRegister.this, StartActivity.class);
            startActivity(intent);
            finishAffinity();
        } else {
            super.onBackPressed();
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
                entity.addPart("model", new StringBody(brand.getText().toString()));
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
                    String imageUrl = Appconfig.ip_img + "uploads/" + model + "/" +
                            imageutils.getfilename_from_path(filepath);
                    samplesList.add(imageUrl);
                    maddImageAdapter.notifyData(samplesList);
                } else {
                    imageUrl = null;
                }
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (Error | Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
            }
            hideDialog();
            super.onPostExecute(result);
        }

    }



    void DownloadImage(String ImageUrl) {
        if (ContextCompat.checkSelfPermission(ProductRegister.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(ProductRegister.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProductRegister.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        } else {
            showToast("Downloading Image...");
            new DownloadsImage().execute(ImageUrl);
        }
    }

    class DownloadsImage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/StockBazaar"); //Creates app specific folder

            if (!path.exists()) {
                path.mkdirs();
            }

            File imageFile = new File(path, System.currentTimeMillis() + ".png");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                out.flush();
                out.close();
                MediaScannerConnection.scanFile(ProductRegister.this, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showToast("Image Saved!");
        }
    }

    protected void showToast(String text) {
        Toast.makeText(ProductRegister.this, text, Toast.LENGTH_SHORT).show();
    }
}



