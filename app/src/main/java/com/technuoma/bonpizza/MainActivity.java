package com.technuoma.bonpizza;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.technuoma.bonpizza.cartPOJO.cartBean;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity implements ResultCallback<LocationSettingsResult> {


    Toolbar toolbar;
    DrawerLayout drawer;
    private FusedLocationProviderClient fusedLocationClient;

    String lat = "", lng = "";

    LocationSettingsRequest.Builder builder;
    LocationRequest locationRequest;

    private static final String TAG = "Main Activity";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    BottomNavigationView navigation;
    ImageButton cart1;
    TextView login, terms, about, address, logout, cart, orders, count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
        count = findViewById(R.id.count);
        orders = findViewById(R.id.orders);
        login = findViewById(R.id.textView3);
        terms = findViewById(R.id.terms);
        about = findViewById(R.id.about);
        address = findViewById(R.id.address);
        logout = findViewById(R.id.logout);
        cart = findViewById(R.id.cart);
        cart1 = findViewById(R.id.imageButton3);


        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("Bon Pizza");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle("Your Location");
        toolbar.setSubtitleTextColor(Color.WHITE);

        //banner.setAdapter(adapter4);
        //banner.setLayoutManager(manager3);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:

                        FragmentManager fm = getSupportFragmentManager();

                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }

                        FragmentTransaction ft = fm.beginTransaction();
                        Home frag1 = new Home();
                        ft.replace(R.id.replace, frag1);
                        //ft.addToBackStack(null);
                        ft.commit();
                        drawer.closeDrawer(GravityCompat.START);

                        break;
                    case R.id.action_order:
                        FragmentManager fm3 = getSupportFragmentManager();

                        for (int i = 0; i < fm3.getBackStackEntryCount(); ++i) {
                            fm3.popBackStack();
                        }

                        FragmentTransaction ft3 = fm3.beginTransaction();
                        Orders frag13 = new Orders();
                        ft3.replace(R.id.replace, frag13);
                        //ft.addToBackStack(null);
                        ft3.commit();
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.action_cart:
                        FragmentManager fm4 = getSupportFragmentManager();

                        for (int i = 0; i < fm4.getBackStackEntryCount(); ++i) {
                            fm4.popBackStack();
                        }

                        FragmentTransaction ft4 = fm4.beginTransaction();
                        Cart frag14 = new Cart();
                        ft4.replace(R.id.replace, frag14);
                        //ft.addToBackStack(null);
                        ft4.commit();
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });

        navigation.setSelectedItemId(R.id.action_home);

        final String uid = SharePreferenceUtils.getInstance().getString("userId");

        if (uid.length() > 0) {
            login.setText(SharePreferenceUtils.getInstance().getString("phone"));
            //rewards.setVisibility(View.VISIBLE);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (uid.length() == 0) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }


            }
        });

        cart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigation.setSelectedItemId(R.id.action_cart);

                drawer.closeDrawer(GravityCompat.START);

            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Web.class);
                intent.putExtra("title", "Terms & Conditions");
                intent.putExtra("url", "https://technuoma.com/pizza/terms.php");
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);

            }
        });


        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Web.class);
                intent.putExtra("title", "About Us");
                intent.putExtra("url", "https://technuoma.com/pizza/about.php");
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);

            }
        });


        /*refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShareCompat.IntentBuilder.from(MainActivity.this)
                        .setType("text/plain")
                        .setChooserTitle("Chooser title")
                        .setText("http://play.google.com/store/apps/details?id=" + getPackageName() + "&referrer=" + SharePreferenceUtils.getInstance().getString("userId"))
                        .startChooser();

                *//*Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(
                        "http://play.google.com/store/apps/details?id=" + getPackageName() + "&referrer=" + SharePreferenceUtils.getInstance().getString("userId")));
                intent.setPackage("com.android.vending");
                startActivity(intent);

                Log.d("adasd", "http://play.google.com/store/apps/details?id=" + getPackageName() + "&referrer=" + SharePreferenceUtils.getInstance().getString("userId"));
                *//*
                drawer.closeDrawer(GravityCompat.START);
                *//*ShareCompat.IntentBuilder.from(MainActivity.this)
                        .setType("text/plain")
                        .setChooserTitle("Chooser title")
                        .setText("http://play.google.com/store/apps/details?id=" + getPackageName() + "&referrer=" + SharePreferenceUtils.getInstance().getString("userId"))
                        .startChooser();*//*

            }
        });*/


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (uid.length() == 0) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }


            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (uid.length() > 0) {
                    Intent intent = new Intent(MainActivity.this, com.technuoma.bonpizza.Address.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please login to continue", Toast.LENGTH_SHORT).show();
                }

                drawer.closeDrawer(GravityCompat.START);

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharePreferenceUtils.getInstance().deletePref();

                Intent intent = new Intent(MainActivity.this, Spalsh.class);
                startActivity(intent);
                finishAffinity();

            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigation.setSelectedItemId(R.id.action_cart);

                drawer.closeDrawer(GravityCompat.START);

            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigation.setSelectedItemId(R.id.action_order);

                drawer.closeDrawer(GravityCompat.START);

            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();

        loadCart();
    }


    void loadCart() {
        String uid = SharePreferenceUtils.getInstance().getString("userId");

        if (uid.length() > 0) {
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

            Call<cartBean> call2 = cr.getCart(SharePreferenceUtils.getInstance().getString("userId"));
            call2.enqueue(new Callback<cartBean>() {
                @Override
                public void onResponse(Call<cartBean> call, Response<cartBean> response) {

                    if (response.body().getData().size() > 0) {


                        count.setText(String.valueOf(response.body().getData().size()));


                    } else {

                        count.setText("0");

                    }


                }

                @Override
                public void onFailure(Call<cartBean> call, Throwable t) {

                }
            });


        } else {
            count.setText("0");
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
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

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


}