package customer.smart.support.client.wallet;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import customer.smart.support.R;
import customer.smart.support.client.seller_as.SellerClick;


public class AllWalletAdapter extends RecyclerView.Adapter<AllWalletAdapter.MyViewHolder> {
    private List<AllWalletBean> productList;
    private Context context;
    private StatusClick statusClick;

    public AllWalletAdapter(Context context, List<AllWalletBean> contactList, StatusClick statusClick) {
        this.context = context;
        this.productList = contactList;
        this.statusClick = statusClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.allwallet_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final AllWalletBean walletBean = productList.get(position);
        holder.dated.setText(walletBean.getCreatedon());
        holder.amount.setText("Rs. " + walletBean.getAmt());
        holder.userId.setText("#" + walletBean.getUserId());
        holder.userName.setText(walletBean.getUserName());
        holder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusClick.status(walletBean,"Viewed");
            }
        });

        if(walletBean.status.equalsIgnoreCase("Viewed")){
            holder.status.setVisibility(View.GONE);
        }else {
            holder.status.setVisibility(View.VISIBLE);
        }

        if (walletBean.operation.equalsIgnoreCase("add")) {
            holder.amount.setTextColor(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        } else {
            holder.amount.setTextColor(ColorStateList.valueOf(Color.parseColor("#F44336")));
        }
        holder.description.setText(walletBean.getDescription());

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void notifyData(List<AllWalletBean> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView amount, dated, description, userName, userId, status;

        public MyViewHolder(View view) {
            super(view);
            amount = view.findViewById(R.id.amount);
            dated = view.findViewById(R.id.dated);
            description = view.findViewById(R.id.description);
            userName = view.findViewById(R.id.userName);
            userId = view.findViewById(R.id.userId);
            status = view.findViewById(R.id.status);

        }
    }


}
