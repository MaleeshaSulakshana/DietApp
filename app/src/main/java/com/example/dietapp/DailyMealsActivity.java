package com.example.dietapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dietapp.Model.DailyMeals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyMealsActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private Button btnAdd, btnUpdate, btnDelete, btnClear, btnExitYes, btnExitNo;
    private Dialog exitDialog;
    private ImageButton btnBack;
    private TextView txtAddDateSelector, txtViewDateSelector;
    private TextInputLayout txtFieldBreakfast, txtFieldSnacks, txtFieldLunch, txtFieldDinner, txtViewBreakfast, txtViewSnacks, txtViewLunch, txtViewDinner;
    private LinearLayout mealsDetails;
    private String addSelectDate = "";
    private String viewSelectDate = "";

    private DatabaseReference reference;

    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_meals);

//        Get layouts
        exitDialog = new Dialog(DailyMealsActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnClear = (Button) findViewById(R.id.btnClear);

        txtAddDateSelector = (TextView) findViewById(R.id.txtAddDateSelector);
        txtViewDateSelector = (TextView) findViewById(R.id.txtViewDateSelector);

        txtFieldBreakfast = (TextInputLayout) findViewById(R.id.txtFieldBreakfast);
        txtFieldSnacks = (TextInputLayout) findViewById(R.id.txtFieldSnacks);
        txtFieldLunch = (TextInputLayout) findViewById(R.id.txtFieldLunch);
        txtFieldDinner = (TextInputLayout) findViewById(R.id.txtFieldDinner);

        mealsDetails = (LinearLayout) findViewById(R.id.mealsDetails);

        txtViewBreakfast = (TextInputLayout) findViewById(R.id.txtViewBreakfast);
        txtViewSnacks = (TextInputLayout) findViewById(R.id.txtViewSnacks);
        txtViewLunch = (TextInputLayout) findViewById(R.id.txtViewLunch);
        txtViewDinner = (TextInputLayout) findViewById(R.id.txtViewDinner);

        addSelectDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        txtAddDateSelector.setText(addSelectDate);

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

        DatePickerDialog.OnDateSetListener viewDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateViewDate();
            }
        };

//        Show date picker
        txtAddDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(DailyMealsActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        Show date picker
        txtViewDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(DailyMealsActivity.this, viewDate, calendar
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

//        For add schedule
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addScheduleBalance();
            }
        });

//        For clear text fields
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMealsClear();
            }
        });

//        For update meals details
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMeals();
            }
        });

//        For remove meals
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMealDetails();
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
        txtAddDateSelector.setText(sdf.format(calendar.getTime()));
    }

//    Method for show date on text view
    private void updateViewDate() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtViewDateSelector.setText(sdf.format(calendar.getTime()));
        getMealsDetails();
    }

//    Method for open dashboard activity
    private void navigateToDashboard()
    {
        Intent intent = new Intent(DailyMealsActivity.this, DashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for add schedule
    private void addScheduleBalance()
    {
        String breakfast = txtFieldBreakfast.getEditText().getText().toString();
        String snacks = txtFieldSnacks.getEditText().getText().toString();
        String lunch = txtFieldLunch.getEditText().getText().toString();
        String dinner = txtFieldDinner.getEditText().getText().toString();

        if (breakfast.isEmpty() || snacks.isEmpty() || lunch.isEmpty() || dinner.isEmpty()) {
            Toast.makeText(DailyMealsActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
        } else {

            String date = txtAddDateSelector.getText().toString();
            reference = FirebaseDatabase.getInstance().getReference("dailyMeals/");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String isInserted = "no";
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        if (data.getKey().equals(date)) {
                            isInserted = "yes";
                        }
                    }
//                    Validate is exist date in firebase and insert data
                    if (isInserted.equals("no")) {
                        DailyMeals dailyMeals = new DailyMeals(breakfast, snacks, lunch, dinner);
                        reference.child(date).setValue(dailyMeals).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(DailyMealsActivity.this, "Meal added successful!", Toast.LENGTH_SHORT).show();
                                    addMealsClear();
                                } else {
                                    Toast.makeText(DailyMealsActivity.this, "Meal added not successful!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    } else {
                        Toast.makeText(DailyMealsActivity.this, "Meals already added "+date+" day!", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

        }
    }

//    Clear add meal text fields
    private void addMealsClear()
    {
        txtFieldBreakfast.getEditText().setText("");
        txtFieldSnacks.getEditText().setText("");
        txtFieldLunch.getEditText().setText("");
        txtFieldDinner.getEditText().setText("");
    }

//    Show meals data on fields
    private void getMealsDetails()
    {
        String date = txtViewDateSelector.getText().toString();
        reference = FirebaseDatabase.getInstance().getReference("dailyMeals/");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String isExist = "no";
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    if (data.getKey().equals(date)) {
                        isExist = "yes";
                        mealsDetails.setVisibility(View.VISIBLE);
                        txtViewBreakfast.getEditText().setText(data.child("breakfast").getValue().toString());
                        txtViewSnacks.getEditText().setText(data.child("snacks").getValue().toString());
                        txtViewLunch.getEditText().setText(data.child("lunch").getValue().toString());
                        txtViewDinner.getEditText().setText(data.child("dinner").getValue().toString());
                    }
                }

                if (isExist.equals("no")){
                    mealsDetails.setVisibility(View.GONE);
                    Toast.makeText(DailyMealsActivity.this, "Meal not available "+date+" day!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

//    Method for update meals details
    private void updateMeals()
    {
        String date = txtViewDateSelector.getText().toString();
        String breakfast = txtViewBreakfast.getEditText().getText().toString();
        String snacks = txtViewSnacks.getEditText().getText().toString();
        String lunch = txtViewLunch.getEditText().getText().toString();
        String dinner = txtViewDinner.getEditText().getText().toString();

        if (breakfast.isEmpty() || snacks.isEmpty() || lunch.isEmpty() || dinner.isEmpty()) {
            Toast.makeText(DailyMealsActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();

        } else {
            DailyMeals dailyMeals = new DailyMeals(breakfast, snacks, lunch, dinner);
            reference = FirebaseDatabase.getInstance().getReference("dailyMeals/");
            reference.child(date).setValue(dailyMeals).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(DailyMealsActivity.this, "Meal details update successful!", Toast.LENGTH_SHORT).show();
                        viewMealsClear();
                    } else {
                        Toast.makeText(DailyMealsActivity.this, "Meal details update not successful!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

//    Method for remove meal details
    private void removeMealDetails()
    {
        String date = txtViewDateSelector.getText().toString();
        reference = FirebaseDatabase.getInstance().getReference("dailyMeals/"+date);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DailyMealsActivity.this, "Meal details remove successful!", Toast.LENGTH_SHORT).show();
                    viewMealsClear();
                } else {
                    Toast.makeText(DailyMealsActivity.this, "Meal details remove not successful!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

//    Clear view meal text fields
    private void viewMealsClear()
    {
        txtViewBreakfast.getEditText().setText("");
        txtViewSnacks.getEditText().setText("");
        txtViewLunch.getEditText().setText("");
        txtViewDinner.getEditText().setText("");
        mealsDetails.setVisibility(View.GONE);
    }


}