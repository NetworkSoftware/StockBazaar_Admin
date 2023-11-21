package customer.smart.support.retail.folder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import customer.smart.support.R;

/**
 * Created by ravi on 16/11/17.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder>
        implements Filterable {
    private final List<FolderModel> folderModelList;
    private List<FolderModel> modelList;
    private final OnFolderClick onFolderClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CardView folderView;
        public ImageView edit, delete, share;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            folderView = view.findViewById(R.id.folderView);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);
            share = view.findViewById(R.id.share);

        }
    }


    public FolderAdapter(List<FolderModel> contactList, OnFolderClick onFolderClick) {
        this.folderModelList = contactList;
        this.modelList = contactList;
        this.onFolderClick = onFolderClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.folder_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final FolderModel contact = modelList.get(position);
        holder.name.setText(contact.getName() + " ( " + contact.getCount() + " )");
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFolderClick.onEditClick(contact);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFolderClick.onDeleteClick(contact);
            }
        });
        holder.folderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFolderClick.onViewClick(contact);
            }
        });
        holder.folderView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onFolderClick.onMoveClick(contact);
                return false;
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFolderClick.onShareClick(contact);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


    public void notifyData(List<FolderModel> folderModels) {
        this.modelList = folderModels;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    modelList = folderModelList;
                } else {
                    List<FolderModel> filteredList = new ArrayList<>();
                    for (FolderModel row : folderModelList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val = row.getName();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    modelList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = modelList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                modelList = (ArrayList<FolderModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
