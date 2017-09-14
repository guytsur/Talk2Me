package com.lb.regev.talk2me;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,GroupAdapterOnClickHandler {
    private static final int GROUP_SCREEN = 1;
    public static final int ACTION_LEAVE = 100;
    public static final String MAIN_ACTIVITY = "MainActivity";


    @Override
    public void onClick(Group group) {
        //Context context = this;
        //Toast.makeText(context, groupName, Toast.LENGTH_SHORT).show();
        //TODO Launch group viewing activity of the clicked group.
        //TODO bom
        Class destinationClass = GroupScreen.class;
        Intent intentToStartDetailActivity = new Intent(mContext, destinationClass);
        intentToStartDetailActivity.putExtra("group",group);
        intentToStartDetailActivity.putExtra("username",new GroupMember(mUsername,"",false));
        //TODO send the activity also all the groups members...
        startActivityForResult(intentToStartDetailActivity,GROUP_SCREEN);
    }

    /**
     * Created by Regev on 9/8/2017.
     */

    public class GroupAdapter extends RecyclerView.Adapter<com.lb.regev.talk2me.MainActivity.GroupAdapter.GroupAdapterViewHolder> {
        private Vector<Group> mGroups;
        private final GroupAdapterOnClickHandler mClickHandler;

        /**
         * Creates a GroupAdapter.
         *
         * @param clickHandler The on-click handler for this adapter. This single handler is called
         *                     when an item is clicked.
         */
        public GroupAdapter(GroupAdapterOnClickHandler clickHandler) {
            mClickHandler = clickHandler;
            mGroups = new Vector<Group>();
        }

        public class GroupAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView groupTextView;
            ImageView groupImageView;

            public GroupAdapterViewHolder(View v) {
                super(v);
                groupTextView = (TextView) itemView.findViewById(R.id.groupNameTextView);
                groupImageView = (CircleImageView) itemView.findViewById(R.id.groupImageView);
                v.setOnClickListener(this);
            }

            /**
             * This gets called by the child views during a click.
             *
             * @param v The View that was clicked
             */
            @Override
            public void onClick(View v) {
                int adapterPosition = getAdapterPosition();
                mClickHandler.onClick(mGroups.get(adapterPosition));
            }
        }

        @Override
        public com.lb.regev.talk2me.MainActivity.GroupAdapter.GroupAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            int layoutIdForListItem = R.layout.group_item_view;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
            return new GroupAdapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(com.lb.regev.talk2me.MainActivity.GroupAdapter.GroupAdapterViewHolder GroupAdapterViewHolder, int position) {
            Group group = mGroups.get(position);
            GroupAdapterViewHolder.groupTextView.setText(group.getmName());
            //GroupAdapterViewHolder.groupImageView.setImageURI(new URI(group[1]));
            if (group.getmPhotoURL() == null || group.getmPhotoURL() == "") {
                GroupAdapterViewHolder.groupImageView.setImageResource(R.drawable.ic_account_circle_black_36dp);
            } else {
                Glide.with(MainActivity.this)
                        .load(group.getmPhotoURL())
                        .into(GroupAdapterViewHolder.groupImageView);
            }
        }

        @Override
        public int getItemCount() {
            if (null == mGroups) return 0;
            return mGroups.size();
        }

        public void setGroupData(Vector<Group> groupData) {
            mGroups = groupData;
            notifyDataSetChanged();
        }
        public void addGroup(Group toAdd)
        {
            for (int i = 0; i < mGroups.size(); i++) {
                Group groupI = mGroups.get(i);
                if (groupI.getmPIN().equals(toAdd.getmPIN()))
                {
                    Log.i(TAG, "we already have this group...");
                    return; //we already have this group...
                }
            }
            mGroups.add(toAdd);
            Log.i(TAG, "group added...");
            notifyDataSetChanged();
        }
        public void removeGroup(Group toRemove)
        {
            for (int i = 0; i < mGroups.size(); i++) {
                Group groupI = mGroups.get(i);

                if (groupI.getmPIN().equals(toRemove.getmPIN()))
                {
                    Log.i(TAG, "Found a group to remove");
                    //TODO check if group really removed..
                    mGroups.remove(i);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private String mPhotoUrl;
    private GoogleApiClient mGoogleApiClient;
    private DataUpdateReceiver mdataUpdateReceiver;
    private FloatingActionButton mCreateButton;
    private FloatingActionButton mJoinButton;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mLinearLayoutManager2;
    private RecyclerView mGroupRecyclerView;
    private GroupAdapter mGroupAdapter;
    final Context mContext = this;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    //database SQL for the app...
    private SQLiteDatabase mDb;
    private Talk2MeDbHelper mdbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mCreateButton = (FloatingActionButton) findViewById(R.id.btn_create_group);
        mJoinButton = (FloatingActionButton) findViewById(R.id.btn_join_group);
        //Check if someone is loggeed in, and send my token to the server.
        if (mFirebaseUser != null) {
            mUsername = mFirebaseUser.getEmail();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.i(TAG, "FCMP Registration Token: " + token);
            //update server of your token
            String type = "sign_in";
            String firebase_token = token;
            ClientToServerMessage msg = new ClientToServerMessage(type,mUsername,firebase_token,mPhotoUrl, "","",
                    null,
                    null, "");
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFirebaseDatabaseReference.push().setValue(msg);
        }
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        //DB init
        mdbHelper = new Talk2MeDbHelper(this);
        mDb = mdbHelper.getReadableDatabase();
        mGroupAdapter = new GroupAdapter(this);
        updateGroupList();

        // Initialize RecyclerView.
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager2 = new LinearLayoutManager(this);
        mLinearLayoutManager2.setStackFromEnd(true);
        mGroupRecyclerView = (RecyclerView) findViewById(R.id.groupRecyclerView);
        mGroupRecyclerView.setAdapter(mGroupAdapter);
        mGroupRecyclerView.setLayoutManager(mLinearLayoutManager2);
        mGroupRecyclerView.setVisibility(View.VISIBLE);
        // set mCreateButton on click listener
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // inflate alert dialog xml
                LayoutInflater li2 = LayoutInflater.from(mContext);
                View dialogView2 = li2.inflate(R.layout.create_group_dialog, null);
                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(mContext);
                // set title
                alertDialogBuilder2.setTitle("Group creation");
                // set custom dialog icon
                alertDialogBuilder2.setView(dialogView2);
                final EditText userInputGroupname = (EditText) dialogView2
                        .findViewById(R.id.et_group_name_input);

                // set dialog message
                alertDialogBuilder2
                        .setCancelable(false)
                        .setPositiveButton("Create",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        if(!(userInputGroupname.getText().length() == 0 ))
                                        {
                                            //Sending a create group message to the server.
                                            //TODO create the group...
                                            String type = "create_group";
                                            String groupName = userInputGroupname.getText().toString();
                                            ClientToServerMessage msg = new ClientToServerMessage(type,mUsername,"token",mPhotoUrl, groupName,
                                                    "gr_pin",
                                                    null,
                                                    null, "user_to_lock");
                                            mFirebaseDatabaseReference.push().setValue(msg);
                                            //group created, now handler for ground_created message will take over.
                                        }

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                //option to add a photo to the group.. in the future :)
                /*.setNeutralButton("Photo", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                int id) {
                    //TODO add possibility to add a group photo to a group...
                    Log.d(TAG, "OMGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_IMAGE);
                    //TODO done opening the photo dialog, now see how to add it into the group..
                }
                    });*/
                // create alert dialog
                AlertDialog alertDialog2 = alertDialogBuilder2.create();
                // show it
                alertDialog2.show();
            }
        });

        // set mJoinButton on click listener
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // inflate alert dialog xml
                LayoutInflater li2 = LayoutInflater.from(mContext);
                View dialogView2 = li2.inflate(R.layout.join_group_dialog, null);
                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(mContext);
                // set title
                alertDialogBuilder2.setTitle("Group join");
                alertDialogBuilder2.setView(dialogView2);
                final EditText userInputGroupPIN = (EditText) dialogView2
                        .findViewById(R.id.et_group_name_join_input);
                // set dialog message
                alertDialogBuilder2
                        .setCancelable(false)
                        .setPositiveButton("Join",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        if(!(userInputGroupPIN.getText().length() == 0 ))
                                        {
                                            //TODO join the group...
                                            String type = "join_group";
                                            String groupPIN = userInputGroupPIN.getText().toString();

                                            ClientToServerMessage msg = new ClientToServerMessage(type,mUsername,"token",mPhotoUrl, "gr_name",
                                                    groupPIN,
                                                    null,
                                                    null, "user_to_lock");
                                            mFirebaseDatabaseReference.push().setValue(msg);
                                            //group joined, now handler for ground_found message will take over.
                                        }

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog2 = alertDialogBuilder2.create();
                // show it
                alertDialog2.show();
            }
        });
    }
    //Updates the group list from the database
    private void updateGroupList() {
        String[] groupColumns = new String[3];
        groupColumns[0] = Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN;
        groupColumns[1] = Talk2MeContract.MemberEntry.COLUMN_GROUP_NAME;
        groupColumns[2] = Talk2MeContract.MemberEntry.COLUMN_GROUP_PHOTO;
        String[] memberColumns = new String[4];
        memberColumns[0] = Talk2MeContract.MemberEntry.COLUMN_USER_NAME;
        memberColumns[1] = Talk2MeContract.MemberEntry.COLUMN_USER_PHOTO;
        memberColumns[2] = Talk2MeContract.MemberEntry.COLUMN_USER_LOCKED;
        memberColumns[3] = Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN;
        //Cursor cursor = mDb.query(Talk2MeContract.MemberEntry.TABLE_NAME,groupColumns,null,null,null,null,Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN);
        Cursor cursor = mDb.query(Talk2MeContract.MemberEntry.TABLE_NAME,groupColumns,null,null,Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN,null,null);
        Vector<Group> groups = new Vector<Group>();
        cursor.moveToFirst();
        //Log.d(TAG, cursor.getCount() + " groups FCM");
        while(!cursor.isAfterLast())
        {
            //add the data to the groups vector
            //Log.d(TAG, cursor.getString(0) + " FCM");
            Group group = new Group(cursor.getString(cursor.getColumnIndex(Talk2MeContract.MemberEntry.COLUMN_GROUP_NAME)),
                    cursor.getString(cursor.getColumnIndex(Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN)),
                    cursor.getString(cursor.getColumnIndex(Talk2MeContract.MemberEntry.COLUMN_GROUP_PHOTO)));
            //TODO query the group ID and add all members. DONE
            Cursor membersOfGroup = mDb.query(Talk2MeContract.MemberEntry.TABLE_NAME,memberColumns,Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN+"='"+cursor.getString(cursor.getColumnIndex(Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN))+"'"
                    ,null,null,null,null);
            //Log.d(TAG, membersOfGroup.getCount() + " members FCM");
            membersOfGroup.moveToFirst();
            while(!membersOfGroup.isAfterLast() && membersOfGroup.getCount() != 0)
            {
                //Log.d(TAG, membersOfGroup.getString(membersOfGroup.getColumnIndex(Talk2MeContract.MemberEntry.COLUMN_USER_NAME)) + " member FCM");
                group.addMember(new GroupMember(membersOfGroup.getString(membersOfGroup.getColumnIndex(Talk2MeContract.MemberEntry.COLUMN_USER_NAME)),
                        membersOfGroup.getString(membersOfGroup.getColumnIndex(Talk2MeContract.MemberEntry.COLUMN_USER_PHOTO)),
                        false));
                membersOfGroup.moveToNext();
            }
            groups.add(group);
            cursor.moveToNext();
        }
        mGroupAdapter.setGroupData(groups);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == GROUP_SCREEN)
        {
            Log.d("FCM","came back from group screen");
            if(resultCode == RESULT_OK)
            {
                Bundle extras = data.getExtras();
                if(extras.getInt("action") == ACTION_LEAVE)
                {
                    //TODO delete group from list..
                    Group toDelete = (Group) extras.get("group");
                    Log.d(TAG,"FCM removing group " + toDelete.getmName());
                    mGroupAdapter.removeGroup(toDelete);
                    String removeGroup = "DELETE FROM " + Talk2MeContract.MemberEntry.TABLE_NAME + " WHERE " + Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN +" = '" + toDelete.getmPIN() +"';";
                    Log.d(TAG,"FCM SQL " + removeGroup);
                    mDb.execSQL(removeGroup);
                    //TODO delete group from database...
                }
            }
        }
        //Code to allow adding an image to the server.. to implement in the furute
        /*
        else if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());
                    FriendlyMessage tempMessage = new FriendlyMessage(null, mUsername, mPhotoUrl,
                            LOADING_IMAGE_URL);
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mFirebaseUser.getUid())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }*/
    }
    //code to allow puuting an image in the server.. to be used in the future
    /*
    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(MainActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            FriendlyMessage friendlyMessage =
                                    new FriendlyMessage(null, mUsername, mPhotoUrl,
                                            task.getResult().getMetadata().getDownloadUrl()
                                                    .toString());
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
                                    .setValue(friendlyMessage);
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }*/

    @Override
    public void onStart() {
        super.onStart();
        updateGroupList();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        //mDb.close();
        if (mdataUpdateReceiver != null) unregisterReceiver(mdataUpdateReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mdbHelper = new Talk2MeDbHelper(this);
        mDb = mdbHelper.getReadableDatabase();
        updateGroupList();
        if (mdataUpdateReceiver == null) mdataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter("refresh");
        registerReceiver(mdataUpdateReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if(mDb != null) {
            mDb.close();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                //TODO clear databse.. since we switch user now
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String dest = extras.getString("dest");
            if(dest.equals(MAIN_ACTIVITY))
            {
                updateGroupList();
            }
        }
    }

}
