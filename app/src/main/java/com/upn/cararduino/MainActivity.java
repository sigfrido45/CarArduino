package com.upn.cararduino;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.upn.cararduino.threads.ConnectedThread;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public ArrayAdapter<String> dispositivos;
    public ListView lista;
    public ConnectedThread connectedThread;
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
        bluetoothAdapter.cancelDiscovery();
        BluetoothSocket socket = getSocket(device);
        connect(socket);
    }

    private void connect(BluetoothSocket socket) {
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mostrarTodo();
            socket.connect();
        } catch (IOException connectException) {
            ocultarTodo();
            Toast.makeText(getApplicationContext(), "No se conecto", Toast.LENGTH_LONG).show();
            try {
                socket.close();
            } catch (IOException closeException) {
            }
        }
    }

    private void ocultarTodo() {
        lista.setVisibility(View.VISIBLE);
        btnAvanzar.setVisibility(View.INVISIBLE);
        btnDerecha.setVisibility(View.INVISIBLE);
        btnIzquierda.setVisibility(View.INVISIBLE);
        btnRetroceder.setVisibility(View.INVISIBLE);
    }

    public BluetoothSocket getSocket(BluetoothDevice device) {
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            BluetoothSocket mmSocket = device.createRfcommSocketToServiceRecord(MainActivity.BTMODULEUUID);
            return mmSocket;
        } catch (IOException e) {
            return null;
        }
    }

    public void mostrarTodo() {
        lista.setVisibility(View.INVISIBLE);
        btnAvanzar.setVisibility(View.VISIBLE);
        btnDerecha.setVisibility(View.VISIBLE);
        btnIzquierda.setVisibility(View.VISIBLE);
        btnRetroceder.setVisibility(View.VISIBLE);
    }

}
