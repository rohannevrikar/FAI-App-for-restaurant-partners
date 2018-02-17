package tastifai.restaurant;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rohannevrikar.restaurant.R;

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 03-02-2018.
 */

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {
    Context context;
    ArrayList<Item> itemArrayList;
    private final LayoutInflater layoutInflater;


    public CustomListAdapter(Context context, ArrayList<Item> itemArrayList) {
        this.context = context;
        this.itemArrayList = itemArrayList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View detailsView = layoutInflater.inflate(R.layout.order_row_item, parent, false);
        return new ViewHolder(detailsView, context, itemArrayList);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item.setText(itemArrayList.get(position).getItem());
        holder.qty.setText("x " + itemArrayList.get(position).getQty());
        //holder.x.setText("x");
        holder.price.setText(itemArrayList.get(position).getPrice());
    }





    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public final TextView item;
        public final TextView qty;
        public final TextView price;
       // public final TextView x;

        public final Context mContext;
        public final ArrayList<Item> itemList;


        public ViewHolder(View detailsView, Context context, ArrayList<Item> itemList) {
            super(detailsView);
            mContext = context;
            this.itemList = itemList;
            item = detailsView.findViewById(R.id.txtItem);
            qty = detailsView.findViewById(R.id.txtQty);
            price = detailsView.findViewById(R.id.txtPrice);
            //x = detailsView.findViewById(R.id.txtX);

        }
    }
}
