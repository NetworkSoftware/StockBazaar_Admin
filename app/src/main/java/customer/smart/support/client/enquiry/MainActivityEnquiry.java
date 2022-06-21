package customer.smart.support.client.enquiry;

import static customer.smart.support.app.Appconfig.CATEGORIES;
import static customer.smart.support.app.Appconfig.ENQUITY;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
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

public class MainActivityEnquiry extends AppCompatActivity implements EnquiryClick {
    ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private List<EnquiryBean> enquiryBeans;
    private EnquiryAdapter enquiryAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_enquiry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Enquiry");


        recyclerView = findViewById(R.id.recycler_view);
        enquiryBeans = new ArrayList<>();
        enquiryAdapter = new EnquiryAdapter(this, enquiryBeans, this);
        final LinearLayoutManager addManager1 = new GridLayoutManager(MainActivityEnquiry.this, 1);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setAdapter(enquiryAdapter);
        whiteNotificationBar(recyclerView);
    }

    /**
     * fetches json by making http calls
     */
    private void fetchContacts(String searchkey) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                ENQUITY + "?searchkey=" + searchkey, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");

                    if (success) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        enquiryBeans = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            EnquiryBean enquiryBean = new EnquiryBean();
                            enquiryBean.setId(jsonObject.getString("id"));
                            enquiryBean.setProduct_name(jsonObject.getString("product_name"));
                            enquiryBean.setDetails(jsonObject.getString("details"));
                            enquiryBean.setQty(jsonObject.getString("qty"));
                            enquiryBean.setWhatsapp_number(jsonObject.getString("whatsapp_number"));
                            enquiryBean.setDistrict(jsonObject.getString("district"));
                            enquiryBeans.add(enquiryBean);
                        }
                        enquiryAdapter.notifyData(enquiryBeans);
                        getSupportActionBar().setSubtitle(enquiryBeans.size() + " Nos");
                    } else {
                        Toast.makeText(MainActivityEnquiry.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityEnquiry.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityEnquiry.this,
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
                fetchContacts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                fetchContacts(query);
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
        fetchContacts("");
    }

    @Override
    public void onDeleteClick(int position) {
        AlertDialog diaBox = AskOption(position);
        diaBox.show();
    }

    @Override
    public void onCallClick(String phone, String name) {
        if ("Call".equalsIgnoreCase(name)) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        } else {
            try {
                Uri uri = Uri.parse("smsto:91" + phone);
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.setPackage("com.whatsapp.w4b");
                startActivity(i);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=91" + phone
                        + "&text=" +
                        "WELCOME TO STOCK BAZAAR\n" +
                        "\n" +
                        "THANK YOU FOR USING STOCK BAZAAR APP"
                       ));
                intent.setPackage("com.whatsapp.w4b");
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=91" + phone
                        + "&text=" +
                        "WELCOME TO STOCK BAZAAR\n" +
                        "\n" +
                        "THANK YOU FOR USING STOCK BAZAAR APP"));
                intent.setPackage("com.whatsapp");
                startActivity(intent);
            }
        }
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
                ENQUITY + "?id=" + enquiryBeans.get(position).id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Register Response: ", response);
                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            if (jObj.getBoolean("success")) {
                                fetchContacts("");
                            }
                            Toast.makeText(MainActivityEnquiry.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Log.e("xxxxxxxxxxx", e.toString());
                            Toast.makeText(MainActivityEnquiry.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityEnquiry.this,
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

}
