package customer.smart.support.client.filter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import customer.smart.support.R;


public class BrandFilterAdapter extends RecyclerView.Adapter<BrandFilterAdapter.MyViewHolder> {


    private final Context context;
    private final OnBrandFilter onBrandFilter;
    private ArrayList<BrandFilterBean> blogArrayList;
    private String selectedPosition;

    public BrandFilterAdapter(Context context, ArrayList<BrandFilterBean> blogArrayList, OnBrandFilter onBrandFilter, String selectedPosition) {
        this.blogArrayList = blogArrayList;
        this.context = context;
        this.onBrandFilter = onBrandFilter;
        this.selectedPosition = selectedPosition;
    }

    public void notifyData(ArrayList<BrandFilterBean> myList) {
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
        final BrandFilterBean bean = blogArrayList.get(position);
        holder.action_chip.setText(bean.brand);
        if (bean.brand.equalsIgnoreCase(selectedPosition)) {
            holder.action_chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.chipSelected)));
            holder.action_chip.setChipStrokeColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
        } else {
            holder.action_chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.chipUnSelect)));
            holder.action_chip.setChipStrokeColor(ColorStateList.valueOf(context.getResources().getColor(R.color.light_gray)));
        }

        holder.action_chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBrandFilter.onSltBrand(bean.brand);
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