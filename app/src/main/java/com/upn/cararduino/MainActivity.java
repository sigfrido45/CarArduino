package com.upn.cararduino;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.upn.cararduino.threads.ConnectThread;
import com.upn.cararduino.threads.ConnectedThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public ArrayAdapter<String> dispositivos;
    public ListView lista;
    public ConnectedThread connectedThread;
    public ConnectThread connectThread;
    public static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Button btnAvanzar;
    private Button btnRetroceder;
    private Button btnIzquierda;
    private Button btnDerecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dispositivos = new ArrayAdapter<>(this, R.layout.nombre_dispositivos);
        lista = findViewById(R.id.lista_dispositivos);
        btnAvanzar = findViewById(R.id.idBtnAvanzar);
        btnRetroceder = findViewById(R.id.idBtnRetroceder);
        btnIzquierda = findViewById(R.id.idBtnIzquierda);
        btnDerecha = findViewById(R.id.idBtnDerecha);

        btnAvanzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "f";
                connectedThread.write(data.getBytes());
                connectedThread.write(data.getBytes());

            }
        });
        btnDerecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "d";
                connectedThread.write(data.getBytes());

            }
        });
        btnIzquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "i";
                connectedThread.write(data.getBytes());

            }
        });
        btnRetroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "r";
                connectedThread.write(data.getBytes());
            }
        });

        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices)
                dispositivos.add(device.getName() + " \n" + device.getAddress());
            lista.setAdapter(dispositivos);
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(), " " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(parent.getItemAtPosition(position).toString().substring(parent.getItemAtPosition(position).toString().length() - 17));
                    empezarHiloParaConectar(device);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No hay ningun dispositivo encontrado", Toast.LENGTH_SHORT).show();
        }
    }
    private void empezarHiloParaConectar(BluetoothDevice device) {
        connectThread = new ConnectThread(device, bluetoothAdapter);
        connectThread.run();
    }
}
