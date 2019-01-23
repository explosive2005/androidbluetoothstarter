package com.example.computerroomdad.bluetoothcolorchanger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    TextView tv;
    ListView listView;
    sendRecieve sendData;
    BluetoothAdapter btAdapter;
    BluetoothSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        tv = (TextView) findViewById(R.id.textview);

        listView = (ListView) findViewById(R.id.listView);
    }


    Handler myHandler = new Handler()
    {


        @Override
        public String getMessageName(Message message) {
            return super.getMessageName(message);
        }
    };

    public void startThread(View v) {

        BluetoothDevice myDevice = btAdapter.getRemoteDevice("20:13:08:20:05:32");

        //myDevice.getAddress()
        clientThread cThread = new clientThread(myDevice);
        cThread.start();



    }

    public void sendData(View v) {
        sendData.write(("Hfsdlfklsdfjsdjfkj /n").getBytes());
    }

    class clientThread extends Thread {
        BluetoothDevice device;



        public clientThread(BluetoothDevice d) {
            device = d;

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        @Override
        public void run() {
            try {
                socket.connect();

                sendData = new sendRecieve(socket);
                sendData.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private class sendRecieve extends Thread {
        private final BluetoothSocket btSocket;
        private final InputStream inStream;
        private final OutputStream outStream;

        public sendRecieve(BluetoothSocket socket) {
            btSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = btSocket.getInputStream();
                tmpOut = btSocket.getOutputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

            inStream = tmpIn;
            outStream = tmpOut;

            write(("hello").getBytes());
        }

        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inStream.read(buffer);
                    myHandler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


