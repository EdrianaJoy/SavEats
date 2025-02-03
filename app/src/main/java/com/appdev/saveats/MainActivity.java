package com.appdev.saveats;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView excelDataText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the TextView
        excelDataText = findViewById(R.id.excelDataText);

        // Read Excel data and display it
        String excelContent = ExcelReader.readExcelFile(this);
        excelDataText.setText(excelContent);
    }
}
