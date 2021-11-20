package customer.smart.support.client.stock;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

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
import customer.smart.support.StartActivity;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.client.category.Categories;
import customer.smart.support.client.filter.BrandFilterAdapter;
import customer.smart.support.client.filter.BrandFilterBean;
import customer.smart.support.client.filter.CategoryFilterAdapter;
import customer.smart.support.client.filter.CategoryFilterBean;
import customer.smart.support.client.filter.OncategoryFilter;
import customer.smart.support.client.shop.MainActivityShop;

import static customer.smart.support.app.Appconfig.CATEGORIES;
import static customer.smart.support.app.Appconfig.SHOP;
import static customer.smart.support.app.Appconfig.STOCK;


public class MainActivityProduct extends AppCompatActivity implements ProductAdapter.ContactsAdapterListener,
        OncategoryFilter {
    private static final String TAG = MainActivityProduct.class.getSimpleName();
    ProgressDialog progressDialog;
    int offset = 0;
    boolean isAlreadyLoading;
    private RecyclerView recyclerView, recycler_category, recycler_brand;
    private List<Product> contactList;
    private ProductAdapter mAdapter;
    private SearchView searchView;
    private List<Categories> category;
    private final List<CategoryFilterBean> permanantList = new ArrayList<>();
    private String shopIdFromintent;
    private String shopnameFromintent;
    private String selectedBrand = "ALL";
    private String selectedCategory = "ALL";
    CategoryFilterAdapter categoryFilterAdapter;
    BrandFilterAdapter brandFilterAdapter;
    private ArrayList<CategoryFilterBean> categoryFilterBeans = new ArrayList<>();
    private ArrayList<BrandFilterBean> brandFilterBeans = new ArrayList<>();
    private Set<String> subCategory = new HashSet<>();
    private Set<String> subBrand = new HashSet<>();
    private String selectPrice = "ALL";
    private Map<String, String> idNameMap = new HashMap<>();
    String[] SHOPNAME = new String[]{"Loading"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_mainstock);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //filter
        recycler_category = findViewById(R.id.recycler_category);
        categoryFilterBeans = new ArrayList<>();
        categoryFilterAdapter = new CategoryFilterAdapter(MainActivityProduct.this, categoryFilterBeans, this, selectedCategory);
        final LinearLayoutManager addManager2 = new LinearLayoutManager(MainActivityProduct.this, LinearLayoutManager.HORIZONTAL, false);
        recycler_category.setLayoutManager(addManager2);
        recycler_category.setAdapter(categoryFilterAdapter);

        recycler_brand = findViewById(R.id.recycler_brand);
        brandFilterBeans = new ArrayList<>();
        brandFilterAdapter = new BrandFilterAdapter(MainActivityProduct.this, brandFilterBeans, this, selectedBrand);
        final LinearLayoutManager addManager3 = new LinearLayoutManager(MainActivityProduct.this, LinearLayoutManager.HORIZONTAL, false);
        recycler_brand.setLayoutManager(addManager3);
        recycler_brand.setAdapter(brandFilterAdapter);


        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);
        recyclerView = findViewById(R.id.recycler_view);
        contactList = new ArrayList<>();
        mAdapter = new ProductAdapter(this, contactList, this, this);

        // white background notification bar
        whiteNotificationBar(recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setAdapter(mAdapter);
        //fetchContacts();
        NestedScrollView nestedScrollview = findViewById(R.id.nestedScrollview);
        nestedScrollview.setNestedScrollingEnabled(false);
        nestedScrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) { //scrollY is the sliding distance
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (!isAlreadyLoading) {
                        if (searchView != null && !searchView.isIconified() && searchView.getQuery().toString().length() > 0) {
                            fetchStock(searchView.getQuery().toString());
                        } else {
                            fetchStock("");
                        }
                    }
                }
            }
        });
        Intent intent = getIntent();
        shopIdFromintent = intent.getStringExtra(MainActivityShop.SHOPID);
        shopnameFromintent = intent.getStringExtra(MainActivityShop.SHOPNAME);
        getAllCategories(shopIdFromintent);
        fetchStock("");
        FloatingActionButton addStock = findViewById(R.id.addStock);
        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityProduct.this, ProductRegister.class);
                intent.putExtra("SHOPNAME", shopnameFromintent);
                intent.putExtra("SHOPID", shopIdFromintent);
                intent.putExtra("CATEGORY", category.get(0).getTitle());
                startActivity(intent);
            }
        });
    }

    private void fetchStock(final String searchKey) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                STOCK + "?offset=" + offset * 10 + "" + "&searchkey=" + searchKey
                        + "&shopId=" + shopIdFromintent + "&selectPrice=ALL" +
                        "&selectBrand=" + selectedBrand.replace("+", "%2B") +
                        "&category=" + selectedCategory + "&isAdmin=true", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response);
                if (offset == 0) {
                    contactList = new ArrayList<>();
                }
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        offset = offset + 1;
                        HashMap<String, String> idNameMap = new HashMap<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Product product = new Product();
                            product.setId(jsonObject.getString("id"));
                            product.setBrand(jsonObject.getString("brand"));
                            product.setModel(jsonObject.getString("model"));
                            product.setPrice(jsonObject.getString("price"));
                            product.setDescription(jsonObject.getString("description"));
                            product.setSelectshop(jsonObject.getString("selectshop"));
                            if (!jsonObject.isNull("categoryTag")) {
                                product.setCategoryTag(jsonObject.getString("categoryTag"));
                            }
                            if (!jsonObject.isNull("shopName")) {
                                product.setShopName(jsonObject.getString("shopName"));
                            }
                            if (!jsonObject.isNull("bulkPrice")) {
                                product.setBulkPrice(jsonObject.getString("bulkPrice"));
                            }

                            if (!jsonObject.isNull("categoryName")) {
                                product.setCategoryName(jsonObject.getString("categoryName"));
                            }
                            product.setSelectcategories(jsonObject.getString("category"));
                            product.setStock_update(jsonObject.getString("stockupdate"));
                            if (!jsonObject.isNull("quantity")) {
                                product.setQty(jsonObject.getString("quantity"));
                            }
                            if (!jsonObject.isNull("incategory")) {
                                product.setIncategory(jsonObject.getString("incategory"));
                            }
                            if (jsonObject.get("image") instanceof JSONArray) {
                                ArrayList<String> samplesList = new ArrayList<>();
                                for (int k = 0; k < jsonObject.getJSONArray("image").length(); k++) {
                                    String image = jsonObject.getJSONArray("image").getString(k);
                                    if (image != null && image.length() > 0) {
                                        samplesList.add(image);
                                    }
                                }
                                product.setImage(new Gson().toJson(samplesList));
                            } else {
                                product.setImage(jsonObject.getString("image"));
                            }
                            contactList.add(product);
                        }
                        JSONArray jsonBrand = jObj.getJSONArray("brand");
                        if (jsonBrand != null) {
                            for (int b = 0; b < jsonBrand.length(); b++) {
                                String string = jsonBrand.getString(b);
                                if (string.length() > 0) {
                                    subBrand.add(string.toUpperCase());
                                }
                            }
                        }
                        if (brandFilterBeans.size() <= 0) {
                            brandFilterBeans = new ArrayList<>();
                            brandFilterBeans.add(new BrandFilterBean("ALL"));
                            for (String e : subBrand) {
                                brandFilterBeans.add(new BrandFilterBean(e));
                            }
                            brandFilterAdapter.notifyData(brandFilterBeans);
                            selectPrice = "ALL";
                            getSupportActionBar().setSubtitle(selectPrice);
                            brandFilterAdapter.notifyData(selectPrice);
                        }

                        mAdapter.notifyData(contactList);
                        getSupportActionBar().setSubtitle(contactList.size() + "  Nos");

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
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                offset = 0;
                fetchStock("");
                return false;
            }
        });
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                //     mAdapter.getFilter().filter(query);
                if (query.length() > 3) {
                    offset = 0;
                    fetchStock(query);
                } else if (query.length() == 0) {
                    fetchStock("");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                // mAdapter.getFilter().filter(query);
                if (query.length() > 3) {
                    offset = 0;
                    fetchStock(query);
                } else if (query.length() == 0) {
                    fetchStock("");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("from") != null
                && getIntent().getStringExtra("from").equalsIgnoreCase("client")) {
            Intent intent = new Intent(MainActivityProduct.this, StartActivity.class);
            startActivity(intent);
            finishAffinity();
        } else if (!searchView.isIconified()) {
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
    public void onContactSelected(Product contact) {
        Intent intent = new Intent(MainActivityProduct.this, ProductRegister.class);
        intent.putExtra("data", contact);
        intent.putExtra("SHOPNAME", shopnameFromintent);
        intent.putExtra("SHOPID", shopIdFromintent);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        offset = 0;
        fetchCategory();
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void fetchCategory() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                CATEGORIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        category = new ArrayList<>();
                        subCategory = new HashSet<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Categories categories = new Categories();
                            categories.setId(jsonObject.getString("id"));
                            categories.setTitle(jsonObject.getString("title"));
                            categories.setImage(jsonObject.getString("image"));
                            category.add(categories);
                        }

                    } else {
                        Toast.makeText(MainActivityProduct.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityProduct.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityProduct.this,
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
    public void onselectBrand(String brand) {
        selectedBrand = brand;
        brandFilterAdapter.notifyData(selectedBrand);
        offset = 0;
        fetchStock("");
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
                        idNameMap = new HashMap<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            idNameMap.put(jsonObject.getString("id"),
                                    jsonObject.getString("title"));
                        }
                        categoryFilterBeans = new ArrayList<>();
                        JSONArray dataArray = jObj.getJSONArray("data");
                        for (int l = 0; l < dataArray.length(); l++) {
                            JSONObject jsonObject = dataArray.getJSONObject(l);
                            String[] selectedIds = jsonObject.getString("category").split(",");
                            StringBuilder selectedNames = new StringBuilder();
                            categoryFilterBeans.add(new CategoryFilterBean("ALL","ALL"));
                            for (int i = 0; i < selectedIds.length; i++) {
                                categoryFilterBeans.add(new CategoryFilterBean(idNameMap.get(selectedIds[i]),
                                        selectedIds[i]));
                                selectedNames.append(idNameMap.get(selectedIds[i]));
                                if (i != selectedIds.length - 1) {
                                    selectedNames.append(",");
                                }
                            }
                            categoryFilterAdapter = new CategoryFilterAdapter(MainActivityProduct.this, categoryFilterBeans, MainActivityProduct.this, selectedCategory);
                            recycler_category.setAdapter(categoryFilterAdapter);
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

    @Override
    public void onselectCategory(CategoryFilterBean categoryFilterBean) {
        selectedCategory = categoryFilterBean.getId();
        categoryFilterAdapter.notifyData(selectedCategory);
        offset = 0;
        fetchStock("");
    }
}
