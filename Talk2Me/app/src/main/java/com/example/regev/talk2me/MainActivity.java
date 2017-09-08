package com.example.regev.talk2me;
/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Dictionary;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,GroupAdapterOnClickHandler {
    @Override
    public void onClick(String groupName, String groupPhoto) {
        Context context = this;
        Toast.makeText(context, groupName, Toast.LENGTH_SHORT)
                .show();
        //TODO Launch group viewing activity of the clicked group.
        //TODO bom
        // COMPLETED (3) Remove the Toast and launch the DetailActivity using an explicit Intent
        Class destinationClass = GroupScreen.class;
        Intent intentToStartDetailActivity = new Intent(mContext, destinationClass);
        intentToStartDetailActivity.putExtra("groupName",groupName);
        intentToStartDetailActivity.putExtra("groupPhoto",groupPhoto);
        //TODO send the activity also all the groups members...
        startActivity(intentToStartDetailActivity);
    }

    /**
     * Created by Regev on 9/8/2017.
     */

    public class GroupAdapter extends RecyclerView.Adapter<com.example.regev.talk2me.MainActivity.GroupAdapter.GroupAdapterViewHolder> {
        private Vector<String[]> mGroups;

        // COMPLETED (3) Create a final private ForecastAdapterOnClickHandler called mClickHandler
    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
        private final GroupAdapterOnClickHandler mClickHandler;

        // COMPLETED (1) Add an interface called ForecastAdapterOnClickHandler
        // COMPLETED (2) Within that interface, define a void method that access a String as a parameter
        /**
         * The interface that receives onClick messages.
         */


        // COMPLETED (4) Add a ForecastAdapterOnClickHandler as a parameter to the constructor and store it in mClickHandler
        /**
         * Creates a ForecastAdapter.
         *
         * @param clickHandler The on-click handler for this adapter. This single handler is called
         *                     when an item is clicked.
         */
        public GroupAdapter(GroupAdapterOnClickHandler clickHandler) {
            mClickHandler = clickHandler;
        }

        // COMPLETED (5) Implement OnClickListener in the ForecastAdapterViewHolder class
        /**
         * Cache of the children views for a forecast list item.
         */
        public class GroupAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            //public final TextView mWeatherTextView;
            TextView groupTextView;
            ImageView groupImageView;

            public GroupAdapterViewHolder(View v) {
                super(v);
                groupTextView = (TextView) itemView.findViewById(R.id.groupNameTextView);
                groupImageView = (CircleImageView) itemView.findViewById(R.id.groupImageView);
                v.setOnClickListener(this);
            }

            // COMPLETED (6) Override onClick, passing the clicked day's data to mClickHandler via its onClick method
            /**
             * This gets called by the child views during a click.
             *
             * @param v The View that was clicked
             */
            @Override
            public void onClick(View v) {
                int adapterPosition = getAdapterPosition();
                String groupName = mGroups.get(adapterPosition)[0];
                String groupPhoto = mGroups.get(adapterPosition)[1];
                mClickHandler.onClick(groupName,groupPhoto);
            }
        }

        /**
         * This gets called when each new ViewHolder is created. This happens when the RecyclerView
         * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
         *
         * @param viewGroup The ViewGroup that these ViewHolders are contained within.
         * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
         *                  can use this viewType integer to provide a different layout. See
         *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
         *                  for more details.
         * @return A new ForecastAdapterViewHolder that holds the View for each list item
         */
        @Override
        public com.example.regev.talk2me.MainActivity.GroupAdapter.GroupAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            int layoutIdForListItem = R.layout.group_item_view;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
            return new GroupAdapterViewHolder(view);
        }

        /**
         * OnBindViewHolder is called by the RecyclerView to display the data at the specified
         * position. In this method, we update the contents of the ViewHolder to display the weather
         * details for this particular position, using the "position" argument that is conveniently
         * passed into us.
         *
         * @param GroupAdapterViewHolder The ViewHolder which should be updated to represent the
         *                                  contents of the item at the given position in the data set.
         * @param position                  The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(com.example.regev.talk2me.MainActivity.GroupAdapter.GroupAdapterViewHolder GroupAdapterViewHolder, int position) {
            String[] group = mGroups.get(position);
            GroupAdapterViewHolder.groupTextView.setText(group[0]);
            //GroupAdapterViewHolder.groupImageView.setImageURI(new URI(group[1]));
            if (group[1] == null || group[1] == "") {
                GroupAdapterViewHolder.groupImageView.setImageResource(R.drawable.ic_account_circle_black_36dp);
            } else {
                Glide.with(MainActivity.this)
                        .load(group[1])
                        .into(GroupAdapterViewHolder.groupImageView);
            }
        }

        /**
         * This method simply returns the number of items to display. It is used behind the scenes
         * to help layout our Views and for animations.
         *
         * @return The number of items available in our forecast
         */
        @Override
        public int getItemCount() {
            if (null == mGroups) return 0;
            return mGroups.size();
        }

        /**
         * This method is used to set the weather forecast on a ForecastAdapter if we've already
         * created one. This is handy when we get new data from the web but don't want to create a
         * new ForecastAdapter to display it.
         *
         * @param groupData The new weather data to be displayed.
         */
        public void setGroupData(Vector<String[]> groupData) {
            mGroups = groupData;
            notifyDataSetChanged();
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private static final String MESSAGE_URL = "http://talk2me.firebase.google.com/message/";
    //private String mUserId;
    private Button mSendButton;
    private Button mCreateButton;
    private Button mJoinButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mLinearLayoutManager2;
    private RecyclerView mGroupRecyclerView;
    private GroupAdapter mGroupAdapter;
    final Context mContext = this;
    //private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>
            mFirebaseAdapter;
    //private String mToken;
    private String mEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mCreateButton = (Button) findViewById(R.id.btn_create_group);
        mJoinButton = (Button) findViewById(R.id.btn_join_group);
        //mToken = FirebaseInstanceId.getInstance().getToken();
        //Log.i(TAG, "FCM Registration Token: " + token);
        if (mFirebaseUser != null) {
            //mUserId = mFirebaseUser.getToken(true).toString();
            //mUsername = mFirebaseUser.getEmail();
            //mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            mUsername = mFirebaseUser.getEmail();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.i(TAG, "FCMP Registration Token: " + token);
            //update server of your token
            //FriendlyMessage message = new FriendlyMessage("USER_ID",MyFirebaseInstanceIdService.UPDATE_TOKEN,mUserId,"aaaa");
            String type = "sign_in";
            //String user_id = "Regev";
            String firebase_token = token;


            ClientToServerMessage msg = new ClientToServerMessage(type,mUsername,firebase_token,mPhotoUrl, "","",
                    null,
                    null, "");
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            //mFirebaseDatabaseReference.child(MESSAGES_CHILD)
            //        .push().setValue(message);
            //mFirebaseDatabaseReference.push().setValue(message);
            mFirebaseDatabaseReference.push().setValue(msg);
        }
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {

        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        //mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager2 = new LinearLayoutManager(this);
        mLinearLayoutManager2.setStackFromEnd(true);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mGroupRecyclerView = (RecyclerView) findViewById(R.id.groupRecyclerView);
        mGroupAdapter = new GroupAdapter(this);
        mGroupRecyclerView.setAdapter(mGroupAdapter);
        //add a group just to see it and debug...
        Vector<String[]> groups = new Vector<String[]>();
        String[] group = new String[2];
        group[0] = "GRgr";
        group[1] = null;
        groups.add(0,group);
        mGroupAdapter.setGroupData(groups);
        //done adding
        mGroupRecyclerView.setLayoutManager(mLinearLayoutManager2);
        mGroupRecyclerView.setVisibility(View.VISIBLE);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage,
                MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.group_item_view,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              FriendlyMessage friendlyMessage, int position) {
                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                /*if (friendlyMessage.getText() != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.getText());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                    viewHolder.messageImageView.setVisibility(ImageView.GONE);
                } else {
                    String imageUrl = friendlyMessage.getImageUrl();
                    if (imageUrl.startsWith("gs://")) {
                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageUrl);
                        storageReference.getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            Glide.with(viewHolder.messageImageView.getContext())
                                                    .load(downloadUrl)
                                                    .into(viewHolder.messageImageView);
                                        } else {
                                            Log.w(TAG, "Getting download url was not successful.",
                                                    task.getException());
                                        }
                                    }
                                });
                    } else {
                        Glide.with(viewHolder.messageImageView.getContext())
                                .load(friendlyMessage.getImageUrl())
                                .into(viewHolder.messageImageView);
                    }
                    viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
                    viewHolder.messageTextView.setVisibility(TextView.GONE);
                }


                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(MainActivity.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }*/

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send messages on click.
                mSendButton = (Button) findViewById(R.id.sendButton);
                mSendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FriendlyMessage friendlyMessage = new
                                FriendlyMessage(mMessageEditText.getText().toString(),
                                mUsername,
                                mPhotoUrl,
                                null /* no image */);
                        mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                                .push().setValue(friendlyMessage);
                        mMessageEditText.setText("");

                    }
                });
                //Also launch the group activity to check if it works...

            }
        });

        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        // set mButton on click listener
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
                //alertDialogBuilder.setIcon(R.drawable.ic_launcher);
                // set custom_dialog.xml to alertdialog builder
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
                                        // get user input and set it to etOutput
                                        // edit text
                                        //etOutput.setText(userInputMission.getText() + " Due "+userInputDate.getText());
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
                                            //mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
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
                // create alert dialog
                AlertDialog alertDialog2 = alertDialogBuilder2.create();
                // show it
                alertDialog2.show();
            }
        });

        // set mButton on click listener
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // inflate alert dialog xml
                LayoutInflater li2 = LayoutInflater.from(mContext);
                View dialogView2 = li2.inflate(R.layout.join_group_dialog, null);
                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(mContext);
                // set title
                alertDialogBuilder2.setTitle("Group join");
                // set custom dialog icon
                //alertDialogBuilder.setIcon(R.drawable.ic_launcher);
                // set custom_dialog.xml to alertdialog builder
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
                                        // get user input and set it to etOutput
                                        // edit text
                                        //etOutput.setText(userInputMission.getText() + " Due "+userInputDate.getText());
                                        if(!(userInputGroupPIN.getText().length() == 0 ))
                                        {
                                            //TODO join the group...
                                            String type = "join_group";
                                            String groupPIN = userInputGroupPIN.getText().toString();


                                            ClientToServerMessage msg = new ClientToServerMessage(type,mUsername,"token",mPhotoUrl, "gr_name",
                                                    groupPIN,
                                                    null,
                                                    null, "user_to_lock");
                                            //mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
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
                // create alert dialog
                AlertDialog alertDialog2 = alertDialogBuilder2.create();
                // show it
                alertDialog2.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
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
        }
    }

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
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
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

    private void sendToServer(String type,Dictionary<String,String> fields)
    {
        //ClientToServerMessage message = new ClientToServerMessage(type,fields.get("user_id"),fields.get("");
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        //mFirebaseDatabaseReference.child(MESSAGES_CHILD)
        //        .push().setValue(message);
        //mFirebaseDatabaseReference.push().setValue(message);
    }
}
