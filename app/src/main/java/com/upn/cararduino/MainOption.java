package com.upn.cararduino;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainOption extends AppCompatActivity {

    public Button activar;
    public Button dispositivos;
    private int REQUEST_ENABLE_BT = 1;
    public ProgressDialog progress;
    public BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_option);
        activar = findViewById(R.id.btn_on);
        dispositivos = findViewById(R.id.btn_buscar);
        progress=new ProgressDialog(MainOption.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "El dispositivo no sopora la conecxion por Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                activar.setText("BLUETOOTH ENCENDIDO");
            } else {
                activar.setText("BLUETOOTH APAGADO");
            }
        }
        activar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    bluetoothAdapter.disable();
                    activar.setText("BLUETOOTH APAGADO");
                }
            }
        });
        dispositivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConectarBlueetooh(bluetoothAdapter);
            }
        });



    }
    public void ConectarBlueetooh(BluetoothAdapter adapter) {
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Conectese a Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            progress.setMessage("Buscando Dispositivos");
            progress.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    Intent intento = new Intent(MainOption.this, MainActivity.class);
                    progress.dismiss();
                    startActivity(intento);

                }
            }, 3000);
        }
    }
}