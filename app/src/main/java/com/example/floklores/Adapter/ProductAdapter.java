package com.example.floklores.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Product;
import com.example.floklores.Models.ProductItem;
import com.example.floklores.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Product> products;
    private OnTaskItemClickListener listener;


    public interface OnTaskItemClickListener {
        void onItemClicked(int position);
        void onDeleteItem(int position);
    }

    public ProductAdapter(List<Product> products, OnTaskItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = products.get(position);
        holder.productName.setText(item.getProductTitle());
//        holder.taskState.setText(task.getTaskState());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ProductImage;
        private TextView productName;
//        private TextView taskState;
        private ImageView delete;

        ViewHolder(@NonNull View itemView, OnTaskItemClickListener listener) {
            super(itemView);

            ProductImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
//            taskState = itemView.findViewById(R.id.task_state);
            delete = itemView.findViewById(R.id.delete);

            // Go to task details
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(getAdapterPosition());
                }
            });

            // delete task
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteItem(getAdapterPosition());
                }
            });
        }
    }
}
