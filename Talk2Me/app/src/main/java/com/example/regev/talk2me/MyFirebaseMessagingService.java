/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.regev.talk2me;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";
    //this runs on all data messages from guy's server
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        //Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData().get("new_member"));
        String messageType = remoteMessage.getData().get("message_type");

        if(messageType.equals("group_created"))
        {
            //TODO create a group by this name with only me as a member
            //Recieved params: group_pin, group_name
        }
        else if (messageType.equals("group_found"))
        {
            //TODO create a group by this name with the list of members given.
            /*Parameters:
            group_pin
                    group_name
            group_members	user_name
                            picture_url
                            user_state	Locked/Not Locked
                            */
        }
        else if (messageType.equals("group_req_failed"))
        {
            //TODO popup a Toast saying group join failed due to non existant pin or full group
            //Params: group_pin,reason	no_PIN  OR group_full
        }
        else if (messageType.equals("new_group_member"))
        {
            //TODO add a new group member to the designated group.
            /*params:
            group_pin
            new_member	user_name
                        picture_url
                        */

        }
        else if (messageType.equals("member_left_group"))
        {
            //TODO Remove the designated member from the group.
            //Parameters:group_PIN, left_member
        }
        else if (messageType.equals("lock_request_worked"))
        {
            //TODO mark the user as locked in the group..
            //Parameters: user_id, group ID
        }
        else if (messageType.equals("lock_request_failed"))
        {
            //TODO popup a fail to lock toast
            //Parameters: user_id
        }
        else if (messageType.equals("lock_device"))
        {
            //TODO Lock the device- pop up an alarm and notify the server that the device was locked.
            //Parameters: group_pin
        }

        //sendNotification(remoteMessage.getFrom() + ": " + remoteMessage.getNotification().getBody() );
        //Toast.makeText(this, "Guy tsur rocks", Toast.LENGTH_SHORT).show();
        //TODO Handle all recieved messages from the server, including Lock message which should pop up an alarm.
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
