package customer.smart.support.client.enquiry;

import android.annotation.SuppressLint;
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
import customer.smart.support.client.category.Categories;
import customer.smart.support.client.category.MainActivityCategories;

public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.MyViewHolder> {
    private final Context context;
    private final EnquiryClick enquiryClick;
    private List<EnquiryBean> enquiryBeans;


    public EnquiryAdapter(Context context, List<EnquiryBean> enquiryBeans, EnquiryClick enquiryClick) {
        this.context = context;
        this.enquiryBeans = enquiryBeans;
        this.enquiryClick = enquiryClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.enquiry_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final EnquiryBean enquiryBean = enquiryBeans.get(position);

        holder.product_name.setText(enquiryBean.product_name);
        holder.details.setText(enquiryBean.details);
        holder.qty.setText(enquiryBean.qty);
        holder.whatsapp_number.setText(enquiryBean.whatsapp_number);
        holder.district.setText(enquiryBean.district);
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enquiryClick.onDeleteClick(position);
            }
        });
        holder.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enquiryClick.onCallClick(enquiryBean.whatsapp_number,"Whatsapp");
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enquiryClick.onCallClick(enquiryBean.whatsapp_number,"Call");
            }
        });
    }

    @Override
    public int getItemCount() {
        return enquiryBeans.size();
    }

    public void notifyData(List<EnquiryBean> categoriesList) {
        this.enquiryBeans = categoriesList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView  cancel,call,whatsapp;
        public TextView product_name, details,qty,whatsapp_number,district;

        public MyViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.product_name);
            call = view.findViewById(R.id.call);
            whatsapp = view.findViewById(R.id.whatsapp);
            details = view.findViewById(R.id.details);
            qty = view.findViewById(R.id.qty);
            whatsapp_number = view.findViewById(R.id.whatsapp_number);
            district = view.findViewById(R.id.district);
            cancel = view.findViewById(R.id.cancel);

        }
    }
}
