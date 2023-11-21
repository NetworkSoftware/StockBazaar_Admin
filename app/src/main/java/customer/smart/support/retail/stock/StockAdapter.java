package customer.smart.support.retail.stock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import customer.smart.support.client.stock.MainActivityProduct;
import customer.smart.support.client.stock.Product;

/**
 * Created by ravi on 16/11/17.
 */

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.MyViewHolder>
        implements Filterable {
    private final Context context;
    private List<StockModel> stockModels;
    private List<StockModel> stockModelList;
    OnStockClick onStockClick;

    public StockAdapter(Context context, List<StockModel> stockModels, OnStockClick onStockClick) {
        this.context = context;
        this.stockModels = stockModels;
        this.stockModelList = stockModels;
        this.onStockClick = onStockClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.retail_product_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final StockModel product = stockModelList.get(position);
        holder.price.setText("₹ " + product.getPrice());
        holder.name.setText(product.getName());
        holder.category.setText(product.getCategoryId());
        holder.createdOn.setText(product.getCreatedOn());
        ArrayList<String> urls = new Gson().fromJson(product.image, (Type) List.class);
        new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        GlideApp.with(context)
                .load(Appconfig.getResizedImage(urls.get(0), false))
                .into(holder.thumbnail);

        holder.stockEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStockClick.onEditClick(product);
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
                    List<StockModel> filteredList = new ArrayList<>();
                    for (StockModel row : stockModels) {

                        String val = row.getName();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        } else if (row.getPrice().contains(charString.toLowerCase())) {
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
                stockModelList = (ArrayList<StockModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyData(List<StockModel> productList) {
        this.stockModelList = productList;
        this.stockModels = productList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView price, name, category, createdOn;
        public ImageView thumbnail;
        RelativeLayout stockEdit;

        public MyViewHolder(View view) {
            super(view);
            stockEdit = view.findViewById(R.id.stockEdit);
            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            category = view.findViewById(R.id.category);
            createdOn = view.findViewById(R.id.createdOn);
        }
    }
}
