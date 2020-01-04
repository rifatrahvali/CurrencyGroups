package com.example.currencygroups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class CurrencyConverterActivity extends AppCompatActivity {

    private Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);


        //Control definitions
        mainToolbar = findViewById(R.id.currency_converter_layout);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Convert your currency");
    }
}
