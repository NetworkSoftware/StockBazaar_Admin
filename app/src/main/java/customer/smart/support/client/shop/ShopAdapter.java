package customer.smart.support.client.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import customer.smart.support.R;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.MyViewHolder> {
    private Context context;
    private List<Shop> categoriesList;
    private ShopClick bannerClick;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout update;
        public ImageView  cancel,thumbnail;
        public TextView shop_name,phone,shop_update;
        public MaterialButton stock;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            cancel = view.findViewById(R.id.cancel);
            shop_name = view.findViewById(R.id.shop_name);
            phone = view.findViewById(R.id.phone);
            shop_update = view.findViewById(R.id.shop_update);
            update=view.findViewById(R.id.update);
            stock = view.findViewById(R.id.stock);
        }
    }


    public ShopAdapter(Context context, List<Shop> categoriesList, ShopClick bannerClick) {
        this.context = context;
        this.categoriesList = categoriesList;
        this.bannerClick =  bannerClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Shop categories = categoriesList.get(position);

        holder.shop_name.setText(categories.shop_name);
        holder.phone.setText(categories.phone);
        holder.shop_update.setText(categories.stock_update);
        GlideApp.with(context)
                .load(Appconfig.getResizedImage(categories.getImage(), true))
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
                bannerClick.onItemClick(position);
            }
        });
        holder.stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick. onStockAdd(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }


    public void notifyData(List<Shop> categoriesList) {
        this.categoriesList = categoriesList;
        notifyDataSetChanged();
    }
}
