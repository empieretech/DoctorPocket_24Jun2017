package com.docpoc.doctor.classes;

/**
 * Created by Sagar Sojitra on 08-10-16.
 */
public class ChatMessage {

    public String type="",uniqueChatId="",message="",thumbnail="", date="", localPath ="";

    public boolean isReceived = false;
    public boolean isSent = false;
    public int progress;
    public long chatID;
}
