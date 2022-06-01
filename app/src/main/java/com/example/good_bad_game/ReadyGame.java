package com.example.good_bad_game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ReadyGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_game);
        Button btn_start = findViewById(R.id.btn_start);

        Thread t = new Thread(){
            @Override
            public void run() {

                while(!isInterrupted()){
                    try {
                        Thread.sleep(3000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InGame.class);
                intent.putExtra("type", "firstIn");
                startActivity(intent);
            }
        });
    }
}