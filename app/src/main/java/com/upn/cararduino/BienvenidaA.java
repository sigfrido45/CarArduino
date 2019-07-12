package com.upn.cararduino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class BienvenidaA extends AppCompatActivity {

    public ImageView logo;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.bienvenida_activity);

        logo=findViewById(R.id.logo);
        Animation scale= AnimationUtils.loadAnimation(BienvenidaA.this,R.anim.scale);
        logo.startAnimation(scale);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {


                Intent intento=new Intent(BienvenidaA.this, MainOption.class);
                startActivity(intento);

            }
        }, 3000);
        }
}
