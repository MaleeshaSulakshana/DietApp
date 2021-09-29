package com.example.dietapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dietapp.Adapters.WaterBalanceAdapter;
import com.example.dietapp.Model.WaterBalance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WaterBalanceActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private Button btnAdd, btnExitYes, btnExitNo;
    private Dialog exitDialog;
    private ImageButton btnBack;
    private TextView txtCalender, waterCount, waterPercentage;
    private TextInputLayout txtFieldWater;
    private ProgressBar progressBar;
    private ListView waterBalanceView;
    private String date;

    private DatabaseReference reference;
    private ArrayList<WaterBalance> arrayList = new ArrayList<>();

    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_balance);

//        Get layouts
        exitDialog = new Dialog(WaterBalanceActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        txtCalender = (TextView) findViewById(R.id.txtCalender);
        waterCount = (TextView) findViewById(R.id.waterCount);
        waterPercentage = (TextView) findViewById(R.id.waterPercentage);
        txtFieldWater = (TextInputLayout) findViewById(R.id.txtFieldWater);

        waterBalanceView = (ListView) findViewById(R.id.waterBalanceView);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

//        Get today date
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

//        Get water balance
        getWaterBalance();

        txtCalender.setText(date);



//        Show calender
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

        };

//        Show date picker
        txtCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(WaterBalanceActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        For go back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDashboard();
            }
        });

//        For add water balance
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWaterBalance();
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

//    Method for show date on text view
    private void updateDate() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtCalender.setText(sdf.format(calendar.getTime()));
        getWaterBalanceDetails();
    }

//    Method for open dashboard activity
    private void navigateToDashboard()
    {
        Intent intent = new Intent(WaterBalanceActivity.this, DashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for add water balance
    private void addWaterBalance()
    {
        String waterBalance = txtFieldWater.getEditText().getText().toString();
        if (waterBalance.isEmpty()) {
            Toast.makeText(WaterBalanceActivity.this, "Please enter water balance", Toast.LENGTH_SHORT).show();
        } else {

            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            reference = FirebaseDatabase.getInstance().getReference("waterBalance/"+date);
            reference.child(time).setValue(waterBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(WaterBalanceActivity.this, "Water balance add successful!", Toast.LENGTH_SHORT).show();
                        txtFieldWater.getEditText().setText("");
                    } else {
                        Toast.makeText(WaterBalanceActivity.this, "Water balance add not successful!", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }

//    Get water balance for the day
    private void getWaterBalance()
    {
        reference = FirebaseDatabase.getInstance().getReference("waterBalance/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double count = 0;
                double liters = 0.0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    if (date.equals(snapshot1.getKey()))
                    {
                        for (DataSnapshot dataSnapshot : snapshot1.getChildren()) {
                            String keyValue = dataSnapshot.getKey();
                            String value = dataSnapshot.getValue().toString();
                            count += Double.parseDouble(value);
                        }

                    }
                }
                liters = (count/1000f);
                double percentage = ((liters/2.7f)*100);
                waterCount.setText("Used "+new DecimalFormat("##.##").format(liters)+"L");
                waterPercentage.setText(String.valueOf(new DecimalFormat("##.##").format(percentage))+"%");
                progressBar.setProgress((int) percentage);
                getWaterBalanceDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    Show water balance data on listview
    private void getWaterBalanceDetails()
    {
        arrayList = new ArrayList<>();
        waterBalanceView.setAdapter(null);
        String date = txtCalender.getText().toString();
        reference = FirebaseDatabase.getInstance().getReference("waterBalance/");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String isExist = "no";
                WaterBalanceAdapter waterBalanceAdapter = new WaterBalanceAdapter(WaterBalanceActivity.this, R.layout.water_balance_row, arrayList);
                waterBalanceView.setAdapter(waterBalanceAdapter);

                for(DataSnapshot data: dataSnapshot.getChildren()){
                    if (data.getKey().equals(date)) {
                        isExist = "yes";

                        for(DataSnapshot dataSnapshot1: data.getChildren()){
                            arrayList.add(new WaterBalance(dataSnapshot1.getKey().toString(),
                                    dataSnapshot1.getValue().toString()+"ml"));
                        }

                    }
                }

                if (isExist.equals("no")){
                    waterBalanceView.setVisibility(View.GONE);
                    Toast.makeText(WaterBalanceActivity.this, "Water drink recodes not available "+date+" day!", Toast.LENGTH_SHORT).show();

                } else {
                    waterBalanceView.setVisibility(View.VISIBLE);
                    waterBalanceAdapter.notifyDataSetChanged();

                    ListAdapter adapter = waterBalanceView.getAdapter();
                    if (waterBalanceView != null) {
                        int totalHeight = 0;
                        for (int i = 0; i < adapter.getCount(); i++) {
                            View item= adapter.getView(i, null, waterBalanceView);
                            item.measure(0, 0);
                            totalHeight += item.getMeasuredHeight();
                        }

                        ViewGroup.LayoutParams params = waterBalanceView.getLayoutParams();
                        params.height = totalHeight + (waterBalanceView.getDividerHeight() * (adapter.getCount() - 1));
                        waterBalanceView.setLayoutParams(params);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

}