package customer.smart.support.marketingstaff;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import customer.smart.support.stock.MyDividerItemDecoration;

import static customer.smart.support.app.Appconfig.mypreference;

public class MainActivityMarketingStaff extends AppCompatActivity implements MarketStaffAdapter.MarketStaffAdapterListener {
    private static final String TAG = MainActivityMarketingStaff.class.getSimpleName();
    SharedPreferences sharedpreferences;
    private RecyclerView recyclerView;
    private List<MarketStaff> contactList;
    private MarketStaffAdapter mAdapter;
    private SearchView searchView;

    // url to fetch contacts json

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_marketingstaff);


        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Marketing List");

        recyclerView = findViewById(R.id.recycler_view);
        contactList = new ArrayList<>();
        mAdapter = new MarketStaffAdapter(this, contactList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);


        FloatingActionButton addStock = findViewById(R.id.addStock);
        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityMarketingStaff.this, MarketStaffRegister.class);
                startActivity(intent);
            }
        });
    }

    /**
     * fetches json by making http calls
     */
    private void getMarketList() {
        String tag_string_req = "req_register";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                Appconfig.MARKETINGSTAFF +
                        (sharedpreferences.getString("data", "").contains("marketing") ?
                        "?staffContact=" + sharedpreferences.getString("user", "") : ""), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  productRecycle.setVisibility(View.GONE);
                Log.d("Register Response: ", response);
                contactList = new ArrayList<>();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            MarketStaff marketStaff = new MarketStaff();
                            marketStaff.setId(jsonObject.getString("id"));
                            marketStaff.setCustomerName(jsonObject.getString("customerName"));
                            marketStaff.setCustomerContact(jsonObject.getString("customerContact"));
                            marketStaff.setModel(jsonObject.getString("model"));
                            marketStaff.setGeotag(jsonObject.getString("geotag"));
                            marketStaff.setBrand(jsonObject.getString("brand"));
                            marketStaff.setImeiNo(jsonObject.getString("imeiNo"));
                            marketStaff.setPrice(jsonObject.getString("price"));
                            marketStaff.setStaffName(jsonObject.getString("staffName"));
                            marketStaff.setStaffContact(jsonObject.getString("staffContact"));
                            marketStaff.setSign(jsonObject.getString("sign"));
                            marketStaff.setImage(jsonObject.getString("image"));
                            marketStaff.setAttachement(jsonObject.getString("attachement"));
                            marketStaff.setDescription(jsonObject.getString("description"));
                            marketStaff.setStatus(jsonObject.getString("status"));


                            contactList.add(marketStaff);
                        }
                        mAdapter.notifyData(contactList);

                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //    productRecycle.setVisibility(View.GONE);
                // Log.e("Registration Error: ", error.getMessage());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }



    @Override
    public void onStaffSelected(MarketStaff contact) {
        Intent intent = new Intent(MainActivityMarketingStaff.this, MarketStaffRegister.class);
        intent.putExtra("data", contact);
        startActivity(intent);
    }

    @Override
    public void onStatusChange(MarketStaff contact) {
        statusAlert(contact.id);
    }

    private void statusChange(String id) {
        String tag_string_req = "req_register";
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                Appconfig.MARKETINGSTATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getBoolean("success")) {
                        getMarketList();
                    }
                    Toast.makeText(MainActivityMarketingStaff.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(MainActivityMarketingStaff.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivityMarketingStaff.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("id", id);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void statusAlert(String id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivityMarketingStaff.this);
        LayoutInflater inflater = MainActivityMarketingStaff.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_delete, null);


        dialogBuilder.setTitle("Delete")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        statusChange(id);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        getMarketList();

    }


}
