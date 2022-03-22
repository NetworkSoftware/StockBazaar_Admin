package customer.smart.support.client.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import java.util.ArrayList;

import customer.smart.support.R;
import customer.smart.support.client.exchange.ExchangeClick;


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<Service> histories;
    private final ServiceClick serviceClick;
    public ServiceAdapter(Context context, ArrayList<Service> histories,ServiceClick serviceClick) {
        this.histories = histories;
        this.context = context;
        this.serviceClick = serviceClick;
    }

    public void notifyData(ArrayList<Service> myList) {
        this.histories = myList;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Service bean = histories.get(position);

        holder.id.setText("#" + bean.getId());
        holder.category.setText(bean.getCategory());
        holder.brand.setText(bean.getBrand());
        holder.model.setText(bean.getModel());
        holder.mobile.setText(bean.getMobile());
        holder.problem.setText(bean.getProblem());
        holder.district.setText(bean.getDistrict());
        holder.pincode.setText(bean.getPincode());
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceClick.onCallClick(bean.mobile);
            }
        });
        holder.arrow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.hidden_view.getVisibility() == View.VISIBLE) {

                    TransitionManager.beginDelayedTransition(holder.cardView,
                            new AutoTransition());
                    holder.hidden_view.setVisibility(View.GONE);
                    holder.arrow_button.setImageResource(R.drawable.ic_baseline_expand_more_24);
                } else {

                    TransitionManager.beginDelayedTransition(holder.cardView,
                            new AutoTransition());
                    holder.hidden_view.setVisibility(View.VISIBLE);
                    holder.arrow_button.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView category, brand, model, mobile, problem, district, pincode, id;
        ImageView arrow_button,call;
        LinearLayout hidden_view, linearActive;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            arrow_button = view.findViewById(R.id.arrow_button);
            hidden_view = view.findViewById(R.id.hidden_view);
            linearActive = view.findViewById(R.id.linearActive);
            cardView = view.findViewById(R.id.base_cardview);
            category = view.findViewById(R.id.category);
            brand = view.findViewById(R.id.brand);
            model = view.findViewById(R.id.model);
            mobile = view.findViewById(R.id.mobile);
            problem = view.findViewById(R.id.problem);
            district = view.findViewById(R.id.district);
            pincode = view.findViewById(R.id.pincode);
            call = view.findViewById(R.id.call);
            id = view.findViewById(R.id.id);


        }
    }
}
