package com.example.dietapp.Model;

public class DailyMeals {

    String breakfast, snacks, lunch, dinner;

    public DailyMeals(String breakfast, String snacks, String lunch, String dinner) {
        this.breakfast = breakfast;
        this.snacks = snacks;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public String getSnacks() {
        return snacks;
    }

    public String getLunch() {
        return lunch;
    }

    public String getDinner() {
        return dinner;
    }
}
