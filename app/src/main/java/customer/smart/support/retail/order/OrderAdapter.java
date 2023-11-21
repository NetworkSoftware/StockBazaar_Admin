package customer.smart.support.retail.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import customer.smart.support.R;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;
import customer.smart.support.retail.stock.StockModel;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder>
        implements Filterable {
    private final Context context;
    private List<OrderModel> stockModels;
    private List<OrderModel> stockModelList;
    OnOrderClick onOrderClick;

    public OrderAdapter(Context context, List<OrderModel> stockModels, OnOrderClick onOrderClick) {
        this.context = context;
        this.stockModels = stockModels;
        this.stockModelList = stockModels;
        this.onOrderClick = onOrderClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.retail_order_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final OrderModel product = stockModelList.get(position);
        holder.price.setText("₹ " + product.getTotalAmt());
        holder.name.setText(product.getName());
        holder.status.setText(product.getStatus());
        holder.createdOn.setText(product.getCreatedOn());
        RetailSubAdapter retailSubAdapter = new RetailSubAdapter(context, product.getProductBeans());
        final LinearLayoutManager addManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                false);
        holder.cart_sub_list.setLayoutManager(addManager1);
        holder.cart_sub_list.setAdapter(retailSubAdapter);

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderClick.onCallClick(product);
            }
        });

        holder.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderClick.onWhatsappClick(product);
            }
        });

    }

    @Override
    public int getItemCount() {
        return stockModelList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    stockModelList = stockModels;
                } else {
                    List<OrderModel> filteredList = new ArrayList<>();
                    for (OrderModel row : stockModels) {

                        String val = row.getName();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        } else if (row.getStatus().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    stockModelList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = stockModelList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                stockModelList = (ArrayList<OrderModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyData(List<OrderModel> productList) {
        this.stockModelList = productList;
        this.stockModels = productList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView price, name, status, createdOn;
        public ImageView  call, whatsapp;
        RelativeLayout stockEdit;
        public RecyclerView cart_sub_list;

        public MyViewHolder(View view) {
            super(view);
            stockEdit = view.findViewById(R.id.stockEdit);
            cart_sub_list = view.findViewById(R.id.cart_sub_list);
            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
            status = view.findViewById(R.id.status);
            call = view.findViewById(R.id.call);
            whatsapp = view.findViewById(R.id.whatsapp);
            createdOn = view.findViewById(R.id.createdOn);
        }
    }
}
