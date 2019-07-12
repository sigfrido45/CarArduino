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
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class VozAcitivity extends AppCompatActivity {
    public static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public de.hdodenhof.circleimageview.CircleImageView btnHablaPerro;
    final String[] posiblesPalabrasAdelante = {"avanza", "abanza", "avansa", "forward", "adelanteperro"};
    final String[] posiblesPalabrasDerecha = {"derecha", "right", "derhecha", "derechaperro"};
    final String[] posiblesPalabrasIzquierda = {"izquierda", "isquierda", "izquerda", "izquierdaperro", "left"};
    final String[] posiblesPalabrasAtras = {"atras", "atrás", "retrocede", "retrocedeperro", "back"};
    private ArrayList<String[]> posiblesPalabras;
    private final String ADELANTE = "f";
    private final String DERECHA = "d";
    private final String IZQUIERDA = "i";
    private BluetoothSocket socket;
    private final String ATRAS = "r";
    private final int CODE = 50;
    private BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voz_acitivity);
        setPalabras();
        btnHablaPerro=findViewById(R.id.btn_habla_perro);
        String mac=getIntent().getStringExtra("mac");
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac);
        Toast.makeText(VozAcitivity.this,mac,Toast.LENGTH_LONG).show();
        socket = getSocket(device);
      
        btnHablaPerro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                startActivityForResult(intent, CODE);
            }
        });
    }
    private void setPalabras() {
        posiblesPalabras = new ArrayList<>();
        posiblesPalabras.add(posiblesPalabrasAdelante);
        posiblesPalabras.add(posiblesPalabrasDerecha);
        posiblesPalabras.add(posiblesPalabrasIzquierda);
        posiblesPalabras.add(posiblesPalabrasAtras);
    }
    public BluetoothSocket getSocket(BluetoothDevice device) {
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(VozAcitivity.BTMODULEUUID);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Socket nulo, no se pudo obtenerlo", Toast.LENGTH_LONG).show();
        }
        return socket;
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
    public void write(byte[] bytes) {
        OutputStream tmpOut;
        try {
            tmpOut = socket.getOutputStream();
            tmpOut.write(bytes);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "No se puede obtener el output", Toast.LENGTH_LONG).show();
        }
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
}
