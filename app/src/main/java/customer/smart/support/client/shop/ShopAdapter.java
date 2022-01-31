package customer.smart.support.client.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import customer.smart.support.R;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;
import customer.smart.support.client.stock.ProductAdapter;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.MyViewHolder>
        implements Filterable {
    private final Context context;
    private List<Shop> shopList;
    private List<Shop> shopFilter;
    private final ShopClick bannerClick;
    private ContactsAdapterListener listener;


    public ShopAdapter(Context context, List<Shop> shops, ContactsAdapterListener listener, ShopClick bannerClick) {
        this.context = context;
        this.shopList = shops;
        this.listener = listener;
        this.bannerClick = bannerClick;
        this.shopFilter = shops;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_row_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    shopFilter = shopList;
                } else {
                    List<Shop> filteredList = new ArrayList<>();
                    for (Shop row : shopList) {

                        String val = row.getShop_name();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        } else if (row.getPhone().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    shopFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = shopFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                shopFilter = (ArrayList<Shop>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Shop shop = shopFilter.get(position);

        holder.shop_name.setText(shop.shop_name);
        holder.phone.setText(shop.phone);
        holder.shop_update.setText(shop.stock_update);
        GlideApp.with(context)
                .load(Appconfig.getResizedImage(shop.getImage(), true))
                .placeholder(R.drawable.profile)
                .into(holder.thumbnail);

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick.onDeleteClick(position);
            }
        });

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick.onItemClick(shop);
            }
        });
        holder.stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick.onStockAdd(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return shopFilter.size();
    }

    public void notifyData(List<Shop> categoriesList) {
        this.shopFilter = categoriesList;
        this.shopList = categoriesList;
        notifyDataSetChanged();
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Shop product);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout update;
        public ImageView cancel, thumbnail;
        public TextView shop_name, phone, shop_update;
        public MaterialButton stock;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            cancel = view.findViewById(R.id.cancel);
            shop_name = view.findViewById(R.id.shop_name);
            phone = view.findViewById(R.id.phone);
            shop_update = view.findViewById(R.id.shop_update);
            update = view.findViewById(R.id.update);
            stock = view.findViewById(R.id.stock);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(shopFilter.get(getAdapterPosition()));
                }
            });
        }
    }

}
