package customer.smart.support.spares;

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
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import customer.smart.support.R;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;
import customer.smart.support.stock.Contact;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ravi on 16/11/17.
 */

public class SparesAdapter extends RecyclerView.Adapter<SparesAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<SparesBean> sparesBeans;
    private List<SparesBean> contactListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView brand,price,spare,instock,spareId;
        public ImageView profileImage;

        public MyViewHolder(View view) {
            super(view);

            brand = view.findViewById(R.id.brand);
            price = view.findViewById(R.id.price);
            spare = view.findViewById(R.id.spare);
            profileImage = view.findViewById(R.id.profileImage);
            instock=view.findViewById(R.id.instock);
            spareId=view.findViewById(R.id.spareId);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public SparesAdapter(Context context, List<SparesBean> sparesBeans, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.sparesBeans = sparesBeans;
        this.contactListFiltered = sparesBeans;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spare_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final SparesBean contact = contactListFiltered.get(position);
        holder.brand.setText(contact.getBrand());
        holder.price.setText("₹ "+contact.getPrice());
        holder.spare.setText(contact.spare);
        holder.instock.setText(contact.stock_update);
        holder.spareId.setText("#"+contact.getId());

        ArrayList<String> urls = new Gson().fromJson(contact.image, (Type) List.class);
        GlideApp.with(context)
                .load(Appconfig.getResizedImage(urls.get(0), false))
                .into(holder.profileImage);
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
                    contactListFiltered = sparesBeans;
                } else {
                    List<SparesBean> filteredList = new ArrayList<>();
                    for (SparesBean row : sparesBeans) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val=row.getBrand()+" "+row.getSpare();
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
                contactListFiltered = (ArrayList<SparesBean>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(SparesBean sparesBean);
    }
}
