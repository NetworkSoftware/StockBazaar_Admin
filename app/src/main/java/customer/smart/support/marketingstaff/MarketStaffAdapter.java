package customer.smart.support.marketingstaff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import customer.smart.support.R;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;
import customer.smart.support.stock.Contact;

/**
 * Created by ravi on 16/11/17.
 */

public class MarketStaffAdapter extends RecyclerView.Adapter<MarketStaffAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<MarketStaff> contactList;
    private List<MarketStaff> contactListFiltered;
    private MarketStaffAdapterListener listener;
    SharedPreferences sharedPreferences;
    public MarketStaffAdapter(Context context, List<MarketStaff> contactList, MarketStaffAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
        sharedPreferences = context.getSharedPreferences(Appconfig.mypreference, Context.MODE_PRIVATE);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marketstaff_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final MarketStaff contact = contactListFiltered.get(position);
        holder.name.setText(contact.getCustomerName());
        holder.contact.setText(contact.customerContact);
        holder.price.setText(contact.brand+"-"+contact.model+"-"+contact.price);
        holder.staff.setText("Status: "+contact.status);
        ArrayList<String> samplesList = new Gson().fromJson(contact.image, (Type) List.class);
        holder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStatusChange(contactListFiltered.get(position));
            }
        });

        if((contact.getStatus().equalsIgnoreCase("done"))||
                (sharedPreferences.getString("data", "").contains("marketing"))){
            holder.status.setVisibility(View.GONE);
        }else {
            holder.status.setVisibility(View.VISIBLE);

        }

        GlideApp.with(context)
                .load(samplesList.get(0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .placeholder(R.drawable.profile)
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<MarketStaff> filteredList = new ArrayList<>();
                    for (MarketStaff row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val = row.getBrand() + " " + row.getBrand();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<MarketStaff>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyData(List<MarketStaff> marketStaffs) {
        this.contactListFiltered = marketStaffs;
        this.contactList = marketStaffs;
        notifyDataSetChanged();
    }

    public interface MarketStaffAdapterListener {
        void onStaffSelected(MarketStaff contact);
        void onStatusChange(MarketStaff contact);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, contact, price,staff;
        public ImageView thumbnail;
        Button status;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            contact = view.findViewById(R.id.contact);
            price = view.findViewById(R.id.price);
            staff=view.findViewById(R.id.staff);
            thumbnail = view.findViewById(R.id.thumbnail);
            status = view.findViewById(R.id.status);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onStaffSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }
}
