package customer.smart.support.client.shop;

import static com.android.volley.Request.Method.GET;
import static customer.smart.support.app.Appconfig.CATEGORIES;
import static customer.smart.support.app.Appconfig.SHOP;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import customer.smart.support.client.category.Categories;
import customer.smart.support.client.stock.MainActivityProduct;
import customer.smart.support.client.stock.Product;
import customer.smart.support.client.stock.ProductAdapter;
import customer.smart.support.client.stock.ProductRegister;


public class MainActivityShop extends AppCompatActivity implements ShopClick, ShopAdapter.ContactsAdapterListener {
    public static final String SHOPID = "id";
    public static final String SHOPNAME = "shopname";
    private static final String TAG = MainActivityShop.class.getSimpleName();
    ProgressDialog progressDialog;
    EditText productId;
    private RecyclerView recyclerView;
    private List<Shop> categoriesList;
    private List<Categories> category;
    private ShopAdapter mAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shop);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shop Name");
        recyclerView = findViewById(R.id.recycler_view);
        categoriesList = new ArrayList<>();
        mAdapter = new ShopAdapter(this, categoriesList,this, this);
        final LinearLayoutManager addManager1 = new GridLayoutManager(MainActivityShop.this, 1);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setAdapter(mAdapter);

        whiteNotificationBar(recyclerView);

        FloatingActionButton addStock = findViewById(R.id.addbanner);
        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityShop.this, ShopRegister.class);
                startActivity(intent);
            }
        });


        ImageView orderIdSearch = findViewById(R.id.productIdSearch);
        productId = findViewById(R.id.productId);
        orderIdSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productId.getText().toString().length() > 0) {
                    productId.setError(null);
                    fetchProductId(productId.getText().toString());
                } else {
                    productId.setError("Enter valid Id");
                }
            }
        });
    }

    /**
     * fetches json by making http calls
     */
    private void fetchShop() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        //  showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                SHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        categoriesList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Shop shop = new Shop();
                            shop.setId(jsonObject.getString("id"));
                            shop.setShop_name(jsonObject.getString("shopname"));
                            shop.setPhone(jsonObject.getString("phone"));
                            shop.setCategory(jsonObject.getString("category"));
                            shop.setStock_update(jsonObject.getString("shop_update"));
                            shop.setPassword(jsonObject.getString("password"));

                            if (!jsonObject.isNull("image")) {
                                shop.setImage(jsonObject.getString("image"));
                            }
                            categoriesList.add(shop);
                        }
                        mAdapter.notifyData(categoriesList);
                        getSupportActionBar().setSubtitle(categoriesList.size() + " Nos");

                    } else {
                        Toast.makeText(MainActivityShop.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityShop.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityShop.this,
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


    private void fetchProductId(String id) {
        progressDialog.setMessage("Processing ...");
        String url = Appconfig.FETCH_ITEM_BY_ID + "?id=" + id + "&type=product";
        JsonObjectRequest request = new JsonObjectRequest(GET, url, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject jsonObject = response.getJSONArray("data").getJSONObject(0);
                                Product productListBean = new Product();
                                productListBean.setId(jsonObject.getString("id"));
                                productListBean.setBrand(jsonObject.getString("brand"));
                                productListBean.setModel(jsonObject.getString("model"));
                                productListBean.setPrice(jsonObject.getString("price"));
                                productListBean.setDescription(jsonObject.getString("description"));
                                productListBean.setSelectshop(jsonObject.getString("selectshop"));
                                productListBean.setCategory(jsonObject.getString("category"));
                                productListBean.setStock_update(jsonObject.getString("stockupdate"));
                                if (!jsonObject.isNull("quantity")) {
                                    productListBean.setQty(jsonObject.getString("quantity"));
                                }
                                if (!jsonObject.isNull("bulkPrice")) {
                                    productListBean.setBulkPrice(jsonObject.getString("bulkPrice"));
                                }

                                if (jsonObject.get("image") instanceof JSONArray) {
                                    ArrayList<String> samplesList = new ArrayList<>();
                                    for (int k = 0; k < jsonObject.getJSONArray("image").length(); k++) {
                                        String image = jsonObject.getJSONArray("image").getString(k);
                                        if (image != null && image.length() > 0) {
                                            samplesList.add(image);
                                        }
                                    }
                                    productListBean.setImage(new Gson().toJson(samplesList));
                                } else {
                                    productListBean.setImage(jsonObject.getString("image"));
                                }
                                Intent intent = new Intent(getApplicationContext(),
                                        ProductRegister.class);
                                intent.putExtra("data", productListBean);
                                intent.putExtra("SHOPNAME", jsonObject.getString("shopName"));
                                intent.putExtra("SHOPID", jsonObject.getString("selectshop"));
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            Log.e("xxxxxxxxxxx", e.toString());
                            Toast.makeText(MainActivityShop.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
            }
        });
        request.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(request);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
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
        fetchShop();
        fetchCategory();
    }

    @Override
    public void onDeleteClick(int position) {
        AlertDialog diaBox = AskOption(position);
        diaBox.show();
    }

    @Override
    public void onItemClick(Shop shop) {
        Intent intent = new Intent(MainActivityShop.this, ShopRegister.class);
        intent.putExtra("data", shop);
        startActivity(intent);
    }

    @Override
    public void onStockAdd(int position) {
        Intent intent = new Intent(MainActivityShop.this, MainActivityProduct.class);
        intent.putExtra(SHOPID, categoriesList.get(position).id);
        intent.putExtra(SHOPNAME, categoriesList.get(position).shop_name);
        intent.putExtra("EXTRA_CAT", category.get(0).getTitle());
        startActivity(intent);
    }

    @Override
    public void onCategoryAdd(int position) {

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
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Categories categories = new Categories();
                            categories.setId(jsonObject.getString("id"));
                            categories.setTitle(jsonObject.getString("title"));
                            categories.setImage(jsonObject.getString("image"));
                            category.add(categories);
                        }
                    } else {
                        Toast.makeText(MainActivityShop.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityShop.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityShop.this,
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
                Appconfig.SHOP + "?id=" + categoriesList.get(position).id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getBoolean("success")) {
                        categoriesList.remove(position);
                        mAdapter.notifyData(categoriesList);
                    }
                    Toast.makeText(MainActivityShop.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityShop.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityShop.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("id", categoriesList.get(position).id);
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

    @Override
    public void onContactSelected(Shop product) {

    }
}
