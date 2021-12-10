package customer.smart.support.client.wallet;

import static customer.smart.support.app.Appconfig.WALLET;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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


public class WalletActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    TextView tol_amt;
    SharedPreferences sharedpreferences;
    private RecyclerView recyclerView;
    private List<WalletBean> contactList;
    private WalletAdapter mAdapter;
    Button wallet;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallet);

        sharedpreferences = getApplicationContext().getSharedPreferences(Appconfig.mypreference, Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        tol_amt = findViewById(R.id.tol_amt);
        wallet = findViewById(R.id.wallet);
        recyclerView = findViewById(R.id.recycler_view);
        contactList = new ArrayList<>();
        mAdapter = new WalletAdapter(WalletActivity.this, contactList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(WalletActivity.this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setAdapter(mAdapter);
        userId = getIntent().getStringExtra("id");

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCashBack(userId);
            }
        });
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
    }

    private void fetchContacts() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                WALLET + "?userId=" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        Log.d("Register Response: ", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int success = jObj.getInt("success");
                            if (success == 1) {
                                JSONArray jsonArray = jObj.getJSONArray("data");
                                contactList = new ArrayList<>();


                                if (jObj.getString("totalAmt") == null) {
                                    tol_amt.setText("0");
                                } else {
                                    tol_amt.setText(jObj.getString("totalAmt"));
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    WalletBean product = new WalletBean();
                                    product.setId(jsonObject.getString("id"));
                                    product.setAmt(jsonObject.getString("amt"));
                                    product.setOperation(jsonObject.getString("operation"));
                                    product.setCreatedon(jsonObject.getString("createdon"));
                                    product.setDescription(jsonObject.getString("description"));
                                    contactList.add(product);
                                }
                                mAdapter.notifyData(contactList);
                            } else {
                                Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("userId", getIntent().getStringExtra("id"));
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchContacts();
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


    private void showCashBack(final String id) {
        final RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(WalletActivity.this);
        LayoutInflater inflater = WalletActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bottom_amount_layout, null);

        TextInputLayout reviewTxt = dialogView.findViewById(R.id.walletTxt);
        final TextInputEditText walletEdit = dialogView.findViewById(R.id.wallet);
        final TextInputEditText description = dialogView.findViewById(R.id.description);


        final RadioButton radioIn = dialogView.findViewById(R.id.radioIn);
        final RadioButton radioOut = dialogView.findViewById(R.id.radioOut);
        radioIn.setChecked(true);

        radioIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioIn.setChecked(true);
                    radioOut.setChecked(false);
                } else {
                    radioIn.setChecked(false);
                    radioOut.setChecked(true);
                }
            }
        });
        radioOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioIn.setChecked(false);
                    radioOut.setChecked(true);
                } else {
                    radioIn.setChecked(true);
                    radioOut.setChecked(false);
                }
            }
        });

        final Button submit = dialogView.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (walletEdit.getText().toString().length() <= 0 ||
                        description.getText().toString().length() <= 0) {
                    Toast.makeText(WalletActivity.this, "Enter Valid Cashback", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    updateWallet(id, walletEdit.getText().toString(), description.getText().toString(),
                            radioIn.isChecked(), "", mBottomSheetDialog);
                }
            }
        });

        mBottomSheetDialog.setContentView(dialogView);
        walletEdit.requestFocus();
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RoundedBottomSheetDialog d = (RoundedBottomSheetDialog) dialog;
                        FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 0);
            }
        });
        mBottomSheetDialog.show();
    }

    private void updateWallet(final String userId, final String wallet, final String description, final boolean isCredit,
                              final String orderId, final RoundedBottomSheetDialog mBottomSheetDialog) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Updating...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                WALLET, new Response.Listener<String>() {
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
                        fetchContacts();
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
                localHashMap.put("userId", userId);
                localHashMap.put("description", description);
                localHashMap.put("credit", isCredit ? "add" : "minus");
                localHashMap.put("amt", wallet);
                localHashMap.put("orderId", orderId);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
