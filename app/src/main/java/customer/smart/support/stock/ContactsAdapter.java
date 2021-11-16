package customer.smart.support.stock;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import customer.smart.support.R;

/**
 * Created by ravi on 16/11/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>
        implements Filterable {
    private final Context context;
    private final List<Contact> contactList;
    private List<Contact> contactListFiltered;
    private final ContactsAdapterListener listener;

    public ContactsAdapter(Context context, List<Contact> contactList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Contact contact = contactListFiltered.get(position);
        holder.name.setText(contact.getBrand() + " " + contact.getModel() + ":");
        holder.price.setText("₹ " + contact.getPrice());
        holder.ram.setText("RAM " + contact.getRam() + ":");
        holder.rom.setText("ROM " + contact.getRom() + ":");
        holder.thumbnail.setText(String.valueOf(position + 1));
        holder.stock_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onContactSelected(contactListFiltered.get(position));
            }
        });
        Glide.with(context)
                .load(contact.getImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImageSelected(contactListFiltered.get(position));
            }
        });
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
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val = row.getBrand() + " " + row.getModel();
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
                contactListFiltered = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Contact contact);
        void onImageSelected(Contact contact);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price, ram, rom;
        public TextView thumbnail;
        public ImageView image;
        public LinearLayout stock_list;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);

            price = view.findViewById(R.id.price);
            ram = view.findViewById(R.id.ram);
            rom = view.findViewById(R.id.rom);
            thumbnail = view.findViewById(R.id.thumbnail);
            image = view.findViewById(R.id.image);
            stock_list = view.findViewById(R.id.stock_list);

        }
    }
}
