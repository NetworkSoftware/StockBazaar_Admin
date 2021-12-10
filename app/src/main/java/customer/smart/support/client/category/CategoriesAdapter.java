package customer.smart.support.client.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import customer.smart.support.R;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {
    private final Context context;
    private final CategoriesClick bannerClick;
    private List<Categories> categoriesList;


    public CategoriesAdapter(Context context, List<Categories> categoriesList, MainActivityCategories bannerClick) {
        this.context = context;
        this.categoriesList = categoriesList;
        this.bannerClick = bannerClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categories_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Categories categories = categoriesList.get(position);

        holder.title.setText(categories.title);
        GlideApp.with(context)
                .load(Appconfig.getResizedImage(categories.getImage(), false))
                .placeholder(R.drawable.profile)
                .into(holder.thumbnail);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick.onItemClick(position);
            }
        });
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public void notifyData(List<Categories> categoriesList) {
        this.categoriesList = categoriesList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail, cancel;
        public TextView title, percentage;

        public MyViewHolder(View view) {
            super(view);


            thumbnail = view.findViewById(R.id.thumbnail);
            cancel = view.findViewById(R.id.cancel);
            title = view.findViewById(R.id.title);

        }
    }
}
