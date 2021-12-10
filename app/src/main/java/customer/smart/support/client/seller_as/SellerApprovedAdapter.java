package customer.smart.support.client.seller_as;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import customer.smart.support.R;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;

public class SellerApprovedAdapter extends RecyclerView.Adapter<SellerApprovedAdapter.MyViewHolder> {
    private Context context;
    private List<Seller> sellers;
    private SellerClick sellerClick;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView cancel, thumbnail;
        public TextView seller_name, phone, shop_update, status;
        public MaterialButton details, pending, approved;

        public MyViewHolder(View view) {
            super(view);
            pending = view.findViewById(R.id.pending);
            approved = view.findViewById(R.id.approved);
            thumbnail = view.findViewById(R.id.thumbnail);
            cancel = view.findViewById(R.id.cancel);
            seller_name = view.findViewById(R.id.seller_name);
            phone = view.findViewById(R.id.phone);
            shop_update = view.findViewById(R.id.shop_update);
            details = view.findViewById(R.id.details);
            status = view.findViewById(R.id.status);
        }
    }


    public SellerApprovedAdapter(Context context, List<Seller> sellers, SellerClick sellerClick) {
        this.context = context;
        this.sellers = sellers;
        this.sellerClick = sellerClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seller_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Seller seller = sellers.get(position);

        holder.seller_name.setText(seller.shopname);
        holder.status.setText("Status : " + seller.status);
        holder.phone.setText(seller.mobile);
        GlideApp.with(context)
                .load(Appconfig.getResizedImage(seller.getShopInside(), true))
                .placeholder(R.drawable.profile)
                .into(holder.thumbnail);

        if (seller.status.equalsIgnoreCase("Request")) {
            holder.approved.setVisibility(View.VISIBLE);
            holder.pending.setVisibility(View.VISIBLE);
        } else if (seller.status.equalsIgnoreCase("Pending")) {
            holder.approved.setVisibility(View.VISIBLE);
            holder.pending.setVisibility(View.GONE);
        } else if (seller.status.equalsIgnoreCase("Approved")) {
            holder.approved.setVisibility(View.GONE);
            holder.pending.setVisibility(View.GONE);
        }
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellerClick.onDeleteClick(position);
            }
        });

        holder.pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellerClick.onStatus(seller, "Pending");
            }
        });

        holder.approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellerClick.onStatus(seller, "Approved");
            }
        });

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellerClick.onDetailClick(seller);
            }
        });


    }

    @Override
    public int getItemCount() {
        return sellers.size();
    }


    public void notifyData(List<Seller> categoriesList) {
        this.sellers = categoriesList;
        notifyDataSetChanged();
    }
}
