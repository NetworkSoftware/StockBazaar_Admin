package customer.smart.support.client.wallet;

import static customer.smart.support.app.Appconfig.GETWALLET;
import static customer.smart.support.app.Appconfig.SELLER;
import static customer.smart.support.app.Appconfig.WALLETSTATUS;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
import customer.smart.support.client.seller_as.MainActivitySeller;
import customer.smart.support.client.seller_as.Seller;


public class MainActivityWallet extends AppCompatActivity implements StatusClick {
    ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    private RecyclerView recyclerView;
    private List<AllWalletBean> allWalletBeans;
    private AllWalletAdapter allWalletAdapter;
    int offset = 0;
    boolean isAlreadyLoading;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wallet);

        sharedpreferences = getApplicationContext().getSharedPreferences(Appconfig.mypreference, Context.MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Wallet History");

        recyclerView = findViewById(R.id.recycler_view);
        allWalletBeans = new ArrayList<>();
        allWalletAdapter = new AllWalletAdapter(MainActivityWallet.this, allWalletBeans, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivityWallet.this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setAdapter(allWalletAdapter);
        whiteNotificationBar(recyclerView);

        //
        NestedScrollView nestedScrollview = findViewById(R.id.nestedScrollview);
        nestedScrollview.setNestedScrollingEnabled(false);
        nestedScrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) { //scrollY is the sliding distance
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (!isAlreadyLoading) {
                        if (searchView != null && !searchView.isIconified() && searchView.getQuery().toString().length() > 0) {
                            allWalletAmt(searchView.getQuery().toString());
                        } else {
                            allWalletAmt("");
                        }
                    }
                }
            }
        });

    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void allWalletAmt(final String searchKey) {
        isAlreadyLoading = true;
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                GETWALLET + "?offset=" + offset * 30 + "&searchkey=" + searchKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        Log.d("Register Response: ", response);
                        if (offset == 0) {
                            allWalletBeans = new ArrayList<>();
                        }
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int success = jObj.getInt("success");
                            if (success == 1) {
                                JSONArray jsonArray = jObj.getJSONArray("data");
                                offset = offset + 1;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    AllWalletBean allWalletBean = new AllWalletBean();
                                    allWalletBean.setId(jsonObject.getString("id"));
                                    allWalletBean.setAmt(jsonObject.getString("amt"));
                                    allWalletBean.setOperation(jsonObject.getString("operation"));
                                    allWalletBean.setCreatedon(jsonObject.getString("createdon"));
                                    allWalletBean.setDescription(jsonObject.getString("description"));
                                    allWalletBean.setUserId(jsonObject.getString("userId"));
                                    allWalletBean.setUserName(jsonObject.getString("userName"));
                                    allWalletBean.setStatus(jsonObject.getString("status"));
                                    allWalletBeans.add(allWalletBean);
                                }
                                allWalletAdapter.notifyData(allWalletBeans);
                            } else {
                                Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                        }
                        isAlreadyLoading = false;

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isAlreadyLoading = false;
                hideDialog();
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
    public void status(AllWalletBean allWalletBean, String status) {
        updateStatus(allWalletBean.id, status);
    }

    private void updateStatus(final String shopId, final String status) {
        String tag_string_req = "req_register";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                WALLETSTATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    Toast.makeText(getApplication(), jObj.getString("message"),
                            Toast.LENGTH_SHORT).show();
                    int success = jObj.getInt("success");
                    String msg = jObj.getString("message");

                    if (success == 1) {
                        offset = 0;
                        allWalletAmt("");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.product_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                offset = 0;
                allWalletAmt("");
                return false;
            }
        });
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                if (query.length() >= 1) {
                    offset = 0;
                    allWalletAmt(query);
                } else if (query.length() == 0) {
                    offset = 0;
                    allWalletAmt("");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if (query.length() >= 1) {
                    offset = 0;
                    allWalletAmt(query);
                } else if (query.length() == 0) {
                    offset = 0;
                    allWalletAmt("");
                }
                return false;
            }
        });

        if (getIntent() != null && getIntent().getBooleanExtra("isSearch", false)) {
            searchView.setIconified(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        allWalletAmt("");
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

        return super.onOptionsItemSelected(item);
    }

}
