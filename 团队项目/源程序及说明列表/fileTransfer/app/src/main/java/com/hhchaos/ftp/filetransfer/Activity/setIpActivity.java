package com.hhchaos.ftp.filetransfer.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.hhchaos.ftp.filetransfer.R;

public class setIpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button lock_button_ok, lock_button_back;
    private Button lock_numpad_0, lock_numpad_1, lock_numpad_2, lock_numpad_3, lock_numpad_4, lock_numpad_5, lock_numpad_6, lock_numpad_7, lock_numpad_8, lock_numpad_9;
    private RadioButton rb1, rb2, rb3;

    private int hadInput;
    private String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);

        setTitle("请输入文件传输码：");

        lock_button_ok = (Button) findViewById(R.id.lock_button_ok);
        lock_button_back = (Button) findViewById(R.id.lock_button_backspace_);
        lock_numpad_0 = (Button) findViewById(R.id.lock_numpad_0_);
        lock_numpad_1 = (Button) findViewById(R.id.lock_numpad_1_);
        lock_numpad_2 = (Button) findViewById(R.id.lock_numpad_2_);
        lock_numpad_3 = (Button) findViewById(R.id.lock_numpad_3_);
        lock_numpad_4 = (Button) findViewById(R.id.lock_numpad_4_);
        lock_numpad_5 = (Button) findViewById(R.id.lock_numpad_5_);
        lock_numpad_6 = (Button) findViewById(R.id.lock_numpad_6_);
        lock_numpad_7 = (Button) findViewById(R.id.lock_numpad_7_);
        lock_numpad_8 = (Button) findViewById(R.id.lock_numpad_8_);
        lock_numpad_9 = (Button) findViewById(R.id.lock_numpad_9_);
        rb1 = (RadioButton) findViewById(R.id.rb1_);
        rb2 = (RadioButton) findViewById(R.id.rb2_);
        rb3 = (RadioButton) findViewById(R.id.rb3_);

        lock_button_ok.setOnClickListener(this);
        lock_button_back.setOnClickListener(this);
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
        rb1.setVisibility(View.GONE);
        rb2.setVisibility(View.GONE);
        rb3.setVisibility(View.GONE);
        input = "";
        hadInput = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lock_button_backspace_:
                if (hadInput > 0) {
                    hadInput--;
                    input = input.substring(0, hadInput);
                    switch (hadInput) {
                        case 0:
                            rb1.setVisibility(View.GONE);
                            break;
                        case 1:
                            rb2.setVisibility(View.GONE);
                            break;
                        case 2:
                            rb3.setVisibility(View.GONE);
                            break;
                    }
                }
                break;
            case R.id.lock_button_ok:
                Intent i=new Intent();
                i.putExtra("ip",input);
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                hadInput++;
                Button btn_num = (Button) v;
                switch (hadInput) {
                    case 1:
                        rb1.setVisibility(View.VISIBLE);
                        input += btn_num.getText();
                        break;
                    case 2:
                        rb2.setVisibility(View.VISIBLE);
                        input += btn_num.getText();
                        break;
                    case 3:
                        rb3.setVisibility(View.VISIBLE);
                        input += btn_num.getText();
                        break;
                }
                break;
        }
    }
}

