package com.example.dietapp.Model;

public class WaterBalance {

    String time, qty;

    public WaterBalance(String time, String qty) {
        this.time = time;
        this.qty = qty;
    }

    public String getTime() {
        return time;
    }

    public String getQty() {
        return qty;
    }
}
