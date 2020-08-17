package com.technuoma.bonpizza;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class loginBean {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("rewards")
    @Expose
    private String rewards;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }
}
