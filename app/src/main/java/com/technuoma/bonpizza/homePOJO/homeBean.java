package com.technuoma.bonpizza.homePOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class homeBean {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("pbanner")
    @Expose
    private List<Banners> pbanner = null;
    @SerializedName("obanner")
    @Expose
    private List<Banners> obanner = null;
    @SerializedName("best")
    @Expose
    private List<Best> best = null;
    @SerializedName("today")
    @Expose
    private List<Best> today = null;
    @SerializedName("member")
    @Expose
    private List<Member> member = null;
    @SerializedName("cat")
    @Expose
    private List<Cat> cat = null;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("city")
    @Expose
    private String city;

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

    public List<Banners> getPbanner() {
        return pbanner;
    }

    public void setPbanner(List<Banners> pbanner) {
        this.pbanner = pbanner;
    }

    public List<Banners> getObanner() {
        return obanner;
    }

    public void setObanner(List<Banners> obanner) {
        this.obanner = obanner;
    }

    public List<Best> getBest() {
        return best;
    }

    public List<Best> getToday() {
        return today;
    }

    public void setBest(List<Best> best) {
        this.best = best;
    }

    public void setToday(List<Best> today) {
        this.today = today;
    }

    public List<Member> getMember() {
        return member;
    }

    public void setMember(List<Member> member) {
        this.member = member;
    }

    public List<Cat> getCat() {
        return cat;
    }

    public void setCat(List<Cat> cat) {
        this.cat = cat;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
