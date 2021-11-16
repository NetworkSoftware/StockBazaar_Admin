package customer.smart.support.client.bulkPrice;

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

public class BulkPriceAdapter extends RecyclerView.Adapter<BulkPriceAdapter.MyViewHolder> {

    ImageClick imageClick;
    private final Context mainActivityUser;
    private ArrayList<BulkPriceBeen> bulkPriceBeens;
    boolean isEdit;
    int selectedPosition = 0;

    public BulkPriceAdapter(Context mainActivityUser, ArrayList<BulkPriceBeen> priceBeens, ImageClick imageClick, boolean isEdit) {
        this.mainActivityUser = mainActivityUser;
        this.bulkPriceBeens = priceBeens;
        this.imageClick = imageClick;
        this.isEdit = isEdit;
    }

    public void notifyData(ArrayList<BulkPriceBeen> myList) {
        Log.d("notifyData ", myList.size() + "");
        this.bulkPriceBeens = myList;
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
        BulkPriceBeen price = bulkPriceBeens.get(position);
        holder.quantity.setText(price.getQuantity());
        holder.price.setText(price.getQty_price());

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
            holder.quantity.setTextColor(Color.WHITE);
            holder.price.setTextColor(Color.WHITE);
        } else {
            holder.sizeLinear.setBackgroundResource(R.drawable.rectangle_box);
            holder.quantity.setTextColor(mainActivityUser.getResources().getColor(R.color.colorPrimary));
            holder.price.setTextColor(mainActivityUser.getResources().getColor(R.color.colorPrimary));
        }

        holder.sizeLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageClick.onImageClick(position);
            }
        });

    }

    public int getItemCount() {
        return bulkPriceBeens.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView quantity,price;
        LinearLayout delete, sizeLinear;

        public MyViewHolder(View view) {
            super((view));
            quantity = view.findViewById(R.id.titleOne);
            price = view.findViewById(R.id.titleTwo);
            delete = view.findViewById(R.id.delete);
            sizeLinear = view.findViewById(R.id.sizeLinear);

        }
    }

}


