package customer.smart.support.client.price;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import customer.smart.support.R;
import customer.smart.support.client.stock.ImageClick;

public class PercentagePriceAdapter extends RecyclerView.Adapter<PercentagePriceAdapter.MyViewHolder> {

    ImageClick imageClick;
    private final Context mainActivityUser;
    private ArrayList<PercentagePriceBeen> percentagePriceBeens;
    boolean isEdit;
    int selectedPosition = 0;

    public PercentagePriceAdapter(Context mainActivityUser, ArrayList<PercentagePriceBeen> priceBeens, ImageClick imageClick, boolean isEdit) {
        this.mainActivityUser = mainActivityUser;
        this.percentagePriceBeens = priceBeens;
        this.imageClick = imageClick;
        this.isEdit = isEdit;
    }

    public void notifyData(ArrayList<PercentagePriceBeen> myList) {
        Log.d("notifyData ", myList.size() + "");
        this.percentagePriceBeens = myList;
        notifyDataSetChanged();
    }

    public void notifyData(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bulk_price_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        PercentagePriceBeen price = percentagePriceBeens.get(position);
        holder.priceRange.setText(price.getPriceRange());
        holder.pricePercentage.setText(price.getPrice_percentage());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageClick.onDeleteClick(position);
            }
        });
        if (isEdit) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }

        if (!isEdit && selectedPosition == position) {
            holder.sizeLinear.setBackgroundResource(R.drawable.rectangle_box_select);
            holder.priceRange.setTextColor(Color.WHITE);
            holder.pricePercentage.setTextColor(Color.WHITE);
        } else {
            holder.sizeLinear.setBackgroundResource(R.drawable.rectangle_box);
            holder.priceRange.setTextColor(mainActivityUser.getResources().getColor(R.color.colorPrimary));
            holder.pricePercentage.setTextColor(mainActivityUser.getResources().getColor(R.color.colorPrimary));
        }

        holder.sizeLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageClick.onImageClick(position);
            }
        });

    }

    public int getItemCount() {
        return percentagePriceBeens.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView priceRange, pricePercentage;
        LinearLayout delete, sizeLinear;

        public MyViewHolder(View view) {
            super((view));
            priceRange = view.findViewById(R.id.titleOne);
            pricePercentage = view.findViewById(R.id.titleTwo);
            delete = view.findViewById(R.id.delete);
            sizeLinear = view.findViewById(R.id.sizeLinear);

        }
    }

}


