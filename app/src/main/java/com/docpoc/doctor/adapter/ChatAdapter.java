package com.docpoc.doctor.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.docpoc.doctor.AudioPlayer;
import com.docpoc.doctor.R;
import com.docpoc.doctor.VideoPlayerController;
import com.docpoc.doctor.classes.ChatMessage;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.utils.RoundCornerImageView;
import com.docpoc.doctor.utils.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatAdapter extends BaseAdapter {

    public Context context;
    public ArrayList<ChatMessage> chatArray;
    public ViewHolder holder = null;
    public AQuery aqList;
    public String recvUrl, sendUrl;
    public static final int SEND_TEXT = 0;
    public static final int SEND_IMAGE = 1;
    public static final int SEND_VIDEO = 2;
    public static final int SEND_AUDIO = 3;

    public static final int RECV_TEXT = 4;
    public static final int RECV_IMAGE = 5;
    public static final int RECV_VIDEO = 6;
    public static final int RECV_AUDIO = 7;


    public ChatAdapter(Context context, ArrayList<ChatMessage> chatArray, String recvUrl, String sendUrl) {

        this.context = context;
        this.chatArray = chatArray;
        this.aqList = new AQuery(context);
        this.recvUrl = recvUrl;
        this.sendUrl = sendUrl;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imagep, imagecam;
        RobottoTextView tvMessage;
        RobottoTextView date_time;
        public RoundedImageView imgPerson;
        public RoundCornerImageView imgMessage;
        public ImageView imgDownload;
        public ProgressBar pBar;
        public TextView tvTime;
        public View viewTranspernt;


    }

    @Override
    public int getItemViewType(int position) {
        if (chatArray.get(position).isReceived) {
            if (chatArray.get(position).type.equalsIgnoreCase("text")) {
                return RECV_TEXT;
            } else if (chatArray.get(position).type.equalsIgnoreCase("image")) {
                return RECV_IMAGE;
            } else if (chatArray.get(position).type.equalsIgnoreCase("audio")) {
                return RECV_AUDIO;
            } else if (chatArray.get(position).type.equalsIgnoreCase("video")) {
                return RECV_VIDEO;
            }
        } else {
            if (chatArray.get(position).type.equalsIgnoreCase("text")) {
                return SEND_TEXT;
            } else if (chatArray.get(position).type.equalsIgnoreCase("image")) {
                return SEND_IMAGE;
            } else if (chatArray.get(position).type.equalsIgnoreCase("audio")) {
                return SEND_AUDIO;
            } else if (chatArray.get(position).type.equalsIgnoreCase("video")) {
                return SEND_VIDEO;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return chatArray.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return chatArray.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {



        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();

            int type = getItemViewType(position);
            switch (type) {
                case RECV_TEXT:
                    Log.i("ChatAdapter", "In Null View  Text Received  : Position : " + position);
                    convertView = mInflater.inflate(R.layout.chat_recv_row, parent, false);
                    holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                    holder.imgPerson = (RoundedImageView) convertView.findViewById(R.id.imgPerson);
                    holder.tvMessage = (RobottoTextView) convertView.findViewById(R.id.tvMessage);
                    break;
                case RECV_IMAGE:
                case RECV_AUDIO:
                case RECV_VIDEO:
                    Log.i("ChatAdapter", "In Null View  Image Received  : Position : " + position);
                    convertView = mInflater.inflate(R.layout.chat_image_recv, parent, false);
                    holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                    holder.imgPerson = (RoundedImageView) convertView.findViewById(R.id.imgPerson);
                    holder.imgMessage = (RoundCornerImageView) convertView.findViewById(R.id.imgMessage);
                    holder.imgDownload = (ImageView) convertView.findViewById(R.id.imgDownload);
                    holder.pBar = (ProgressBar) convertView.findViewById(R.id.pBar);
                    holder.viewTranspernt = (View) convertView.findViewById(R.id.viewTransprent);
                    holder.pBar.setMax(100);

                    holder.imgDownload.setTag(position);
                    holder.imgMessage.setTag(position);
                    break;

                case SEND_TEXT:
                    Log.i("ChatAdapter", "In Null View text Sent  : Position : " + position);
                    convertView = mInflater.inflate(R.layout.chat_send_row, parent, false);
                    holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                    holder.imgPerson = (RoundedImageView) convertView.findViewById(R.id.imgPerson);
                    holder.tvMessage = (RobottoTextView) convertView.findViewById(R.id.tvMessage);
                    break;
                case SEND_IMAGE:
                case SEND_AUDIO:
                case SEND_VIDEO:
                    Log.i("ChatAdapter", "In Null View image Sent  : Position : " + position);
                    convertView = mInflater.inflate(R.layout.chat_image_send, parent, false);
                    holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                    holder.imgPerson = (RoundedImageView) convertView.findViewById(R.id.imgPerson);
                    holder.imgMessage = (RoundCornerImageView) convertView.findViewById(R.id.imgMessage);
                    holder.imgDownload = (ImageView) convertView.findViewById(R.id.imgDownload);
                    holder.pBar = (ProgressBar) convertView.findViewById(R.id.pBar);
                    holder.viewTranspernt = (View) convertView.findViewById(R.id.viewTransprent);
                    holder.imgDownload.setTag(position);
                    holder.imgMessage.setTag(position);
                    break;


            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            Log.i("ChatAdapter", "In Not Null View  : Position : " + position);
        }
        ChatMessage chat = getItem(position);
        holder.tvTime.setText(chat.date);
        AQuery aq = aqList.recycle(convertView);
        if (chat.isReceived) {
            aq.id(holder.imgPerson).progress(null)
                    .image(recvUrl, true, true, 0, R.drawable.about, null, 0, 1.0f);
        } else {
            aq.id(holder.imgPerson).progress(null)
                    .image(sendUrl.replaceAll(" ", "%20"), true, true, 0, R.drawable.about, null, 0, 1.0f);
        }


        if (chat.type.equalsIgnoreCase("text")) {
            holder.tvMessage.setText(chat.message);
        } else if (chat.type.equalsIgnoreCase("image")) {
            if (chat.isReceived) {
                aq.id(holder.imgMessage).progress(holder.pBar)
                        .image(chat.message, true, true, 0, 0, null, 0, 1.0f);

                holder.imgMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = Integer.parseInt(v.getTag().toString());
                        showBiggerImage(chatArray.get(pos).message);
                    }
                });


            } else {
                if (chat.isSent) {
                    //  String drawablePath = Environment.getExternalStorageDirectory().toString() + imagePath;

                    Log.i("Local path :", chat.localPath);
                    String drawablePath = chat.localPath;

                    holder.imgMessage.setImageDrawable(Drawable.createFromPath(drawablePath));
                    holder.pBar.setVisibility(View.GONE);
                    holder.viewTranspernt.setVisibility(View.GONE);
                } else {
                    holder.pBar.setVisibility(View.VISIBLE);
                    holder.viewTranspernt.setVisibility(View.VISIBLE);
                    String drawablePath = chat.localPath;
                    holder.imgMessage.setImageDrawable(Drawable.createFromPath(drawablePath));


                }


                holder.imgMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = Integer.parseInt(v.getTag().toString());
                        showBiggerImage(chatArray.get(pos).message);
                    }
                });


            }


        } else if (chat.type.equalsIgnoreCase("video")) {
            if (chat.isReceived) {
                aq.id(holder.imgMessage).progress(holder.pBar)
                        .image(chat.thumbnail, true, true, 0, R.drawable.video, null, 0, 1.0f);
                holder.imgMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = Integer.parseInt(v.getTag().toString());
                        context.startActivity(new Intent(context, VideoPlayerController.class).putExtra("url", chatArray.get(pos).thumbnail));
                    }
                });
            } else {
                if (chat.isSent) {
                    //  String drawablePath = Environment.getExternalStorageDirectory().toString() + imagePath;

                    Log.i("Local path :", chat.localPath);
                    String drawablePath = chat.localPath;

                    Bitmap bmThumbnail;
                    bmThumbnail = ThumbnailUtils.createVideoThumbnail(drawablePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                    holder.imgMessage.setImageBitmap(bmThumbnail);

                    //    holder.imgMessage.setImageDrawable(Drawable.createFromPath(drawablePath));
                    holder.pBar.setVisibility(View.GONE);
                    holder.viewTranspernt.setVisibility(View.GONE);
                } else {
                    holder.pBar.setVisibility(View.VISIBLE);
                    holder.viewTranspernt.setVisibility(View.VISIBLE);
                    ;
                    String drawablePath = chat.localPath;
                    Bitmap bmThumbnail;
                    bmThumbnail = ThumbnailUtils.createVideoThumbnail(drawablePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                    holder.imgMessage.setImageBitmap(bmThumbnail);


                }
            }
        } else if (chat.type.equalsIgnoreCase("audio")) {
            Log.i("ChatAdapter", "In Null View audio Sent  : Position : " + position);
            if (chat.isReceived) {
                aq.id(holder.imgMessage).progress(holder.pBar)
                        .image(chat.message, true, true, 0, R.drawable.audio_icon, null, 0, 1.0f);
                holder.imgMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = Integer.parseInt(v.getTag().toString());
                        context.startActivity(new Intent(context, AudioPlayer.class).putExtra("url", chatArray.get(pos).thumbnail));
                    }
                });
            } else {
                if (chat.isSent) {
                    //  String drawablePath = Environment.getExternalStorageDirectory().toString() + imagePath;

                    Log.i("Local path :", chat.localPath);
                    String drawablePath = chat.localPath;

                    holder.imgMessage.setImageResource(R.drawable.audio_icon);
                    holder.pBar.setVisibility(View.GONE);
                    holder.viewTranspernt.setVisibility(View.GONE);
                } else {
                    holder.pBar.setVisibility(View.VISIBLE);
                    holder.viewTranspernt.setVisibility(View.VISIBLE);
                    ;
                    String drawablePath = chat.localPath;
                    holder.imgMessage.setImageResource(R.drawable.audio_icon);


                }
            }
        }

        return convertView;
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();


        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }


    public void showBiggerImage(String url) {

        final Dialog nagDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(false);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_bigger_image, null);
        nagDialog.setContentView(view);

        ProgressBar pBar = (ProgressBar) view.findViewById(R.id.pBar);

        Button btnClose = (Button) view.findViewById(R.id.btnIvClose);
        AQuery aq = aqList.recycle(view);
        ImageView ivPreview = (ImageView) view.findViewById(R.id.iv_preview_image);
        aq.id(ivPreview).progress(pBar)
                .image(url, true, true, 0, 0, null, 0, 1.0f);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                nagDialog.dismiss();
            }
        });
        nagDialog.show();
    }
}
