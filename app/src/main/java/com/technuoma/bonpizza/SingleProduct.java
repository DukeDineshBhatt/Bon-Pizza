package com.technuoma.bonpizza;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nostra13.universalimageloader.BuildConfig;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.technuoma.bonpizza.addOnPOJO.addOnBean;
import com.technuoma.bonpizza.seingleProductPOJO.Data;
import com.technuoma.bonpizza.seingleProductPOJO.singleProductBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.steppertouch.StepperTouch;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SingleProduct extends Fragment {

    ImageView image;
    TextView discount, title, price;
    Button add;
    TextView brand, unit, seller;
    TextView description, key_features, packaging, life, disclaimer, stock;
    ProgressBar progress;

    String id, name;

    String pid, nv1;

    MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_single_product, container, false);
        mainActivity = (MainActivity) getActivity();
        id = getArguments().getString("id");
        name = getArguments().getString("title");

        image = view.findViewById(R.id.image);
        discount = view.findViewById(R.id.discount);
        title = view.findViewById(R.id.title);
        price = view.findViewById(R.id.price);
        add = view.findViewById(R.id.add);
        brand = view.findViewById(R.id.brand);
        unit = view.findViewById(R.id.unit);
        seller = view.findViewById(R.id.seller);
        description = view.findViewById(R.id.description);
        key_features = view.findViewById(R.id.key_features);
        packaging = view.findViewById(R.id.packaging);
        life = view.findViewById(R.id.life);
        disclaimer = view.findViewById(R.id.disclaimer);
        progress = view.findViewById(R.id.progress);
        stock = view.findViewById(R.id.stock);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        progress.setVisibility(View.VISIBLE);

        Bean b = (Bean) mainActivity.getApplicationContext();

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

        Call<singleProductBean> call = cr.getProductById(id);
        call.enqueue(new Callback<singleProductBean>() {
            @Override
            public void onResponse(Call<singleProductBean> call, Response<singleProductBean> response) {


                if (response.body().getStatus().equals("1")) {
                    Data item = response.body().getData();

                    pid = item.getId();

                    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(false).build();
                    ImageLoader loader = ImageLoader.getInstance();
                    loader.displayImage(item.getImage(), image, options);

                    float dis = Float.parseFloat(item.getDiscount());

                    if (dis > 0) {

                        float pri = Float.parseFloat(item.getPrice());
                        float dv = (dis / 100) * pri;

                        float nv = pri - dv;

                        nv1 = String.valueOf(nv);

                        discount.setVisibility(View.VISIBLE);
                        discount.setText(item.getDiscount() + "% OFF");
                        price.setText(Html.fromHtml("<font color=\"#000000\"><b>\u20B9 " + String.valueOf(nv) + " </b></font><strike>\u20B9 " + item.getPrice() + "</strike>"));
                    } else {

                        nv1 = item.getPrice();
                        discount.setVisibility(View.GONE);
                        price.setText(Html.fromHtml("<font color=\"#000000\"><b>\u20B9 " + String.valueOf(item.getPrice()) + " </b></font>"));
                    }


                    title.setText(item.getName());

                    brand.setText(item.getBrand());
                    unit.setText(item.getUnit());
                    seller.setText(item.getSeller());

                    description.setText(item.getDescription());
                    key_features.setText(item.getKeyFeatures());
                    packaging.setText(item.getPackagingType());
                    life.setText(item.getShelfLife());
                    disclaimer.setText(item.getDisclaimer());

                    stock.setText(item.getStock());

                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if (pid.length() > 0) {
                                String uid = SharePreferenceUtils.getInstance().getString("userId");

                                if (uid.length() > 0) {

                                    final Dialog dialog = new Dialog(mainActivity);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setCancelable(true);
                                    dialog.setContentView(R.layout.add_cart_dialog);
                                    dialog.show();

                                    final StepperTouch stepperTouch = dialog.findViewById(R.id.stepperTouch);
                                    Button add = dialog.findViewById(R.id.button8);
                                    final ProgressBar progressBar = dialog.findViewById(R.id.progressBar2);
                                    final TextView name = dialog.findViewById(R.id.name);
                                    final TextView size = dialog.findViewById(R.id.size);
                                    final TextView rate = dialog.findViewById(R.id.rate);
                                    final TextView discount = dialog.findViewById(R.id.discount);
                                    final LinearLayout toppings = dialog.findViewById(R.id.checkBox);
                                    final TextView addontext = dialog.findViewById(R.id.textView6);

                                    List<String> aons = new ArrayList<>();

                                    if (item.getHas_addon().equals("yes")) {
                                        toppings.setVisibility(View.VISIBLE);
                                        addontext.setVisibility(View.VISIBLE);
                                    } else {
                                        toppings.setVisibility(View.GONE);
                                        addontext.setVisibility(View.GONE);
                                    }


                                    name.setText(item.getName());
                                    size.setText(item.getSize());

                                    progressBar.setVisibility(View.VISIBLE);

                                    Bean b = (Bean) mainActivity.getApplicationContext();

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

                                    Call<List<addOnBean>> call2 = cr.getAddon();

                                    call2.enqueue(new Callback<List<addOnBean>>() {
                                        @Override
                                        public void onResponse(Call<List<addOnBean>> call, Response<List<addOnBean>> response) {

                                            toppings.removeAllViews();

                                            /*for (int i = 0; i < response.body().size(); i++) {
                                                CheckBox checkBox = new CheckBox(mainActivity);
                                                checkBox.setText(response.body().get(i).getTitle());

                                                int finalI = i;
                                                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                                                        if (b) {
                                                            aons.add(response.body().get(finalI).getId());
                                                        } else {
                                                            aons.remove(response.body().get(finalI).getId());
                                                        }

                                                    }
                                                });

                                                toppings.addView(checkBox);

                                            }*/

                                            progressBar.setVisibility(View.GONE);

                                        }

                                        @Override
                                        public void onFailure(Call<List<addOnBean>> call, Throwable t) {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });


                                    if (dis > 0) {

                                        float pri = Float.parseFloat(item.getPrice());
                                        float dv = (dis / 100) * pri;

                                        float nv = pri - dv;

                                        rate.setText(Html.fromHtml("\u20B9 " + nv));
                                        discount.setText(Html.fromHtml("<strike>\u20B9 " + item.getPrice() + "</strike>"));
                                        discount.setVisibility(View.VISIBLE);
                                    } else {

                                        rate.setText("\u20B9 " + item.getPrice());
                                        discount.setVisibility(View.GONE);
                                    }

                                    stepperTouch.setMinValue(1);
                                    stepperTouch.setMaxValue(99);
                                    stepperTouch.setSideTapEnabled(true);
                                    stepperTouch.setCount(1);

                                    add.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            progressBar.setVisibility(View.VISIBLE);

                                            Bean b = (Bean) mainActivity.getApplicationContext();


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

                                            Log.d("userid", SharePreferenceUtils.getInstance().getString("userid"));
                                            Log.d("pid", item.getId());
                                            Log.d("quantity", String.valueOf(stepperTouch.getCount()));
                                            Log.d("price", nv1);

                                            int versionCode = com.nostra13.universalimageloader.BuildConfig.VERSION_CODE;
                                            String versionName = BuildConfig.VERSION_NAME;


                                            TextUtils.join(",", aons);
                                            Log.d("addons", TextUtils.join(",", aons));

                                            Call<singleProductBean> call = cr.addCart(SharePreferenceUtils.getInstance().getString("userId"), item.getId(), String.valueOf(stepperTouch.getCount()), nv1, versionName, TextUtils.join(", ", aons));

                                            call.enqueue(new Callback<singleProductBean>() {
                                                @Override
                                                public void onResponse(Call<singleProductBean> call, Response<singleProductBean> response) {

                                                    if (response.body().getStatus().equals("1")) {
                                                        mainActivity.loadCart();
                                                        dialog.dismiss();
                                                    }

                                                    Toast.makeText(mainActivity, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                                    progressBar.setVisibility(View.GONE);

                                                }

                                                @Override
                                                public void onFailure(Call<singleProductBean> call, Throwable t) {
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            });


                                        }
                                    });

                                } else {
                                    Toast.makeText(mainActivity, "Please login to continue", Toast.LENGTH_SHORT).show();
                                    //Intent intent = new Intent(mainActivity , Login.class);
                                    //startActivity(intent);

                                }
                            }


                        }
                    });

                }

                progress.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<singleProductBean> call, Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });

    }
}
