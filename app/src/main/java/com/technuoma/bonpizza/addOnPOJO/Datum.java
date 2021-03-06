package com.technuoma.bonpizza.addOnPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("price_regular")
    @Expose
    private String priceRegular;
    @SerializedName("price_couple")
    @Expose
    private String priceCouple;
    @SerializedName("price_family")
    @Expose
    private String priceFamily;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPriceRegular() {
        return priceRegular;
    }

    public void setPriceRegular(String priceRegular) {
        this.priceRegular = priceRegular;
    }

    public String getPriceCouple() {
        return priceCouple;
    }

    public void setPriceCouple(String priceCouple) {
        this.priceCouple = priceCouple;
    }

    public String getPriceFamily() {
        return priceFamily;
    }

    public void setPriceFamily(String priceFamily) {
        this.priceFamily = priceFamily;
    }

}
