package com.custom.laddertextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private LadderTextView leftLadderTv, rightLadderTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftLadderTv = findViewById(R.id.leftLadderTv);
        rightLadderTv = findViewById(R.id.rightLadderTv);

        initListeners();
    }

    private void initListeners() {
        leftLadderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!leftLadderTv.isSelected()) {
                    leftLadderTv.setMSelected(!leftLadderTv.isSelected());
                    rightLadderTv.setMSelected(!rightLadderTv.isSelected());
                }
            }
        });

        rightLadderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!rightLadderTv.isSelected()) {
                    leftLadderTv.setMSelected(!leftLadderTv.isSelected());
                    rightLadderTv.setMSelected(!rightLadderTv.isSelected());
                }
            }
        });
    }
}
