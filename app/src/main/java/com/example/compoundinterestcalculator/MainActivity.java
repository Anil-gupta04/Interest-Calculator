package com.example.compoundinterestcalculator;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {

    private EditText editTextPrincipal, editTextRate;
    private Button buttonSelectStartDate, buttonSelectEndDate, buttonCalculate, buttonClear;
    private TextView textViewStartDate, textViewEndDate, textViewResult, textViewTotalAmount;
    private LocalDate startDate, endDate;
    private Spinner spinnerFrequency;
    private String frequency = "Yearly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextPrincipal = findViewById(R.id.editTextPrincipal);
        editTextRate = findViewById(R.id.editTextRate);
        buttonSelectStartDate = findViewById(R.id.buttonSelectStartDate);
        buttonSelectEndDate = findViewById(R.id.buttonSelectEndDate);
        textViewStartDate = findViewById(R.id.textViewStartDate);
        textViewEndDate = findViewById(R.id.textViewEndDate);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonClear = findViewById(R.id.buttonClear);
        textViewResult = findViewById(R.id.textViewResult);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        LinearLayout layout = findViewById(R.id.linearLayout);
        layout.setBackgroundColor(Color.rgb(239, 238, 232));

        // Setup spinner for frequency selection
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

        buttonSelectStartDate.setOnClickListener(v -> showDatePickerDialog(true));
        buttonSelectEndDate.setOnClickListener(v -> showDatePickerDialog(false));
        buttonCalculate.setOnClickListener(v -> calculateCompoundInterest());
        buttonClear.setOnClickListener(v -> clearFields());
    }

    private void showDatePickerDialog(boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    LocalDate date = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                    if (isStartDate) {
                        startDate = date;
                        textViewStartDate.setText(date.toString());
                    } else {
                        endDate = date;
                        textViewEndDate.setText(date.toString());
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void calculateCompoundInterest() {
        String principalStr = editTextPrincipal.getText().toString();
        String rateStr = editTextRate.getText().toString();

        if (principalStr.isEmpty() || rateStr.isEmpty() || startDate == null || endDate == null) {
            textViewResult.setText("Please enter all fields");
            return;
        }

        double principal = Double.parseDouble(principalStr);
        double rate = Double.parseDouble(rateStr) / 100; // Convert to decimal

        Period period = Period.between(startDate, endDate);
        int totalMonths = period.getYears() * 12 + period.getMonths();

        double totalAmount;

        if (frequency.equals("Monthly")) {
            // Monthly compounding
            totalAmount = principal + (principal * rate * totalMonths);
        } else {
            // Yearly compounding
            double years = totalMonths / 12.0;
            totalAmount = principal + (principal * rate * years);
        }

        double compoundInterest = totalAmount - principal;
        textViewResult.setText(String.format("Interest: NPR. %.2f", compoundInterest));
        textViewTotalAmount.setText(String.format("Total Amount: NPR. %.2f", totalAmount));
    }
    private void clearFields() {
        editTextPrincipal.setText("");
        editTextRate.setText("");
        textViewStartDate.setText("");
        textViewEndDate.setText("");
        textViewResult.setText("");
        textViewTotalAmount.setText("");
        spinnerFrequency.setSelection(0); // Reset the spinner to "Yearly"
        startDate = null;
        endDate = null;
    }
}
