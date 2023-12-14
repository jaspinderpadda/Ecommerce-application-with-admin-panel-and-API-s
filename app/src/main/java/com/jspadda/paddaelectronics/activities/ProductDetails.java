package com.jspadda.paddaelectronics.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.jspadda.paddaelectronics.R;
import com.jspadda.paddaelectronics.databinding.ActivityProductDetailsBinding;
import com.jspadda.paddaelectronics.model.Product;
import com.jspadda.paddaelectronics.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;


public class ProductDetails extends AppCompatActivity {

    ActivityProductDetailsBinding binding;
    Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

         String name = getIntent().getStringExtra("name");
         String image = getIntent().getStringExtra("image");
         int id = getIntent().getIntExtra("id", 0);
         double price = getIntent().getDoubleExtra("price", 0);

        Glide.with(this)
                .load(image).into(binding.productImage);
        getProductDetails(id);

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Cart cart = TinyCartHelper.getCart();

        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart.addItem(currentProduct, 1);
                binding.addToCartBtn.setEnabled(false);
                binding.addToCartBtn.setText("Added to cart");
            }
        });
    }

    void getProductDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.GET_PRODUCT_DETAILS_URL + id;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("success")){
                        JSONObject product = object.getJSONObject("product");
                        String description = product.getString("description");
                        binding.productDescription.setText(
                                Html.fromHtml(description)
                        );

                        currentProduct = new Product(
                                product.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL + product.getString("image"),
                                product.getString("status"),
                                product.getDouble("price"),
                                product.getDouble("price_discount"),
                                product.getInt("stock"),
                                product.getInt("id")
                        );
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cart){
         startActivity(new Intent(this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}