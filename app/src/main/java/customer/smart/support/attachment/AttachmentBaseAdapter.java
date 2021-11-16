package customer.smart.support.attachment;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import customer.smart.support.R;
import customer.smart.support.app.GlideApp;


public class AttachmentBaseAdapter extends RecyclerView.Adapter<AttachmentBaseAdapter.MyViewHolder> {

    private Context mainActivityUser;
    private ArrayList<Base> moviesList;
    private BaseClick attachmentClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView cancelImg, image;

        public MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            cancelImg = (ImageView) view.findViewById(R.id.cancelImg);

        }
    }


    public AttachmentBaseAdapter(Context mainActivityUser, ArrayList<Base> moviesList, BaseClick attachmentClick) {
        this.moviesList = moviesList;
        this.mainActivityUser = mainActivityUser;
        this.attachmentClick = attachmentClick;

    }

    public void notifyData(ArrayList<Base> myList) {
        this.moviesList = myList;
        notifyDataSetChanged();
    }

    public void notifyDataItem(ArrayList<Base> myList, int position) {
        this.moviesList = myList;
        notifyItemChanged(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_attachment_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Base bean = moviesList.get(position);

        if (position == 0) {
            GlideApp.with(mainActivityUser).load(R.drawable.ic_add_a_photo_black_24dp)
                    .dontAnimate()
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.ic_add_a_photo_black_24dp)
                    .into(holder.image);
            holder.cancelImg.setVisibility(View.GONE);
        } else {
            holder.cancelImg.setVisibility(View.VISIBLE);
            GlideApp.with(mainActivityUser).load(bean.url)
                    .placeholder(R.drawable.default_error)
                    .into(holder.image);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentClick.onBaseClick(position);
            }
        });
        holder.cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentClick.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
