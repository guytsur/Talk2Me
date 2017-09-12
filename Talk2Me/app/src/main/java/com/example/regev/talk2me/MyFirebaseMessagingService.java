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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final String MESSAGE_TYPE = "ttm_message_type";
    final String GROUP_PIN = "group_pin";
    final String GROUP_NAME = "group_name";
    final String USER_PHOTO = "user_picture_url";
    final String GROUP_PHOTO = "group_picture_url";
    final String USER_ID= "user_id";
    final String FIREBASE_TOKEN = "firebase_token";
    final String GROUP_MEMBERS = "group_members";
    final String USER_STATE = "user_state";
    final String REASON = "reason";
    final String NEW_MEMBER = "new_member";
    final String USER_ID_TO_LOCK = "user_id_to_lock";
    final String LOCKER_ID = "locker_id";


    private static final String TAG = "MyFMService";
    private SQLiteDatabase mDb;
    //this runs on all data messages from guy's server
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String,String> data = remoteMessage.getData();
        Log.d(TAG, "FCM Data Message: " +data);
        Talk2MeDbHelper dbHelper = new Talk2MeDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        /*String[] groupColumns = new String[3];
        groupColumns[0] = Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN;
        groupColumns[1] = Talk2MeContract.MemberEntry.COLUMN_GROUP_NAME;
        groupColumns[2] = Talk2MeContract.MemberEntry.COLUMN_GROUP_PHOTO;
        Cursor cursor = mDb.query(Talk2MeContract.MemberEntry.TABLE_NAME,groupColumns,null,null,Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN,null,null);
        Log.d(TAG, cursor.getCount() + " groups FCM READ FROM THE SERVICE");*/
        // Handle data payload of FCM messages.
        //Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        //Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        //Log.d(TAG, "FCM Data Message: " +remoteMessage.getData());

        String messageType = data.get(MESSAGE_TYPE);
        if (messageType == null)
        {
            Log.d(TAG, "FCM Data Message: null" );
        }
        else if(messageType.equals("group_created"))
        {
            //TODO create a group by this name with only me as a member
            //Recieved params: group_pin, group_name, group photo,creating user
            Log.d(TAG, "FCM: creating group " + data.get(GROUP_NAME) );
            Log.d(TAG, "FCM: creating group PIN " + data.get(GROUP_PIN) );
            //push the new group into the databse
            ContentValues cv = new ContentValues();
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN, data.get(GROUP_PIN));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_NAME, data.get(GROUP_NAME));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_PHOTO, data.get(GROUP_PHOTO) );
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_LOCKED, false);
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_NAME, data.get(USER_ID));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_PHOTO, data.get(USER_PHOTO));
            // add a new group with the member given in the message.
            mDb.insert(Talk2MeContract.MemberEntry.TABLE_NAME, null, cv);
            //Context context = this;
            //Toast.makeText(context, groupName, Toast.LENGTH_SHORT).show();
            //TODO Launch group viewing activity of the group.
            //TODO bom
            // COMPLETED (3) Remove the Toast and launch the DetailActivity using an explicit Intent
            Class destinationClass = GroupScreen.class;
            Intent intentToStartDetailActivity = new Intent(this, destinationClass);
            Group gr = new Group(data.get(GROUP_NAME),data.get(GROUP_PIN),data.get(GROUP_PHOTO));
            GroupMember grr = new GroupMember(data.get(USER_ID),data.get(USER_PHOTO),false);
            gr.addMember(grr);
            intentToStartDetailActivity.putExtra("group",gr);
            intentToStartDetailActivity.putExtra("username",grr);
            //intentToStartDetailActivity.putExtra("groupName",groupName);
            //intentToStartDetailActivity.putExtra("groupPhoto",groupPhoto);
            //TODO send the activity also all the groups members...
            startActivity(intentToStartDetailActivity);
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
            //Map<String,String> groupMembers = remoteMessage.getData().get(GROUP_MEMBERS);
            //TODO how does a list of users look like?
            Log.d("FCM", data.get(GROUP_MEMBERS));
            ContentValues cv = new ContentValues();
            Group gr = new Group(data.get(GROUP_NAME),data.get(GROUP_PIN),data.get(GROUP_PHOTO));

            //String manipulation + adding users to group...
            String[] members = data.get(GROUP_MEMBERS).split(",");
            //TODO update these arrays from guy's message
            String[] members_photos = new String[members.length];
            boolean[] members_locked = new boolean[members.length];
            members[0] = members[0].substring(2,members[0].length()-1);
            gr.addMember(new GroupMember(members[0],"",false));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN, data.get(GROUP_PIN));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_NAME, data.get(GROUP_NAME));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_PHOTO, data.get(GROUP_PHOTO) );
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_LOCKED, members_locked[0]);
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_NAME, members[0]);
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_PHOTO, members_photos[0]);
            mDb.insert(Talk2MeContract.MemberEntry.TABLE_NAME, null, cv);
            members[members.length-1] = members[members.length-1].substring(1,members[members.length-1].length() - 2);
            gr.addMember(new GroupMember(members[members.length-1],"",false));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN, data.get(GROUP_PIN));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_NAME, data.get(GROUP_NAME));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_PHOTO, data.get(GROUP_PHOTO) );
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_LOCKED, members_locked[members.length-1]);
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_NAME, members[members.length-1]);
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_PHOTO, members_photos[members.length-1]);
            mDb.insert(Talk2MeContract.MemberEntry.TABLE_NAME, null, cv);

            for (int i=1;i < members.length-1;i++)
            {
                members[i] = members[i].substring(1,members[i].length() - 1);
                gr.addMember(new GroupMember(members[i],"",false));
                cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN, data.get(GROUP_PIN));
                cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_NAME, data.get(GROUP_NAME));
                cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_PHOTO, data.get(GROUP_PHOTO) );
                cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_LOCKED, members_locked[i]);
                cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_NAME, members[i]);
                cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_PHOTO, members_photos[i]);
                mDb.insert(Talk2MeContract.MemberEntry.TABLE_NAME, null, cv);
            }
            //Log.d("FCM", "first split: " + members[0]);
            //Log.d("FCM", "last split: " + members[members.length-1]);


            //Context context = this;
            //Toast.makeText(context, groupName, Toast.LENGTH_SHORT).show();
            //TODO Launch group viewing activity of the group.
            //TODO bom
            // COMPLETED (3) Remove the Toast and launch the DetailActivity using an explicit Intent
            Class destinationClass = GroupScreen.class;
            Intent intentToStartDetailActivity = new Intent(this, destinationClass);
            GroupMember grr = new GroupMember(data.get(USER_ID),data.get(USER_PHOTO),false);
            //gr.addMember(grr);
            intentToStartDetailActivity.putExtra("group",gr);
            intentToStartDetailActivity.putExtra("username",grr);
            //intentToStartDetailActivity.putExtra("groupName",groupName);
            //intentToStartDetailActivity.putExtra("groupPhoto",groupPhoto);
            //TODO send the activity also all the groups members...
            startActivity(intentToStartDetailActivity);
        }
        else if (messageType.equals("group_req_failed"))
        {
            //TODO popup a Toast saying group join failed due to non existant pin or full group
            //Params: group_pin,reason	no_PIN  OR group_full
            //Context context = this;
            //Toast.makeText(context, "Error joining group: " + data.get(GROUP_PIN) + " reason: " + data.get(REASON), Toast.LENGTH_SHORT).show();
        }
        else if (messageType.equals("new_group_member"))
        {
            //TODO add a new group member to the designated group.
            /*params:
            group_pin
            new_member	user_name
                        picture_url
                        */
            ContentValues cv = new ContentValues();
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN, data.get(GROUP_PIN));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_NAME, data.get(GROUP_NAME));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_GROUP_PHOTO, data.get(GROUP_PHOTO) );
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_LOCKED, false);
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_NAME, data.get(USER_ID));
            cv.put(Talk2MeContract.MemberEntry.COLUMN_USER_PHOTO, data.get(USER_PHOTO));
            // add a new group with the member given in the message.
            mDb.insert(Talk2MeContract.MemberEntry.TABLE_NAME, null, cv);
            //Context context = this;
            //Toast.makeText(context, groupName, Toast.LENGTH_SHORT).show();
            //TODO Launch group viewing activity of the group?
            //TODO Think about popping a notification which onclick sends us to the group
            // COMPLETED (3) Remove the Toast and launch the DetailActivity using an explicit Intent
            Class destinationClass = GroupScreen.class;
            Intent intentToStartDetailActivity = new Intent(this, destinationClass);
            Group gr = new Group(data.get(GROUP_NAME),data.get(GROUP_PIN),data.get(GROUP_PHOTO));
            GroupMember grr = new GroupMember(data.get(USER_ID),data.get(USER_PHOTO),false);
            //////////////////////////////////////////////////////////
            //go over all the original group members and add them also to the group.
            //query the group ID and add all members. DONE
            String[] memberColumns = new String[4];
            memberColumns[0] = Talk2MeContract.MemberEntry.COLUMN_USER_NAME;
            memberColumns[1] = Talk2MeContract.MemberEntry.COLUMN_USER_PHOTO;
            memberColumns[2] = Talk2MeContract.MemberEntry.COLUMN_USER_LOCKED;
            memberColumns[3] = Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN;
            Cursor membersOfGroup = mDb.query(Talk2MeContract.MemberEntry.TABLE_NAME,memberColumns,Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN+"='"+data.get(GROUP_PIN)+"'"
                    ,null,null,null,null);
            membersOfGroup.moveToFirst();
            while(!membersOfGroup.isAfterLast() && membersOfGroup.getCount() != 0)
            {
                //Log.d(TAG, membersOfGroup.getString(membersOfGroup.getColumnIndex(Talk2MeContract.MemberEntry.COLUMN_USER_NAME)) + " member FCM");
                gr.addMember(new GroupMember(membersOfGroup.getString(membersOfGroup.getColumnIndex(Talk2MeContract.MemberEntry.COLUMN_USER_NAME)),
                        membersOfGroup.getString(membersOfGroup.getColumnIndex(Talk2MeContract.MemberEntry.COLUMN_USER_PHOTO)),
                        false));
                membersOfGroup.moveToNext();
            }
            ///////////////////////////////////////////////////////////
            intentToStartDetailActivity.putExtra("group",gr);
            intentToStartDetailActivity.putExtra("username",grr);
            //TODO send the activity also all the groups members...
            startActivity(intentToStartDetailActivity);
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
            Class destinationClass = UrLockedActivity.class;
            Intent intentToStartDetailActivity = new Intent(this, destinationClass);
            //intentToStartDetailActivity.putExtra("groupPhoto",groupPhoto);
            //TODO send the activity also all the groups members...
            startActivity(intentToStartDetailActivity);
        }
        if(mDb != null) {
            mDb.close();
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
