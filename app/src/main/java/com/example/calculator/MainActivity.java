package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity
{
    private MaterialButton btMc, btnMPlus, btnMMinus, btnMr, btnClear, btnPlusMinus, btnDivide, btnSeven, btnEight, btnNine,
    btnMultiplication, btnFour, btnFive, btnSix, btnOne, btnTwo, btnThree, btnPercent, btnZero, btnDot, btnMinus, btnPlus, btnEqual;

    private ShapeableImageView btnBack;

    private TextView txtResult, txtStoreResult;

    private RelativeLayout mainRelLayout;

    private final String operatorsWithoutPercent = "/*+-";

    private final String operatorsWithoutMinus = "/*+%";

    private final String operators = "/*+-%";

    private final String digits = "0123456789";

    private boolean resultSetByEqualSignOrMrButton;

    private void initData()
    {
        btMc = findViewById(R.id.btnMc);
        btnMPlus = findViewById(R.id.btnMPlus);
        btnMMinus = findViewById(R.id.btnMMinus);
        btnMr = findViewById(R.id.btnMr);
        btnClear = findViewById(R.id.btnClear);
        btnPlusMinus = findViewById(R.id.btnPlusMinus);
        btnBack = findViewById(R.id.btnBack);
        btnDivide = findViewById(R.id.btnDivide);
        btnSeven = findViewById(R.id.btnSeven);
        btnEight = findViewById(R.id.btnEight);
        btnNine = findViewById(R.id.btnNine);
        btnMultiplication = findViewById(R.id.btnMultiplication);
        btnFour = findViewById(R.id.btnFour);
        btnFive = findViewById(R.id.btnFive);
        btnSix = findViewById(R.id.btnSix);
        btnMinus = findViewById(R.id.btnMinus);
        btnOne = findViewById(R.id.btnOne);
        btnTwo = findViewById(R.id.btnTwo);
        btnThree = findViewById(R.id.btnThree);
        btnPlus = findViewById(R.id.btnPlus);
        btnPercent = findViewById(R.id.btnPercent);
        btnZero = findViewById(R.id.btnZero);
        btnDot = findViewById(R.id.btnDot);
        btnEqual = findViewById(R.id.btnEqual);
        txtResult = findViewById(R.id.txtResult);
        txtStoreResult = findViewById(R.id.txtStoreResult);
        mainRelLayout = findViewById(R.id.mainRelLayout);
    }

    private boolean isADigit(String expression)
    {
       return digits.contains(expression);
    }

    /*private boolean digitAfterAnOperator(String expression)
    {
        String result = txtResult.getText().toString();
        if (result.isEmpty())
            return false;

        return result.charAt(result.length() - 1) != '-' && isADigit(expression) && operators.contains(String.valueOf(result.charAt(result.length() - 1)));
    }*/

    private String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d", (long)d);
        else
            return String.format("%s", d);
    }

    private void updateTxtStoreResult()
    {
        String expressionOnScreen = txtResult.getText().toString();
        if (expressionOnScreen.isEmpty())
        {
            txtStoreResult.setText("");
            return;
        }

        char lastCharacter = expressionOnScreen.charAt(expressionOnScreen.length() - 1);
        String lastCharacterAsString = String.valueOf(lastCharacter);
        // if there hasn't been an operator yet or there is only one argument for the binary operators,
        // don't show anything on the other TextView
        if (operators.contains(lastCharacterAsString))
            return;

        try
        {
            Expression expression = new ExpressionBuilder(expressionOnScreen).build();
            double result = expression.evaluate();
            String formattedResult = fmt(result);
            txtStoreResult.setText(formattedResult);
        }
        catch (Exception e)
        {
            txtStoreResult.setText(getResources().getString(R.string.error)); // print error on the second TextView when exception occurred while evaluating expression
        }

    }

    private String clearFromZeroesBeforeDigits(String result) // clears zeroes before digits, for example: 06 becomes 6
    {
        StringBuilder newResult = new StringBuilder();
        for (int i=0; i<result.length(); i++)
        {
            if (i + 1 < result.length() && result.charAt(i) == '0' && isADigit(String.valueOf(result.charAt(i + 1))) && result.charAt(i + 1) != '0')
                continue;

            newResult.append(result.charAt(i));
        }

        return newResult.toString();
    }

    private void updateResult(String expressionToAdd)
    {
        String result = txtResult.getText().toString();
        if (operatorsWithoutMinus.contains(expressionToAdd) && result.isEmpty()) // only allow minus and all the digits to be placed without anything else on the screen
            return;

        if (operators.contains(expressionToAdd)) // if there is one of these operators at the end, then overwrite it with the operator that the user wants to add
        {
            if (result.isEmpty() && expressionToAdd.equals("-"))
            {
                txtResult.setText("-");
                return;
            }

            int resultLastIndex = result.length() - 1;
            char lastCharacter = result.charAt(resultLastIndex);
            if (operators.contains(String.valueOf(lastCharacter)))
            {
                overwriteOperator(expressionToAdd.charAt(0), resultLastIndex);
                return;
            }
        }

        if (clearAndReset() && isADigit(expressionToAdd)) // if result on the screen came from user clicking equality sign and then next expression is a digit, then clear the screen
            result = "";

        result += expressionToAdd;
        result = clearFromZeroesBeforeDigits(result);
        txtResult.setText(result);
        updateTxtStoreResult(); // update second TextView
    }

   /* private boolean overwriteZero(String expression)
    {
        String result = txtResult.getText().toString();
        if (result.equals("0"))
        {
            result = expression;
            txtResult.setText(result);
            return true;
        }

        return false;
    }*/

    private void overwriteOperator(char operator, int resultLastIndex)
    {
        StringBuilder result = new StringBuilder(txtResult.getText().toString());
        if (result.length() == 0)
            return;

        result.setCharAt(resultLastIndex, operator);
        txtResult.setText(result.toString());
    }

    private boolean clearAndReset() // clears screen and resets resultSetByEqualSignOrMrButton boolean
    {
        if (resultSetByEqualSignOrMrButton)
        {
            btnClear.performClick();
            resultSetByEqualSignOrMrButton = false;
            return true;
        }

        return false;
    }

    SharedPreferences.Editor putDouble(final SharedPreferences.Editor editor, final String key, final double value)
    {
        return editor.putLong(key, Double.doubleToRawLongBits(value));
    }

    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue)
    {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    private void setNewValueInMemory(boolean plus)
    {
        String value = txtStoreResult.getText().toString();
        String expression = txtResult.getText().toString();
        double result;
        double valueInMemory;
        if (!value.isEmpty()) // if the value is in the second TextView
        {
            String key = MainActivity.this.getResources().getString(R.string.key);
            result = Double.parseDouble(value);
            SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(key, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            valueInMemory = getDouble(sharedPreferences, key, 0);
            if (plus)
                valueInMemory += result;
            else
                valueInMemory -= result;

            editor = putDouble(editor, key, valueInMemory);
            editor.apply();
        }
        else if (!expression.isEmpty()) // if the value is in the first TextView
        {
            String key = MainActivity.this.getResources().getString(R.string.key);
            result = Double.parseDouble(expression);
            SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(key, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            valueInMemory = getDouble(sharedPreferences, key, 0);
            if (plus)
                valueInMemory += result;
            else
                valueInMemory -= result;

            editor = putDouble(editor, key, valueInMemory);
            editor.apply();
        }
    }

    private void setOnClickListeners()
    {
        btnSeven.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                updateResult("7");
            }
        });

        btnEight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("8");
            }
        });

        btnNine.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("9");
            }
        });

        btnFour.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("4");
            }
        });

        btnFive.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("5");
            }
        });

        btnSix.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("6");
            }
        });

        btnOne.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("1");
            }
        });

        btnTwo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("2");
            }
        });

        btnThree.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("3");
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                txtResult.setText("");
                updateTxtStoreResult();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String result = txtResult.getText().toString();
                if (!result.isEmpty())
                    result = result.substring(0 , result.length() - 1);

                txtResult.setText(result);
                updateTxtStoreResult();
            }
        });

        btnPlusMinus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String result = txtResult.getText().toString();
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("+");
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("-");
            }
        });

        btnMultiplication.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("*");
            }
        });

        btnDivide.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("/");
            }
        });

        btnPercent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("%");
            }
        });

        btnDot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String result = txtResult.getText().toString();
                if (!result.contains("."))
                    updateResult(".");
            }
        });

        btnZero.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateResult("0");
            }
        });

        btnEqual.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // if there is an error on the second TextView, don't do the animation
                String result = txtStoreResult.getText().toString();
                String error = getResources().getString(R.string.error);
                if (result.equals(error))
                    return;

                Animation slideUp = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_down);
                slideUp.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        String result = txtStoreResult.getText().toString();
                        if (result.isEmpty())
                            return;

                        txtResult.setText(result);
                        txtStoreResult.setText("");
                        resultSetByEqualSignOrMrButton = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {

                    }
                });

               txtStoreResult.startAnimation(slideUp);
            }
        });

        btnPlusMinus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String result = txtResult.getText().toString();
                if (result.isEmpty())
                    return;

                String storedResult = txtStoreResult.getText().toString();
                StringBuilder resultChangedSign = new StringBuilder(result);
                // change sign of expression on screen
                for (int i=0; i<resultChangedSign.length(); i++)
                {
                    if (resultChangedSign.charAt(i) == '+')
                        resultChangedSign.setCharAt(i, '-');

                    else if (resultChangedSign.charAt(i) == '-')
                        resultChangedSign.setCharAt(i, '+');
                }

                result = resultChangedSign.toString();
                if (result.charAt(0) == '+')
                    result = result.substring(1, result.length());

                else if (result.charAt(0) != '-')
                    result = '-' + result;

                txtResult.setText(result);
                updateTxtStoreResult();
            }
        });

        btMc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String key = MainActivity.this.getResources().getString(R.string.key);
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(key, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor = putDouble(editor, key, 0);
                editor.apply();
            }
        });

        btnMPlus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setNewValueInMemory(true);
            }
        });

        btnMMinus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setNewValueInMemory(false);
            }
        });

        btnMr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String key = MainActivity.this.getResources().getString(R.string.key);
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(key, MODE_PRIVATE);
                double valueInMemory = getDouble(sharedPreferences, key, 0);
                String result = fmt(valueInMemory);
                txtResult.setText(result);
                txtStoreResult.setText("");
                resultSetByEqualSignOrMrButton = true;
            }
        });
    }

    // Attempted animation of blinking cursor (it doesn't work very well)
    /*private void runCursorThread()
    {
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                if (displayCursor)
                {
                    String result = txtResult.getText().toString();
                    if (!result.isEmpty() && result.charAt(result.length() - 1) == '|')
                        result = result.substring(0, result.length() - 1);

                    if (cursorOn)
                        txtResult.setText(result);

                    else
                    {
                        result += '|';
                        txtResult.setText(result);
                    }

                    cursorOn = !cursorOn;
                }
                txtResult.postDelayed(this, 400);
            }
        };

        runnable.run();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        setOnClickListeners();
        resultSetByEqualSignOrMrButton = false;
    }
}