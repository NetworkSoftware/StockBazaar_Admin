package customer.smart.support.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import customer.smart.support.R;
import customer.smart.support.app.Appconfig;


/**
 * Created by ravi on 16/11/17.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder>
        implements Filterable {
    StatusListener statusListener;
    private final Context context;
    private List<Order> orderList;
    private List<Order> orderListFiltered;
    private final ContactsAdapterListener listener;

    public OrderAdapter(Context context, List<Order> orderList, ContactsAdapterListener listener, StatusListener statusListener) {
        this.context = context;
        this.listener = listener;
        this.orderList = orderList;
        this.orderListFiltered = orderList;
        this.statusListener = statusListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Order order = orderListFiltered.get(position);
        holder.price.setText(order.getAmount());
        holder.quantity.setText(order.getId());
        holder.status.setText(order.getStatus());
        holder.phone.setText(order.getPhone());
        holder.name.setText(order.getName());
        holder.orderedOn.setText(Appconfig.convertTimeToLocal(order.createdOn));
        if (order.getStatus().equalsIgnoreCase("ordered")) {
            holder.deliveredBtn.setVisibility(View.VISIBLE);
        } else {
            holder.deliveredBtn.setVisibility(View.GONE);
        }

        if (!order.getStatus().equalsIgnoreCase("canceled")
                && order.getStatus().equalsIgnoreCase("ordered")) {
            holder.cancalOrder.setVisibility(View.VISIBLE);
        } else {
            holder.cancalOrder.setVisibility(View.GONE);
        }

        holder.deliveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onDeliveredClick(order.id);
            }
        });
        holder.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onWhatsAppClick(order.phone);
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onCallClick(order.phone);
            }
        });
        holder.cancalOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onCancelClick(order.id);
            }
        });

        holder.hintLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusListener.onItemClick(order);
            }
        });
        holder.valueLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusListener.onItemClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    orderListFiltered = orderList;
                } else {
                    List<Order> filteredList = new ArrayList<>();
                    for (Order row : orderList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val = row.getName();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        } else if (row.getPhone().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }else if (row.getId().contains(charString.toLowerCase())) {
                            filteredList.add(row);

                        }
                    }

                    orderListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = orderListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                orderListFiltered = (ArrayList<Order>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyData(List<Order> orderList) {
        this.orderListFiltered = orderList;
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Order order);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price, status, quantity, phone, orderedOn;
        ImageView deliveredBtn, whatsapp, call, cancalOrder;
        LinearLayout valueLinear, hintLinear;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            orderedOn = view.findViewById(R.id.orderedOn);

            price = view.findViewById(R.id.price);
            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            status = view.findViewById(R.id.status);
            quantity = view.findViewById(R.id.quantity);
            deliveredBtn = view.findViewById(R.id.deliveredBtn);
            whatsapp = view.findViewById(R.id.whatsapp);
            cancalOrder = view.findViewById(R.id.cancalOrder);
            call = view.findViewById(R.id.call);
            valueLinear = view.findViewById(R.id.valueLinear);
            hintLinear = view.findViewById(R.id.hintLinear);
        }
    }
}
