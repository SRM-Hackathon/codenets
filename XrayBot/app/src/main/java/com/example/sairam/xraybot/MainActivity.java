package com.example.sairam.xraybot;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class MainActivity extends Activity {

    ImageButton uploadImgBtn;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    EditText inputMsgEdt;
    List<UserMessage> chatlist;
    public static final int PICK_XRAY_IMAGE = 1;
    public static final String DIALOGFLOW_TOKEN = "11d22cdbc45b4f11b1ed9d1713e225bd";

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            chatlist.add((UserMessage) msg.obj);
            messageAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(chatlist.size()-1);
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        Toast.makeText(this, "on save state ", Toast.LENGTH_SHORT).show();
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("savedchat", (ArrayList) chatlist);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            ArrayList<UserMessage> arrayList = savedInstanceState.getParcelableArrayList("savedchat");
            chatlist.clear();
            chatlist.addAll(arrayList);
            messageAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(chatlist.size() - 1);
        }
        else{
            chatlist.add(new UserMessage("Hi", false));
            chatlist.add(new UserMessage("I am DocBot", false));
            chatlist.add(new UserMessage("Give me a chest X-Ray image." +
                    " I'll analyse and tell you about it", false));
            messageAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(chatlist.size() - 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadImgBtn = findViewById(R.id.upload_img_btn);
        recyclerView = findViewById(R.id.conversation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndUploadImage();
            }
        });
        inputMsgEdt = findViewById(R.id.inputMessage);
        inputMsgEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(!inputMsgEdt.getText().toString().isEmpty()) {

                    getResponse(inputMsgEdt.getText().toString());

                    chatlist.add(new UserMessage(inputMsgEdt.getText().toString(), true));
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(chatlist.size() - 1);
                    inputMsgEdt.setText("");
                }
                return true;
            }
        });

        chatlist = new ArrayList<>();
        messageAdapter = new MessageAdapter(chatlist);
        recyclerView.setAdapter(messageAdapter);

        if(chatlist.size() == 0) {
            chatlist.add(new UserMessage("Hi", false));
            chatlist.add(new UserMessage("I am DocBot", false));
            chatlist.add(new UserMessage("Give me a chest X-Ray image." +
                    " I'll analyse and tell you about it", false));
            messageAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(chatlist.size() - 1);
        }
    }

    private String uploadImage(InputStream imageStream) throws IOException{
        String serverURL = "http://10.4.59.68:5000/api/test";
        String boundary = "*****";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String fileName = "myfile.jpeg";
        //Need to update url
        URL url = new URL(serverURL);
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
        conn.setRequestProperty("uploaded_file", fileName);

        dos = new DataOutputStream(conn.getOutputStream());
        dos.writeBytes(twoHyphens + boundary + crlf);
        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                    + fileName + "\"" + crlf);
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
            bytesRead = imageStream.read(buffer, 0, bufferSize);
        }

        dos.writeBytes(crlf);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Message msg = new Message();
            msg.obj = new UserMessage("Image Uploaded", true);
            handler.sendMessage(msg);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            conn.disconnect();
            return sb.toString();
        }
        else{
            conn.disconnect();
            return "Error Response Code not 200";
        }
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
                final InputStream imageStream = MainActivity.this.getContentResolver().openInputStream(data.getData());
                new Thread(){
                    @Override
                    public void run() {
                        if(imageStream != null) {
                            String response = null;
                            try {
                                response = uploadImage(imageStream);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Message msg = new Message();
                            msg.obj = new UserMessage(response, false);
                            handler.sendMessage(msg);

                            Log.d("MyLog", response);
                        }
                        else{
                            Log.d("MyLog", "Img not obtained");
                        }
                    }
                }.start();

            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getResponse(String requestText){
        final AIConfiguration config = new AIConfiguration(DIALOGFLOW_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(requestText);

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                    // process aiResponse here
                    String responseStr = aiResponse.getResult().getFulfillment().getSpeech();
                    Message msg = new Message();
                    msg.obj = new UserMessage(responseStr, false);
                    handler.sendMessage(msg);
                    Log.d("MyLog", responseStr);
                }
            }
        }.execute(aiRequest);

    }
}