package org.udu.alphamorze_android;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public final String ACTION_USB_PERMISSION = "org.udu.alphamorze_android.USB_PERMISSION";

    private Button buttonConnect;
    private Button buttonDisconnect;
    private Button buttonSend;

    private EditText editText;

    private IntentFilter filter;

    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbManager usbManager;
    private UsbSerialDevice serialPort;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.

        @Override
        public void onReceivedData(byte[] arg0) {

            String data;
            try {
                data = new String(arg0, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {

                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);

                if (granted) {

                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);

                    if (serialPort != null) {

                        if (serialPort.open()) { //Set Serial Connection Parameters.

                            setUIEnabled(true);

                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickConnect(buttonConnect);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onClickDisconnect(buttonDisconnect);
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConnect    = findViewById(R.id.button_connect);
        buttonDisconnect = findViewById(R.id.button_disconnect);
        buttonSend       = findViewById(R.id.button_send);
        editText         = findViewById(R.id.enter_text_field);

        usbManager = (UsbManager) getSystemService(USB_SERVICE);

        filter = new IntentFilter();

        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        registerReceiver(broadcastReceiver, filter);

        setUIEnabled(false);

    }

    private boolean isConnected() { // Подключено ли устройство

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();

        if (!usbDevices.isEmpty()) {

            boolean keep = true;

            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {

                device = entry.getValue();
                int deviceVID = device.getVendorId();

                if (deviceVID == 0x2341) { //Arduino Vendor ID

                    PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                            new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep) {
                    return true;
                }
            }
        }

        return false;

    }

    private void setUIEnabled(final boolean isEnabled) {

        buttonDisconnect.setEnabled(isEnabled);
        buttonSend.setEnabled(isEnabled);
        editText.setEnabled(isEnabled);

    }

    public void onClickConnect(final View view) {   // Обработчик нажатия для buttonConnect

        if (isConnected()) {
            setUIEnabled(false);
            buttonConnect.setEnabled(false);
        }

    }

    public void onClickDisconnect(final View view) { // Обработчик нажатия на buttonDisconnect

        setUIEnabled(false);
        buttonConnect.setEnabled(true);
        serialPort.close();

    }

    public void onClickSend(final View view) { // Обработчик нажатия для buttonSend

        byte[] result_text;

        result_text = byteCodeFromText(editText.getText().toString().toUpperCase()).clone();

        serialPort.write(result_text);

    }

    private byte[] byteCodeFromText(final String text) { // Создание команд из 0 и 1

        ArrayList<Byte> tempArrList = new ArrayList<>();

        toCommand(text, tempArrList);

        Byte[] res = tempArrList.toArray(new Byte[tempArrList.size()]);

        return toPrimByte(res);

    }

    private byte[] toPrimByte(final Byte[] objBool) { // Конвертация объекта в примитив

        byte[] result = new byte[objBool.length];

        for (int e = 0; e < objBool.length; e++) {
            result[e] = objBool[e];
        }

        return result;

    }

    private void toCommand(final String text, ArrayList<Byte> arrayList) { // Преобразование текста в код

        for (int e = 0; e < text.length(); e++) {
            for (final byte temp : toCode(text.charAt(e))) {
                arrayList.add(temp);
            }
        }

    }

    private byte[] toCode(final char ch) { // Получение кода символа

        ArrayList<Byte> tempArrList = new ArrayList<>();

        switch (ch) {
            case 'A':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                break;
            case 'B':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                break;
            case 'C':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                break;
            case 'D':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                break;
            case 'E':
                tempArrList.add((byte) 0);
                break;
            case 'F':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                break;
            case 'G':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                break;
            case 'H':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                break;
            case 'I':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                break;
            case 'J':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                break;
            case 'K':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                break;
            case 'L':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                break;
            case 'M':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                break;
            case 'N':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                break;
            case 'O':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                break;
            case 'P':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                break;
            case 'Q':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                break;
            case 'R':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                break;
            case 'S':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                break;
            case 'T':
                tempArrList.add((byte) 1);
                break;
            case 'U':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                break;
            case 'V':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                break;
            case 'W':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                break;
            case 'X':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                break;
            case 'Y':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                break;
            case 'Z':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                break;
            case '0':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                break;
            case '1':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                break;
            case '2':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                break;
            case '3':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                break;
            case '4':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 1);
                break;
            case '5':
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                break;
            case '6':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                break;
            case '7':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                break;
            case '8':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                tempArrList.add((byte) 0);
                break;
            case '9':
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
                break;
            default:
                break;
        }

        Byte[] res = tempArrList.toArray(new Byte[tempArrList.size()]);

        return toPrimByte(res);

    }

}
