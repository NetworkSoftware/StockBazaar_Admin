package customer.smart.support.contact;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import customer.smart.support.R;
import customer.smart.support.app.GlideApp;

/**
 * Created by ravi on 16/11/17.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder>
        implements Filterable {
    private List<Contact> contactListone;
    private List<Contact> contactListFilteredone;
    private ContactAdapterListener listenerone;
    private OnContactClick onContactClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, pincode, phone, address,area;
        public Button editImg;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            pincode = view.findViewById(R.id.pincode);
            phone = view.findViewById(R.id.phone);
            address = view.findViewById(R.id.address);
            editImg = view.findViewById(R.id.editImg);
            area = view.findViewById(R.id.area);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listenerone.onContactSelected(contactListFilteredone.get(getAdapterPosition()));
                }
            });
        }
    }


    public ContactAdapter(List<Contact> contactList, ContactAdapterListener listenerone,OnContactClick onContactClick) {
        this.listenerone = listenerone;
        this.contactListone = contactList;
        this.contactListFilteredone = contactList;
        this.onContactClick=onContactClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Contact contact = contactListFilteredone.get(position);
        holder.name.setText(contact.getName());
        holder.pincode.setText(contact.getPincode());
        holder.phone.setText(contact.getPhone());
        holder.address.setText(contact.address);
        holder.area.setText(contact.area);
        holder.editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactClick.onEditClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactListFilteredone.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFilteredone = contactListone;
                } else {
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact row : contactListone) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val = row.getName() + " " + row.getPincode();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    contactListFilteredone = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFilteredone;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFilteredone = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactAdapterListener {
        void onContactSelected(Contact contactone);
    }
}
