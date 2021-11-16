package customer.smart.support.offer;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import customer.smart.support.R;
import customer.smart.support.app.GlideApp;


public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyViewHolder> {

    private List<Offer> moviesList;
    private Context context;
    private OnOfferClick onShopClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        private ImageView editImg, deleteImg;

        public MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            editImg = (ImageView) view.findViewById(R.id.editImg);
            deleteImg = (ImageView) view.findViewById(R.id.deleteImg);


        }
    }


    public OfferAdapter(List<Offer> moviesList, Context context, OnOfferClick onShopClick) {


        this.moviesList = moviesList;
        this.context = context;
        this.onShopClick = onShopClick;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Offer shop = moviesList.get(position);
       GlideApp.with(context)
                .load(shop.getImage())
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .placeholder(R.drawable.profile)
                .into(holder.image);

        holder.editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShopClick.onEditClick(position);
            }
        });

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShopClick.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void notifyData(List<Offer> moviesList) {
        this.moviesList = moviesList;
        notifyDataSetChanged();
    }


}
