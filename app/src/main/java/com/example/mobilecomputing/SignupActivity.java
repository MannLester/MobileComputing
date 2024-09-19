package com.example.mobilecomputing;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, ageEditText, confirmPassword;
    private TextView birthdateEditText;
    private AutoCompleteTextView citySpinner, countrySpinner, zipcodeSpinner;
    private Spinner genderSpinner;
    private FirebaseFirestore db;

    private int selectedDay = 0;
    private int selectedMonth = 0;

    String[] countries = {"United States", "Canada", "United Kingdom", "Australia", "Germany", "India", "Brazil", "Japan", "South Africa", "Philippines" };
    String[][] cities = {
            {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"}, //United States
            {"Toronto", "Vancouver", "Montreal", "Calgary", "Edmonton", "Ottawa", "Quebec City", "Winnipeg", "Halifax", "Victoria"}, //Canada
            {"London", "Birmingham", "Manchester", "Glasgow", "Liverpool", "Edinburgh", "Leeds", "Bristol", "Sheffield", "Leicester"}, //United Kingdom
            {"Sydney", "Melborne", "Brisbane", "Perth", "Adelaide", "Canberra", "Hobart", "Darwin", "Gold Coast", "Newcastle"}, //Australia
            {"Berlin", "Hamburg", "Munich", "Cologne", "Frankfurt", "Stuttgart", "Dusseldorf", "Dortmund", "Essen", "Leipzig"}, //Germany
            {"Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Kolkata", "Pune", "Ahmedabad", "Jaipur", "Surat"}, //India
            {"Sao Paulo", "Rio de Janeiro", "Brasilia", "Salvador", "Fortaleza", "Belo Horizonte", "Curitiba", "Manaus", "Recife", "Porte Alegre"}, //Brazil
            {"Tokyo", "Yokohama", "Osaka", "Naggoya", "Sapporo", "Fukuoka", "Kyoto", "Kobe", "Hiroshima", "Sendai"}, //Japan
            {"Johannesburg", "Cape Town", "Durban", "Pretoria", "Port Elizabeth", "Bloemfontein", "East London", "Pietermaritzburg", "Kimberly", "Polokwane"}, //South Africa
            {"Manila", "Quezon City", "Cebu City", "Davao City", "Bacolod", "Iloilo City", "Cagayan de Oro", "Zamboanga City", "Batangas", "Baguio"} //Philippines
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        birthdateEditText = findViewById(R.id.birthdate);
        ageEditText = findViewById(R.id.age);
        citySpinner = findViewById(R.id.city);
        countrySpinner = findViewById(R.id.country);
        zipcodeSpinner = findViewById(R.id.zipcode);
        genderSpinner = findViewById(R.id.sex);
        Button signupButton = findViewById(R.id.register);
        confirmPassword = findViewById(R.id.confirm_password);

        setupAutoCompleteTextView(countrySpinner, countries);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        countrySpinner.setOnItemClickListener(((adapterView, view, i, l) -> updateCitySuggestions(i)));

        signupButton.setOnClickListener(v -> saveUserData());

        birthdateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                this.selectedDay = selectedDay;
                this.selectedMonth = selectedMonth;
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                birthdateEditText.setText(selectedDate);

                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int age = currentYear - selectedYear;

                ageEditText.setText(String.valueOf(age));
            }, year, month, day);

            datePickerDialog.show();
        });

        ageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed during the text change
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ageText = s.toString().trim();
                if (!TextUtils.isEmpty(ageText)) {
                    try {
                        int age = Integer.parseInt(ageText);
                        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                        int selectedYear = currentYear - age;

                        String calculatedBirthdate = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                        birthdateEditText.setText(calculatedBirthdate);
                    } catch (NumberFormatException e) {
                        ageEditText.setError("Invalid age input");
                    }
                }
            }
        });
    }

    private void setupAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, data);
        autoCompleteTextView.setAdapter(adapter);
    }

    private void updateCitySuggestions(int countryIndex){
        String[] selectedCities = cities[countryIndex];
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, selectedCities);
        citySpinner.setAdapter(cityAdapter);
    }

    private boolean isCityValidForCountry(String city, String country) {
        int countryIndex = -1;

        for (int i = 0; i < countries.length; i++) {
            if (countries[i].equalsIgnoreCase(country)) {
                countryIndex = i;
                break;
            }
        }

        if (countryIndex >= 0) {
            String[] validCities = cities[countryIndex];
            for (String validCity : validCities) {
                if (validCity.equalsIgnoreCase(city)) {
                    return true;
                }
            }
        }

        return false;
    }


    private void saveUserData() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String birthdate = birthdateEditText.getText().toString().trim();
        String ageField = ageEditText.getText().toString().trim();
        String city = citySpinner.getText().toString().trim();
        String country = countrySpinner.getText().toString().trim();
        String zipcode = zipcodeSpinner.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();
        String confirm = confirmPassword.getText().toString().trim();

        int age = Integer.parseInt(ageField);

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        if (TextUtils.isEmpty(birthdate)) {
            birthdateEditText.setError("Birthdate is required");
            return;
        }

        if (age <= 0) {
            ageEditText.setError("Age wrong input");
            return;
        } else if (TextUtils.isEmpty(ageField)) {
            ageEditText.setError("Age is required");
        }

        if (!confirm.equals(password)) {
            confirmPassword.setError("Passwords do not match");
            passwordEditText.setError("Passwords do not match");
            return;
        }

        int countryIndex = getCountryIndex(country);
        if (countryIndex == -1) {
            countrySpinner.setError("Invalid country");
            return;
        }

        String[] validCities = cities[countryIndex];
        boolean cityValid = false;
        for (String validCity : validCities) {
            if (validCity.equalsIgnoreCase(city)) {
                cityValid = true;
                break;
            }
        }

        if (!cityValid) {
            citySpinner.setError("That city does not exist in the chosen country");
            return;
        }

        // Create a new user with the provided data
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("password", password);
        user.put("birthdate", birthdate);
        user.put("age", age);
        user.put("city", city);
        user.put("country", country);
        user.put("zipcode", zipcode);
        user.put("gender", gender);

        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Username already exists
                            usernameEditText.setError("Username already taken. Please choose a different one.");
                        } else {

                            int age = Integer.parseInt(ageField);

                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username);
                            user.put("email", email);
                            user.put("password", password);
                            user.put("birthdate", birthdate);
                            user.put("age", age);
                            user.put("city", city);
                            user.put("country", country);
                            user.put("zipcode", zipcode);
                            user.put("gender", gender);

                            db.collection("users")
                                    .add(user)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Map<String, Object> account = new HashMap<>();
                                                account.put("username", username);
                                                account.put("password", password);
                                                account.put("cardCount", 0);
                                                account.put("newPlayer", true);

                                                db.collection("accounts")
                                                        .add(account)
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> accountTask) {
                                                                if (accountTask.isSuccessful()) {
                                                                    Toast.makeText(SignupActivity.this, "User registered and account saved!", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                                                    startActivity(intent);
                                                                } else {
                                                                    Toast.makeText(SignupActivity.this, "Registration failed: " + accountTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(SignupActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private int getCountryIndex(String country) {
        for (int i = 0; i < countries.length; i++) {
            if (countries[i].equals(country)) {
                return i;
            }
        }
        return -1;
    }
}
