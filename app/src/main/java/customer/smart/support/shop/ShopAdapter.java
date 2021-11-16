package customer.smart.support.shop;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import customer.smart.support.R;
import customer.smart.support.app.GlideApp;
import de.hdodenhof.circleimageview.CircleImageView;


public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.MyViewHolder> implements Filterable {

    private List<Shop> moviesList;
    private Context context;
    private OnShopClick onShopClick;
    private List<Shop> moviesListFiltered;
    private ContactsAdapterListener listener;
    private String role;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    moviesListFiltered = moviesList;
                } else {
                    List<Shop> filteredList = new ArrayList<>();
                    for (Shop row : moviesList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val = row.getContact();
                        if (val.toLowerCase().contains(charString.toLowerCase())
                                || row.getShopname().toLowerCase().contains(charString.toLowerCase()) || row.getPincode().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    moviesListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = moviesListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                moviesListFiltered = (ArrayList<Shop>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView shopName, name, phone, pincode;
        public CircleImageView profiletImage;
        private ImageView editImg, deleteImg;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            shopName = (TextView) view.findViewById(R.id.shopName);
            phone = (TextView) view.findViewById(R.id.phone);
            pincode = (TextView) view.findViewById(R.id.pincode);
            profiletImage = (CircleImageView) view.findViewById(R.id.profileImage);
            editImg = (ImageView) view.findViewById(R.id.editImg);
            deleteImg = (ImageView) view.findViewById(R.id.deleteImg);


        }
    }


    public ShopAdapter(List<Shop> moviesList, Context context, OnShopClick onShopClick, ContactsAdapterListener listener, String role) {


        this.moviesList = moviesList;
        this.moviesListFiltered = moviesList;
        this.context = context;
        this.onShopClick = onShopClick;
        this.listener = listener;
        this.role = role;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Shop shop = moviesListFiltered.get(position);
        holder.name.setText(shop.getShopname());
        holder.shopName.setText(shop.area);
        holder.phone.setText(shop.contact);
        holder.pincode.setText(shop.pincode);
        GlideApp.with(context)
                .load(shop.image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .placeholder(R.drawable.profile)
                .into(holder.profiletImage);

        if (role.equalsIgnoreCase("sadmin")) {
            holder.profiletImage.setVisibility(View.VISIBLE);
            holder.editImg.setVisibility(View.VISIBLE);
            holder.deleteImg.setVisibility(View.VISIBLE);
        } else {
            holder.profiletImage.setVisibility(View.GONE);
            holder.editImg.setVisibility(View.GONE);
            holder.deleteImg.setVisibility(View.GONE);
        }
        holder.profiletImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShopClick.onEditClick(moviesListFiltered.get(position));
            }
        });

        holder.editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShopClick.onEditClick(moviesListFiltered.get(position));
            }
        });

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShopClick.onDeleteClick(shop);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesListFiltered.size();
    }

    public void notifyData(List<Shop> moviesList) {
        this.moviesList = moviesList;
        this.moviesListFiltered = moviesList;
        notifyDataSetChanged();
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Shop shop);
    }
}
