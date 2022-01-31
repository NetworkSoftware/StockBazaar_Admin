package customer.smart.support.shop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import customer.smart.support.R;

/**
 * Created by ravi on 16/11/17.
 */

public class AssignFilterAdapter extends RecyclerView.Adapter<AssignFilterAdapter.MyViewHolder>
        implements Filterable {
    private final Context context;
    private ArrayList<String> contactList;
    private final String selectedStrings;
    private ArrayList<String> contactListFiltered;
    private final OnFilterCheck listener;

    public AssignFilterAdapter(Context context, ArrayList<String> contactList,
                               OnFilterCheck listener, String listString) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
        this.selectedStrings = listString;
    }

    public void notifyData(ArrayList<String> myList) {
        Log.d("notifyData ", myList.size() + "");
        this.contactList = myList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final String contact = contactListFiltered.get(position);
        holder.title.setText(contact);
        holder.subtitle.setText("Tap to select");
        holder.inevestCheck.setChecked(selectedStrings.contains(contact));
        holder.inevestCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                listener.onChecked(contact, isChecked);
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
                if (charString.isEmpty() || charString.length() == 0) {
                    contactListFiltered = contactList;
                } else {
                    ArrayList<String> filteredList = new ArrayList<>();
                    for (String row : contactList) {
                        if (row.toLowerCase().contains(charString.toLowerCase())) {
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
                contactListFiltered = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Shop contact);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle;
        CheckBox inevestCheck;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.subtitle);
            inevestCheck = view.findViewById(R.id.inevestCheck);
        }
    }
}
