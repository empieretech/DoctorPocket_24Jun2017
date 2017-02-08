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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.docpoc.doctor.AudioPlayer;
import com.docpoc.doctor.R;
import com.docpoc.doctor.VideoPlayerController;
import com.docpoc.doctor.classes.ChatMessage;
import com.docpoc.doctor.classes.ViewHolder;
import com.docpoc.doctor.classes.ViewHolderTEXT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChatAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<ChatMessage> chatArray = Collections.emptyList();
    Context context;
    public View v;


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


    public ChatAdapterNew(Context context, ArrayList<ChatMessage> chatArray, String recvUrl, String sendUrl) {
        this.chatArray = chatArray;
        this.context = context;
        this.aqList = new AQuery(context);
        this.recvUrl = recvUrl;
        this.sendUrl = sendUrl;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case RECV_TEXT:
                View v1 = inflater.inflate(R.layout.chat_recv_row, parent, false);
                viewHolder = new ViewHolderTEXT(v1);
                break;
            case RECV_AUDIO:
            case RECV_IMAGE:
            case RECV_VIDEO:

                View v2 = inflater.inflate(R.layout.chat_image_recv, parent, false);
                viewHolder = new ViewHolder(v2);
                break;
            case SEND_TEXT:
                View v3 = inflater.inflate(R.layout.chat_send_row, parent, false);
                viewHolder = new ViewHolderTEXT(v3);
                break;
            case SEND_AUDIO:
            case SEND_IMAGE:
            case SEND_VIDEO:

                View v4 = inflater.inflate(R.layout.chat_image_send, parent, false);
                viewHolder = new ViewHolder(v4);
                break;
        }
        return viewHolder;


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {

            case RECV_TEXT:
                ViewHolderTEXT textHolder = (ViewHolderTEXT) holder;
                configureTextViewHolder(textHolder, position);
                break;
            case RECV_AUDIO:
                ViewHolder audioHolder = (ViewHolder) holder;
                configureAudioHolder(audioHolder, position);
                break;
            case RECV_IMAGE:
                ViewHolder imgaHolder = (ViewHolder) holder;
                configureImageViewHolder(imgaHolder, position);
                break;
            case RECV_VIDEO:
                ViewHolder videoHolder = (ViewHolder) holder;
                configureVideoHolder(videoHolder, position);

                break;
            case SEND_TEXT:
                ViewHolderTEXT sTextHolder = (ViewHolderTEXT) holder;
                configureTextViewHolder(sTextHolder, position);
                break;
            case SEND_AUDIO:
                ViewHolder sAudioHolder = (ViewHolder) holder;
                configureAudioHolder(sAudioHolder, position);
                break;
            case SEND_IMAGE:
                ViewHolder sImgaHolder = (ViewHolder) holder;
                configureImageViewHolder(sImgaHolder, position);
                break;
            case SEND_VIDEO:
                ViewHolder sVideoHolder = (ViewHolder) holder;
                configureVideoHolder(sVideoHolder, position);
                break;


        }

    }

    private void configureTextViewHolder(ViewHolderTEXT holder, int position) {
        ChatMessage chat = (ChatMessage) chatArray.get(position);
        AQuery aq = aqList.recycle(v);
        holder.tvTime.setText(chat.date);
        if (chat.isReceived) {
            aq.id(holder.imgPerson).progress(null)
                    .image(recvUrl, true, true, 0, R.drawable.about, null, 0, 1.0f);
        } else {
            aq.id(holder.imgPerson).progress(null)
                    .image(sendUrl.replaceAll(" ", "%20"), true, true, 0, R.drawable.about, null, 0, 1.0f);
        }
        holder.tvMessage.setText(chat.message);
    }

    private void configureImageViewHolder(ViewHolder holder, final int position) {
        ChatMessage chat = (ChatMessage) chatArray.get(position);
        holder.tvTime.setText(chat.date);
        AQuery aq = aqList.recycle(v);
        if (chat.isReceived) {
            aq.id(holder.imgPerson).progress(null)
                    .image(recvUrl, true, true, 0, R.drawable.about, null, 0, 1.0f);
        } else {
            aq.id(holder.imgPerson).progress(null)
                    .image(sendUrl.replaceAll(" ", "%20"), true, true, 0, R.drawable.about, null, 0, 1.0f);
        }
        if (chat.isReceived) {
            aq.id(holder.imgMessage).progress(holder.pBar)
                    .image(chat.message, true, true, 0, 0, null, 0, 1.0f);

            holder.imgMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showBiggerImage(chatArray.get(position).message);
                }
            });
        } else {
            if (chat.isSent) {
                //  String drawablePath = Environment.getExternalStorageDirectory().toString() + imagePath;

                Log.i("Local path :", chat.localPath);
                String drawablePath = chat.localPath;


                aq.id(holder.imgMessage).progress(holder.pBar)
                        .image(chat.message, true, true, 0, 0, null, 0, 1.0f);
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

                    showBiggerImage(chatArray.get(position).message);
                }
            });


        }
    }


    private void configureAudioHolder(ViewHolder holder, final int position) {
        ChatMessage chat = (ChatMessage) chatArray.get(position);
        holder.tvTime.setText(chat.date);
        AQuery aq = aqList.recycle(v);
        if (chat.isReceived) {
            aq.id(holder.imgPerson).progress(null)
                    .image(recvUrl, true, true, 0, R.drawable.about, null, 0, 1.0f);
        } else {
            aq.id(holder.imgPerson).progress(null)
                    .image(sendUrl.replaceAll(" ", "%20"), true, true, 0, R.drawable.about, null, 0, 1.0f);
        }
        Log.i("ChatAdapter", "In Null View audio Sent  : Position : " + position);
        if (chat.isReceived) {
            aq.id(holder.imgMessage).progress(holder.pBar)
                    .image(chat.thumbnail, true, true, 0, R.drawable.audio_icon, null, 0, 1.0f);
            holder.imgMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    context.startActivity(new Intent(context, AudioPlayer.class).putExtra("url", chatArray.get(position).message));
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


    private void configureVideoHolder(ViewHolder holder, final int position) {
        ChatMessage chat = (ChatMessage) chatArray.get(position);
        holder.tvTime.setText(chat.date);
        AQuery aq = aqList.recycle(v);
        if (chat.isReceived) {
            aq.id(holder.imgPerson).progress(null)
                    .image(recvUrl, true, true, 0, R.drawable.about, null, 0, 1.0f);
        } else {
            aq.id(holder.imgPerson).progress(null)
                    .image(sendUrl.replaceAll(" ", "%20"), true, true, 0, R.drawable.about, null, 0, 1.0f);
        }
        if (chat.isReceived) {
            aq.id(holder.imgMessage).progress(holder.pBar)
                    .image(chat.thumbnail, true, true, 0, R.drawable.video, null, 0, 1.0f);
            holder.imgMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    context.startActivity(new Intent(context, VideoPlayerController.class).putExtra("url", chatArray.get(position).thumbnail));
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
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return chatArray.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, ChatMessage data) {
        chatArray.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(ChatMessage data) {
        int position = chatArray.indexOf(data);
        chatArray.remove(position);
        notifyItemRemoved(position);
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
