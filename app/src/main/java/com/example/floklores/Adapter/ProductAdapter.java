package com.example.floklores.Adapter;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Product;
import com.example.floklores.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Product> products;
    private OnTaskItemClickListener listener;
    private URL url =null;
    private static final String IMAGE_URL_PREFIX = "https://folkloreb59a9082957b40879d7a28ed9441caea161824-dev.s3.amazonaws.com/public/";



    public ProductAdapter(List<Product> products, OnTaskItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public interface OnTaskItemClickListener {
        void onItemClicked(int position);
        void onDeleteItem(int position);
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
        holder.productPrice.setText(item.getProductPrice() +" JOD");

        // TODO: 2021-09-08 Use picaso to fetch and load the image in to the image view
        Picasso.get().load(IMAGE_URL_PREFIX+item.getFileName()).into(holder.ProductImage);
//        Picasso.get().load(mData.get(position).getImage()).into(holder.imgNutrient);

//        holder.ProductImage.setImageBitmap(BitmapFactory.decodeFile(item.getFileName()));
//        holder.taskState.setText(task.getTaskState());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ProductImage;
        private TextView productName;
        private TextView productPrice;
        private ImageView delete;

        ViewHolder(@NonNull View itemView, OnTaskItemClickListener listener) {
            super(itemView);

            ProductImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.textView);
//            delete = itemView.findViewById(R.id.delete);

            // Go to task details
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(getAdapterPosition());
                }
            });

            // delete task
//            delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onDeleteItem(getAdapterPosition());
//                }
//            });
        }

    }
}
