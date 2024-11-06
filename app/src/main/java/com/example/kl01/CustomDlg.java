package com.example.kl01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDlg extends AppCompatActivity {

    String sfName = "myFile";
    TextView tv;
    EditText message;
    final String foldername = "./mnt/sdcard/Android/kl01files/";
    final String filename = "friendlog.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.custom_dlg);

        Button okButton = (Button) findViewById(R.id.pbutton);
        Button cancelButton = (Button) findViewById(R.id.nbutton);
        message = (EditText) findViewById(R.id.msg);
        tv = (TextView) findViewById(R.id.text_friend);

        okButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msgStr = message.getText().toString();
                msgStr = msgStr.trim();

                if(msgStr.getBytes().length <= 0){//빈값이 넘어올때의 처리
                    Toast.makeText(getApplicationContext(), "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("number", msgStr);
                    Toast.makeText(getApplicationContext(), msgStr + "을 입력하였습니다.", Toast.LENGTH_SHORT).show();

                    String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    String short_now = new SimpleDateFormat("MM월dd일 HH시mm분").format(new Date());
                    tv.setText(short_now+"친구 " + msgStr);
                    String contents = now + " 친구수 " + msgStr +"\n";
                    WriteTextFile(foldername, filename, contents);
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "취소했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        SharedPreferences sf = getSharedPreferences(sfName, 0);
        String str = sf.getString("name", "");
        if(TextUtils.isEmpty(str)) {
            tv.setText("친구 0");
        } else {
            tv.setText(str);
        }
    }

    public void WriteTextFile(String foldername, String filename, String contents){
        try{
            File dir = new File (foldername);
            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    protected void onStop() {
        super.onStop();
        // Activity 가 종료되기 전에 저장한다
        // SharedPreferences 에 설정값(특별히 기억해야할 사용자 값)을 저장하기
        SharedPreferences sf = getSharedPreferences(sfName, 0);
        SharedPreferences.Editor editor = sf.edit();//저장하려면 editor가 필요
        String str = tv.getText().toString(); // 사용자가 입력한 값
        editor.putString("name", str); // 입력
        editor.apply(); // 파일에 최종 반영함
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
