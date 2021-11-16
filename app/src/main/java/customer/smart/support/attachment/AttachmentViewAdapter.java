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


public class AttachmentViewAdapter extends RecyclerView.Adapter<AttachmentViewAdapter.MyViewHolder> {

    private Context mainActivityUser;
    private ArrayList<String> moviesList;
    private ViewClick attachmentClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageview;

        public MyViewHolder(View view) {
            super(view);
            imageview = (ImageView) view.findViewById(R.id.imageview);
        }
    }


    public AttachmentViewAdapter(Context mainActivityUser, ArrayList<String> moviesList, ViewClick attachmentClick) {
        this.moviesList = moviesList;
        this.mainActivityUser = mainActivityUser;
        this.attachmentClick = attachmentClick;

    }

    public void notifyData(ArrayList<String> myList) {
        this.moviesList = myList;
        notifyDataSetChanged();
    }

    public void notifyDataItem(ArrayList<String> myList, int position) {
        this.moviesList = myList;
        notifyItemChanged(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_attachment_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final String bean = moviesList.get(position);
        GlideApp.with(mainActivityUser).load(bean)
                .placeholder(R.drawable.default_error)
                .into(holder.imageview);
        holder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentClick.onViewClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
