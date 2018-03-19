package com.hhchaos.ftp.filetransfer.Activity;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hhchaos.ftp.filetransfer.R;

public class setLinkCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button lock_button_backspace, lock_button_re;
    private Button lock_numpad_0, lock_numpad_1, lock_numpad_2, lock_numpad_3, lock_numpad_4, lock_numpad_5, lock_numpad_6, lock_numpad_7, lock_numpad_8, lock_numpad_9;
    private RadioButton rb1, rb2, rb3, rb4;

    private int hadInput;
    private String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_link_code);

        setTitle("请设置文件传输码：");

        lock_button_backspace = (Button) findViewById(R.id.lock_button_backspace);
        lock_button_re = (Button) findViewById(R.id.lock_button_re);
        lock_numpad_0 = (Button) findViewById(R.id.lock_numpad_0);
        lock_numpad_1 = (Button) findViewById(R.id.lock_numpad_1);
        lock_numpad_2 = (Button) findViewById(R.id.lock_numpad_2);
        lock_numpad_3 = (Button) findViewById(R.id.lock_numpad_3);
        lock_numpad_4 = (Button) findViewById(R.id.lock_numpad_4);
        lock_numpad_5 = (Button) findViewById(R.id.lock_numpad_5);
        lock_numpad_6 = (Button) findViewById(R.id.lock_numpad_6);
        lock_numpad_7 = (Button) findViewById(R.id.lock_numpad_7);
        lock_numpad_8 = (Button) findViewById(R.id.lock_numpad_8);
        lock_numpad_9 = (Button) findViewById(R.id.lock_numpad_9);
        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);
        rb4 = (RadioButton) findViewById(R.id.rb4);

        lock_button_backspace.setOnClickListener(this);
        lock_button_re.setOnClickListener(this);
        lock_numpad_0.setOnClickListener(this);
        lock_numpad_1.setOnClickListener(this);
        lock_numpad_2.setOnClickListener(this);
        lock_numpad_3.setOnClickListener(this);
        lock_numpad_4.setOnClickListener(this);
        lock_numpad_5.setOnClickListener(this);
        lock_numpad_6.setOnClickListener(this);
        lock_numpad_7.setOnClickListener(this);
        lock_numpad_8.setOnClickListener(this);
        lock_numpad_9.setOnClickListener(this);

        reSet();


    }

    private void reSet() {
        rb1.setEnabled(false);
        rb2.setEnabled(false);
        rb3.setEnabled(false);
        rb4.setEnabled(false);
        input = "";
        hadInput = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lock_button_backspace:
                if (hadInput > 0) {
                    hadInput--;
                    input = input.substring(0, hadInput);
                    switch (hadInput) {
                        case 0:
                            rb1.setEnabled(false);
                            break;
                        case 1:
                            rb2.setEnabled(false);
                            break;
                        case 2:
                            rb3.setEnabled(false);
                            break;
                        case 3:
                            rb4.setEnabled(false);
                            break;
                    }
                }
                break;
            case R.id.lock_button_re:
                reSet();
                break;
            default:
                hadInput++;
                Button btn_num = (Button) v;
                switch (hadInput) {
                    case 1:
                        rb1.setEnabled(true);
                        input += btn_num.getText();
                        break;
                    case 2:
                        rb2.setEnabled(true);
                        input += btn_num.getText();
                        break;
                    case 3:
                        rb3.setEnabled(true);
                        input += btn_num.getText();
                        break;
                    case 4:
                        rb4.setEnabled(true);
                        input += btn_num.getText();
                        Intent i=new Intent();
                        i.putExtra("linkcode",input);
                        setResult(RESULT_OK,i);
                        finish();
                        break;
                }

                break;
        }
    }
}

