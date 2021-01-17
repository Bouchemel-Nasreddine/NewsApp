package com.example.newsapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class PreferencesActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";

    MaterialButton back, reset, apply;
    DatePickerDialog dateDialog;
    EditText page_size_view;
    TextView datePicker1, datePicker2;
    Spinner sectionSpinner;
    String order, page_size, section, from_date, to_date;
    int sectionID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_main);

        loadPrefs();

        back = findViewById(R.id.settingBackBtn);
        apply = findViewById(R.id.apply);
        reset = findViewById(R.id.reset);
        datePicker1 = findViewById(R.id.datePickerView1);
        datePicker2 = findViewById(R.id.datePickerView2);
        page_size_view = findViewById(R.id.edit_text);
        sectionSpinner = findViewById(R.id.section_spinner);

        page_size_view.setFilters(new InputFilter[]{new InputFilterMinMax(this, "0", "200")});

        page_size_view.setText(page_size);

        sectionSpinner.setSelection(sectionID);

        datePicker1.setText(from_date);
        datePicker2.setText(to_date);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                savePrefs();
                Intent intent = new Intent(PreferencesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        datePicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int mYear = stringToDate((String) datePicker1.getText())[0];
                int mMonth = stringToDate((String) datePicker1.getText())[1];
                int mDay = stringToDate((String) datePicker1.getText())[2];

                dateDialog = new DatePickerDialog(PreferencesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override

                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        datePicker1.setText(year + "-" + month + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);

                dateDialog.show();
            }
        });

        datePicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int mYear = stringToDate((String) datePicker2.getText())[0];
                int mMonth = stringToDate((String) datePicker2.getText())[1];
                int mDay = stringToDate((String) datePicker2.getText())[2];

                dateDialog = new DatePickerDialog(PreferencesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override

                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        datePicker2.setText(year + "-" + month + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);

                dateDialog.show();

            }
        });


    }

    public int[] stringToDate(String date) {
        String[] parts = date.split("-");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2])};
    }

    private void savePrefs() {

        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putString("page_size", String.valueOf(page_size_view.getText()));
        editor.putString("section", sectionSpinner.getSelectedItem().toString().toLowerCase());
        editor.putInt("sectionID", sectionSpinner.getSelectedItemPosition());
        editor.putString("from_date", (String) datePicker1.getText());
        editor.putString("to_date", (String) datePicker2.getText());
        editor.apply();


    }


    private void loadPrefs() {
        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);


        order = sharedPrefs.getString("order", getString(R.string.relevance));
        page_size = sharedPrefs.getString("page_size", "100");
        section = sharedPrefs.getString("section", getString(R.string.all));
        sectionID = sharedPrefs.getInt("sectionID", 0);
        from_date = sharedPrefs.getString("from_date", "2020-01-01");
        to_date = sharedPrefs.getString("to_date", "2020-01-12");
    }

}
