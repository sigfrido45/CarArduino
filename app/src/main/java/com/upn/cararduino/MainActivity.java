package com.upn.cararduino;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private final String ADELANTE = "f";
    private final String DERECHA = "d";
    private final String IZQUIERDA = "i";
    private final String ATRAS = "r";
    private final int CODE = 50;
    public boolean entro= true;
    public AdapterDispositivo dispositivos;
    public BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public List<Dispositivo> lista_data = new ArrayList<Dispositivo>();
    public ListView lista;
    public static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket socket;
    private TextView txtViewDispositivos;
    private Button btnHablaPerro;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lista = findViewById(R.id.lista_dispositivos);
        txtViewDispositivos = findViewById(R.id.txt_titulo_dispositivos);





        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices){

                lista_data.add(new Dispositivo(device.getName(),device.getAddress()));
            }
            dispositivos = new AdapterDispositivo(this,R.layout.nombre_dispositivos,lista_data);
            lista.setAdapter(dispositivos);
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView mac= view.findViewById(R.id.mac);
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac.getText().toString());
                    empezarHiloParaConectar(device);
                    if(entro) {
                        Intent intent = new Intent(MainActivity.this, VozAcitivity.class);
                        intent.putExtra("mac", device.getAddress());
                        startActivity(intent);
                    }

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No hay ningun dispositivo encontrado", Toast.LENGTH_SHORT).show();
        }
    }



    private void empezarHiloParaConectar(BluetoothDevice device) {
        bluetoothAdapter.cancelDiscovery();
        socket = getSocket(device);
        connect(socket);
    }







    private void connect(BluetoothSocket socket) {
        try {
            socket.connect();
        } catch (IOException connectException) {
            ocultarTodo();
            entro=false;

            Toast.makeText(getApplicationContext(), "No se conecto", Toast.LENGTH_LONG).show();
            try {
                socket.close();
            } catch (IOException closeException) {
                Toast.makeText(getApplicationContext(), "No se cerro el socket", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void write(byte[] bytes) {
        OutputStream tmpOut;
        try {
            tmpOut = socket.getOutputStream();
            tmpOut.write(bytes);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "No se puede obtener el output", Toast.LENGTH_LONG).show();
        }
    }

    private void ocultarTodo() {
        lista.setVisibility(View.VISIBLE);
        txtViewDispositivos.setVisibility(View.INVISIBLE);
    }

    public BluetoothSocket getSocket(BluetoothDevice device) {
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(MainActivity.BTMODULEUUID);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Socket nulo, no se pudo obtenerlo", Toast.LENGTH_LONG).show();
        }
        return socket;
    }

    public void mostrarTodo() {
        lista.setVisibility(View.INVISIBLE);
        txtViewDispositivos.setVisibility(View.VISIBLE);
    }
}
