package customer.smart.support.retail.category;

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
import customer.smart.support.retail.folder.FolderModel;
import customer.smart.support.retail.folder.OnFolderClick;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>
        implements Filterable {
    private final List<CategoryModel> categoryModelList;
    private List<CategoryModel> categoryModels;
    private final OnCategoryClick onCategoryClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CardView folderView;
        public ImageView delete;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            folderView = view.findViewById(R.id.folderView);
            delete = view.findViewById(R.id.delete);

        }
    }


    public CategoryAdapter(List<CategoryModel> contactList, OnCategoryClick onCategoryClick) {
        this.categoryModelList = contactList;
        this.categoryModels = contactList;
        this.onCategoryClick = onCategoryClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CategoryModel contact = categoryModels.get(position);
        holder.name.setText(contact.getTitle());
        holder.folderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick.onEditClick(contact);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick.onDeleteClick(contact);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }


    public void notifyData(List<CategoryModel> folderModels) {
        this.categoryModels = folderModels;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    categoryModels = categoryModelList;
                } else {
                    List<CategoryModel> filteredList = new ArrayList<>();
                    for (CategoryModel row : categoryModelList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val = row.getTitle();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    categoryModels = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = categoryModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                categoryModels = (ArrayList<CategoryModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
