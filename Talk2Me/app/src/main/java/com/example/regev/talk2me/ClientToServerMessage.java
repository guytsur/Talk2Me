package com.example.regev.talk2me;

import java.util.Dictionary;
import java.util.Vector;

/**
 * Created by Regev on 8/24/2017.
 */

public class ClientToServerMessage {
    public String message_type;
    public String user_id;
    public String firebase_token;
    public String picture_url;
    public String group_name;
    public String group_PIN;
    public Dictionary<String,Dictionary<String,String>> group_members;
    public Dictionary<String,String> member_info;
    public String user_id_to_lock;

    public ClientToServerMessage()
    {
    }

    public ClientToServerMessage(String message_type,String user_id,String firebase_token,String picture_url,String group_name,String group_PIN,
                                 Dictionary<String,Dictionary<String,String>> group_members,
                                 Dictionary<String,String> member_info, String user_id_to_lock) {
        this.message_type=message_type;
        this.user_id = user_id;
        this.firebase_token = firebase_token;
        this.picture_url = picture_url;
        this.group_name = group_name;
        this.group_PIN = group_PIN;
        this.group_members = group_members;
        this.member_info = member_info;
        this.user_id_to_lock = user_id_to_lock;
    }



}
