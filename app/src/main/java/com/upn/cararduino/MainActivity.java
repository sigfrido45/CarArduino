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
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private final int ADELANTE = 10;
    private final int DERECHA = 20;
    private final int IZQUIERDA = 30;
    private final int ATRAS = 40;
    public BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public ArrayAdapter<String> dispositivos;
    public ListView lista;
    public static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Button btnAvanzar;
    private Button btnRetroceder;
    private Button btnIzquierda;
    private Button btnDerecha;
    private BluetoothSocket socket;

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
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                startActivityForResult(intent, ADELANTE);
            }
        });
        btnDerecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                startActivityForResult(intent, DERECHA);
            }
        });
        btnIzquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                startActivityForResult(intent, IZQUIERDA);
            }
        });
        btnRetroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                startActivityForResult(intent, ATRAS);
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
        socket = getSocket(device);
        connect(socket);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case ADELANTE: {
                    final String[] posiblesPalabras = {"avanza", "abanza", "avansa", "forward", "adelante perro"};
                    ArrayList<String> posiblesComandos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String comando = "f";
                    if (esPalabraCorrecta(posiblesComandos, posiblesPalabras))
                        write(comando.getBytes());
                    else
                        Toast.makeText(getApplicationContext(), "No se encontro coincidencias", Toast.LENGTH_LONG).show();
                    break;
                }
                case DERECHA: {
                    final String[] posiblesPalabras = {"derecha", "right", "derhecha", "derecha perro"};
                    ArrayList<String> posiblesComandos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String comando = "d";
                    if (esPalabraCorrecta(posiblesComandos, posiblesPalabras))
                        write(comando.getBytes());
                    else
                        Toast.makeText(getApplicationContext(), "No se encontro coincidencias", Toast.LENGTH_LONG).show();
                    break;
                }
                case IZQUIERDA: {
                    String comando = "i";
                    final String[] posiblesPalabras = {"izquierda", "isquierda", "izquerda", "izquierda perro"};
                    ArrayList<String> posiblesComandos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (esPalabraCorrecta(posiblesComandos, posiblesPalabras))
                        write(comando.getBytes());
                    else
                        Toast.makeText(getApplicationContext(), "No se encontro coincidencias", Toast.LENGTH_LONG).show();
                    break;
                }
                case ATRAS: {
                    String comando = "r";
                    final String[] posiblesPalabras = {"atras", "atrás", "retrocede", "retrocede perro"};
                    ArrayList<String> posiblesComandos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (esPalabraCorrecta(posiblesComandos, posiblesPalabras))
                        write(comando.getBytes());
                    else
                        Toast.makeText(getApplicationContext(), "No se encontro coincidencias", Toast.LENGTH_LONG).show();
                    break;
                }
            }
        } else
            Toast.makeText(getApplicationContext(), "Failed to recognize speech!", Toast.LENGTH_LONG).show();
    }

    private boolean esPalabraCorrecta(ArrayList<String> posiblesComandos, String[] posiblesPalabras) {
        boolean esCorrecto = false;
        for (int i = 0; i < posiblesComandos.size(); i++) {
            for (int j = 0; j < posiblesPalabras.length; j++)
                if (posiblesComandos.get(i).trim().equals(posiblesPalabras[j].trim())) {
                    esCorrecto = true;
                    break;
                }
        }
        return esCorrecto;
    }

    private void connect(BluetoothSocket socket) {
        try {
            mostrarTodo();
            socket.connect();
        } catch (IOException connectException) {
            ocultarTodo();
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
        btnAvanzar.setVisibility(View.INVISIBLE);
        btnDerecha.setVisibility(View.INVISIBLE);
        btnIzquierda.setVisibility(View.INVISIBLE);
        btnRetroceder.setVisibility(View.INVISIBLE);
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
        btnAvanzar.setVisibility(View.VISIBLE);
        btnDerecha.setVisibility(View.VISIBLE);
        btnIzquierda.setVisibility(View.VISIBLE);
        btnRetroceder.setVisibility(View.VISIBLE);
    }
}
