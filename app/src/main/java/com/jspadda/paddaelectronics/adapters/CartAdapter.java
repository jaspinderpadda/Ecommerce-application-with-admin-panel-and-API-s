package com.jspadda.paddaelectronics.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.jspadda.paddaelectronics.R;
import com.jspadda.paddaelectronics.databinding.ItemCartBinding;
import com.jspadda.paddaelectronics.databinding.QuantityDialogBinding;
import com.jspadda.paddaelectronics.model.Product;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    Context context;
    ArrayList<Product> products;
    CartListener cartListener;
    Cart cart;

    public interface CartListener {
        public void onQuantityChanged();
    }

    public CartAdapter(Context context, ArrayList<Product> products, CartListener cartListener) {
        this.context = context;
        this.products = products;
        this.cartListener = cartListener;
        cart = TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.imageView2);

        holder.binding.name.setText(product.getName());
        holder.binding.price.setText("INR "+product.getPrice());
        holder.binding.quantity.setText(product.getQuantity()+ " item(s)");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuantityDialogBinding dialogBinding = QuantityDialogBinding.inflate(LayoutInflater.from(context));

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(dialogBinding.getRoot())
                        .create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

                dialogBinding.productName.setText(product.getName());
                dialogBinding.productStock.setText("Stock: "+product.getStock());
                dialogBinding.quantity.setText(String.valueOf(product.getQuantity()));

                dialogBinding.plusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int quantity = product.getQuantity();
                        quantity++;

                        if (quantity>product.getStock()){
                            Toast.makeText(context,"Max stock available: "+product.getStock(), Toast.LENGTH_LONG).show();
                            return;
                        }else {
                            product.setQuantity(quantity);
                            dialogBinding.quantity.setText(String.valueOf(quantity));
                        }

                        notifyDataSetChanged();
                        cart.updateItem(product, product.getQuantity());
                        cartListener.onQuantityChanged();
                    }
                });

                dialogBinding.minusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = product.getQuantity();
                        if (quantity > 1)
                            quantity--;
                        product.setQuantity(quantity);
                        dialogBinding.quantity.setText(String.valueOf(quantity));

                        notifyDataSetChanged();
                        cart.updateItem(product, product.getQuantity());
                        cartListener.onQuantityChanged();
                    }

                });

                dialogBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        notifyDataSetChanged();
                        cart.updateItem(product, product.getQuantity());
                        cartListener.onQuantityChanged();

                    }
                });


                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ItemCartBinding binding;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);
        }
    }
}
