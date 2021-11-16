package customer.smart.support.shop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import customer.smart.support.R;

public class FilteredAdapater extends RecyclerView.Adapter<FilteredAdapater.MyViewHolder> {

    private Context mainActivityUser;
    private ArrayList<String> regMainbeans;
    private OnFilterClose onItemClick;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final Chip chipname;

        public MyViewHolder(View view) {
            super((view));
            chipname = view.findViewById(R.id.chipname);

        }
    }

    public FilteredAdapater(Context mainActivityUser, ArrayList<String> regMainbeans,
                            OnFilterClose onItemClick) {
        this.mainActivityUser = mainActivityUser;
        this.regMainbeans=regMainbeans;
        this.onItemClick = onItemClick;
    }

    public void notifyData(ArrayList<String> myList) {
        Log.d("notifyData ", myList.size() + "");
        this.regMainbeans = myList;
        notifyDataSetChanged();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filtered_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String bean = regMainbeans.get(position);
        holder.chipname.setText(bean);

        holder.chipname.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClosed(position);
            }
        });
    }

    public int getItemCount() {
        return regMainbeans.size();
    }

}
