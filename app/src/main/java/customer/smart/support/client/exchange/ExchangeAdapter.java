package customer.smart.support.client.exchange;

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
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.GlideApp;


public class ExchangeAdapter extends RecyclerView.Adapter<ExchangeAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<Exchange> exchanges;
    private final ExchangeClick exchangeClick;

    public ExchangeAdapter(Context context, ArrayList<Exchange> exchanges, ExchangeClick exchangeClick) {
        this.exchanges = exchanges;
        this.context = context;
        this.exchangeClick = exchangeClick;
    }

    public void notifyData(ArrayList<Exchange> myList) {
        this.exchanges = myList;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exchange_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Exchange bean = exchanges.get(position);

        holder.id.setText("#" + bean.getId());
        holder.price.setText(bean.getPrice());
        holder.brand.setText(bean.getBrand());
        holder.model.setText(bean.getModel());
        holder.ram.setText("Ram:" + bean.getRam());
        holder.rom.setText("Rom:" + bean.getRom());
        holder.warranty.setText("Warranty:" + bean.getWarranty());
        holder.box.setText("Box:" + bean.getBox());
        holder.accessories.setText("Accessories:" + bean.getAccessories());
        holder.mobileCondition.setText("Mobile Conditions:" + bean.getMobileCondition());
        holder.hardwareProblem.setText("Hardware:" + bean.getHardwareProblem());
        holder.softwareChanged.setText("Software:" + bean.getSoftwareChanged());
        holder.whatsapp.setText(bean.getWhatsapp());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeClick.onImageClick(bean);
            }
        });
        if((bean.whatsapp!=null)&&
                (!bean.whatsapp.equalsIgnoreCase("null")&&
                        (!bean.whatsapp.equalsIgnoreCase("NA")))){
            holder.callLi.setVisibility(View.VISIBLE);
        }else {
            holder.callLi.setVisibility(View.GONE);
        }
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeClick.onCallClick(bean.whatsapp);
            }
        });
        GlideApp.with(context)
                .load(Appconfig.getResizedImage(bean.getImage(), false))
                .placeholder(R.drawable.profile)
                .into(holder.image);
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
        return exchanges.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView brand, warranty, price, model, ram, rom, box, accessories,
                mobileCondition, hardwareProblem, softwareChanged, id,whatsapp;
        ImageView arrow_button, image,call;
        LinearLayout hidden_view, linearActive,callLi;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            arrow_button = view.findViewById(R.id.arrow_button);
            hidden_view = view.findViewById(R.id.hidden_view);
            linearActive = view.findViewById(R.id.linearActive);
            cardView = view.findViewById(R.id.base_cardview);
            brand = view.findViewById(R.id.brand);
            model = view.findViewById(R.id.model);
            id = view.findViewById(R.id.id);
            price = view.findViewById(R.id.price);
            image = view.findViewById(R.id.image);
            whatsapp = view.findViewById(R.id.whatsapp);

            callLi = view.findViewById(R.id.callLi);
            warranty = view.findViewById(R.id.warranty);
            ram = view.findViewById(R.id.ram);
            rom = view.findViewById(R.id.rom);
            box = view.findViewById(R.id.box);
            accessories = view.findViewById(R.id.accessories);
            mobileCondition = view.findViewById(R.id.mobileCondition);
            hardwareProblem = view.findViewById(R.id.hardwareProblem);
            softwareChanged = view.findViewById(R.id.softwareChanged);
            call = view.findViewById(R.id.call);
        }
    }
}
