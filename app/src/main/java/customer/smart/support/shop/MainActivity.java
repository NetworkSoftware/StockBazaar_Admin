package customer.smart.support.shop;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import customer.smart.support.stock.MyDividerItemDecoration;

import static customer.smart.support.app.Appconfig.mypreference;

public class MainActivity extends AppCompatActivity implements OnShopClick,
        ShopAdapter.ContactsAdapterListener, OnFilterClose, OnFilterCheck {

    ShopAdapter shopAdapter;
    List<Shop> shopList = new ArrayList<>();
    List<Shop> filteredShopList = new ArrayList<>();
    SharedPreferences sharedpreferences;
    ProgressDialog progressDialog;
    private SearchView searchView;
    private RoundedBottomSheetDialog mBottomSheetDialog;
    private RecyclerView districtList;
    private ArrayList<String> allDistrict = new ArrayList<>();
    private final ArrayList<String> filteredDistrict = new ArrayList<>();
    private FilteredAdapater fileredAdapter;

    private ArrayList<String> allPincode = new ArrayList<>();
    private final ArrayList<String> filteredPincode = new ArrayList<>();
    private RecyclerView pincodeList;
    private FilteredAdapater pincodeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        final FloatingActionButton addShop = findViewById(R.id.addShop);
        final FloatingActionButton addFilter = findViewById(R.id.addFilter);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        shopList = new ArrayList<>();
        shopAdapter = new ShopAdapter(shopList, this, this, this,
                sharedpreferences.getString("role", "admin"));
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(shopAdapter);

            String data = sharedpreferences.getString("data", "");

            if (data.contains("addshop")) {
                addShop.setVisibility(View.VISIBLE);
            } else {
                addShop.setVisibility(View.GONE);
            }

       /* if (sharedpreferences.getString("role", "admin").equalsIgnoreCase("sadmin")) {
            addShop.setVisibility(View.VISIBLE);
        } else {
            addShop.setVisibility(View.GONE);
        }
*/
        addShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShopRegister.class);
                startActivity(intent);
            }
        });
        addFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog = new RoundedBottomSheetDialog(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.shops_filter, null);
                Button submitBtn = dialogView.findViewById(R.id.submitBtn);
                ImageView cancelBtn = dialogView.findViewById(R.id.close);


                districtList = dialogView.findViewById(R.id.districtList);
                fileredAdapter = new FilteredAdapater(MainActivity.this, filteredDistrict, MainActivity.this);
                final LinearLayoutManager addManager5 = new GridLayoutManager(MainActivity.this, 2);
                districtList.setLayoutManager(addManager5);
                districtList.setAdapter(fileredAdapter);

                pincodeList = dialogView.findViewById(R.id.pincodeList);
                pincodeAdapter = new FilteredAdapater(MainActivity.this, filteredPincode, new OnFilterClose() {
                    @Override
                    public void onClosed(int position) {
                        filteredPincode.remove(position);
                        pincodeAdapter.notifyData(filteredPincode);
                    }
                });

                final LinearLayoutManager addManager6 = new GridLayoutManager(MainActivity.this, 2);
                pincodeList.setLayoutManager(addManager6);
                pincodeList.setAdapter(pincodeAdapter);

                Chip assignInvest = dialogView.findViewById(R.id.assignDistrict);
                assignInvest.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View v) {

                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.activity_main_filter_list, null);
                        PopupWindow window = new PopupWindow(dialogView, WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.WRAP_CONTENT, true);

                        EditText search_field = dialogView.findViewById(R.id.search_field);

                        RecyclerView result_list = dialogView.findViewById(R.id.result_list);
                        AssignFilterAdapter mAdapter = new AssignFilterAdapter(MainActivity.this,
                                allDistrict, MainActivity.this, String.join(",",
                                filteredDistrict));
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        result_list.setLayoutManager(mLayoutManager);
                        result_list.setItemAnimator(new DefaultItemAnimator());
                        result_list.addItemDecoration(new
                                MyDividerItemDecoration(MainActivity.this,
                                DividerItemDecoration.VERTICAL, 10));
                        result_list.setAdapter(mAdapter);
                        search_field.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (s.toString().length() > 2) {
                                    mAdapter.getFilter().filter(s.toString());
                                } else {
                                    mAdapter.getFilter().filter("");
                                }
                            }
                        });

                        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        window.setOutsideTouchable(true);
                        window.setAnimationStyle(android.R.style.Animation_Dialog);
                        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.setElevation(20);
                        }
                        window.showAsDropDown(districtList, 0, 0);

                    }
                });


                Chip assignPincode = dialogView.findViewById(R.id.assignPincode);
                assignPincode.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View v) {

                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.activity_main_filter_list, null);
                        PopupWindow window = new PopupWindow(dialogView, WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.WRAP_CONTENT, true);

                        EditText search_field = dialogView.findViewById(R.id.search_field);

                        RecyclerView result_list = dialogView.findViewById(R.id.result_list);
                        AssignFilterAdapter mAdapter = new AssignFilterAdapter(MainActivity.this,
                                allPincode, new OnFilterCheck() {
                            @Override
                            public void onChecked(String position, boolean isCheck) {
                                if (isCheck) {
                                    filteredPincode.add(position);
                                } else {
                                    for (int i = 0; i < filteredPincode.size(); i++) {
                                        if (position.equals(filteredPincode.get(i))) {
                                            filteredPincode.remove(i);
                                        }
                                    }
                                }
                                pincodeAdapter.notifyData(filteredPincode);
                            }
                        }, String.join(",",
                                filteredPincode));
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        result_list.setLayoutManager(mLayoutManager);
                        result_list.setItemAnimator(new DefaultItemAnimator());
                        result_list.addItemDecoration(new
                                MyDividerItemDecoration(MainActivity.this,
                                DividerItemDecoration.VERTICAL, 10));
                        result_list.setAdapter(mAdapter);
                        search_field.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (s.toString().length() > 2) {
                                    mAdapter.getFilter().filter(s.toString());
                                } else {
                                    mAdapter.getFilter().filter("");
                                }
                            }
                        });

                        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        window.setOutsideTouchable(true);
                        window.setAnimationStyle(android.R.style.Animation_Dialog);
                        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.setElevation(20);
                        }
                        window.showAsDropDown(pincodeList, 0, 0);

                    }
                });


                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View v) {
                        applyFilter();
                        bottomSheetCancel();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetCancel();
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
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void applyFilter() {
        String listString = String.join(",",
                filteredDistrict);
        String listPincode = String.join(",",
                filteredPincode);
        if (listString.length() > 0 || listPincode.length() > 0) {
            shopList = new ArrayList<>();
            for (int i = 0; i < filteredShopList.size(); i++) {
                String address = filteredShopList.get(i).getAddress();
                String pincode = filteredShopList.get(i).getPincode();
                if ((address != null && address.length() > 0 && listString.contains(address))
                        || (pincode != null && pincode.length() > 0 && listPincode.contains(pincode))) {
                    shopList.add(filteredShopList.get(i));
                }
            }
            shopAdapter.notifyData(shopList);
            getSupportActionBar().setSubtitle(shopList.size() + "/" + filteredShopList.size());
        } else {
            shopAdapter.notifyData(shopList);
            getSupportActionBar().setSubtitle(shopList.size() + "shops");
        }
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


    private void getAllStaff() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Fetching ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Appconfig.ALL_SHOP, new Response.Listener<String>() {
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
                        Set<String> districts = new HashSet<>();
                        Set<String> pincodes = new HashSet<>();
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
                            shop.setType(jsonObject.getString("isDealer"));
                            shopList.add(shop);
                            if (shop.address.length() > 0) {
                                districts.add(shop.address);
                            }
                            if (shop.pincode.length() > 0) {
                                pincodes.add(shop.pincode);
                            }
                        }
                        int n = districts.size();
                        allDistrict = new ArrayList<String>(n);
                        int m = districts.size();
                        allPincode = new ArrayList<String>(m);
                        for (String x : districts) {
                            allDistrict.add(x);
                        }
                        for (String x : pincodes) {
                            allPincode.add(x);
                        }
                        filteredShopList = shopList;
                        applyFilter();
                        getSupportActionBar().setSubtitle(shopList.size() + "  Nos");

                    } else {
                        Toast.makeText(MainActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivity.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivity.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("isAdmin", "true");
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
    public void onDeleteClick(Shop position) {
        AlertDialog diaBox = AskOption(position);
        diaBox.show();
    }

    @Override
    public void onEditClick(Shop shop) {
        Intent intent = new Intent(MainActivity.this, ShopUpdate.class);
        intent.putExtra("object", shop);
        startActivity(intent);
    }

    @Override
    public void onCallClick(String phone) {

    }

    @Override
    public void onStatusClick(Shop position) {

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
                    Toast.makeText(MainActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    shopAdapter.notifyData(shopList);

                } catch (
                        JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivity.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivity.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("id", shop.getId());
                localHashMap.put("shopname", shop.getShopname());
                return localHashMap;
            }
        };
        AppController.getInstance().

                addToRequestQueue(strReq, tag_string_req);
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


    @Override
    public void onContactSelected(Shop shop) {

    }

    private void bottomSheetCancel() {
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.cancel();
        }
    }

    @Override
    public void onChecked(String name, boolean isCheck) {
        if (isCheck) {
            filteredDistrict.add(name);
        } else {
            for (int i = 0; i < filteredDistrict.size(); i++) {
                if (name.equals(filteredDistrict.get(i))) {
                    filteredDistrict.remove(i);
                }
            }
        }
        fileredAdapter.notifyData(filteredDistrict);
    }

    @Override
    public void onClosed(int position) {
        filteredDistrict.remove(position);
        fileredAdapter.notifyData(filteredDistrict);
    }
}
