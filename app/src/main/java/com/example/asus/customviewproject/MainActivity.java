package com.example.asus.customviewproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.asus.customviewproject.customview.HorizontalScrollView;
import com.example.asus.customviewproject.customview.PullTextView;
import com.example.asus.customviewproject.customview.RedPointView;
import com.example.asus.customviewproject.view.FlowActivity;
import com.example.asus.customviewproject.view.HorizontalScrollActivity;
import com.example.asus.customviewproject.view.PullTextActivity;
import com.example.asus.customviewproject.view.RedPointActivity;
import com.example.asus.customviewproject.view.SVGActivity;
import com.example.asus.customviewproject.view.StickNavActivity;
import com.example.asus.customviewproject.view.WaveTextActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.cp_wave_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WaveTextActivity.class));
            }
        });

        findViewById(R.id.cp_flow_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FlowActivity.class));
            }
        });

        findViewById(R.id.cp_svg_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SVGActivity.class));
            }
        });

        findViewById(R.id.cp_red_point).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RedPointActivity.class));
            }
        });

        findViewById(R.id.cp_pull_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PullTextActivity.class));
            }
        });

        findViewById(R.id.cp_stick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StickNavActivity.class));
            }
        });

        findViewById(R.id.cp_scroll_pager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HorizontalScrollActivity.class));
            }
        });
    }
}
