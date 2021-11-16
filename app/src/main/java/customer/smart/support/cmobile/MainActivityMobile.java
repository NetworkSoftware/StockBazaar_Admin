package customer.smart.support.cmobile;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import customer.smart.support.R;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.shop.MainActivity;
import customer.smart.support.shop.OnShopClick;
import customer.smart.support.shop.Shop;

import static customer.smart.support.app.Appconfig.STATUSUPDATE;
import static customer.smart.support.app.Appconfig.mypreference;

public class MainActivityMobile extends AppCompatActivity implements MobileAdapterNew.ContactsAdapterListener, OnShopClick, OnStatus {

    private static final String TAG = MainActivityMobile.class.getSimpleName();
    private final List<Shop> permanantList = new ArrayList<>();
    SharedPreferences sharedpreferences;
    List<Shop> shopList;
    MobileAdapterNew shopAdapter;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    StatusAdapter statusAdapter;
    RecyclerView recycler_status;
    private ArrayList<StatusBean> statusBean = new ArrayList<>();
    private List<Cmobile> contactList;
    private SearchView searchView;
    private RoundedBottomSheetDialog mBottomSheetDialog;
    private Shop shop;
    private String selectedType = "ALL";
    private Set<String> subCategories = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maincustomer);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // toolbar fancy stuff

        contactList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        shopList = new ArrayList<>();
        shopAdapter = new MobileAdapterNew(shopList, this, this, this,
                sharedpreferences.getString("role", "dealer"));
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(shopAdapter);
        shop = (Shop) getIntent().getSerializableExtra("object");

        statusBean = new ArrayList<>();
        recycler_status = findViewById(R.id.recycler_view_chip);
        statusAdapter = new StatusAdapter(MainActivityMobile.this, statusBean, this, selectedType);
        final LinearLayoutManager addManager2 = new LinearLayoutManager(MainActivityMobile.this, LinearLayoutManager.HORIZONTAL, false);
        recycler_status.setLayoutManager(addManager2);
        recycler_status.setAdapter(statusAdapter);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                shopAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                shopAdapter.getFilter().filter(query);
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
        if (id == R.id.action_search) {
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

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void getAllStaff() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Fetching ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                Appconfig.GET_ALL_DEALER, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("shops");

                        shopList = new ArrayList<>();
                        subCategories = new HashSet<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Shop shop = new Shop();
                            shop.setId(jsonObject.getString("id"));
                            shop.setName(jsonObject.getString("name"));
                            shop.setShopname(jsonObject.getString("shopname"));
                            shop.setConfirmPass(jsonObject.getString("password"));
                            shop.setPassword(jsonObject.getString("password"));
                            shop.setContact(jsonObject.getString("mobile"));
                            shop.setImage(jsonObject.getString("imgurl"));
                            shop.setArea(jsonObject.getString("area"));
                            shop.setAddress(jsonObject.getString("address"));
                            shop.setLatlon(jsonObject.getString("latLon"));
                            shop.setPincode(jsonObject.getString("pincode"));
                            shop.setbusinesstype(jsonObject.getString("businesstype"));
                            shop.setStatusTxt(jsonObject.getString("status"));
                            shop.setType(jsonObject.getString("isDealer"));
                            subCategories.add(shop.getStatusTxt().toUpperCase());
                            shopList.add(shop);
                            permanantList.add(shop);
                        }
                        shopAdapter.notifyData(shopList);
                        if (statusBean.size() <= 0) {
                            statusBean = new ArrayList<>();
                            for (String e : subCategories) {
                                statusBean.add(new StatusBean(e));
                            }
                            statusAdapter.notifyData(statusBean);
                            statusAdapter.notifyData(selectedType);
                        }
                        shopAdapter.notifyData(shopList);

                    } else {
                        Toast.makeText(MainActivityMobile.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityMobile.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityMobile.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                //  localHashMap.put("isAdmin", "true");
                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllStaff();

    }

    @Override
    public void onContactSelected(Shop shop) {
        //
    }

    @Override
    public void onDeleteClick(Shop position) {
        AlertDialog diaBox = AskOption(position);
        diaBox.show();
    }

    private AlertDialog AskOption(final Shop shop) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.ic_delete_black_24dp)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteShop(shop);
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

    private void deleteShop(final Shop shop) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Fetching ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Appconfig.DELETE_SHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        getAllStaff();
                    }
                    Toast.makeText(MainActivityMobile.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    shopAdapter.notifyData(shopList);

                } catch (
                        JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityMobile.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityMobile.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("id", shop.getId());
                localHashMap.put("shopname", shop.getId());
                return localHashMap;
            }
        };
        AppController.getInstance().

                addToRequestQueue(strReq, tag_string_req);
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
    public void onEditClick(Shop position) {

    }

    @Override
    public void onCallClick(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }


    @Override
    public void onStatusClick(Shop shop) {
        mBottomSheetDialog = new RoundedBottomSheetDialog(MainActivityMobile.this);
        LayoutInflater inflater = MainActivityMobile.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_main_status, null);
        final String[] STOCKUPDATE = new String[]{
                "Very interested", "Interested", "not interested", "Worst customer", "calls not pick up", "Call back list",
        };
        final MaterialBetterSpinner status = dialogView.findViewById(R.id.status);
        final ExtendedFloatingActionButton submit = dialogView.findViewById(R.id.submit);


        ArrayAdapter<String> stockAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, STOCKUPDATE);
        status.setAdapter(stockAdapter);
        status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.getText().toString().length() <= 0) {
                    status.setError("Select the Category");
                } else {
                    updateStatus(shop.getId(), status.getText().toString(), mBottomSheetDialog);
                }
            }
        });


        mBottomSheetDialog.setContentView(dialogView);
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BottomSheetDialog d = (BottomSheetDialog) dialog;
                        FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 0);
            }
        });
        mBottomSheetDialog.show();

    }

    private void updateStatus(final String shopId, final String status, RoundedBottomSheetDialog mBottomSheetDialog) {
        String tag_string_req = "req_register";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                STATUSUPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        if (mBottomSheetDialog != null) {
                            mBottomSheetDialog.cancel();
                        }
                        getAllStaff();
                    }
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
                localHashMap.put("shopId", shopId);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void filterStatus() {
        shopList = new ArrayList<>();
        for (int i = 0; i < permanantList.size(); i++) {
            Shop shop = permanantList.get(i);
            if (selectedType.equalsIgnoreCase(shop.statusTxt)) {
                shopList.add(shop);
            }
        }
        shopAdapter.notifyData(shopList);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onStatus(String status) {
        selectedType = status;
        statusAdapter.notifyData(selectedType);
        filterStatus();
    }
}
