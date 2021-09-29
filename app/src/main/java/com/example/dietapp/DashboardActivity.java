package com.example.dietapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private Button btnExitYes, btnExitNo;
    private Dialog exitDialog;
    private RelativeLayout btnWaterBalance, btnDailyMeals;
    private TextView txtDay, txtMonth, txtYear;
    private String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

//        Get layouts
        exitDialog = new Dialog(DashboardActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnWaterBalance = (RelativeLayout) findViewById(R.id.btnWaterBalance);
        btnDailyMeals = (RelativeLayout) findViewById(R.id.btnDailyMeals);

        txtDay = (TextView) findViewById(R.id.txtDay);
        txtMonth = (TextView) findViewById(R.id.txtMonth);
        txtYear = (TextView) findViewById(R.id.txtYear);

//        Set today date
        txtDay.setText((String)android.text.format.DateFormat.format("dd", new Date()));
        txtMonth.setText((String)android.text.format.DateFormat.format("MMMM", new Date()));
        txtYear.setText((String)android.text.format.DateFormat.format("yyyy", new Date()));

//        For open water balance
        btnWaterBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToWaterBalance();
            }
        });

//        For open daily meals
        btnDailyMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDailyMeals();
            }
        });

    }

//    Hide status bar and navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

//    Tap to close app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        exitDialog.show();

        btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }

//    Method for open water balance activity
    private void navigateToWaterBalance()
    {
        Intent intent = new Intent(DashboardActivity.this, WaterBalanceActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open daily meals activity
    private void navigateToDailyMeals()
    {
        Intent intent = new Intent(DashboardActivity.this, DailyMealsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}