package com.jspadda.paddaelectronics.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;
import com.jspadda.paddaelectronics.R;
import com.jspadda.paddaelectronics.adapters.CartAdapter;
import com.jspadda.paddaelectronics.databinding.ActivityCartBinding;
import com.jspadda.paddaelectronics.model.Product;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    CartAdapter adapter;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        products = new ArrayList<>();

        Cart cart = TinyCartHelper.getCart();

        for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()){

            Product product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);

            products.add(product);
        }

        adapter = new CartAdapter(this, products, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subtotal.setText(String.format("INR %.2f", cart.getTotalPrice()));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.setAdapter(adapter);

        binding.subtotal.setText(String.format("INR %.2f", cart.getTotalPrice()));

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}