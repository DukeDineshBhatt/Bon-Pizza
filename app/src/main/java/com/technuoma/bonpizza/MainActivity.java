package com.technuoma.bonpizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

public class MainActivity extends AppCompatActivity implements ResultCallback<LocationSettingsResult> {

    ProgressBar progress;
    Toolbar toolbar;
    DrawerLayout drawer;
    RecyclerView category, deal, banner;

    DealAdapter adapter3;
    CategoryAdapter adapter6;
    OfferAdapter adapter;
    List<Best> list;
    List<Cat> list3;
    List<Banners> list4;
    private FusedLocationProviderClient fusedLocationClient;

    String lat = "", lng = "";

    LocationSettingsRequest.Builder builder;
    LocationRequest locationRequest;

    private static final String TAG = "Main Activity";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    TextView location;
    AutoViewPager pager;
    ImageView banner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        banner1 = findViewById(R.id.banner1);
        progress = findViewById(R.id.progress);
        location = findViewById(R.id.location);
        pager = findViewById(R.id.viewPager);
        pager.setPageMargin(20);
        category = findViewById(R.id.category);
        deal = findViewById(R.id.deal);
        banner = findViewById(R.id.banner);

        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        adapter3 = new DealAdapter(this, list);
        adapter6 = new CategoryAdapter(this, list3);
        adapter = new OfferAdapter(this, list4);
        //adapter4 = new BannerAdapter(this, list);

        GridLayoutManager manager1 = new GridLayoutManager(this, 3);
        LinearLayoutManager manager2 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager manager3 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        GridLayoutManager manager5 = new GridLayoutManager(this, 3);
        GridLayoutManager manager7 = new GridLayoutManager(this, 1);

        category.setAdapter(adapter6);
        category.setLayoutManager(manager5);

        deal.setAdapter(adapter3);
        deal.setLayoutManager(manager2);

        banner.setAdapter(adapter);
        banner.setLayoutManager(manager7);

        //banner.setAdapter(adapter4);
        //banner.setLayoutManager(manager3);

        createLocationRequest();

    }

    @Override
    public void onResume() {
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

        Call<homeBean> call = cr.getHome(SharePreferenceUtils.getInstance().getString("lat"), SharePreferenceUtils.getInstance().getString("lng"));
        call.enqueue(new Callback<homeBean>() {
            @Override
            public void onResponse(Call<homeBean> call, Response<homeBean> response) {


                if (response.body().getStatus().equals("1")) {

                    try {
                        BannerAdapter adapter1 = new BannerAdapter(getSupportFragmentManager(), response.body().getPbanner());
                        pager.setAdapter(adapter1);

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

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

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

                   /* FragmentManager fm4 = mainActivity.getSupportFragmentManager();

                    for (int i = 0; i < fm4.getBackStackEntryCount(); ++i) {
                        fm4.popBackStack();
                    }

                    FragmentTransaction ft4 = fm4.beginTransaction();
                    SubCat frag14 = new SubCat();
                    Bundle b = new Bundle();
                    b.putString("id", item.getId());
                    b.putString("title", item.getName());
                    b.putString("image", item.getImage());
                    frag14.setArguments(b);
                    ft4.replace(R.id.replace, frag14);
                    ft4.addToBackStack(null);
                    //ft.addToBackStack(null);
                    ft4.commit();
*/

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

                holder.price.setText(Html.fromHtml("\u20B9 " + String.valueOf(nv)));
                holder.discount.setText(Html.fromHtml("<strike>\u20B9 " + item.getPrice() + "</strike>"));
                holder.discount.setVisibility(View.VISIBLE);
            } else {

                nv1 = item.getPrice();
                holder.price.setText("\u20B9 " + item.getPrice());
                holder.discount.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                  /*  Intent intent = new Intent(context, SingleProduct.class);
                    intent.putExtra("id", item.getId());
                    intent.putExtra("title", item.getName());
                    context.startActivity(intent);*/

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

                    /*if (cid != null) {
                        FragmentManager fm4 = mainActivity.getSupportFragmentManager();

                        for (int i = 0; i < fm4.getBackStackEntryCount(); ++i) {
                            fm4.popBackStack();
                        }

                        FragmentTransaction ft4 = fm4.beginTransaction();
                        SubCat frag14 = new SubCat();
                        Bundle b = new Bundle();
                        b.putString("id", cid);
                        b.putString("title", tit);
                        b.putString("image", image2);
                        frag14.setArguments(b);
                        ft4.replace(R.id.replace, frag14);
                        ft4.addToBackStack(null);
                        //ft.addToBackStack(null);
                        ft4.commit();
                    }*/


                }
            });


            return view;
        }
    }


    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(MainActivity.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLocation();
            }
        });

        task.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(MainActivity.this, "Location is required for this app", Toast.LENGTH_LONG).show();
                        MainActivity.this.finishAffinity();
                        break;
                }
                break;
        }
    }

    void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                for (Location location1 : locationResult.getLocations()) {
                    if (location1 != null) {
                        //TODO: UI updates.
                        lat = String.valueOf(location1.getLatitude());
                        lng = String.valueOf(location1.getLongitude());

                        SharePreferenceUtils.getInstance().saveString("lat", lat);
                        SharePreferenceUtils.getInstance().saveString("lng", lng);

                        Log.d("lat123", lat);

                        try {
                            location.setText(getPostalCodeByCoordinates(MainActivity.this, location1.getLatitude(), location1.getLongitude()));
                            SharePreferenceUtils.getInstance().saveString("postal", getPostalCodeByCoordinates(MainActivity.this, location1.getLatitude(), location1.getLongitude()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);

                    }
                }
            }
        };

        LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest, mLocationCallback, null);

    }


    public static String getPostalCodeByCoordinates(Context context, double lat, double lon) throws IOException {

        Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
        String zipcode = null;
        Address address = null;

        if (mGeocoder != null) {

            List<Address> addresses = mGeocoder.getFromLocation(lat, lon, 5);

            if (addresses != null && addresses.size() > 0) {

                for (int i = 0; i < addresses.size(); i++) {
                    address = addresses.get(i);
                    if (address.getPostalCode() != null) {
                        zipcode = address.getPostalCode();
                        Log.d(TAG, "Postal code: " + address.getPostalCode());
                        break;
                    }

                }
                return zipcode;
            }
        }

        return null;
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

}