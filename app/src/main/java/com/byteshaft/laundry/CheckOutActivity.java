package com.byteshaft.laundry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class CheckOutActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addButton;
    private Button minusButton;
    private TextView weightTextView;
    private int weight = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        addButton = (Button) findViewById(R.id.add);
        minusButton = (Button) findViewById(R.id.minus);
        weightTextView = (TextView) findViewById(R.id.weight);
        addButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                if (weight < 20) {
                    weight++;
                    weightTextView.setText(String.valueOf(weight) + "Kg");
                } else {
                    Toast.makeText(this, "Over weight", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.minus:
                if (weight > 2) {
                    weight--;
                    weightTextView.setText(String.valueOf(weight) + "Kg");
                } else {
                    Toast.makeText(this, "Minimum weight is 2Kg", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
