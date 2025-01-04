package com.example.myapplication;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;



    }
    public void finish(){
        double A=Double.parseDouble(e1.getText().toString());
        double B=Double.parseDouble(e2.getText().toString());
        double C=Double.parseDouble(e3.getText().toString());
        Intent i=new Intent(NextIncome.this,Finalinc.class);
        i.putExtra("80A",e1.getText().toString());
        i.putExtra("base",base);
        i.putExtra("hra",HRA);
        i.putExtra("lta",LTA);
        i.putExtra("sa",SA);
        i.putExtra("80B",e2.getText().toString());
        i.putExtra("80C",e3.getText().toString());
        startActivity(i);

    }
}
