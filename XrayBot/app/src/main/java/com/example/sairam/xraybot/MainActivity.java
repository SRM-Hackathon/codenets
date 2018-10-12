package com.example.sairam.xraybot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {

    Button uploadImgBtn;
    public static final int PICK_XRAY_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadImgBtn = findViewById(R.id.upload_img_btn);
        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndUploadImage();
            }
        });

    }

    private String uploadImage(InputStream imageStream) throws IOException{
        String boundary = "*****";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String filename = "myfile.jpeg";
        //Need to update url
        URL url = new URL("http://10.4.59.68/api/test");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        DataOutputStream dos = null;
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

        dos = new DataOutputStream(conn.getOutputStream());
        dos.writeBytes(twoHyphens + boundary + crlf);
        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                    + filename + "\"" + crlf);
        dos.writeBytes(crlf);

        byte[] buffer;
        int bufferSize, bytesAvailable, bytesRead;
        int maxBufferSize = 1 * 1024 * 1024;

        bytesAvailable = imageStream.available();
        bufferSize = Math.min(maxBufferSize, bytesAvailable);
        buffer = new byte[bufferSize];
        bytesRead = imageStream.read(buffer, 0, bufferSize);
        while(bytesRead > 0){
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = imageStream.available();
            bufferSize = Math.min(maxBufferSize, bytesAvailable);
            buffer = new byte[bufferSize];
        }
        dos.writeBytes(twoHyphens + boundary + crlf);
        dos.writeBytes(crlf);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = bufferedReader.readLine()) != null){
            sb.append(line);
        }
        return sb.toString();
    }

    private void getAndUploadImage() {
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
