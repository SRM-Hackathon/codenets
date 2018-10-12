package com.example.sairam.xraybot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {

    Button
    public static final int PICK_XRAY_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    private void uploadImage(InputStream imageStream) {

    }

    private void getImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pick an X-Ray Image"), PICK_XRAY_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_XRAY_IMAGE && data != null) {
            try {
                InputStream imageStream = MainActivity.this.getContentResolver().openInputStream(data.getData());
                uploadImage(imageStream);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
