package com.instaporters.instaporters;

/**
 * Created by maheshkumar on 3/5/16.
 */
public class FeedItem {
    String time;
    int paymentPerPorter;
    String locDetails;

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
