package com.example.audiotaperhelper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.*;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "tag";
    String connectedDevice = null;
    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothDevice bluetoothDevice = null;
    String[] deviceNames;
    BluetoothDevice[] devices;
    private BluetoothSocket mBluetoothSocket;
    private OutputStream mOutputStream;

    private EditText etConsole;
    private LinearLayout recordList;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private boolean isLedOn;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isLedOn = true;
        etConsole = findViewById(R.id.et_console);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("connecting");
        progressDialog.setTitle("please, wait...");

        connect();

        // setting listeners
        findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                sendMessage("1");
//                sendMessage("0");
            }
        });
        findViewById(R.id.connect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    protected void addRec(String text) {
        // todo
    }

    protected void clearRecs() {//todo
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();

        unregisterReceiver(receiver);

        if (connectedThread != null) {
            connectedThread.cancel();
        }
        if (connectThread != null) {
            connectThread.cancel();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
//                    btnEnableSearch.setText(R.string.stop_search);
//                    pbProgress.setVisibility(View.VISIBLE);
//                    setListAdapter(BT_SEARCH);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
//                    btnEnableSearch.setText(R.string.start_search);
//                    pbProgress.setVisibility(View.GONE);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
//                        bluetoothDevices.add(device);
//                        listAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    void connect () {
        // creating vars
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        if (bluetooth != null) {

            deviceNames = null;
            devices = null;
            String status;
            if (bluetooth.isEnabled()) {
                Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    deviceNames = new String[pairedDevices.size()];
                    devices = new BluetoothDevice[pairedDevices.size()];
                    int count = 0;
                    for (BluetoothDevice device : pairedDevices) {
                        deviceNames[count] = device.getName(); // adding name
                        devices[count] = device; // adding mac

                        count++;
                    }
                }

            } else {
                // Bluetooth выключен. Предложим пользователю включить его.
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                status = "Bluetooth выключен";
            }

            showChooseMacDialog(deviceNames, devices);


        }
    }

    public void setDevice (BluetoothDevice device){
        this.bluetoothDevice = device;
        Log.e("tafffg", "GADFASasfdfdassssssssssL:" + bluetoothDevice);
//        startConnection(this.bluetoothDevice);
        if (device != null) {
            connectThread = new ConnectThread(device);
            connectThread.start();
        }
    }

//    void sendMessage(String command) {
//        byte[] buffer = command.getBytes();
//
//        if (mOutputStream != null) {
//            try {
//                mOutputStream.write(buffer);
//                mOutputStream.flush();
//            } catch (IOException e) {
//                Toast.makeText(this, "cant send command!", Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//        }
//    }

    private void sendMessage (String command){
        if (connectedThread != null && connectThread.isConnect()) {
            connectedThread.write(command);
        }
    }

    void startConnection (BluetoothDevice device){
        if (device != null) {
            try {
                Method method = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                mBluetoothSocket = (BluetoothSocket) method.invoke(device, 1);
                mBluetoothSocket.connect();

                mOutputStream = mBluetoothSocket.getOutputStream();

                Toast.makeText(this, "connected!", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(this, "failed!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void showChooseMacDialog (String[]devicesNames,final BluetoothDevice[] devices){
        int index = 0;
        final int[] resIndex = {0};
        resIndex[0] = 0;
        DialogInterface.OnClickListener onClickListener1 = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resIndex[0] = which;
            }
        };

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                setDevice(devices[resIndex[0]]);
            }
        };
        AlertDialog chooseDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setSingleChoiceItems(devicesNames, index, onClickListener1)
                .setPositiveButton("ok", onClickListener)
                .setNegativeButton("no", onClickListener1)
                .create();

        chooseDialog.show();
    }

    private class ConnectThread extends Thread {
        /* Этот класс отвечает за подключение к устройству*/
        private BluetoothSocket bluetoothSocket = null;
        private boolean success = false;

        public ConnectThread(BluetoothDevice device) {
            try {
                Method method = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                bluetoothSocket = (BluetoothSocket) method.invoke(device, 1);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                bluetoothSocket.connect();
                success = true;

                progressDialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "cant connect", Toast.LENGTH_SHORT).show();
                    }
                });

                cancel();
            }

            if (success) {
                connectedThread = new ConnectedThread(bluetoothSocket);
                connectedThread.start();
//                showFrameLedControls; мне это не надо
            }

        }

        public boolean isConnect() {
            return bluetoothSocket.isConnected();
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // класс отвечающий за взаимодействия с ардуино, когда она уже подключена
    private class ConnectedThread extends Thread {
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private boolean isConnected = false;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.inputStream = inputStream;
            this.outputStream = outputStream;
            isConnected = true;
        }

        @Override
        public void run() {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            StringBuffer buffer = new StringBuffer();
            final StringBuffer sbConsole = new StringBuffer();
            final ScrollingMovementMethod movementMethod = new ScrollingMovementMethod();


            while (isConnected) {
                try {
                    int bytes = bis.read();
                    buffer.append((char) bytes);
                    int eof = buffer.indexOf("\r\n");
                    if (eof > 0) {
                        sbConsole.append(buffer.toString());
                        buffer.delete(0, buffer.length());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                etConsole.setText(sbConsole.toString());
                                etConsole.setMovementMethod(movementMethod);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void write(String command) {
            byte[] bytes = command.getBytes();
            if (outputStream != null) {
                try {
                    outputStream.write(bytes);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            try {
                isConnected = false;
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // пример отправки сигнала на arduino (что нужно обязательно проверить перед отправкой) todo
//    private void enableLed(int led, boolean state) {
//        if (connectedThread != null && connectThread.isConnect()) {
//            String command = "";
//            switch (led) {
//                case LED_RED:
//                    command = (state) ? "red on" : "red off#";
//                    break;
//                case LED_GREEN:
//                    command = (state) ? "green on#" : "green off#";
//                    break;
//            }
//
//            connectedThread.write(command);
//        }
//    }
}

