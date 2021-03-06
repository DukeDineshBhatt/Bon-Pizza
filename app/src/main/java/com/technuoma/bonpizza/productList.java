package com.technuoma.bonpizza;

import android.app.Dialog;
import android.content.Context;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.nostra13.universalimageloader.BuildConfig;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.technuoma.bonpizza.addOnPOJO.addOnBean;
import com.technuoma.bonpizza.productsPOJO.Datum;
import com.technuoma.bonpizza.productsPOJO.productsBean;
import com.technuoma.bonpizza.seingleProductPOJO.singleProductBean;

import java.util.ArrayList;
import java.util.HashSet;
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

import static android.content.ContentValues.TAG;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class productList extends Fragment {

    RecyclerView grid;
    ProgressBar progress;

    String id;

    List<Datum> list;
    BestAdapter adapter;
    MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_layout, container, false);
        mainActivity = (MainActivity) getActivity();
        list = new ArrayList<>();

        id = getArguments().getString("id");

        grid = view.findViewById(R.id.grid);
        progress = view.findViewById(R.id.progress);

        adapter = new BestAdapter(getActivity(), list);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);

        grid.setAdapter(adapter);
        grid.setLayoutManager(manager);


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        progress.setVisibility(View.VISIBLE);

        Bean b = (Bean) getActivity().getApplicationContext();

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

        Call<productsBean> call = cr.getProducts(id, SharePreferenceUtils.getInstance().getString("location"));
        call.enqueue(new Callback<productsBean>() {
            @Override
            public void onResponse(Call<productsBean> call, Response<productsBean> response) {


                if (response.body().getStatus().equals("1")) {
                    adapter.setData(response.body().getData());
                }

                progress.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<productsBean> call, Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    class BestAdapter extends RecyclerView.Adapter<BestAdapter.ViewHolder> {

        Context context;
        List<Datum> list = new ArrayList<>();
        LayoutInflater inflater;

        public BestAdapter(Context context, List<Datum> list) {
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
            this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.best_list_model2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            final Datum item = list.get(position);

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

            final String finalNv = nv1;
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
                        final LinearLayout toppings = dialog.findViewById(R.id.checkBox);
                        final TextView addontext = dialog.findViewById(R.id.textView6);

                        List<Integer> aons = new ArrayList<>();

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

                        Call<List<addOnBean>> call2 = cr.getAddon();

                        call2.enqueue(new Callback<List<addOnBean>>() {
                            @Override
                            public void onResponse(Call<List<addOnBean>> call, Response<List<addOnBean>> response) {

                                toppings.removeAllViews();

                                for (int i = 0; i < response.body().size(); i++) {

                                    View addonmodel = inflater.inflate(R.layout.addon_model, null);
                                    TextView ty = addonmodel.findViewById(R.id.textView8);
                                    TextView spinner = addonmodel.findViewById(R.id.spinner3);

                                    ty.setText("Addons");

                                    ArrayList<MultiSelectModel> nm = new ArrayList<>();
                                    ArrayList<String> pr = new ArrayList<>();


                                    for (int j = 0; j < response.body().get(i).getData().size(); j++) {

                                        int iidd = Integer.parseInt(response.body().get(i).getData().get(j).getId());

                                        String title = "";

                                        if (item.getSize().equals("Regular")) {
                                            title = response.body().get(i).getData().get(j).getType() + " - " + response.body().get(i).getData().get(j).getTitle() + " ( ₹ " + response.body().get(i).getData().get(j).getPriceRegular() + ")";
                                            pr.add(response.body().get(i).getData().get(j).getPriceRegular());
                                        } else if (item.getSize().equals("Couple")) {
                                            title = response.body().get(i).getData().get(j).getType() + " - " + response.body().get(i).getData().get(j).getTitle() + " ( ₹ " + response.body().get(i).getData().get(j).getPriceCouple() + ")";
                                            pr.add(response.body().get(i).getData().get(j).getPriceCouple());
                                        } else {
                                            title = response.body().get(i).getData().get(j).getType() + " - " + response.body().get(i).getData().get(j).getTitle() + " ( ₹ " + response.body().get(i).getData().get(j).getPriceFamily() + ")";
                                            pr.add(response.body().get(i).getData().get(j).getPriceFamily());
                                        }

                                        MultiSelectModel model = new MultiSelectModel(iidd, title);

                                        //nm.add(response.body().get(i).getData().get(j).getTitle());
                                        nm.add(model);

                                    }

                                    //ArrayAdapter<String> adapter = new ArrayAdapter <String>(context, android.R.layout.simple_list_item_multiple_choice, nm);

                                    spinner.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            ArrayList<Integer> sel = new ArrayList<>();

                                            MultiSelectDialog multiSelectDialog = new MultiSelectDialog()
                                                    .title("Addon") //setting title for dialog
                                                    .titleSize(25)
                                                    .positiveText("Done")
                                                    .negativeText("Cancel")
                                                    .preSelectIDsList(sel)
                                                    .setMinSelectionLimit(1) //you can set minimum checkbox selection limit (Optional)
                                                    .multiSelectList(nm) // the multi select model list with ids and name
                                                    .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                                                        @Override
                                                        public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String dataString) {
                                                            //will return list of selected IDS
                                                            sel.addAll(selectedIds);

                                                            aons.clear();

                                                            Log.d("datastring", TextUtils.join(", ", selectedIds));
                                                            aons.addAll(selectedIds);
                                                            spinner.setText(TextUtils.join(", ", selectedNames));


                                                        }

                                                        @Override
                                                        public void onCancel() {
                                                            Log.d(TAG, "Dialog cancelled");
                                                        }


                                                    });

                                            multiSelectDialog.show(getChildFragmentManager(), "multiSelectDialog");

                                        }
                                    });

                                    toppings.addView(addonmodel);

                                }

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


                                ArrayList<Integer> values = new ArrayList<>();
                                HashSet<Integer> hashSet = new HashSet<>(aons);
                                values.clear();
                                values.addAll(hashSet);

                                TextUtils.join(",", values);
                                Log.d("addons", TextUtils.join(",", values));

                                Call<singleProductBean> call = cr.addCart(SharePreferenceUtils.getInstance().getString("userId"), item.getId(), String.valueOf(stepperTouch.getCount()), nv1, versionName, TextUtils.join(", ", values));

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
                add = itemView.findViewById(R.id.add);
                discount = itemView.findViewById(R.id.discount);

            }
        }
    }

}
