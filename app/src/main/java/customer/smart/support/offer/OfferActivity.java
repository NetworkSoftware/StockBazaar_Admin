package customer.smart.support.offer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

public class OfferActivity extends AppCompatActivity implements OnOfferClick {

    OfferAdapter shopAdapter;
    List<Offer> shopList = new ArrayList<>();

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        final FloatingActionButton addShop = (FloatingActionButton) findViewById(R.id.addShop);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        shopAdapter = new OfferAdapter(shopList, this, this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(shopAdapter);
        addShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfferActivity.this, OfferRegister.class);
                startActivity(intent);
            }
        });

        //getAllStaff();

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
                Appconfig.ALL_OFFER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("offer");
                        shopList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Offer shop = new Offer();
                            shop.setId(jsonObject.getString("id"));
                            shop.setName(jsonObject.getString("name"));
                            shop.setStartDate(jsonObject.getString("startDate"));
                            shop.setEndDate(jsonObject.getString("endDate"));
                            shop.setPrice(jsonObject.getString("price"));
                            shop.setCategory(jsonObject.getString("isDealer"));
                            shop.setMinQuantity(jsonObject.getString("minQuantity"));
                            shop.setMaxQuantity(jsonObject.getString("maxQuantity"));
                            shop.setProductId(jsonObject.getString("productId"));
                            shop.setImage(jsonObject.getString("image"));

                            ArrayList<String> imageList = new ArrayList<>();
                            try {
                                if (!jsonObject.isNull("image1")) {
                                    JSONArray jsonArray1 = jsonObject.getJSONArray("image1");
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        imageList.add(jsonArray1.getString(j));
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("xxxxxxxxxxxxx", e.toString());
                            }
                            shop.setImage1(imageList);

                            shopList.add(shop);
                        }
                        shopAdapter.notifyData(shopList);

                    } else {
                        Toast.makeText(OfferActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(OfferActivity.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
             //   Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(OfferActivity.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
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
    public void onDeleteClick(int position) {
        AlertDialog diaBox = AskOption(position);
        diaBox.show();
    }

    @Override
    public void onEditClick(int position) {
        Intent intent = new Intent(OfferActivity.this, OfferUpdate.class);
        intent.putExtra("object", shopList.get(position));
        startActivity(intent);
    }

    private void deleteShop(final Offer shop, final int position) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Fetching ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Appconfig.DELETE_OFFER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        shopList.remove(position);
                    }
                    Toast.makeText(OfferActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    shopAdapter.notifyData(shopList);

                } catch (
                        JSONException e)

                {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(OfferActivity.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(OfferActivity.this,
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        })

        {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("id", shop.getId());
                return localHashMap;
            }
        };
        AppController.getInstance().

                addToRequestQueue(strReq, tag_string_req);
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
                        deleteShop(shopList.get(position), position);
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
}