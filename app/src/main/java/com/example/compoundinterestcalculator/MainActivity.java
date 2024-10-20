package com.example.compoundinterestcalculator;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextPrincipal, editTextRate;
    private Button buttonCalculate, buttonClear;
    private TextView textViewResult, textViewTotalAmount;

    // **New date spinners**
    private Spinner spinnerStartDay, spinnerStartMonth, spinnerStartYear;
    private Spinner spinnerEndDay, spinnerEndMonth, spinnerEndYear;


    private Spinner spinnerFrequency;
    private String frequency = "Yearly";

    // Constants for frequency options
    private static final String MONTHLY = "Monthly";
    private static final String YEARLY = "Yearly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        editTextPrincipal = findViewById(R.id.editTextPrincipal);
        editTextRate = findViewById(R.id.editTextRate);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonClear = findViewById(R.id.buttonClear);
        textViewResult = findViewById(R.id.textViewResult);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);

        // **Initialize date spinners**
        spinnerStartDay = findViewById(R.id.spinnerStartDay);
        spinnerStartMonth = findViewById(R.id.spinnerStartMonth);
        spinnerStartYear = findViewById(R.id.spinnerStartYear);
        spinnerEndDay = findViewById(R.id.spinnerEndDay);
        spinnerEndMonth = findViewById(R.id.spinnerEndMonth);
        spinnerEndYear = findViewById(R.id.spinnerEndYear);

        // **Setup spinners for day, month, year**
        setupDateSpinners();

        // Setup interest frequency spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.interest_frequencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(adapter);
        spinnerFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                frequency = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        buttonCalculate.setOnClickListener(v -> calculateCompoundInterest());
        buttonClear.setOnClickListener(v -> clearFields());
    }

    // **Method to setup date spinners**
    private void setupDateSpinners() {

        Calendar calendar = Calendar.getInstance();

        // Get current date
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH); // 0 = Jan, 11 = Dec
        int currentYear = calendar.get(Calendar.YEAR);

        // **Days (1-31)**
        List<Integer> days = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            days.add(i);
        }

        ArrayAdapter<Integer> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartDay.setAdapter(dayAdapter);
        spinnerEndDay.setAdapter(dayAdapter);
        spinnerStartDay.setSelection(currentDay - 1);  // Set current day
        spinnerEndDay.setSelection(currentDay - 1);    // Set default to the current day

        // **Months (1-12)**
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartMonth.setAdapter(monthAdapter);
        spinnerEndMonth.setAdapter(monthAdapter);
        spinnerStartMonth.setSelection(currentMonth);  // Set current month
        spinnerEndMonth.setSelection(currentMonth);    // Set default to the current month

        /// **Years for start date (1990 to current year only)**
        List<Integer> startYears = new ArrayList<>();
        for (int i = currentYear; i >= 1990; i--) {  // **No future years for start date**
            startYears.add(i);
        }
        ArrayAdapter<Integer> startYearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, startYears);
        startYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartYear.setAdapter(startYearAdapter);
        spinnerStartYear.setSelection(0);  // Set current year for start date

        // **Years for end date (1990 to 2035)**
        List<Integer> endYears = new ArrayList<>();
        for (int i = 2035; i >= 1990; i--) {  // **Allow future years for end date**
            endYears.add(i);
        }
        ArrayAdapter<Integer> endYearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, endYears);
        endYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEndYear.setAdapter(endYearAdapter);
        spinnerEndYear.setSelection(endYears.indexOf(currentYear));  // Set current year for end date
    }

    // **Method to calculate compound interest**
    private void calculateCompoundInterest() {
        String principalStr = editTextPrincipal.getText().toString();
        String rateStr = editTextRate.getText().toString();

        // Input validation
        if (principalStr.isEmpty() || rateStr.isEmpty()) {
            textViewResult.setText("Please enter all fields");
            return;
        }

        double principal;
        double rate;

        try {
            principal = Double.parseDouble(principalStr);
            rate = Double.parseDouble(rateStr) / 100; // Convert to decimal
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        // **Retrieve the start and end dates from the spinners**
        int startDay = (int) spinnerStartDay.getSelectedItem();
        int startMonth = spinnerStartMonth.getSelectedItemPosition(); // Get position for month
        int startYear = (int) spinnerStartYear.getSelectedItem();
        int endDay = (int) spinnerEndDay.getSelectedItem();
        int endMonth = spinnerEndMonth.getSelectedItemPosition(); // Get position for month
        int endYear = (int) spinnerEndYear.getSelectedItem();

        // Validate dates (you can add additional validation logic here if needed)
        if (endYear < startYear || (endYear == startYear && endMonth < startMonth) ||
                (endYear == startYear && endMonth == startMonth && endDay < startDay)) {
            Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
            return;
        }

        // **Additional validation for yearly frequency**
        if (frequency.equals(YEARLY)) {
            if (endYear - startYear < 1 || (endYear == startYear && endMonth <= startMonth && endDay < startDay)) {
                Toast.makeText(this, "For yearly interest, the duration must be at least one year", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // **Date period calculation**
        int totalMonths = (endYear - startYear) * 12 + (endMonth - startMonth);

        double totalAmount;
        if (frequency.equals(MONTHLY)) {
            // Monthly compounding
            totalAmount = principal * Math.pow((1 + rate), totalMonths);
        } else {
            // Yearly compounding
            double years = totalMonths / 12.0; // Convert to annual rate
            totalAmount = principal * Math.pow((1 + rate), years);
        }

        double compoundInterest = totalAmount - principal;
        textViewResult.setText(String.format("Interest: NPR. %.2f", compoundInterest));
        textViewTotalAmount.setText(String.format("Total Amount: NPR. %.2f", totalAmount));
    }
    private void clearFields() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Fields")
                .setMessage("Are you sure you want to clear all fields?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    editTextPrincipal.setText("");
                    editTextRate.setText("");
                    textViewResult.setText("");
                    textViewTotalAmount.setText("");
                    spinnerFrequency.setSelection(0); // Reset the spinner to "Yearly"
                    spinnerStartDay.setSelection(0);
                    spinnerStartMonth.setSelection(0);
                    spinnerStartYear.setSelection(0);
                    spinnerEndDay.setSelection(0);
                    spinnerEndMonth.setSelection(0);
                    spinnerEndYear.setSelection(0);
                })
                .setNegativeButton("No", null) // Close the dialog if the user clicks "No"
                .show();
    }
}
