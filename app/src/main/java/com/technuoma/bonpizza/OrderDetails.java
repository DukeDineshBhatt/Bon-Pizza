package com.technuoma.bonpizza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.technuoma.bonpizza.orderDetailsPOJO.Datum;
import com.technuoma.bonpizza.orderDetailsPOJO.orderDetailsBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class OrderDetails extends AppCompatActivity {


    Toolbar toolbar;
    RecyclerView grid;
    ProgressBar progress;
    List<Datum> list;
    CategoryAdapter adapter;
    String oid, status;
    FloatingActionButton track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        oid = getIntent().getStringExtra("oid");
        status = getIntent().getStringExtra("status");

        list = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar2);
        grid = findViewById(R.id.grid);
        progress = findViewById(R.id.progressBar2);
        track = findViewById(R.id.floatingActionButton);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Order Details");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        if (status.equals("out for delivery")) {
            track.setVisibility(View.VISIBLE);
        } else {
            track.setVisibility(View.GONE);
        }

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OrderDetails.this, MapsActivity.class);
                intent.putExtra("order", oid);
                startActivity(intent);

            }
        });

        adapter = new CategoryAdapter(this, list);
        GridLayoutManager manager = new GridLayoutManager(this, 1);

        grid.setAdapter(adapter);
        grid.setLayoutManager(manager);

    }

    @Override
    protected void onResume() {
        super.onResume();

        progress.setVisibility(View.VISIBLE);

        Bean b = (Bean) getApplicationContext();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.HEADERS);
        logging.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().writeTimeout(1000, TimeUnit.SECONDS).readTimeout(1000, TimeUnit.SECONDS).connectTimeout(1000, TimeUnit.SECONDS).addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(b.baseurl)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

        Call<orderDetailsBean> call = cr.getOrderDetails(oid);
        call.enqueue(new Callback<orderDetailsBean>() {
            @Override
            public void onResponse(Call<orderDetailsBean> call, Response<orderDetailsBean> response) {


                if (response.body().getStatus().equals("1")) {
                    adapter.setData(response.body().getData());
                }

                progress.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<orderDetailsBean> call, Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });

    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

        Context context;
        List<Datum> list = new ArrayList<>();

        public CategoryAdapter(Context context, List<Datum> list) {
            this.context = context;
            this.list = list;
        }

        public void setData(List<Datum> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.category_list_model, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            final Datum item = list.get(position);


            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(false).build();
            ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(item.getImage(), holder.image, options);

            holder.quantity.setText("Quantity - " + item.getQuantity());
            holder.title.setText(item.getName());
            holder.price.setText("₹ " + item.getPrice());
            String[] addons = item.getAddon().split(", ");

            StringBuilder ao = new StringBuilder();

            for (int i = 0; i < addons.length; i++) {
                ao.append("\n").append(addons[i]);
            }

            holder.addon.setText("Add on: " + ao);


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView image;
            TextView quantity, title, price, addon;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.imageView5);
                title = itemView.findViewById(R.id.textView17);
                quantity = itemView.findViewById(R.id.textView18);
                price = itemView.findViewById(R.id.textView19);
                addon = itemView.findViewById(R.id.textView7);


            }
        }
    }

}