package customer.smart.support.client.seller_as;

import static customer.smart.support.app.Appconfig.SELLER;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import customer.smart.support.R;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.attachment.ActivityMediaOnline;

public class MainActivitySeller extends AppCompatActivity implements SellerClick {
    private static final String TAG = MainActivitySeller.class.getSimpleName();
    ProgressDialog progressDialog;
    EditText sellerId;
    private RecyclerView recyclerRequest, recyclerPending, recycler_viewApproved;
    private List<Seller> sellers;
    private SellerRequestAdapter sellerRequestAdapter;
    private SellerApprovedAdapter sellerApprovedAdapter;
    private SellerPendingAdapter sellerPendingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Seller Detalis");
        recyclerRequest = findViewById(R.id.recycler_viewRequest);
        recyclerPending = findViewById(R.id.recycler_viewPending);
        recycler_viewApproved = findViewById(R.id.recycler_viewApproved);
        //request
        sellers = new ArrayList<>();
        sellerRequestAdapter = new SellerRequestAdapter(this, sellers, this);
        final LinearLayoutManager addManager1 = new GridLayoutManager(MainActivitySeller.this,
                1);
        recyclerRequest.setLayoutManager(addManager1);
        recyclerRequest.setAdapter(sellerRequestAdapter);

        //pending
        sellers = new ArrayList<>();
        sellerPendingAdapter = new SellerPendingAdapter(this, sellers, this);
        final LinearLayoutManager addManagerpending = new GridLayoutManager(MainActivitySeller.this,
                1);
        recyclerPending.setLayoutManager(addManagerpending);
        recyclerPending.setAdapter(sellerPendingAdapter);
        pendingShop("");

        //approved
        sellers = new ArrayList<>();
        sellerApprovedAdapter = new SellerApprovedAdapter(this, sellers, this);
        final LinearLayoutManager addManagerapproved = new GridLayoutManager(MainActivitySeller.this,
                1);
        recycler_viewApproved.setLayoutManager(addManagerapproved);
        recycler_viewApproved.setAdapter(sellerApprovedAdapter);
        approvedShop("");

        whiteNotificationBar(recyclerRequest);


