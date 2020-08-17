package com.technuoma.bonpizza;


import com.technuoma.bonpizza.addressPOJO.addressBean;
import com.technuoma.bonpizza.cartPOJO.cartBean;
import com.technuoma.bonpizza.homePOJO.homeBean;
import com.technuoma.bonpizza.ordersPOJO.ordersBean;
import com.technuoma.bonpizza.productsPOJO.productsBean;
import com.technuoma.bonpizza.seingleProductPOJO.singleProductBean;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AllApiIneterface {

    @Multipart
    @POST("pizza/api/login.php")
    Call<loginBean> login(
            @Part("phone") String phone,
            @Part("token") String token,
            @Part("referrer") String referrer
    );

    @Multipart
    @POST("pizza/api/verify.php")
    Call<loginBean> verify(
            @Part("phone") String phone,
            @Part("otp") String otp
    );

    @Multipart
    @POST("pizza/api/getHome2.php")
    Call<homeBean> getHome(
            @Part("lat") String lat,
            @Part("lng") String lng
    );

    @Multipart
    @POST("pizza/api/getOrders.php")
    Call<ordersBean> getOrders(
            @Part("user_id") String user_id
    );

    @Multipart
    @POST("pizza/api/clearCart.php")
    Call<singleProductBean> clearCart(
            @Part("user_id") String user_id
    );

    @Multipart
    @POST("pizza/api/getCart.php")
    Call<cartBean> getCart(
            @Part("user_id") String user_id
    );

    @Multipart
    @POST("pizza/api/updateCart.php")
    Call<singleProductBean> updateCart(
            @Part("id") String id,
            @Part("quantity") String quantity,
            @Part("unit_price") String unit_price
    );

    @Multipart
    @POST("pizza/api/deleteCart.php")
    Call<singleProductBean> deleteCart(
            @Part("id") String id
    );

    @Multipart
    @POST("pizza/api/getProducts.php")
    Call<productsBean> getProducts(
            @Part("subcat2") String cat,
            @Part("location_id") String location_id
    );

    @Multipart
    @POST("pizza/api/getProductById.php")
    Call<singleProductBean> getProductById(
            @Part("id") String cat
    );

    @Multipart
    @POST("pizza/api/getAddress.php")
    Call<addressBean> getAddress(
            @Part("user_id") String user_id
    );

    @Multipart
    @POST("pizza/api/deleteAddress.php")
    Call<addressBean> deleteAddress(
            @Part("id") String id
    );


}
