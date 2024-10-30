package com.example.mobilecomputing;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CalculatorActivity extends AppCompatActivity {

    private TextView display1;  // For showing the current expression
    private TextView display2;  // For showing the result
    private String currentExpression = "";  // The expression being built
    private double lastResult = 0;  // The last computed result
    private boolean isResultDisplayed = false;

    // List to store history
    private List<String> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        // Initialize the displays
        display1 = findViewById(R.id.display);
        display2 = findViewById(R.id.display2);

        // Initialize buttons
        Button[] numberButtons = {
                findViewById(R.id.button0), findViewById(R.id.button1), findViewById(R.id.button2),
                findViewById(R.id.button3), findViewById(R.id.button4), findViewById(R.id.button5),
                findViewById(R.id.button6), findViewById(R.id.button7), findViewById(R.id.button8),
                findViewById(R.id.button9)
        };

        Button addButton = findViewById(R.id.buttonAdd);
        Button minusButton = findViewById(R.id.buttonSubtract);
        Button multiplyButton = findViewById(R.id.buttonMultiply);
        Button divideButton = findViewById(R.id.buttonDivide);
        Button equalsButton = findViewById(R.id.buttonEquals);
        Button acButton = findViewById(R.id.buttonAC);
        Button clearButton = findViewById(R.id.buttonClear);
        Button percentButton = findViewById(R.id.buttonPercent);
        Button periodButton = findViewById(R.id.period_button);
        Button historyButton = findViewById(R.id.buttonHistory);

        // Set onClickListeners for number buttons
        for (Button button : numberButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button btn = (Button) view;
                    appendToExpression(btn.getText().toString(), true);
                }
            });
        }

        // Set onClickListeners for operator buttons
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendToExpression("+", false);
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendToExpression("-", false);
            }
        });

        multiplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendToExpression("*", false);
            }
        });

        divideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendToExpression("/", false);
            }
        });

        periodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendToExpression(".", true);
            }
        });

        // Equals button logic
        equalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    lastResult = evaluateExpression(currentExpression);
                    String resultString = String.valueOf(lastResult);
                    display2.setText(resultString);
                    isResultDisplayed = true;

                    // Add to history
                    addToHistory(currentExpression + " = " + resultString);
                } catch (Exception e) {
                    display2.setText("Error");
                    isResultDisplayed = true;
                }
            }
        });

        // AC (All Clear) button logic
        acButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentExpression = "";
                lastResult = 0;  // Reset last result
                display1.setText("");
                display2.setText("");
                isResultDisplayed = false;
            }
        });

        // Clear button logic (backspace)
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentExpression.isEmpty()) {
                    currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
                    display1.setText(currentExpression);
                }
            }
        });

        // Percent button logic
        percentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentExpression.isEmpty()) {
                    double value = Double.parseDouble(currentExpression);
                    value = value / 100;
                    currentExpression = String.valueOf(value);
                    display1.setText(currentExpression);
                }
            }
        });

        // History button logic
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistoryDialog();
            }
        });
    }

    // Append characters to the expression and display it
    private void appendToExpression(String string, boolean canClear) {
        if (isResultDisplayed) {
            currentExpression = String.valueOf(lastResult);  // Use the last result as the base for new expressions
            isResultDisplayed = false;
        }

        if (canClear) {
            display2.setText("");  // Clear the result when typing a new number
        }

        currentExpression += string;
        display1.setText(currentExpression);
    }

    // Evaluate the expression
    private double evaluateExpression(String expression) {
        // A simple expression evaluator
        try {
            return new ExpressionParser().parse(expression);
        } catch (Exception e) {
            return 0;
        }
    }

    // Add expression and result to history
    private void addToHistory(String entry) {
        if (historyList.size() == 5) {
            historyList.remove(0);  // Remove the oldest entry if we exceed the limit
        }
        historyList.add(entry);
    }

    // Show the history dialog
    private void showHistoryDialog() {
        String[] historyArray = historyList.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("History")
                .setItems(historyArray, (dialog, which) -> {
                    String selectedHistory = historyArray[which];
                    String[] parts = selectedHistory.split(" = ");
                    currentExpression = parts[0];
                    display1.setText(currentExpression);
                    display2.setText(parts[1]);
                })
                .setPositiveButton("OK", null)
                .show();
    }

    // A simple expression parser
    private static class ExpressionParser {
        public double parse(String expression) {
            try {
                return new Object() {
                    int pos = -1, ch;

                    void nextChar() {
                        ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                    }

                    boolean eat(int charToEat) {
                        while (ch == ' ') nextChar();
                        if (ch == charToEat) {
                            nextChar();
                            return true;
                        }
                        return false;
                    }

                    double parse() {
                        nextChar();
                        double x = parseExpression();
                        if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                        return x;
                    }

                    double parseExpression() {
                        double x = parseTerm();
                        for (; ; ) {
                            if (eat('+')) x += parseTerm(); // addition
                            else if (eat('-')) x -= parseTerm(); // subtraction
                            else return x;
                        }
                    }

                    double parseTerm() {
                        double x = parseFactor();
                        for (; ; ) {
                            if (eat('*')) x *= parseFactor(); // multiplication
                            else if (eat('/')) x /= parseFactor(); // division
                            else return x;
                        }
                    }

                    double parseFactor() {
                        if (eat('+')) return parseFactor(); // unary plus
                        if (eat('-')) return -parseFactor(); // unary minus

                        double x;
                        int startPos = this.pos;
                        if (eat('(')) { // parentheses
                            x = parseExpression();
                            eat(')');
                        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                            x = Double.parseDouble(expression.substring(startPos, this.pos));
                        } else {
                            throw new RuntimeException("Unexpected: " + (char) ch);
                        }

                        return x;
                    }
                }.parse();
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
