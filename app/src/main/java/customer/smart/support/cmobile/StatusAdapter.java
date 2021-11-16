package customer.smart.support.cmobile;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import customer.smart.support.R;


public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder> {


    private final Context context;
    private final OnStatus onBlock;
    private ArrayList<StatusBean> blogArrayList;
    private String selectedPosition;

    public StatusAdapter(Context context, ArrayList<StatusBean> blogArrayList, OnStatus onBlock, String selectedPosition) {
        this.blogArrayList = blogArrayList;
        this.context = context;
        this.onBlock = onBlock;
        this.selectedPosition = selectedPosition;
    }

    public void notifyData(ArrayList<StatusBean> myList) {
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
                .inflate(R.layout.status_filter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final StatusBean bean = blogArrayList.get(position);
        holder.action_chip.setText(bean.status);
        if (bean.status.equalsIgnoreCase(selectedPosition)) {
            holder.action_chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.chipSelected)));
            holder.action_chip.setChipStrokeColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
        } else {
            holder.action_chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.chipUnSelect)));
            holder.action_chip.setChipStrokeColor(ColorStateList.valueOf(context.getResources().getColor(R.color.light_gray)));
        }

        holder.action_chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBlock.onStatus(bean.status);
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
            action_chip = view.findViewById(R.id.status_filter);
        }
    }
}