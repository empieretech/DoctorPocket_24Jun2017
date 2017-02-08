package com.docpoc.doctor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.adapter.ChatAdapterNew;
import com.docpoc.doctor.classes.ChatMessage;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.MultipartUtility;
import com.docpoc.doctor.utils.MyCustomLayoutManager;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.Utils;
import com.google.android.gms.common.api.GoogleApiClient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

public class ChatActivityNew extends Activity implements AsynchTaskListner {


    public WebSocketClient webSocket;
    public ImageView imgSend, imgMic, imgCamera;
    public EditText etChat;
    public RobottoTextView tvTimer;
    ;

    public MyCustomLayoutManager layoutManager;

    public ChatAdapterNew cAdapter;
    public RecyclerView lstChat;
    public RelativeLayout relAudioPanel, relChatPanel;
    public String imgDecodableString;
    public static final int PiCK_IMAGE = 111;
    public static final int PiCK_AUDIO = 222;
    public static final int PICK_VIDEO = 333;
    public static final int TAKE_PICTURE = 444;
    public static final int TAKE_VIDEO = 555;
    public boolean isStart = false;
    public String outputFile = null;
    public MediaRecorder myAudioRecorder;
    public Handler mHandler, mStatusHandler;
    public Runnable mAudioRunnable, mStatusRunnable;
    public byte[] bytes;
    public Uri selectedUri;
    public ImageView imgOffOn;
    public String FileType = "", FileName = "", booking_id = "", pt_id = "", dr_id = "", uniqueChatId = "";
    private static final String AUDIO_RECORDER_FOLDER = "dr_pocket/audio/";
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".mp3";
    public boolean isLive = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    public static String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA};
    public static String[] mediaColumns = {MediaStore.Video.Media._ID};
    private GoogleApiClient client;
    public Fragment ft;
    public Constant.REQUESTS request;
    public Map<String, String> map;
    public AsynchTaskListner aListner;
    public Constant.POST_TYPE post_type;
    public static String file;
    public long timer = 1;
    public AngellinaTextView txtheader;
    String senderImageUrl, recvImageUrl;
    public ImageView img_back2;
    public TextView tvEndChat;
    public App app;

    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";

    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_chat_new);
        Utils.logUser();


        app = App.getInstance();
        if (getIntent().hasExtra("startTime")) {
            recvImageUrl = getIntent().getStringExtra("recvImageUrl");

            uniqueChatId = getIntent().getStringExtra("uniqueChatId");
            pt_id = getIntent().getStringExtra("pt_id");
            dr_id = getIntent().getStringExtra("dr_id");
        } else {
            recvImageUrl = getIntent().getStringExtra("recvImageUrl");
            pt_id = getIntent().getStringExtra("pt_id");
            dr_id = getIntent().getStringExtra("dr_id");
            uniqueChatId = getIntent().getStringExtra("uniqueChatId");

         /*   if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                new CallRequest(ChatActivity.this).checkCancelStatus(uniqueChatId, "patient", pt_id);
            } else {
                new CallRequest(ChatActivity.this).checkCancelStatus(uniqueChatId, "doctor", dr_id);

            }*/


        }

        if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
            String user_id = App.user.getUserID();

            new CallRequest(ChatActivityNew.this).checkCancelStatus(uniqueChatId, "patient", user_id);

        } else {
            String user_id = App.user.getUserID();

            new CallRequest(ChatActivityNew.this).checkCancelStatus(uniqueChatId, "doctor", dr_id);

        }

        connectWebSocket();
        senderImageUrl = App.user.getProfileUrl();
        txtheader = (AngellinaTextView) findViewById(R.id.txtheader);

        if (getIntent().hasExtra("fName")) {
            txtheader.setText(getIntent().getStringExtra("fName"));
        } else {
            txtheader.setText("Dr. Pocket");
        }
        etChat = (EditText) findViewById(R.id.etChat);
        lstChat = (RecyclerView) findViewById(R.id.lstChat);
        layoutManager = new MyCustomLayoutManager(this);
        lstChat.setLayoutManager(layoutManager);

        imgSend = (ImageView) findViewById(R.id.imgSend);
        imgCamera = (ImageView) findViewById(R.id.imgCamera);
        img_back2 = (ImageView) findViewById(R.id.img_back2);
        imgMic = (ImageView) findViewById(R.id.imgMic);
        tvTimer = (RobottoTextView) findViewById(R.id.tvTimer);
        relAudioPanel = (RelativeLayout) findViewById(R.id.relAudioPanel);
        relChatPanel = (RelativeLayout) findViewById(R.id.relChatPanel);
        imgOffOn = (ImageView) findViewById(R.id.imgOffOn);
        tvEndChat = (TextView) findViewById(R.id.tvEndChat);


        if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
            tvEndChat.setVisibility(View.VISIBLE);
            tvEndChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CallRequest(ChatActivityNew.this).cancelChat(dr_id, uniqueChatId);
                }
            });
        }


        if (!app.recentChatId.equalsIgnoreCase("uniqueChatId")) {
            app.recentChatId = uniqueChatId;
            app.chatArray.clear();
        } else {
            app.recentChatId = uniqueChatId;
        }

        cAdapter = new ChatAdapterNew(this, app.chatArray, recvImageUrl, senderImageUrl);
        lstChat.setAdapter(cAdapter);
        mHandler = new Handler();


        mAudioRunnable = new Runnable() {
            @Override
            public void run() {

                long minutes = TimeUnit.MILLISECONDS
                        .toMinutes(timer * 1000);
                long seconds = TimeUnit.MILLISECONDS
                        .toSeconds(timer * 1000);

                tvTimer.setText(addZero(minutes) + ":" + addZero(seconds));

                timer++;
                mHandler.postDelayed(this, 1000);

            }
        };
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("doctor pocket", "Going to check");
                if (webSocket.getConnection().isOpen()) {
                    if (!etChat.getText().toString().isEmpty())
                        sendMessage(etChat.getText().toString());
                    Log.i("doctor pocket", "Going to send message");
                } else {

                    Log.i("doctor pocket", "Going to Connect");
                }
            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
                selectImage();
            }
        });


        imgMic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        startRecording();
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:

                        timer = 0;
                        tvTimer.setText("");
                        mHandler.removeCallbacks(mAudioRunnable);

                        relChatPanel.setVisibility(View.VISIBLE);
                        relAudioPanel.setVisibility(View.GONE);

                        stopRecording();
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
       /* imgMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isStart) {

                } else {


                    startRecording();
                }

            }
        });*/


        img_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        new CallRequest(this).geChatHistory(uniqueChatId);


    }

    public void startRecording() {
        try {
            outputFile = "doctorPocket/recording.3gp";
            ;
            boolean result = Utils.checkAudioPermission(ChatActivityNew.this);

            if (result) {
                isStart = true;
                relChatPanel.setVisibility(View.GONE);
                relAudioPanel.setVisibility(View.VISIBLE);
                timer = 0;
                tvTimer.setText("00:00");
                mHandler.postDelayed(mAudioRunnable, 1000);

                myAudioRecorder = new MediaRecorder();
                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
               // myAudioRecorder.setOutputFormat(output_formats[currentFormat]);
                myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
              //  myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                file = getFilename();
                myAudioRecorder.setOutputFile(file);

                myAudioRecorder.setOnErrorListener(errorListener);
                myAudioRecorder.setOnInfoListener(infoListener);
                myAudioRecorder.prepare();
                myAudioRecorder.start();

                Utils.showToast("Recording Started", ChatActivityNew.this);
            } else {
                Utils.showToast("Permission not granted", ChatActivityNew.this);
            }

        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == Utils.RECORD_AUDDIO) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();

            } else {
                //Displaying another toast if permission is not granted

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    /*    if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
            new CallRequest(ChatActivity.this).checkCancelStatus(uniqueChatId, "doctor", dr_id);
        } else {
            new CallRequest(ChatActivity.this).checkCancelStatus(uniqueChatId, "patient", pt_id);
        }*/

        if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
            String user_id = App.user.getUserID();

            new CallRequest(ChatActivityNew.this).checkCancelStatus(uniqueChatId, "patient", user_id);

        } else {
            String user_id = App.user.getUserID();

            new CallRequest(ChatActivityNew.this).checkCancelStatus(uniqueChatId, "doctor", dr_id);

        }

    }

    @Override
    protected void onPause() {


        if (webSocket.getConnection().isOpen() && !webSocket.getConnection().hasBufferedData()) {
            webSocket.getConnection().close();
        }

        if (mStatusHandler != null && mStatusRunnable != null) {
            mStatusHandler.removeCallbacks(mStatusRunnable);
        }

        if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
            String user_id = App.user.getUserID();
            new CallRequest(ChatActivityNew.this).setOnOffStatus("doctor", user_id, "0");
        } else {
            String user_id = App.user.getUserID();
            new CallRequest(ChatActivityNew.this).setOnOffStatus("patient", user_id, "0");
        }

        super.onPause();
    }

    public void stopRecording() {

        try {
            if (null != myAudioRecorder) {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
                isStart = false;


                // m.start();
                imgDecodableString = file;
                Log.i("Tag", "Path :" + file);
                String pure = file.replace("/storage", "");
                int bytesRead;

                File f = new File(file);
                FileInputStream is = new FileInputStream(f);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int size = is.available();
                byte[] buffer = new byte[size];

                while ((bytesRead = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                is.close();
                bytes = bos.toByteArray();
                System.out.println(" One Recording values is: " + bytes + " \n");

                FileType = "AudioFiles";

                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
                String formattedDate = df.format(c.getTime());

                chatBean = new ChatMessage();
                chatBean.type = "audio";
                chatBean.uniqueChatId = uniqueChatId;
                //   chatBean.message = message;
                chatBean.date = formattedDate;
                chatBean.localPath = imgDecodableString;
                chatBean.isReceived = false;
                chatBean.isSent = false;
                chatBean.chatID = System.currentTimeMillis();
                app.chatArray.add(chatBean);
                new UploadAudio().execute();

                cAdapter.notifyDataSetChanged();

                relAudioPanel.setVisibility(View.GONE);
                relChatPanel.setVisibility(View.VISIBLE);

                layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalThreadStateException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (RuntimeException ex) {
            //Ignore
        }

    }

    public String addZero(long l) {

        return l > 9 ? l + "" : "0" + l;
    }


    private String getFilename() {
        //    String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(getFilesDir(), AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }
        FileName = System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_3GP;
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_3GP);
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(ChatActivityNew.this,
                    "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(ChatActivityNew.this,
                    "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    private void connectWebSocket() {


        URI uri;
        try {
            uri = new URI(MyConstants.chat_socket_url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocket = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                // webSocket.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);//
                //  sendMessage("Hello");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.i("Message", "Message Revieved: " + message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!message.isEmpty()) {
                            handleMessage(message);
                        }
                    }
                });
            }


            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);

            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };

        webSocket.connect();


    }

    public void sendMessage(String message) {

        Log.i("doctor pocket", "Going to sendMessage");
        sendTextMessage(message);
    }

    public ChatMessage chatBean;

    public void handleMessage(String msg) {

        try {
            JSONObject jObj = new JSONObject(msg);


            String type = jObj.getString("messageType");
            String receiverId = jObj.getString("receiverId");

            if (receiverId.equalsIgnoreCase(App.user.getUserID())) {
                if (type.equalsIgnoreCase("text")) {
                    chatBean = new ChatMessage();

                    chatBean.type = "text";

                    chatBean.uniqueChatId = jObj.getString("uniqueChatId");
                    chatBean.message = jObj.getString("message");
                    chatBean.date = jObj.getString("date");
                    chatBean.isReceived = true;
                    chatBean.chatID = System.currentTimeMillis();
                    app.chatArray.add(chatBean);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cAdapter.notifyDataSetChanged();
                            layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                        }
                    });

                } else if (type.equalsIgnoreCase("image")) {
                    chatBean = new ChatMessage();
                    chatBean.type = "image";

                    chatBean.uniqueChatId = jObj.getString("uniqueChatId");
                    chatBean.message = jObj.getString("message");
                    chatBean.date = jObj.getString("date");
                    chatBean.isReceived = true;
                    chatBean.chatID = System.currentTimeMillis();
                    app.chatArray.add(chatBean);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cAdapter.notifyDataSetChanged();
                            layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                        }
                    });
                } else if (type.equalsIgnoreCase("video")) {

                    chatBean = new ChatMessage();
                    chatBean.type = "video";

                    chatBean.uniqueChatId = jObj.getString("uniqueChatId");
                    chatBean.message = jObj.getString("message");
                    chatBean.date = jObj.getString("date");
                    chatBean.isReceived = true;
                    chatBean.thumbnail = jObj.getString("thumbnail");
                    chatBean.chatID = System.currentTimeMillis();
                    app.chatArray.add(chatBean);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cAdapter.notifyDataSetChanged();
                            layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                        }
                    });

                } else {
                    chatBean = new ChatMessage();
                    chatBean.type = "audio";

                    chatBean.uniqueChatId = jObj.getString("uniqueChatId");
                    chatBean.message = jObj.getString("message");
                    chatBean.date = jObj.getString("date");
                    chatBean.isReceived = true;
                    chatBean.thumbnail = jObj.getString("thumbnail");
                    chatBean.chatID = System.currentTimeMillis();
                    app.chatArray.add(chatBean);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cAdapter.notifyDataSetChanged();
                            layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                        }
                    });
                }
            } else {
                Log.i("NOT:", "  Not attandable message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendTextMessage(String message) {
        Log.i("doctor pocket", "Going to sendTextMessage");
        try {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
            String formattedDate = df.format(c.getTime());

            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put("uniqueChatId", uniqueChatId);

            if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                messageMap.put("senderId", App.user.getUserID());
                messageMap.put("receiverId", pt_id);
            } else {
                messageMap.put("senderId", App.user.getUserID());
                messageMap.put("receiverId", dr_id);
            }

            messageMap.put("messageType", "text");
            messageMap.put("message", message);
            messageMap.put("date", formattedDate);


            JSONObject obj = new JSONObject(messageMap);

            String ms = obj.toString(2);
            Log.i("Message", "Message : " + ms);

            if (isLive) {
                sendWebMessage(ms);

                sendNotificaionMessage(ms, false);
            } else {
                sendNotificaionMessage(ms, true);
            }

            chatBean = new ChatMessage();
            chatBean.type = "text";
            chatBean.uniqueChatId = uniqueChatId;
            chatBean.message = message;
            chatBean.date = formattedDate;
            chatBean.isSent = true;
            chatBean.isReceived = false;
            chatBean.chatID = System.currentTimeMillis();
            app.chatArray.add(chatBean);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
                    cAdapter.notifyDataSetChanged();
                    layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                    etChat.setText("");
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Capture Video", "Choose Photo from Library", "Choose Video from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivityNew.this, R.style.MyDialogTheme);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utils.checkPermission(ChatActivityNew.this);
                if (items[item].equals("Take Photo")) {

                    if (result) {
                        File f = new File(Environment.getExternalStorageDirectory() + "/dr_pocket/images");
                        if (!f.exists()) {
                            f.mkdirs();
                        }
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(), "dr_pocket/images/img_" + System.currentTimeMillis() + ".jpg");
                        selectedUri = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedUri);

                        startActivityForResult(intent, TAKE_PICTURE);
                    }
                } else if (items[item].equals("Capture Video")) {
                    if (result) {
                        File f = new File(Environment.getExternalStorageDirectory() + "/dr_pocket/video");
                        if (!f.exists()) {
                            f.mkdirs();

                        }
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(), "dr_pocket/video/vid__" + System.currentTimeMillis() + ".mp4");
                        selectedUri = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedUri);

                        startActivityForResult(intent, TAKE_VIDEO);
                    }
                } else if (items[item].equals("Choose Video from Library")) {
                    if (result) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("video/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
                        } catch (Exception e) {
                            Log.d("EXChooseVideo", e.getMessage());
                        }
                    }
                } else if (items[item].equals("Choose Photo from Library")) {

                    if (result) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                     /*   intent.setType("image*//*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);//*/
                        startActivityForResult(Intent.createChooser(intent, "Select File"), PiCK_IMAGE);

                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_VIDEO && resultCode == RESULT_OK) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
            String formattedDate = df.format(c.getTime());

            chatBean = new ChatMessage();
            chatBean.type = "video";
            chatBean.uniqueChatId = uniqueChatId;
            //   chatBean.message = message;
            chatBean.date = formattedDate;
            chatBean.localPath = getPath(selectedUri);
            chatBean.isReceived = false;
            chatBean.isSent = false;
            chatBean.chatID = System.currentTimeMillis();
            app.chatArray.add(chatBean);

            new UploadVideo().execute();
            cAdapter.notifyDataSetChanged();
            layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);


        }

        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
            String formattedDate = df.format(c.getTime());

            chatBean = new ChatMessage();
            chatBean.type = "image";
            chatBean.uniqueChatId = uniqueChatId;
            //   chatBean.message = message;
            chatBean.date = formattedDate;
            chatBean.localPath = getPath(selectedUri);
            chatBean.isReceived = false;
            chatBean.isSent = false;
            chatBean.chatID = System.currentTimeMillis();
            app.chatArray.add(chatBean);

            new UploadImage().execute();
            cAdapter.notifyDataSetChanged();
            layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

        }


        if ((requestCode == PiCK_IMAGE || requestCode == PICK_VIDEO) && resultCode == RESULT_OK) {
            Uri u = data.getData();
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
            String formattedDate = df.format(c.getTime());
            chatBean = new ChatMessage();
            if (requestCode == PICK_VIDEO) {
                chatBean.type = "video";
            } else {
                chatBean.type = "image";
            }
            chatBean.uniqueChatId = uniqueChatId;
            //   chatBean.message = message;
            chatBean.date = formattedDate;
            chatBean.localPath = getRealVideoPathFromUri(u);
            chatBean.isReceived = false;
            chatBean.isSent = false;
            chatBean.chatID = System.currentTimeMillis();
            app.chatArray.add(chatBean);

            if (requestCode == PICK_VIDEO) {
                new UploadVideo().execute();
            } else {
                new UploadImage().execute();
            }
            cAdapter.notifyDataSetChanged();
            layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

        }
    }


    public String getPath(Uri uri) {
        File myFile = new File(uri.getPath());
        myFile.getAbsolutePath();
        return myFile.getAbsolutePath();
    }

    public String getRealImagePath(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getRealVideoPathFromUri(Uri contentURI) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(contentURI, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    @Override
    public void onStop() {
        super.onStop();


    }


    @Override
    public void onTaskCompleted(String result, Constant.REQUESTS request) {
        try {
            if (result != null && !result.isEmpty()) {
                Log.i("TAG", "TAG Result : " + result);
                //      {"document":{"response":{"status":1,"message":"Success."}}}
                switch (request) {

                    case chatHistory:


                        if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                            new CallRequest(ChatActivityNew.this).getOnOffStatus("patient", pt_id, uniqueChatId);
                        } else {
                            new CallRequest(ChatActivityNew.this).getOnOffStatus("doctor", dr_id, uniqueChatId);

                        }

                        app.chatArray.clear();

                        try {
                            JSONObject obj = new JSONObject(result);

                            boolean success = obj.getBoolean("success");

                            if (success) {
                                JSONArray jArray = obj.getJSONArray("result");

                                if (jArray != null && jArray.length() > 0) {


                                    for (int i = 0; i < jArray.length(); i++) {
                                        JSONObject jObj = jArray.getJSONObject(i);

                                        chatBean = new ChatMessage();
                                        chatBean.chatID = Long.valueOf(jObj.getString("id"));
                                        chatBean.uniqueChatId = jObj.getString("uniqueChatId");
                                        String sender_id = jObj.getString("senderId");
                                        String receiverId = jObj.getString("receiverId");

                                        if (sender_id.equalsIgnoreCase(App.user.getUserID())) {

                                            chatBean.isReceived = false;
                                        } else {
                                            chatBean.isReceived = true;

                                        }
                                        chatBean.isSent = true;
                                       /* } else if (sender_id.equalsIgnoreCase(pt_id)) {
                                            if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_PT)) {
                                                chatBean.isSent = true;
                                                chatBean.isReceived = false;
                                            } else {
                                                chatBean.isReceived = true;
                                                chatBean.isSent = false;
                                            }
                                        } else {
                                            chatBean.isReceived = true;
                                            chatBean.isSent = false;
                                        }*/
                                        chatBean.type = jObj.getString("messageType");

                                        if(chatBean.type.equalsIgnoreCase("audio")){
                                            chatBean.message = jObj.getString("message");
                                            chatBean.date = jObj.getString("date");
                                            chatBean.localPath = jObj.getString("message");
                                            chatBean.thumbnail = jObj.getString("message");
                                        } else {
                                            chatBean.message = jObj.getString("message");
                                            chatBean.date = jObj.getString("date");
                                            chatBean.localPath = jObj.getString("message");
                                            chatBean.thumbnail = jObj.getString("message");
                                        }
                                        chatBean.message = jObj.getString("message");
                                        chatBean.date = jObj.getString("date");
                                        chatBean.localPath = jObj.getString("message");
                                        chatBean.thumbnail = jObj.getString("message");

                                        app.chatArray.add((chatBean));
                                    }

                                    cAdapter.notifyDataSetChanged();
                                    layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
/*
                        "id": "5",
                            "uniqueChatId": "438",
                            "senderId": "444",
                            "receiverId": "802",
                            "messageType": "text",
                            "message": "Nnnnn",
                            "senderDisplayName": "",
                            "date": "08:44",
                            "dateTimeNow": "2016-09-30 14:57:19"*/
                        break;

                    case cancelChat:
                        Utils.removeSimpleSpinProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(result);

                            boolean success = obj.getBoolean("success");

                            if (!success) {
                                // Utils.showToast("Please try again later", this);
                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setTitle("Thank you for using Doctor Pocket");
                                builder.setMessage("You've ended your virtual consult.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                ChatActivityNew.this.finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case getOnOffStatus:

                        Utils.removeSimpleSpinProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(result);

                            boolean success = obj.getBoolean("success");


                            if (!success) {
                                // Utils.showToast("Please try again later", this);
                            } else {
                                JSONObject resultObj = obj.getJSONObject("result");

                                if (resultObj.getString("cancel_status").equalsIgnoreCase("1") &&
                                        !App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                                    showPatientCanceledAlert();
                                } else {
                                    String live_status = resultObj.getString("live_status");
                                    if (live_status.equalsIgnoreCase("1")) {
                                        isLive = true;
                                        imgOffOn.setBackgroundResource(R.drawable.green_round);
                                    } else {
                                        isLive = false;
                                        imgOffOn.setBackgroundResource(R.drawable.red_round_offline);
                                    }
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case checkCancelStatus:
                        Utils.removeSimpleSpinProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(result);
                            boolean success = obj.getBoolean("success");
                            if (!success) {
                                String message = obj.getString("message");
                                Utils.showToast(message, this);
                            } else {

                                String cancel_status = obj.getJSONObject("result").getString("cancel_status");

                                if (cancel_status.equalsIgnoreCase("1")) {
                                    if (cancel_status.equalsIgnoreCase("1") && App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                                        String drName = App.user.getUserName();
                                        if (!drName.contains("Dr.")) {
                                            drName = "Dr. " + drName;
                                        }
                                        Utils.showToast(drName + " has ended consult.", ChatActivityNew.this);

                                        showDoctorCancelAlert();
                                    } else {
                                        showPatientCanceledAlert();
                                    }
                                } else {

                                    if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                                        String user_id = App.user.getUserID();
                                        new CallRequest(ChatActivityNew.this).setOnOffStatus("doctor", user_id, "1");
                                    } else {
                                        String user_id = App.user.getUserID();
                                        new CallRequest(ChatActivityNew.this).setOnOffStatus("patient", user_id, "1");
                                    }
                                    startStatusThread();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;


                }

            } else {
                //  Utils.showToast("Please try again later", this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendNotificaionMessage(String message, boolean isShow) {
        String sender_id = "", user_type = "", is_live = "", reciver_id = "";
        if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {

            user_type = "d";
            reciver_id = pt_id;
        } else {
            sender_id = pt_id;
            user_type = "p";
            reciver_id = dr_id;
        }
        sender_id = App.user.getUserID();
        if (isShow) {
            is_live = "n";
        } else {
            is_live = "y";
        }

        new CallRequest(this).sendNotification(message, sender_id, reciver_id, user_type, is_live);


    }

    @Override
    public void onProgressUpdate(String uniqueMessageId, int progres) {

    }

    @Override
    public void onProgressComplete(String uniqueMessageId, String result, Constant.REQUESTS
            request) {


    }

    public void sendImageMessage(ChatMessage chatBean) {
        Log.i("doctor pocket", "Going to sendTextMessage");


        try {


            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
            String formattedDate = df.format(c.getTime());

            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put("uniqueChatId", chatBean.uniqueChatId + "");

            if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                messageMap.put("senderId", App.user.getUserID());
                messageMap.put("receiverId", pt_id);
            } else {
                messageMap.put("senderId", App.user.getUserID());
                messageMap.put("receiverId", dr_id);
            }


            messageMap.put("messageType", "image");
            messageMap.put("message", chatBean.message);
            messageMap.put("thumbnail", chatBean.thumbnail);
            messageMap.put("date", formattedDate);


            JSONObject obj = new JSONObject(messageMap);

            String ms = obj.toString(2);
            Log.i("Message", "Message : " + ms);


            if (isLive) {
                sendWebMessage(ms);
                sendNotificaionMessage(ms, false);
            } else {
                sendNotificaionMessage(ms, true);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cAdapter.notifyDataSetChanged();
                    layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                    etChat.setText("");
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void sendVideoMessage(ChatMessage chatBean) {
        Log.i("doctor pocket", "Going to sendTextMessage");


        //    self.startTime = @"2016-06-30 16:30:00";
        //    self.endTime = @"2016-06-30 16:45:00";
        //   kk:mm:ss
        try {

            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
            String formattedDate = df.format(c.getTime());

            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put("uniqueChatId", chatBean.uniqueChatId + "");

            if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                messageMap.put("senderId", App.user.getUserID());
                messageMap.put("receiverId", pt_id);
            } else {
                messageMap.put("senderId", App.user.getUserID());
                messageMap.put("receiverId", dr_id);
            }


            messageMap.put("messageType", "video");
            messageMap.put("message", chatBean.message);
            messageMap.put("thumbnail", chatBean.thumbnail);
            messageMap.put("date", formattedDate);


            JSONObject obj = new JSONObject(messageMap);

            String ms = obj.toString(2);
            Log.i("Message", "Message : " + ms);


            if (isLive) {
                sendWebMessage(ms);
                sendNotificaionMessage(ms, false);
            } else {
                sendNotificaionMessage(ms, true);
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cAdapter.notifyDataSetChanged();
                    layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                    etChat.setText("");
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void sendAudioMessage(ChatMessage chatBean) {
        Log.i("doctor pocket", "Going to send Audio MEssage");

        try {

            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
            String formattedDate = df.format(c.getTime());

            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put("uniqueChatId", chatBean.uniqueChatId + "");

            if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                messageMap.put("senderId", App.user.getUserID());
                messageMap.put("receiverId", pt_id);
            } else {
                messageMap.put("senderId", App.user.getUserID());
                messageMap.put("receiverId", dr_id);
            }


            messageMap.put("messageType", "audio");
            messageMap.put("message", chatBean.message);
            messageMap.put("thumbnail", chatBean.thumbnail);
            messageMap.put("date", formattedDate);


            JSONObject obj = new JSONObject(messageMap);

            String ms = obj.toString(2);
            Log.i("Message", "Message : " + ms);
            if (isLive) {

                sendWebMessage(ms);
                sendNotificaionMessage(ms, false);
            } else {
                sendNotificaionMessage(ms, true);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cAdapter.notifyDataSetChanged();
                    layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                    etChat.setText("");
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void sendWebMessage(String ms){

        if(webSocket.getConnection().isClosed()){
            webSocket.connect();
        }

        webSocket.send(ms);

    }
    class UploadImage extends AsyncTask<Void, Void, String> {
        String response;

        JSONObject jsonObject;
        public long chatId;
        public ChatMessage cBean;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.chatId = chatBean.chatID;
            this.cBean = chatBean;
            chatBean.isSent = false;
        }
       /*
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            uploading.setProgress(Integer.parseInt(progress[0]));
        }*/

        @Override
        protected String doInBackground(Void... params) {
            String charset = "UTF-8";
            try {
                MultipartUtility multipart = new MultipartUtility(MyConstants.CHAT_UPLOAD_URL + "uploadsFiles.php?", charset);
                multipart.addFormField("type", "image");

                File file = new File(this.cBean.localPath);
                multipart.addFilePart("uploadFile", file);
                multipart.addFormField("name", file.getName());
                response = multipart.finish();
                Log.d("PostActivity", "SERVER REPLIED: " + response);
            } catch (IOException e) {
                Log.d("ExPostActivity", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                Log.d("PostActivity", "SERVER REPLIED : " + response);

                //     {"response":1,"message":"File uploaded..","type":"type","thumbnail":"","data":"http:\/\/192.206.6.82\/~doctorpocket\/doctorApp\/PHPWebSocket7\/api\/files\/1183(New)_Home_page1.jpg"}
                JSONObject obj = new JSONObject(response);

                if (obj.getInt("response") == 1) {
                    cBean.message = obj.getString("thumbnail");
                    cBean.thumbnail = obj.getString("data");
                    cBean.isSent = true;
                    cAdapter.notifyDataSetChanged();
                    layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                    sendImageMessage(cBean);
                }

            } catch (Exception e) {
                e.printStackTrace();
                //    Log.d("ExPostActivity", e.printStackTrace());
            }
        }
    }

    class UploadAudio extends AsyncTask<Void, Void, String> {
        String response;

        JSONObject jsonObject;
        public long chatId;
        public ChatMessage cBean;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.chatId = chatBean.chatID;
            this.cBean = chatBean;
            chatBean.isSent = false;
        }
       /*
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            uploading.setProgress(Integer.parseInt(progress[0]));
        }*/

        @Override
        protected String doInBackground(Void... params) {


            String charset = "UTF-8";

            try {
                MultipartUtility multipart = new MultipartUtility(MyConstants.CHAT_UPLOAD_URL + "uploadsFiles.php?", charset);
                multipart.addFormField("type", "audio");

                File file = new File(this.cBean.localPath);
                int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                Log.d("PostActivity", "Audio Size: " + file_size);
                multipart.addFilePart("uploadFile", file);
                multipart.addFormField("name", file.getName());
                response = multipart.finish();
                Log.d("PostActivity", "SERVER REPLIED: " + response);


            } catch (IOException e) {
                Log.d("ExPostActivity", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                Log.d("PostActivity", "SERVER REPLIED : " + response);

                //     {"response":1,"message":"File uploaded..","type":"type","thumbnail":"","data":"http:\/\/192.206.6.82\/~doctorpocket\/doctorApp\/PHPWebSocket7\/api\/files\/1183(New)_Home_page1.jpg"}
                JSONObject obj = new JSONObject(response);

                if (obj.getInt("response") == 1) {
                    cBean.thumbnail = obj.getString("thumbnail");
                    cBean.message = obj.getString("data");
                    cBean.isSent = true;
                    cAdapter.notifyDataSetChanged();
                    layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                    sendAudioMessage(cBean);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class UploadVideo extends AsyncTask<Void, Void, String> {
        String response;

        JSONObject jsonObject;
        public long chatId;
        public ChatMessage cBean;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.chatId = chatBean.chatID;
            this.cBean = chatBean;
            chatBean.isSent = false;
        }
       /*
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            uploading.setProgress(Integer.parseInt(progress[0]));
        }*/

        @Override
        protected String doInBackground(Void... params) {


            String charset = "UTF-8";

            try {

               /* map.put("url", MyConstants.CHAT_UPLOAD_URL + "uploadsFiles.php?");

                map.put("type", "image");

                map.put("filename", imagePath);
                map.put("chatId", chatId);*/
                MultipartUtility multipart = new MultipartUtility(MyConstants.CHAT_UPLOAD_URL + "uploadsFiles.php?", charset);
                multipart.addFormField("type", "video");

                File file = new File(this.cBean.localPath);
                multipart.addFilePart("uploadFile", file);
                multipart.addFormField("name", file.getName());


                response = multipart.finish();
                Log.d("PostActivity", "SERVER REPLIED: " + response);


            } catch (IOException e) {
                Log.d("ExPostActivity", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                Log.d("PostActivity", "SERVER REPLIED : " + response);

                //     {"response":1,"message":"File uploaded..","type":"type","thumbnail":"","data":"http:\/\/192.206.6.82\/~doctorpocket\/doctorApp\/PHPWebSocket7\/api\/files\/1183(New)_Home_page1.jpg"}
                JSONObject obj = new JSONObject(response);

                if (obj.getInt("response") == 1) {
                    cBean.message = obj.getString("thumbnail");
                    cBean.thumbnail = obj.getString("data");
                    cBean.isSent = true;
                    cAdapter.notifyDataSetChanged();
                    layoutManager.scrollToPositionWithOffset(cAdapter.getItemCount() - 1, 0);

                    sendVideoMessage(cBean);
                }

            } catch (Exception e) {
                e.printStackTrace();
                //    Log.d("ExPostActivity", e.printStackTrace());
            }
        }
    }

    public void showDoctorCancelAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Thank you for using Doctor Pocket");
        builder.setMessage(App.user.getUserName() + " has ended the consult.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChatActivityNew.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }


    public void showPatientCanceledAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Thank you for using Doctor Pocket");
        builder.setMessage("Your doctor has ended the consult.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChatActivityNew.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void startStatusThread() {

        Log.i("Tag", "Tag : Start Thread TRUE");

        if (mStatusHandler != null && mStatusRunnable != null) {
            return;
        }
        mStatusHandler = new Handler();
        mStatusRunnable = new Runnable() {
            @Override
            public void run() {

                if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                    new CallRequest(ChatActivityNew.this).getOnOffStatus("patient", pt_id, uniqueChatId);
                } else {
                    new CallRequest(ChatActivityNew.this).getOnOffStatus("doctor", dr_id, uniqueChatId);

                }

                mStatusHandler.postDelayed(mStatusRunnable, 15000);
            }
        };
        mStatusHandler.postDelayed(mStatusRunnable, 15000);

    }


    public static String getThumbnailPathForLocalFile(Activity context,
                                                      Uri fileUri) {

        long fileId = getFileId(context, fileUri);

        MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null);

        Cursor thumbCursor = null;
        try {

            thumbCursor = context.managedQuery(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + " = "
                            + fileId, null, null);

            if (thumbCursor.moveToFirst()) {
                String thumbPath = thumbCursor.getString(thumbCursor
                        .getColumnIndex(MediaStore.Video.Thumbnails.DATA));

                return thumbPath;
            }

        } finally {
        }

        return null;
    }

    public static long getFileId(Activity context, Uri fileUri) {

        Cursor cursor = context.managedQuery(fileUri, mediaColumns, null, null,
                null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int id = cursor.getInt(columnIndex);

            return id;
        }

        return 0;
    }

    public void startOnOffTimer() {

    }

    public class AppLog {
        private static final String APP_TAG = "AudioRecorder";

        public int logString(String message) {
            return Log.i(APP_TAG, message);
        }
    }
}
