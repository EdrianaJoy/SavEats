package com.appdev.saveats;

import android.content.Context;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.io.IOException;

public class ExcelReader {
    public static String readExcelFile(Context context) {
        StringBuilder result = new StringBuilder();

        try {
            // Open the Excel file from assets
            InputStream is = context.getAssets().open("database.xlsx");
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0); // First sheet

            for (Row row : sheet) {
                for (Cell cell : row) {
                    result.append(cell.toString()).append(" | ");
                }
                result.append("\n");
            }

            workbook.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            result.append("Error reading Excel file.");
        }

        return result.toString();
    }
}
