package customer.smart.support.client.service;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
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
import java.util.Map;

import customer.smart.support.R;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.BaseFragment;
import customer.smart.support.attachment.ActivityMediaOnline;
import customer.smart.support.client.exchange.Exchange;
import customer.smart.support.client.exchange.ExchangeAdapter;
import customer.smart.support.client.exchange.ExchangeClick;

public class ExchangeTab extends BaseFragment implements ExchangeClick {

    View view;
    ArrayList<Exchange> exchangeArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ExchangeAdapter exchangeAdapter;

    @Override
    protected View startDemo(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.service_tab_list, container, false);
        showTickets();
        return view;
    }

    private void showTickets() {
        recyclerView = view.findViewById(R.id.recycler_view);
        exchangeArrayList = new ArrayList<>();
        exchangeAdapter = new ExchangeAdapter(getActivity(), exchangeArrayList,this);
        final LinearLayoutManager addManager1 = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(exchangeAdapter);
        getAllBooking();
    }

    private void getAllBooking() {
        String tag_string_req = "req_reg" +
                "ster";
        pDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                Appconfig.EXCHANGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");

                    if (success) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        exchangeArrayList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Exchange service = new Exchange();
                            service.setId(jsonObject.getString("id"));
                            service.setBrand(jsonObject.getString("brand"));
                            service.setModel(jsonObject.getString("model"));
                            service.setRam(jsonObject.getString("ram"));
                            service.setRom(jsonObject.getString("rom"));
                            service.setWarranty(jsonObject.getString("warranty"));
                            service.setBox(jsonObject.getString("box"));
                            service.setPrice(jsonObject.getString("price"));
                            service.setImage(jsonObject.getString("image"));
                            service.setWhatsapp(jsonObject.getString("whatsapp"));
                            service.setAccessories(jsonObject.getString("accessories"));
                            service.setMobileCondition(jsonObject.getString("mobileCondition"));
                            service.setHardwareProblem(jsonObject.getString("hardwareProblem"));
                            service.setSoftwareChanged(jsonObject.getString("softwareChanged"));
                            exchangeArrayList.add(service);
                        }
                        exchangeAdapter.notifyData(exchangeArrayList);

                    } else {
                        Toast.makeText(getActivity(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getActivity(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getActivity(),
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
    public void onImageClick(Exchange exchange) {
        Intent localIntent = new Intent(getActivity(), ActivityMediaOnline.class);
        localIntent.putExtra("filePath", exchange.getImage());
        localIntent.putExtra("isImage", true);
        startActivity(localIntent);
    }
}