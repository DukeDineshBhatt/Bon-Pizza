package com.technuoma.bonpizza;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class trackBean {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("mylat")
    @Expose
    private String mylat;
    @SerializedName("mylng")
    @Expose
    private String mylng;
    @SerializedName("driverlat")
    @Expose
    private String driverlat;
    @SerializedName("driverlng")
    @Expose
    private String driverlng;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMylat() {
        return mylat;
    }

    public void setMylat(String mylat) {
        this.mylat = mylat;
    }

    public String getMylng() {
        return mylng;
    }

    public void setMylng(String mylng) {
        this.mylng = mylng;
    }

    public String getDriverlat() {
        return driverlat;
    }

    public void setDriverlat(String driverlat) {
        this.driverlat = driverlat;
    }

    public String getDriverlng() {
        return driverlng;
    }

    public void setDriverlng(String driverlng) {
        this.driverlng = driverlng;
    }

}
