package com.example.android.routegradient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText mRoute1EditText;
    private EditText mRoute2EditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRoute1EditText = (EditText)findViewById(R.id.et_route1_entry);
        mRoute2EditText = (EditText)findViewById(R.id.et_route2_entry);

        Button routeButton = (Button)findViewById(R.id.btn_find_route);
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startLocation = mRoute1EditText.getText().toString();
                String endLocation = mRoute2EditText.getText().toString();
                if (!TextUtils.isEmpty(startLocation) && !TextUtils.isEmpty(endLocation)) {
                    // make API call and move to ResultActivity
                    mRoute1EditText.setText("");
                    mRoute2EditText.setText("");
                }
            }
        });

    }
}
