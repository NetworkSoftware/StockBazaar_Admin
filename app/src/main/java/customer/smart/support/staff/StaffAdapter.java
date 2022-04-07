package customer.smart.support.staff;

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

/**
 * Created by ravi on 16/11/17.
 */

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Staff> contactList;
    private List<Staff> contactListFiltered;
    private StaffAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,contact,password,data,role;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            data = view.findViewById(R.id.data);
            role = view.findViewById(R.id.role);
            contact = view.findViewById(R.id.contact);
            password = view.findViewById(R.id.password);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onStaffSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public StaffAdapter(Context context, List<Staff> contactList, StaffAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.staff_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Staff contact = contactListFiltered.get(position);
        holder.name.setText(contact.getName());
        holder.password.setText(contact.getPassword());
        holder.contact.setText(contact.contact);
        holder.data.setText(contact.data);
        holder.role.setText(contact.role);

        GlideApp.with(context)
                .load(contact.image)
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
                    List<Staff> filteredList = new ArrayList<>();
                    for (Staff row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val=row.getName()+" "+row.getContact();
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
                contactListFiltered = (ArrayList<Staff>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface StaffAdapterListener {
        void onStaffSelected(Staff contact);
    }
}