        ImageView orderIdSearch = findViewById(R.id.productIdSearch);
        sellerId = findViewById(R.id.productId);
        orderIdSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sellerId.getText().toString().length() > 0) {
                    sellerId.setError(null);
                    requestShop(sellerId.getText().toString());
                } else {
                    sellerId.setError("Enter valid Name / Phone / Id");
                }
            }
        });
    }

    /**
     * fetches json by making http calls
     */
    private void requestShop(String search) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        //  showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                SELLER + "?searchkey=" + search + "&status=Request", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        sellers = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Seller seller = new Seller();
                            seller.setId(jsonObject.getString("id"));
                            seller.setShopname(jsonObject.getString("shopname"));
                            seller.setMobile(jsonObject.getString("mobile"));
                            seller.setDistrict(jsonObject.getString("district"));
                            seller.setPincode(jsonObject.getString("pincode"));
                            seller.setShopInside(jsonObject.getString("shopInside"));
                            seller.setShopOutside(jsonObject.getString("shopOutside"));
                            seller.setStatus(jsonObject.getString("status"));
                            seller.setGst(Boolean.parseBoolean(jsonObject.getString("gst")));
                            sellers.add(seller);
                        }
                        sellerRequestAdapter.notifyData(sellers);
                        getSupportActionBar().setSubtitle(sellers.size() + " Nos");

                    } else {
                        Toast.makeText(MainActivitySeller.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivitySeller.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivitySeller.this,
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

    private void pendingShop(String search) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        //  showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                SELLER + "?searchkey=" + search + "&status=Pending", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        sellers = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Seller seller = new Seller();
                            seller.setId(jsonObject.getString("id"));
                            seller.setShopname(jsonObject.getString("shopname"));
                            seller.setMobile(jsonObject.getString("mobile"));
                            seller.setDistrict(jsonObject.getString("district"));
                            seller.setPincode(jsonObject.getString("pincode"));
                            seller.setShopInside(jsonObject.getString("shopInside"));
                            seller.setShopOutside(jsonObject.getString("shopOutside"));
                            seller.setStatus(jsonObject.getString("status"));
                            seller.setGst(Boolean.parseBoolean(jsonObject.getString("gst")));
                            sellers.add(seller);
                        }
                        sellerPendingAdapter.notifyData(sellers);
                        getSupportActionBar().setSubtitle(sellers.size() + " Nos");

                    } else {
                        Toast.makeText(MainActivitySeller.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivitySeller.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivitySeller.this,
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

    private void approvedShop(String search) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        //  showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                SELLER + "?searchkey=" + search + "&status=Approved", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        sellers = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Seller seller = new Seller();
                            seller.setId(jsonObject.getString("id"));
                            seller.setShopname(jsonObject.getString("shopname"));
                            seller.setMobile(jsonObject.getString("mobile"));
                            seller.setDistrict(jsonObject.getString("district"));
                            seller.setPincode(jsonObject.getString("pincode"));
                            seller.setShopInside(jsonObject.getString("shopInside"));
                            seller.setShopOutside(jsonObject.getString("shopOutside"));
                            seller.setStatus(jsonObject.getString("status"));
                            seller.setGst(Boolean.parseBoolean(jsonObject.getString("gst")));
                            sellers.add(seller);
                        }
                        sellerApprovedAdapter.notifyData(sellers);
                        getSupportActionBar().setSubtitle(sellers.size() + " Nos");

                    } else {
                        Toast.makeText(MainActivitySeller.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivitySeller.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivitySeller.this,
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

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        requestShop("");
    }


    private AlertDialog AskOption(final int position) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.ic_delete_black_24dp)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteFile(position);
                        dialog.dismiss();
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


    private void deleteFile(final int position) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Fetching ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.DELETE,
                SELLER + "?id=" + sellers.get(position).id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getBoolean("success")) {
                        sellers.remove(position);
                        sellerRequestAdapter.notifyData(sellers);
                        sellerPendingAdapter.notifyData(sellers);
                        sellerApprovedAdapter.notifyData(sellers);
                    }
                    Toast.makeText(MainActivitySeller.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivitySeller.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivitySeller.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
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

    @Override
    public void onDetailClick(Seller seller) {
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.alert_seller_details, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(dialogView);

        final TextView shopname = dialogView.findViewById(R.id.shopname);
        final TextView gst = dialogView.findViewById(R.id.gst);
        final LinearLayout gstLi = dialogView.findViewById(R.id.gstLi);
        final TextView mobile = dialogView.findViewById(R.id.mobile);
        final TextView district = dialogView.findViewById(R.id.district);
        final TextView pincode = dialogView.findViewById(R.id.pincode);
        final ImageView insideOne = dialogView.findViewById(R.id.insideOne);
        final ImageView insideTwo = dialogView.findViewById(R.id.insideTwo);
        final ImageView insideThree = dialogView.findViewById(R.id.insideThree);
        final ImageView outsideOne = dialogView.findViewById(R.id.outsideOne);
        final ImageView outsideTwo = dialogView.findViewById(R.id.outsideTwo);
        final ImageView outsideThree = dialogView.findViewById(R.id.outsideThree);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        shopname.setText(seller.shopname);
        mobile.setText(seller.mobile);
        district.setText(seller.district);
        pincode.setText(seller.pincode);
        if (seller.gst == true) {
            gst.setText("Gst Holder");
            gstLi.setVisibility(View.VISIBLE);
        } else {
            gstLi.setVisibility(View.GONE);
        }
        try {
            ArrayList<String> urls = new Gson().fromJson(seller.shopInside, (Type) List.class);
            Glide.with(MainActivitySeller.this)
                    .load(Appconfig.getResizedImage(urls.get(0), false))
                    .into(insideOne);
            insideOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent localIntent = new Intent(MainActivitySeller.this, ActivityMediaOnline.class);
                    localIntent.putExtra("filePath", urls.get(0));
                    localIntent.putExtra("isImage", true);
                    startActivity(localIntent);
                }
            });
            Glide.with(MainActivitySeller.this)
                    .load(Appconfig.getResizedImage(urls.get(1), false))
                    .into(insideTwo);
            insideTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent localIntent = new Intent(MainActivitySeller.this, ActivityMediaOnline.class);
                    localIntent.putExtra("filePath", urls.get(1));
                    localIntent.putExtra("isImage", true);
                    startActivity(localIntent);
                }
            });
            Glide.with(MainActivitySeller.this)
                    .load(Appconfig.getResizedImage(urls.get(2), false))
                    .into(insideThree);
            insideThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent localIntent = new Intent(MainActivitySeller.this, ActivityMediaOnline.class);
                    localIntent.putExtra("filePath", urls.get(2));
                    localIntent.putExtra("isImage", true);
                    startActivity(localIntent);
                }
            });
        } catch (Exception e) {
            Log.e("", e.toString());
        }
        try {

            ArrayList<String> urlsone = new Gson().fromJson(seller.shopOutside, (Type) List.class);
            Glide.with(MainActivitySeller.this)
                    .load(Appconfig.getResizedImage(urlsone.get(0), false))
                    .into(outsideOne);
            outsideOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent localIntent = new Intent(MainActivitySeller.this, ActivityMediaOnline.class);
                    localIntent.putExtra("filePath", urlsone.get(0));
                    localIntent.putExtra("isImage", true);
                    startActivity(localIntent);
                }
            });
            Glide.with(MainActivitySeller.this)
                    .load(Appconfig.getResizedImage(urlsone.get(1), false))
                    .into(outsideTwo);
            outsideTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent localIntent = new Intent(MainActivitySeller.this, ActivityMediaOnline.class);
                    localIntent.putExtra("filePath", urlsone.get(1));
                    localIntent.putExtra("isImage", true);
                    startActivity(localIntent);
                }
            });
            Glide.with(MainActivitySeller.this)
                    .load(Appconfig.getResizedImage(urlsone.get(2), false))
                    .into(outsideThree);
            outsideThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent localIntent = new Intent(MainActivitySeller.this, ActivityMediaOnline.class);
                    localIntent.putExtra("filePath", urlsone.get(2));
                    localIntent.putExtra("isImage", true);
                    startActivity(localIntent);
                }
            });
        } catch (Exception e) {
            Log.e("", e.toString());
        }

    }

    @Override
    public void onDeleteClick(int position) {
        AlertDialog diaBox = AskOption(position);
        diaBox.show();
    }

    @Override
    public void onImageClick(int position) {

    }

    @Override
    public void onStatus(Seller seller, String status) {
        updateStatus(seller.id, status);
    }

    private void updateStatus(final String shopId, final String status) {
        String tag_string_req = "req_register";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                SELLER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    Boolean success = jObj.getBoolean("success");
                    String msg = jObj.getString("message");

                    if (success) {
                        requestShop("");
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("status", status);
                localHashMap.put("id", shopId);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
