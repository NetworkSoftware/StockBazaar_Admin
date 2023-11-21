package customer.smart.support.retail.order;

import static customer.smart.support.app.Appconfig.RETAIL_ORDER;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import customer.smart.support.R;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.retail.stock.StockModel;

public class MainRetailOrder extends AppCompatActivity implements OnOrderClick {
    private RecyclerView recyclerView;
    private List<OrderModel> orderModelList;
    private OrderAdapter mAdapter;
    private SearchView searchView;
    ProgressDialog progressDialog;


    // url to fetch contacts json

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retail_activity_mainorder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Retail Orders");


        recyclerView = findViewById(R.id.recycler_view);
        orderModelList = new ArrayList<>();
        mAdapter = new OrderAdapter(MainRetailOrder.this, orderModelList, this);
        whiteNotificationBar(recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(mAdapter);


    }

    private void fetchContacts() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET, RETAIL_ORDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        orderModelList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            OrderModel orderModel = new OrderModel();
                            orderModel.setId(jsonObject.getString("id"));
                            orderModel.setName(jsonObject.getString("name"));
                            orderModel.setPhone(jsonObject.getString("phone"));
                            orderModel.setTotalAmt(jsonObject.getString("totalAmt"));
                            orderModel.setItems(jsonObject.getString("items"));
                            orderModel.setStatus(jsonObject.getString("status"));
                            orderModel.setCreatedOn(jsonObject.getString("createdOn"));

                            ObjectMapper mapper = new ObjectMapper();
                            Object listBeans = new Gson().fromJson(jsonObject.getString("items"),
                                    Object.class);
                            ArrayList<CartItemModel> accountList = mapper.convertValue(
                                    listBeans,
                                    new TypeReference<ArrayList<CartItemModel>>() {
                                    }
                            );
                            orderModel.setProductBeans(accountList);
                            orderModelList.add(orderModel);
                        }
                        mAdapter.notifyData(orderModelList);
                        getSupportActionBar().setSubtitle(orderModelList.size() + " Nos");
                    } else {
                        Toast.makeText(MainRetailOrder.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainRetailOrder.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainRetailOrder.this, "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
//        if (!searchView.isIconified()) {
//            searchView.setIconified(true);
//            return;
//        }
        super.onBackPressed();
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
        fetchContacts();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void showDialog() {
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }


    @Override
    public void onCallClick(OrderModel mOrder) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mOrder.getPhone()));
        startActivity(intent);
    }

    @Override
    public void onWhatsappClick(OrderModel mOrder) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=91" + mOrder.getPhone()
                + "&text=" +
                "WELCOME TO STOCK BAZAAR\n" +
                "\n" +
                "THANK YOU FOR USING STOCK BAZAAR APP"));
        intent.setPackage("com.whatsapp");
        startActivity(intent);
    }

    @Override
    public void onStatusClick(OrderModel mOrder) {

    }
}
