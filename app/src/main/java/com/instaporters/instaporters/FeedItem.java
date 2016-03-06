package com.instaporters.instaporters;

/**
 * Created by maheshkumar on 3/5/16.
 */
public class FeedItem {
    int jobId;

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    String time;
    int paymentPerPorter;
    String locDetails;
    double distance;
    double lat;

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    double lng;

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {

        return distance;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPaymentPerPorter(int paymentPerPorter) {
        this.paymentPerPorter = paymentPerPorter;
    }

    public void setLocDetails(String locDetails) {
        this.locDetails = locDetails;
    }

    public String getTime() {

        return time;
    }

    public int getPaymentPerPorter() {
        return paymentPerPorter;
    }

    public String getLocDetails() {
        return locDetails;
    }
}
