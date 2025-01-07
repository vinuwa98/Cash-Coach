package com.example.myapplication;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class NextIncome extends AppCompatActivity {
    Button go;
    EditText e1,e2,e3;
    String base,LTA,HRA,SA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_income);
        e1=(EditText)findViewById(R.id.edit1);
        e2=(EditText)findViewById(R.id.edit2);
        e3=(EditText)findViewById(R.id.edit3);
        Intent i=getIntent();
        base=i.getStringExtra("baseincome");
        LTA=i.getStringExtra("LTA");
        HRA=i.getStringExtra("HRA");
        SA=i.getStringExtra("SA");

        go=(Button)findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    

    }
}
