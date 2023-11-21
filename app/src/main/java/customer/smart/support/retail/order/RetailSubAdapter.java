package customer.smart.support.retail.order;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import customer.smart.support.R;
import customer.smart.support.app.Appconfig;
import customer.smart.support.order.CartItems;
import customer.smart.support.retail.stock.StockModel;


public class RetailSubAdapter extends RecyclerView.Adapter<RetailSubAdapter.MyViewHolder> {

    private final Context mainActivityUser;
    private ArrayList<CartItemModel> modelArrayList;
    int selectedPosition = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView product_image;
        private final TextView qty;
        private final TextView name;


        public MyViewHolder(View view) {
            super((view));
            product_image = view.findViewById(R.id.product_image);
            qty = view.findViewById(R.id.qty);
            name = view.findViewById(R.id.name);
        }
    }

    public RetailSubAdapter(Context mainActivityUser, ArrayList<CartItemModel> modelArrayList) {
        this.mainActivityUser = mainActivityUser;
        this.modelArrayList = modelArrayList;
    }

    public void notifyData(ArrayList<CartItemModel> orderBeans) {
        this.modelArrayList = orderBeans;
        notifyDataSetChanged();
    }

    public void notifyData(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myorders_list_sub, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CartItemModel orderBean = modelArrayList.get(position);
        holder.qty.setText("QTY : " + orderBean.getCartVal().split("\\.")[0]);
        holder.name.setText(orderBean.getName());
        Glide.with(mainActivityUser)
                .load(Appconfig.getResizedImage(orderBean.getImage().get(0), false))
                .into(holder.product_image);

    }

    public int getItemCount() {
        return modelArrayList.size();
    }

}


