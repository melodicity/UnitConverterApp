package com.example.unitconverterapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    // Widgets in the activity
    Spinner UnitTypeSpinner;
    Spinner SourceSpinner;
    Spinner DestinationSpinner;
    EditText ValueEditText;
    TextView ConvertedTextView;
    Button ConvertButton;

    // Conversion logic values
    double[] lengthConversionValues = {1, 2.54, 30.48, 91.44, 160934};
    double[] weightConversionValues = {1, 0.453592, 0.0283495, 907.185};
    // temperature conversion done in the performConversion method

    // Unit type enum
    enum UnitType
    {
        LENGTH,
        WEIGHT,
        TEMPERATURE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set widgets to their matching ids, using findViewById
        UnitTypeSpinner = findViewById(R.id.UnitTypeSpinner);
        SourceSpinner = findViewById(R.id.SourceSpinner);
        DestinationSpinner = findViewById(R.id.DestinationSpinner);
        ValueEditText = findViewById(R.id.ValueEditText);
        ConvertedTextView = findViewById(R.id.ConvertedTextView);
        ConvertButton = findViewById(R.id.ConvertButton);

        // Create an ArrayAdapter for the array of unit types, and a default spinner layout
        ArrayAdapter<CharSequence> UnitTypeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.units_array,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout for UnitTypeSpinner to use
        UnitTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        UnitTypeSpinner.setAdapter(UnitTypeAdapter);

        // Initial Setup of Unit Types (Length is default selection)
        setSpinnerUnits(UnitType.LENGTH);

        // UnitTypeSpinner updates the other 2 spinners
        UnitTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (UnitType.values()[position])
                {
                    case LENGTH:
                        setSpinnerUnits(UnitType.LENGTH);
                        break;
                    case WEIGHT:
                        setSpinnerUnits(UnitType.WEIGHT);
                        break;
                    case TEMPERATURE:
                        setSpinnerUnits(UnitType.TEMPERATURE);
                        break;
                }
            }

            // Must implement the abstract class onNothingSelected; even if not doing anything
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set ConvertButton to activate conversion on press
        ConvertButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ConvertUnits();
            }
        });
    }

    // Method to change the type of units shown by both the source and destination spinners
    private void setSpinnerUnits(UnitType type)
    {
        ArrayAdapter<CharSequence> adapter;
        // Switch statement for each type of unit case
        switch (type)
        {
            case LENGTH:
            default:
                adapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.length_array,
                        android.R.layout.simple_spinner_item
                );
                break;

            case WEIGHT:
                adapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.weight_array,
                        android.R.layout.simple_spinner_item
                );
                break;

            case TEMPERATURE:
                adapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.temperature_array,
                        android.R.layout.simple_spinner_item
                );
                break;
        }

        // Set both spinners to use the selected unit category's array
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SourceSpinner.setAdapter(adapter);
        DestinationSpinner.setAdapter(adapter);
    }

    private void ConvertUnits()
    {
        // Try statement in case of invalid user inputs (e.g. character that isn't a number)
        try
        {
            int unitTypeIndex = UnitTypeSpinner.getSelectedItemPosition();   // LENGTH, WEIGHT, TEMPERATURE
            int sourceUnit = SourceSpinner.getSelectedItemPosition();
            int destinationUnit = DestinationSpinner.getSelectedItemPosition();
            double inputValue = Double.parseDouble(ValueEditText.getText().toString());
            // converts EditText to string, and then to double
            double outputValue;

            double sourceFactor;
            double destinationFactor;

            // Depending on the type of unit (length, width, temp), perform different calculations
            switch (UnitType.values()[unitTypeIndex])
            {
                case LENGTH:
                default:
                    sourceFactor = lengthConversionValues[sourceUnit];
                    destinationFactor = lengthConversionValues[destinationUnit];
                    outputValue = inputValue * sourceFactor / destinationFactor;
                    break;

                case WEIGHT:
                    sourceFactor = weightConversionValues[sourceUnit];
                    destinationFactor = weightConversionValues[destinationUnit];
                    outputValue = inputValue * sourceFactor / destinationFactor;
                    break;

                case TEMPERATURE:
                    // Nested switch statement! Since temp. conversion is more complicated
                    switch (sourceUnit)
                    {
                        case 0:  // If the source is Celsius
                        default:
                            if (destinationUnit == 0) outputValue = inputValue;  // Celsius to Celsius
                            else if (destinationUnit == 1) outputValue = (inputValue * 1.8) + 32;  // Celsius to Fahrenheit
                            else outputValue = inputValue + 273.15;  // Celsius to Kelvin
                            break;

                        case 1:  // If the source is Fahrenheit
                            if (destinationUnit == 0) outputValue = (inputValue - 32) / 1.8;  // Fahrenheit to Celsius
                            else if (destinationUnit == 1) outputValue = inputValue;  // Fahrenheit to Fahrenheit
                            else outputValue = (inputValue + 459.67) * 5 / 9;  // Fahrenheit to Kelvin
                            break;

                        case 2:  // If the source is Kelvin
                            if (destinationUnit == 0) outputValue = inputValue - 273.15;  // Kelvin to Celsius
                            else if (destinationUnit == 1)outputValue = (inputValue * 9 / 5) - 459.67;  // Kelvin to Fahrenheit
                            else outputValue = inputValue;  // Kelvin to Kelvin
                            break;
                    }

                    break;
            }

            // Change the ConvertedTextView to show the new output value
            ConvertedTextView.setText(String.valueOf(outputValue));
        } catch (Exception e)
        {
            ConvertedTextView.setText(R.string.input_not_valid);
        }

    }
}