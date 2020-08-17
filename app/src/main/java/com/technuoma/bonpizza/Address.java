package com.technuoma.bonpizza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.technuoma.bonpizza.addressPOJO.Datum;
import com.technuoma.bonpizza.addressPOJO.addressBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Address extends AppCompatActivity {

    private Toolbar toolbar;
    ProgressBar progress;
    String base;
    OrdersAdapter adapter;
    List<Datum> list;

    //CartAdapter adapter;

    GridLayoutManager manager;

    RecyclerView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        list = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar3);
        progress = findViewById(R.id.progressBar3);
        grid = findViewById(R.id.grid);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("My Addresses");


        adapter = new OrdersAdapter(list, this);

        manager = new GridLayoutManager(this, 1);

        grid.setAdapter(adapter);
        grid.setLayoutManager(manager);


    }

    @Override
    protected void onResume() {
        super.onResume();

        loadCart();

    }

    void loadCart() {
        progress.setVisibility(View.VISIBLE);

        Bean b = (Bean) getApplicationContext();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(b.baseurl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AllApiIneterface cr = retrofit.create(AllApiIneterface.class);


        Call<addressBean> call = cr.getAddress(SharePreferenceUtils.getInstance().getString("userId"));
        call.enqueue(new Callback<addressBean>() {
            @Override
            public void onResponse(Call<addressBean> call, Response<addressBean> response) {

                if (response.body().getData().size() > 0) {


                    adapter.setgrid(response.body().getData());

                } else {
                    adapter.setgrid(response.body().getData());
                    Toast.makeText(Address.this, "No address found", Toast.LENGTH_SHORT).show();
                    finish();
                }

                progress.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<addressBean> call, Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });

    }

    class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

        List<Datum> list = new ArrayList<>();
        Context context;
        LayoutInflater inflater;

        OrdersAdapter(List<Datum> list, Context context) {
            this.context = context;
            this.list = list;
        }

        void setgrid(List<Datum> list) {

            this.list = list;
            notifyDataSetChanged();

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.order_list_item2, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i1) {

            final Datum item = list.get(i1);

            //viewHolder.setIsRecyclable(false);


            viewHolder.txn.setText(item.getName());


            viewHolder.name.setText(item.getArea());

            viewHolder.pay.setText(item.getCity());

            viewHolder.amount.setText(item.getHouse());

            viewHolder.deldate.setText(item.getPin());


            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progress.setVisibility(View.VISIBLE);

                    Bean b = (Bean) getApplicationContext();


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(b.baseurl)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    AllApiIneterface cr = retrofit.create(AllApiIneterface.class);


                    Call<addressBean> call = cr.deleteAddress(item.getId());
                    call.enqueue(new Callback<addressBean>() {
                        @Override
                        public void onResponse(Call<addressBean> call, Response<addressBean> response) {

                            Toast.makeText(Address.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            onResume();

                            progress.setVisibility(View.GONE);

                        }

                        @Override
                        public void onFailure(Call<addressBean> call, Throwable t) {
                            progress.setVisibility(View.GONE);
                        }
                    });

                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txn, name, amount, pay, deldate;

            ImageButton delete;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                txn = itemView.findViewById(R.id.textView27);


                name = itemView.findViewById(R.id.textView32);

                amount = itemView.findViewById(R.id.textView30);
                pay = itemView.findViewById(R.id.textView40);

                deldate = itemView.findViewById(R.id.textView42);
                delete = itemView.findViewById(R.id.imageView7);


            }
        }
    }
}