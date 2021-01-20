package com.technuoma.bonpizza.checkPromoPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("pid")
    @Expose
    private String pid;
    @SerializedName("type")
    @Expose
    private String type;

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
