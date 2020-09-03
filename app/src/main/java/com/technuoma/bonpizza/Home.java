package com.technuoma.bonpizza;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.nostra13.universalimageloader.BuildConfig;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.santalu.autoviewpager.AutoViewPager;
import com.technuoma.bonpizza.homePOJO.Banners;
import com.technuoma.bonpizza.homePOJO.Best;
import com.technuoma.bonpizza.homePOJO.Cat;
import com.technuoma.bonpizza.homePOJO.homeBean;
import com.technuoma.bonpizza.seingleProductPOJO.singleProductBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import me.relex.circleindicator.CircleIndicator;
import nl.dionsegijn.steppertouch.StepperTouch;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Home extends Fragment {

    private static final String TAG = "Home";
    ProgressBar progress;
    RecyclerView category, deal, banner;

    DealAdapter adapter3;
    CategoryAdapter adapter6;
    OfferAdapter adapter;
    List<Best> list;
    List<Cat> list3;
    List<Banners> list4;

    AutoViewPager pager;
    ImageView banner1;

    static MainActivity mainActivity;
    CircleIndicator indicator;


    private FusedLocationProviderClient fusedLocationClient;

    String lat = "", lng = "";

    LocationSettingsRequest.Builder builder;
    LocationRequest locationRequest;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        mainActivity = (MainActivity) getActivity();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity);

        list = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();

        banner1 = view.findViewById(R.id.banner1);
        indicator = view.findViewById(R.id.indicator);
        progress = view.findViewById(R.id.progressBar);
        pager = view.findViewById(R.id.viewPager);
        pager.setPageMargin(20);
        category = view.findViewById(R.id.category);
        deal = view.findViewById(R.id.deal);
        banner = view.findViewById(R.id.banner);

        adapter3 = new DealAdapter(mainActivity, list);
        adapter6 = new CategoryAdapter(mainActivity, list3);
        adapter = new OfferAdapter(mainActivity, list4);
        //adapter4 = new BannerAdapter(this, list);

        GridLayoutManager manager1 = new GridLayoutManager(mainActivity, 3);
        LinearLayoutManager manager2 = new LinearLayoutManager(mainActivity, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager manager3 = new LinearLayoutManager(mainActivity, RecyclerView.VERTICAL, false);
        GridLayoutManager manager5 = new GridLayoutManager(mainActivity, 3);
        GridLayoutManager manager7 = new GridLayoutManager(mainActivity, 1);

        category.setAdapter(adapter6);
        category.setLayoutManager(manager5);

        deal.setAdapter(adapter3);
        deal.setLayoutManager(manager2);

        banner.setAdapter(adapter);
        banner.setLayoutManager(manager7);


        progress.setVisibility(View.VISIBLE);

        createLocationRequest();

        return view;
    }


    void loaddat() {
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

        Call<homeBean> call = cr.getHome(SharePreferenceUtils.getInstance().getString("lat"), SharePreferenceUtils.getInstance().getString("lng"));
        call.enqueue(new Callback<homeBean>() {
            @Override
            public void onResponse(Call<homeBean> call, Response<homeBean> response) {


                if (response.body().getStatus().equals("1")) {

                    try {
                        BannerAdapter adapter1 = new BannerAdapter(getChildFragmentManager(), response.body().getPbanner());
                        pager.setAdapter(adapter1);
                        indicator.setViewPager(pager);

                        adapter3.setData(response.body().getBest());
                        adapter6.setData(response.body().getCat());
                        //adapter4.setData(response.body().getBest());

                        Log.d("ssiizzee", String.valueOf(response.body().getObanner().size()));
                    } catch (Exception e) {

                        e.printStackTrace();
                    }


                    try {
                        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(false).build();
                        ImageLoader loader = ImageLoader.getInstance();
                        String url = response.body().getObanner().get(0).getImage();
                        loader.displayImage(url, banner1, options);

                        String cid = response.body().getObanner().get(0).getCid();
                        String tit = response.body().getObanner().get(0).getCname();
                        String image = response.body().getObanner().get(0).getCatimage();

                        banner1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (cid != null) {

                                    /*FragmentManager fm4 = getSupportFragmentManager();

                                    for (int i = 0; i < fm4.getBackStackEntryCount(); ++i) {
                                        fm4.popBackStack();
                                    }

                                    FragmentTransaction ft4 = fm4.beginTransaction();
                                    SubCat frag14 = new SubCat();
                                    Bundle b = new Bundle();
                                    b.putString("id", cid);
                                    b.putString("title", tit);
                                    b.putString("image", image);
                                    frag14.setArguments(b);
                                    ft4.replace(R.id.replace, frag14);
                                    ft4.addToBackStack(null);
                                    //ft.addToBackStack(null);
                                    ft4.commit();*/

                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        if (response.body().getObanner().size() > 1) {
                            List<Banners> ll = response.body().getObanner();
                            ll.remove(0);
                            adapter.setData(ll);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                progress.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<homeBean> call, Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

        Context context;
        List<Cat> list = new ArrayList<>();

        public CategoryAdapter(Context context, List<Cat> list) {
            this.context = context;
            this.list = list;
        }

        public void setData(List<Cat> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.best_list_model, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            final Cat item = list.get(position);

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(false).build();
            ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(item.getImage(), holder.image, options);

            //holder.tag.setText(item.getTag());
            holder.title.setText(item.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentManager fm4 = mainActivity.getSupportFragmentManager();

                    for (int i = 0; i < fm4.getBackStackEntryCount(); ++i) {
                        fm4.popBackStack();
                    }

                    FragmentTransaction ft4 = fm4.beginTransaction();
                    productList frag14 = new productList();
                    Bundle b = new Bundle();
                    b.putString("id", item.getId());
                    frag14.setArguments(b);
                    ft4.replace(R.id.replace, frag14);
                    ft4.addToBackStack(null);
                    //ft.addToBackStack(null);
                    ft4.commit();


                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView image;
            TextView tag, title;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.imageView4);
                //tag = itemView.findViewById(R.id.textView17);
                title = itemView.findViewById(R.id.textView11);


            }
        }
    }

    class DealAdapter extends RecyclerView.Adapter<DealAdapter.ViewHolder> {

        Context context;
        List<Best> list = new ArrayList<>();

        public DealAdapter(Context context, List<Best> list) {
            this.context = context;
            this.list = list;
        }

        public void setData(List<Best> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.deal_list_model, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            final Best item = list.get(position);

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(false).build();
            ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(item.getImage(), holder.image, options);

            holder.name.setText(item.getName());

            float dis = Float.parseFloat(item.getDiscount());

            final String nv1;

            if (dis > 0) {

                float pri = Float.parseFloat(item.getPrice());
                float dv = (dis / 100) * pri;

                float nv = pri - dv;

                nv1 = String.valueOf(nv);

                holder.price.setText(Html.fromHtml("\u20B9 " + nv));
                holder.discount.setText(Html.fromHtml("<strike>\u20B9 " + item.getPrice() + "</strike>"));
                holder.discount.setVisibility(View.VISIBLE);
            } else {

                nv1 = item.getPrice();
                holder.price.setText("\u20B9 " + item.getPrice());
                holder.discount.setVisibility(View.GONE);
            }

            holder.size.setText(item.getSize());

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String uid = SharePreferenceUtils.getInstance().getString("userId");

                    if (uid.length() > 0) {

                        final Dialog dialog = new Dialog(context);
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
                        final CheckBox toppings = dialog.findViewById(R.id.checkBox);
                        final CheckBox sauce = dialog.findViewById(R.id.checkBox2);

                        name.setText(item.getName());
                        size.setText(item.getSize());

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

                                Bean b = (Bean) context.getApplicationContext();


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

                                List<String> aons = new ArrayList<>();

                                if (toppings.isChecked()) {
                                    aons.add("Extra Toppings");
                                }

                                if (sauce.isChecked()) {
                                    aons.add("Deep Sauce");
                                }

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

                                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(context, "Please login to continue", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);

                    }

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentManager fm4 = mainActivity.getSupportFragmentManager();

                    FragmentTransaction ft4 = fm4.beginTransaction();
                    SingleProduct frag14 = new SingleProduct();
                    Bundle b = new Bundle();
                    b.putString("id", item.getId());
                    b.putString("title", item.getName());
                    frag14.setArguments(b);
                    ft4.replace(R.id.replace, frag14);
                    ft4.addToBackStack(null);
                    ft4.commit();

                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView image;
            TextView price, name, size, discount;
            Button add;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);


                price = itemView.findViewById(R.id.rate);
                image = itemView.findViewById(R.id.image);
                name = itemView.findViewById(R.id.name);
                size = itemView.findViewById(R.id.size);
                discount = itemView.findViewById(R.id.discount);
                add = itemView.findViewById(R.id.add);


            }
        }
    }


    class BannerAdapter extends FragmentStatePagerAdapter {

        List<Banners> blist = new ArrayList<>();

        public BannerAdapter(FragmentManager fm, List<Banners> blist) {
            super(fm);
            this.blist = blist;
        }

        @Override
        public Fragment getItem(int position) {
            page frag = new page();
            frag.setData(blist.get(position).getImage(), blist.get(position).getCname(), blist.get(position).getCid(), blist.get(position).getCatimage());
            return frag;
        }

        @Override
        public int getCount() {
            return blist.size();
        }
    }


    public static class page extends Fragment {

        String url, tit, cid = "", image2;

        ImageView image;

        void setData(String url, String tit, String cid, String image2) {
            this.url = url;
            this.tit = tit;
            this.cid = cid;
            this.image2 = image2;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.banner_layout, container, false);

            image = view.findViewById(R.id.imageView3);

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(false).build();
            ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(url, image, options);


            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (cid != null) {
                        FragmentManager fm4 = mainActivity.getSupportFragmentManager();

                        for (int i = 0; i < fm4.getBackStackEntryCount(); ++i) {
                            fm4.popBackStack();
                        }

                        FragmentTransaction ft4 = fm4.beginTransaction();
                        productList frag14 = new productList();
                        Bundle b = new Bundle();
                        b.putString("id", cid);
                        b.putString("title", tit);
                        b.putString("image", image2);
                        frag14.setArguments(b);
                        ft4.replace(R.id.replace, frag14);
                        ft4.addToBackStack(null);
                        //ft.addToBackStack(null);
                        ft4.commit();
                    }


                }
            });


            return view;
        }
    }


    class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

        Context context;
        List<Banners> list = new ArrayList<>();

        public OfferAdapter(Context context, List<Banners> list) {
            this.context = context;
            this.list = list;
        }

        public void setData(List<Banners> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.best_list_model1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            Banners item = list.get(position);

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(false).build();
            ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(item.getImage(), holder.image, options);

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*if (item.getCid() != null) {
                        FragmentManager fm4 = mainActivity.getSupportFragmentManager();

                        for (int i = 0; i < fm4.getBackStackEntryCount(); ++i) {
                            fm4.popBackStack();
                        }

                        FragmentTransaction ft4 = fm4.beginTransaction();
                        SubCat frag14 = new SubCat();
                        Bundle b = new Bundle();
                        b.putString("id", item.getCid());
                        b.putString("title", item.getCname());
                        b.putString("image", item.getCatimage());
                        frag14.setArguments(b);
                        ft4.replace(R.id.replace, frag14);
                        ft4.addToBackStack(null);
                        //ft.addToBackStack(null);
                        ft4.commit();
                    }*/


                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView image;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.imageView4);


            }
        }
    }


    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(mainActivity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        task.addOnSuccessListener(mainActivity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLocation();
            }
        });

        task.addOnFailureListener(mainActivity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but mainActivity can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(mainActivity,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }


    void getLocation() {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                        lat = String.valueOf(location.getLatitude());
                        lng = String.valueOf(location.getLongitude());

                        SharePreferenceUtils.getInstance().saveString("lat", lat);
                        SharePreferenceUtils.getInstance().saveString("lng", lng);

                        Log.d("lat123", lat);

                        LocationServices.getFusedLocationProviderClient(mainActivity).removeLocationUpdates(this);

                        Geocoder geocoder = new Geocoder(mainActivity, Locale.getDefault());
                        List<android.location.Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(Double.parseDouble(SharePreferenceUtils.getInstance().getString("lat")), Double.parseDouble(SharePreferenceUtils.getInstance().getString("lng")), 1);
                            mainActivity.toolbar.setSubtitle(addresses.get(0).getPostalCode());
                            SharePreferenceUtils.getInstance().saveString("pin", addresses.get(0).getPostalCode());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        loaddat();

                    }
                }
            }
        };

        LocationServices.getFusedLocationProviderClient(mainActivity).requestLocationUpdates(locationRequest, mLocationCallback, null);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(mainActivity, "Location is required for mainActivity app", Toast.LENGTH_LONG).show();
                        mainActivity.finishAffinity();
                        break;
                }
                break;
        }


    }

}
