package org.udu.alphamorze_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button buttonSend;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = findViewById(R.id.button_send);
        editText   = findViewById(R.id.enter_text_field);

        char char_array[] ={'A', 'B', 'C', 'D', 'E', 'F',
                            'G', 'H', 'I', 'J', 'K', 'L',
                            'M', 'N', 'O', 'P', 'Q', 'R',
                            'S', 'T', 'U', 'V', 'W', 'X',
                                      'Y', 'Z'};

    }

    private byte[] byteCodeFromText(final String text) { // Создание команд из 0 и 1

        ArrayList<Byte> tempArrList = new ArrayList<>();



        Byte[] res = tempArrList.toArray(new Byte[tempArrList.size()]);

        return toPrimByte(res);

    }

    public void sendBinaryText(final View view) { // Обработчик нажатия на buttonSend

        byte[] result_text;

        result_text = byteCodeFromText(editText.getText().toString()).clone();

    }

    private byte[] toPrimByte(final Byte[] objByte) { // Конвертация объекта в примитив

        byte[] result = new byte[objByte.length];

        for (int e = 0; e < objByte.length; e++) {
            result[e] = objByte[e];
        }

        return result;

    }

    private void toCommand(final String text, ArrayList<Byte> arrayList) {



    }

    private byte[] toCode(final char ch) {

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
        }

        Byte[] res = tempArrList.toArray(new Byte[tempArrList.size()]);;

        return toPrimByte(res);

    }

}
