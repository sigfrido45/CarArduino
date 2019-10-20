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
    private final String SUBIR_CUCHILLAS = "s";
    private final String BAJAR_CUCHILLAS = "b";


    public BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public ArrayAdapter<String> dispositivos;
    public ListView lista;
    public static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket socket;
    private TextView txtViewDispositivos;
    private Button btnHablaPerro;
    private Button btnSubirCuchillas;
    private Button btnBajarCuchillas;

    final String[] posiblesPalabrasAdelante = {"avanza", "abanza", "avansa", "forward", "adelanteperro"};
    final String[] posiblesPalabrasDerecha = {"derecha", "right", "derhecha", "derechaperro"};
    final String[] posiblesPalabrasIzquierda = {"izquierda", "isquierda", "izquerda", "izquierdaperro", "left"};
    final String[] posiblesPalabrasAtras = {"atras", "atrás", "retrocede", "retrocedeperro", "back"};
    private ArrayList<String[]> posiblesPalabras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dispositivos = new ArrayAdapter<>(this, R.layout.nombre_dispositivos);
        lista = findViewById(R.id.lista_dispositivos);
        txtViewDispositivos = findViewById(R.id.txt_titulo_dispositivos);
        btnHablaPerro = findViewById(R.id.btn_habla_perro);
        btnBajarCuchillas = findViewById(R.id.btn_bajar_cuchillas);
        btnSubirCuchillas = findViewById(R.id.btn_subir_cuchillas);
        setPalabras();

        btnHablaPerro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                startActivityForResult(intent, CODE);
            }
        });

        btnSubirCuchillas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subeOBajaCuchillas(SUBIR_CUCHILLAS);
            }
        });

        btnBajarCuchillas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subeOBajaCuchillas(BAJAR_CUCHILLAS);
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

    private void setPalabras() {
        posiblesPalabras = new ArrayList<>();
        posiblesPalabras.add(posiblesPalabrasAdelante);
        posiblesPalabras.add(posiblesPalabrasDerecha);
        posiblesPalabras.add(posiblesPalabrasIzquierda);
        posiblesPalabras.add(posiblesPalabrasAtras);
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
                case CODE: {
                    ArrayList<String> posiblesComandos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String palabra = getPalabraCorrecta(posiblesComandos);
                    if (!palabra.isEmpty())
                        enviarDatoCorrecto(palabra);
                    else
                        Toast.makeText(getApplicationContext(), "No se encontro coincidencias", Toast.LENGTH_LONG).show();
                    break;
                }
                default:
                    Toast.makeText(getApplicationContext(), "el codigo del intent es incorrecto", Toast.LENGTH_LONG).show();
                    break;
            }
        } else
            Toast.makeText(getApplicationContext(), "Failed to recognize speech!", Toast.LENGTH_LONG).show();
    }

    private void enviarDatoCorrecto(String palabra) {
        if (Arrays.asList(posiblesPalabrasAdelante).contains(palabra))
            write(ADELANTE.getBytes());
        if (Arrays.asList(posiblesPalabrasAtras).contains(palabra))
            write(ATRAS.getBytes());
        if (Arrays.asList(posiblesPalabrasDerecha).contains(palabra))
            write(DERECHA.getBytes());
        if (Arrays.asList(posiblesPalabrasIzquierda).contains(palabra))
            write(IZQUIERDA.getBytes());
    }

    private void subeOBajaCuchillas(String caracter) {
        write(caracter.getBytes());
    }

    private String getPalabraCorrecta(ArrayList<String> posiblesComandos) {
        Toast.makeText(getApplicationContext(), "words " + posiblesComandos, Toast.LENGTH_LONG).show();
        for (String[] palabras : posiblesPalabras) {
            List<String> lista = Arrays.asList(palabras);
            for (String comando : posiblesComandos) {
                if (lista.contains(comando.trim())) {
                    return comando.trim();
                }
            }
        }
        return "";
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
        txtViewDispositivos.setVisibility(View.INVISIBLE);
        btnHablaPerro.setVisibility(View.INVISIBLE);
        btnSubirCuchillas.setVisibility(View.INVISIBLE);
        btnBajarCuchillas.setVisibility(View.INVISIBLE);
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
        btnHablaPerro.setVisibility(View.VISIBLE);
        btnSubirCuchillas.setVisibility(View.VISIBLE);
        btnBajarCuchillas.setVisibility(View.VISIBLE);
    }
}
