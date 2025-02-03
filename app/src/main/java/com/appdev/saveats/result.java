package com.appdev.saveats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class result extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedText = sharedPreferences.getString("USER_INPUT", "Default Value");
//        int budget = sharedPreferences.getInt("USER_INPUT", 0);
//        TextView textView = findViewById(R.id.textView);
//        textView.setText(savedText);



        // Create a list to hold the states of the normal checkboxes
        List<Boolean> checkboxStates1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            checkboxStates1.add(sharedPreferences.getBoolean("MEAL_PREFERENCE_CHECKBOX_" + i, false));
        }

        // Create a list to hold the states of the normal checkboxes
        List<Boolean> checkboxStates2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            checkboxStates2.add(sharedPreferences.getBoolean("DIETARY_RESTRICTIONS_CHECKBOX_" + i, false));
        }

        // Retrieve the state of the exclusive checkbox
        boolean isExclusiveChecked3 = sharedPreferences.getBoolean("ALLERGEN_CHECKBOX_EXCLUSIVE", false);

        // Create a list to hold the states of the normal checkboxes
        List<Boolean> checkboxStates3 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            checkboxStates3.add(sharedPreferences.getBoolean("ALLERGEN_CHECKBOX_NORMAL_" + i, false));
        }

        // Retrieve the state of the exclusive checkbox
        boolean isExclusiveChecked4 = sharedPreferences.getBoolean("NUTRITIONAL_CHECKBOX_EXCLUSIVE", false);

        // Create a list to hold the states of the normal checkboxes
        List<Boolean> checkboxStates4 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            checkboxStates4.add(sharedPreferences.getBoolean("NUTRITIONAL_CHECKBOX_NORMAL_" + i, false));
        }

        // Retrieve food items from the database or hardcoded
        List<FoodItem> foodItems = getFoodItemsFromDatabase();

        // Apply knapsack algorithm
        List<FoodItem> recommendedFoods = knapsackAlgorithm(12, foodItems, checkboxStates1);

        // Display recommended food items on the result page
        displayRecommendedFoods(recommendedFoods);

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(result.this, know_more.class);
                startActivity(intent);
            }
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();  // Clears all saved preferences
                editor.putBoolean("RESET_CHECKBOXES", true);  // Set flag to true
                editor.apply();

                Intent intent = new Intent(result.this, budget.class);
                startActivity(intent);
            }
        });
    }

    // Knapsack algorithm to select food items based on the budget and preferences
    public List<FoodItem> knapsackAlgorithm(int budget, List<FoodItem> foodItems, List<Boolean> preferences) {
        int n = foodItems.size();
        int[][] dp = new int[n + 1][budget + 1]; // dp[i][w] = max value achievable with i items and weight w

        // Calculate values based on user preferences (checkbox states)
        for (int i = 0; i < n; i++) {
            int value = 0;
            // Compare each food item with the user preferences
            if (preferences.contains(foodItems.get(i).mealType)) {
                value += 1; // Add score if meal type matches
            }
            if (preferences.contains(foodItems.get(i).dietaryRestrictions)) {
                value += 1; // Add score if dietary restriction matches
            }
            if (preferences.contains(foodItems.get(i).allergenRestrictions)) {
                value += 1; // Add score if allergen restriction matches
            }
            if (preferences.contains(foodItems.get(i).nutritionalPreference)) {
                value += 1; // Add score if nutritional preference matches
            }

            for (int w = budget; w >= foodItems.get(i).price; w--) {
                dp[i + 1][w] = Math.max(dp[i + 1][w], dp[i][w - foodItems.get(i).price] + value);
            }
        }

        // Backtrack to find the selected food items
        List<FoodItem> selectedItems = new ArrayList<>();
        int w = budget;
        for (int i = n; i > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                selectedItems.add(foodItems.get(i - 1));
                w -= foodItems.get(i - 1).price;
            }
        }

        return selectedItems;
    }

    // Mock method to fetch food items from the database or a predefined list
    private List<FoodItem> getFoodItemsFromDatabase() {
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(new FoodItem("Pizza", 12, "Snack", "Vegetarian", "Nut-Free", "High-Protein"));
        foodItems.add(new FoodItem("Burger", 8, "Bread", "None", "None", "None"));
        foodItems.add(new FoodItem("Pasta", 10, "Snacks", "Pescatarian", "Dairy-Free", "High-Protein"));
        // Add more food items as needed
        return foodItems;
    }

    // Method to display the recommended food items on the result page
    private void displayRecommendedFoods(List<FoodItem> recommendedFoods) {
        TextView resultTextView = findViewById(R.id.textView); // Assuming you have a TextView to display results
        StringBuilder resultText = new StringBuilder("Recommended Foods:\n");

        for (FoodItem food : recommendedFoods) {
            resultText.append(food.name).append(" - $").append(food.price).append("\n");
        }

        resultTextView.setText(resultText.toString());
    }
}

// FoodItem class to hold food details
class FoodItem {
    String name;
    int price;
    String mealType;
    String dietaryRestrictions;
    String allergenRestrictions;
    String nutritionalPreference;

    public FoodItem(String food_name, int food_price, String mealTypeScore, String dietaryScore, String allergenScore, String nutritionalScore) {
        name = food_name;
        price = food_price;
        mealType = mealTypeScore;
        dietaryRestrictions = dietaryScore;
        allergenRestrictions = allergenScore;
        nutritionalPreference = nutritionalScore;
    }
}

