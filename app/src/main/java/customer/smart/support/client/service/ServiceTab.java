package customer.smart.support.client.service;

import android.content.Intent;
import android.net.Uri;
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


public class ServiceTab extends BaseFragment implements ServiceClick{

    View view;
    ArrayList<Service> serviceArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ServiceAdapter serviceAdapter;

    @Override
    protected View startDemo(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.service_tab_list, container, false);
        showTickets();
        return view;
    }

    private void showTickets() {
        recyclerView = view.findViewById(R.id.recycler_view);
        serviceArrayList = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(getActivity(), serviceArrayList,this);
        final LinearLayoutManager addManager1 = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(serviceAdapter);
        getAllService();
    }

    private void getAllService() {
        String tag_string_req = "req_reg" +
                "ster";
        pDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                Appconfig.SERVICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");

                    if (success) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        serviceArrayList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Service service = new Service();
                            service.setId(jsonObject.getString("id"));
                            service.setBrand(jsonObject.getString("brand"));
                            service.setModel(jsonObject.getString("model"));
                            service.setMobile(jsonObject.getString("mobile"));
                            service.setCategory(jsonObject.getString("category"));
                            service.setDistrict(jsonObject.getString("district"));
                            service.setPincode(jsonObject.getString("pincode"));
                            service.setProblem(jsonObject.getString("problem"));
                            serviceArrayList.add(service);

                        }
                        serviceAdapter.notifyData(serviceArrayList);

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
    public void onCallClick(String whatsapp) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + whatsapp));
        startActivity(intent);
    }
}