package org.udu.alphamorze_android;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public final String ACTION_USB_PERMISSION = "org.udu.alphamorze_android.USB_PERMISSION";

    private Button buttonConnect;
    private Button buttonSend;

    private EditText editText;

    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbManager usbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConnect = findViewById(R.id.button_connect);
        buttonSend    = findViewById(R.id.button_send);
        editText      = findViewById(R.id.enter_text_field);

        setUIEnabled(false);

        if (isConnected()) {
            buttonConnect.setText(R.string.button_connect_disconnect);
            setUIEnabled(true);
        }

    }

    private boolean isConnected() {

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

        buttonSend.setEnabled(isEnabled);
        editText.setEnabled(isEnabled);

    }

    public void touchConnect(final View view) {   // Обработчик нажатия для buttonConnect

        if (buttonConnect.getText().toString() == "Connect") {

        }

        if (buttonConnect.getText().toString() == "Disconnect") {

        }

    }

    public void sendBinaryText(final View view) { // Обработчик нажатия для buttonSend

        byte[] result_text;

        result_text = byteCodeFromText(editText.getText().toString().toUpperCase()).clone();

    }

    private byte[] byteCodeFromText(final String text) { // Создание команд из 0 и 1

        ArrayList<Byte> tempArrList = new ArrayList<>();

        toCommand(text, tempArrList);

        Byte[] res = tempArrList.toArray(new Byte[tempArrList.size()]);

        return toPrimByte(res);

    }

    private byte[] toPrimByte(final Byte[] objByte) { // Конвертация объекта в примитив

        byte[] result = new byte[objByte.length];

        for (int e = 0; e < objByte.length; e++) {
            result[e] = objByte[e];
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
                tempArrList.add((byte) 1);
                tempArrList.add((byte) 0);
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
        }

        Byte[] res = tempArrList.toArray(new Byte[tempArrList.size()]);;

        return toPrimByte(res);

    }

}
