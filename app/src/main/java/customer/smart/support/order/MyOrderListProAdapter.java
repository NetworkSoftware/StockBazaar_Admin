package customer.smart.support.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import customer.smart.support.R;

import static customer.smart.support.app.Appconfig.decimalFormat;


public class MyOrderListProAdapter extends RecyclerView.Adapter<MyOrderListProAdapter.MyViewHolder> {

    private final Context mainActivityUser;
    int selectedPosition = 0;
    private ArrayList<CartItems> myorderBeans;

    public MyOrderListProAdapter(Context mainActivityUser, ArrayList<CartItems> myorderBeans) {
        this.mainActivityUser = mainActivityUser;
        this.myorderBeans = myorderBeans;
    }

    public void notifyData(ArrayList<CartItems> myorderBeans) {
        this.myorderBeans = myorderBeans;
        notifyDataSetChanged();
    }

    public void notifyData(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_payment_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CartItems myorderBean = myorderBeans.get(position);

        holder.title.setText(myorderBean.getName() + "(" + myorderBean.getId() + ")"
                +"(" + myorderBean.getShopName() + ")");
        String qty = myorderBean.getQuantity();

        try {
            if (qty == null || !qty.matches("-?\\d+(\\.\\d+)?")) {
                qty = "1";
            }
        } catch (Exception e) {

        }
        float startValue = Float.parseFloat(myorderBean.getPrice()) * Integer.parseInt(qty);
        holder.subtitle.setText(myorderBean.getQuantity() + "*" + myorderBean.getPrice() +
                "=" + "₹" + decimalFormat.format(startValue) + ".00");

    }

    public int getItemCount() {
        return myorderBeans.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView subtitle;


        public MyViewHolder(View view) {
            super((view));
            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.subtitle);

        }
    }

}


