package com.technuoma.bonpizza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class OTP extends AppCompatActivity {

    EditText phone;
    Button login;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        phone = findViewById(R.id.editText);
        login = findViewById(R.id.button);
        progress = findViewById(R.id.progressBar);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String p = phone.getText().toString();

                if (p.length() == 4)
                {


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

                    Call<loginBean> call = cr.verify(SharePreferenceUtils.getInstance().getString("phone") , p);

                    call.enqueue(new Callback<loginBean>() {
                        @Override
                        public void onResponse(@NotNull Call<loginBean> call, @NotNull Response<loginBean> response) {

                            assert response.body() != null;
                            if (response.body().getStatus().equals("1"))
                            {
                                SharePreferenceUtils.getInstance().saveString("userId" , response.body().getUserId());
                                SharePreferenceUtils.getInstance().saveString("phone" , response.body().getPhone());
                                SharePreferenceUtils.getInstance().saveString("rewards" , response.body().getRewards());
                                Toast.makeText(OTP.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(OTP.this , MainActivity.class);
                                startActivity(intent);
                                finishAffinity();

                            }

                            progress.setVisibility(View.GONE);

                        }

                        @Override
                        public void onFailure(@NotNull Call<loginBean> call, @NotNull Throwable t) {
                            progress.setVisibility(View.GONE);
                        }
                    });


                }
                else
                {
                    Toast.makeText(OTP.this, "Please enter a valid OTP", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}