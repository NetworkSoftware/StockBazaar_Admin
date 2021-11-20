package customer.smart.support.client.filter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import customer.smart.support.R;


public class CategoryFilterAdapter extends RecyclerView.Adapter<CategoryFilterAdapter.MyViewHolder> {


    private final Context context;
    private final OncategoryFilter oncategoryFilter;
    private ArrayList<CategoryFilterBean> blogArrayList;
    private String selectedPosition;

    public CategoryFilterAdapter(Context context, ArrayList<CategoryFilterBean> blogArrayList, OncategoryFilter oncategoryFilter, String selectedPosition) {
        this.blogArrayList = blogArrayList;
        this.context = context;
        this.oncategoryFilter = oncategoryFilter;
        this.selectedPosition = selectedPosition;
    }

    public void notifyData(ArrayList<CategoryFilterBean> myList) {
        this.blogArrayList = myList;
        notifyDataSetChanged();
    }

    public void notifyData(String selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.brand_filter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CategoryFilterBean bean = blogArrayList.get(position);
        holder.action_chip.setText(bean.category);
        if (bean.id.equalsIgnoreCase(selectedPosition)) {
            holder.action_chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.chipSelected)));
            holder.action_chip.setChipStrokeColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
        } else {
            holder.action_chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.chipUnSelect)));
            holder.action_chip.setChipStrokeColor(ColorStateList.valueOf(context.getResources().getColor(R.color.light_gray)));
        }

        holder.action_chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oncategoryFilter.onselectCategory(bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.chip.Chip action_chip;

        public MyViewHolder(View view) {
            super(view);
            action_chip = view.findViewById(R.id.brand_filter);
        }
    }
}