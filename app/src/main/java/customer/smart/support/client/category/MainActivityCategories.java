package customer.smart.support.client.category;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import static customer.smart.support.app.Appconfig.CATEGORIES;

public class MainActivityCategories extends AppCompatActivity implements CategoriesClick {
    private static final String TAG = MainActivityCategories.class.getSimpleName();
    ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private List<Categories> categoriesList;
    private CategoriesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_categories);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Categories");


        recyclerView = findViewById(R.id.recycler_view);
        categoriesList = new ArrayList<>();
        mAdapter = new CategoriesAdapter(this, categoriesList, this);
        final LinearLayoutManager addManager1 = new GridLayoutManager(MainActivityCategories.this, 2);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setAdapter(mAdapter);
        whiteNotificationBar(recyclerView);

        FloatingActionButton addStock = findViewById(R.id.addbanner);
        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityCategories.this, CategoriesRegister.class);
                startActivity(intent);
            }
        });
    }

    /**
     * fetches json by making http calls
     */
    private void fetchContacts() {
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
                        categoriesList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Categories categories = new Categories();
                            categories.setId(jsonObject.getString("id"));
                            categories.setTitle(jsonObject.getString("title"));
                            categories.setBrand(jsonObject.getString("brand"));
                            categories.setTag(jsonObject.getString("tag"));
                            categories.setImage(jsonObject.getString("image"));
                            categories.setPercentage(jsonObject.getString("qtyPercent"));
                            categoriesList.add(categories);
                        }
                        mAdapter.notifyData(categoriesList);
                        getSupportActionBar().setSubtitle(categoriesList.size() + " Nos");
                    } else {
                        Toast.makeText(MainActivityCategories.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityCategories.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityCategories.this,
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
    public void onDeleteClick(int position) {
        AlertDialog diaBox = AskOption(position);
        diaBox.show();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivityCategories.this, CategoriesRegister.class);
        intent.putExtra("data", categoriesList.get(position));
        startActivity(intent);
    }

    private AlertDialog AskOption(final int position) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.ic_delete_black_24dp)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
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
                CATEGORIES + "?id=" + categoriesList.get(position).id,
                new Response.Listener<String>() {
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
                            Toast.makeText(MainActivityCategories.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Log.e("xxxxxxxxxxx", e.toString());
                            Toast.makeText(MainActivityCategories.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityCategories.this,
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

}
