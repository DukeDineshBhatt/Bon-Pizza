package com.technuoma.bonpizza;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.technuoma.bonpizza.cartPOJO.Datum;
import com.technuoma.bonpizza.cartPOJO.cartBean;
import com.technuoma.bonpizza.seingleProductPOJO.singleProductBean;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Cart extends Fragment {

    ProgressBar bar;
    String base;
    TextView btotal, bproceed, clear;

    float amm = 0;

    View bottom;

    CartAdapter adapter;

    GridLayoutManager manager;

    RecyclerView grid;

    List<Datum> list;

    String client, txn;
    MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart , container , false);
        mainActivity = (MainActivity)getActivity();
        list = new ArrayList<>();

        bar = view.findViewById(R.id.progressBar3);
        bottom = view.findViewById(R.id.cart_bottom);
        btotal = view.findViewById(R.id.textView9);
        bproceed = view.findViewById(R.id.textView10);
        grid = view.findViewById(R.id.grid);
        clear = view.findViewById(R.id.textView12);


        adapter = new CartAdapter(list, mainActivity);

        manager = new GridLayoutManager(mainActivity, 1);

        grid.setAdapter(adapter);
        grid.setLayoutManager(manager);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bar.setVisibility(View.VISIBLE);

                Bean b = (Bean) mainActivity.getApplicationContext();

                base = b.baseurl;

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(b.baseurl)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

                Call<singleProductBean> call = cr.clearCart(SharePreferenceUtils.getInstance().getString("userId"));

                call.enqueue(new Callback<singleProductBean>() {
                    @Override
                    public void onResponse(Call<singleProductBean> call, Response<singleProductBean> response) {

                        if (response.body().getStatus().equals("1")) {
                            mainActivity.navigation.setSelectedItemId(R.id.action_home);
                            mainActivity.loadCart();
                        }

                        Toast.makeText(mainActivity, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        bar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onFailure(Call<singleProductBean> call, Throwable t) {
                        bar.setVisibility(View.GONE);
                    }
                });


            }
        });

        bproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (amm > 0)
                {
                    Intent intent = new Intent(mainActivity , Checkout.class);
                    intent.putExtra("amount" , String.valueOf(amm));
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(mainActivity, "Invalid amount", Toast.LENGTH_SHORT).show();
                }



            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        loadCart();

    }

    void loadCart() {
        bar.setVisibility(View.VISIBLE);

        Bean b = (Bean) mainActivity.getApplicationContext();

        base = b.baseurl;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(b.baseurl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

        Log.d("userid", SharePreferenceUtils.getInstance().getString("userId"));


        Call<cartBean> call = cr.getCart(SharePreferenceUtils.getInstance().getString("userId"));
        call.enqueue(new Callback<cartBean>() {
            @Override
            public void onResponse(Call<cartBean> call, Response<cartBean> response) {

                if (response.body().getData().size() > 0) {


                    adapter.setgrid(response.body().getData());

                    amm = Float.parseFloat(response.body().getTotal());


                    btotal.setText("Total: \u20B9 " + response.body().getTotal());

                    bottom.setVisibility(View.VISIBLE);
                } else {
                    adapter.setgrid(response.body().getData());
                    bottom.setVisibility(View.GONE);
                    Toast.makeText(mainActivity, "Cart is empty", Toast.LENGTH_SHORT).show();
                    mainActivity.navigation.setSelectedItemId(R.id.action_home);
                }

                bar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<cartBean> call, Throwable t) {
                bar.setVisibility(View.GONE);
            }
        });

    }

    class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

        List<Datum> list = new ArrayList<>();
        Context context;
        LayoutInflater inflater;

        CartAdapter(List<Datum> list, Context context) {
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
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.prod_list_model4, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i1) {

            final Datum item = list.get(i1);

            //viewHolder.setIsRecyclable(false);


            viewHolder.title.setText(item.getName());
            viewHolder.brand.setText(item.getBrand());


            viewHolder.quantity.setText(item.getQuantity());

            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int q = Integer.parseInt(item.getQuantity());

                    if (q < 99) {

                        q++;

                        viewHolder.quantity.setText(String.valueOf(q));

                        bar.setVisibility(View.VISIBLE);

                        Bean b = (Bean) context.getApplicationContext();

                        base = b.baseurl;

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(b.baseurl)
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

                        Call<singleProductBean> call = cr.updateCart(item.getPid(), String.valueOf(q), item.getPrice());

                        call.enqueue(new Callback<singleProductBean>() {
                            @Override
                            public void onResponse(Call<singleProductBean> call, Response<singleProductBean> response) {

                                if (response.body().getStatus().equals("1")) {
                                    loadCart();
                                    mainActivity.loadCart();
                                }

                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                bar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onFailure(Call<singleProductBean> call, Throwable t) {
                                bar.setVisibility(View.GONE);
                            }
                        });

                    }


                }
            });


            viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int q = Integer.parseInt(item.getQuantity());

                    if (q > 1) {

                        q--;

                        viewHolder.quantity.setText(String.valueOf(q));

                        bar.setVisibility(View.VISIBLE);

                        Bean b = (Bean) context.getApplicationContext();

                        base = b.baseurl;

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(b.baseurl)
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

                        Call<singleProductBean> call = cr.updateCart(item.getPid(), String.valueOf(q), item.getPrice());

                        call.enqueue(new Callback<singleProductBean>() {
                            @Override
                            public void onResponse(Call<singleProductBean> call, Response<singleProductBean> response) {

                                if (response.body().getStatus().equals("1")) {
                                    loadCart();
                                    mainActivity.loadCart();
                                }

                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                bar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onFailure(Call<singleProductBean> call, Throwable t) {
                                bar.setVisibility(View.GONE);
                            }
                        });

                    }


                }
            });






            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    bar.setVisibility(View.VISIBLE);

                    Bean b = (Bean) context.getApplicationContext();

                    base = b.baseurl;

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(b.baseurl)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

                    Call<singleProductBean> call = cr.deleteCart(item.getPid());

                    call.enqueue(new Callback<singleProductBean>() {
                        @Override
                        public void onResponse(Call<singleProductBean> call, Response<singleProductBean> response) {

                            if (response.body().getStatus().equals("1")) {
                                loadCart();
                                mainActivity.loadCart();
                            }

                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            bar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onFailure(Call<singleProductBean> call, Throwable t) {
                            bar.setVisibility(View.GONE);
                            Log.d("error", t.toString());
                        }
                    });

                }
            });


            viewHolder.price.setText("\u20B9 " + item.getPrice());


            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(false).build();
            ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(item.getImage() , viewHolder.imageView , options);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            ImageButton delete;

            Button add, remove;
            TextView quantity , title , brand , price;


            ViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.image);
                //buy = itemView.findViewById(R.id.play);


                delete = itemView.findViewById(R.id.delete);

                add = itemView.findViewById(R.id.increment);
                remove = itemView.findViewById(R.id.decrement);
                quantity = itemView.findViewById(R.id.display);
                title = itemView.findViewById(R.id.textView20);
                brand = itemView.findViewById(R.id.textView21);
                price = itemView.findViewById(R.id.textView22);

                //buy.setSideTapEnabled(true);

                //name = itemView.findViewById(R.id.name);


            }
        }
    }

}
