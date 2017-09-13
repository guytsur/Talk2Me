package com.example.regev.talk2me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Member;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupScreen extends AppCompatActivity implements MemberAdapterOnClickHandler {
    //RECYCLERVIEW stuff
    public static final String ADD_MEMBER = "add_member";
    public static final String REMOVE_MEMBER = "remove_member";
    public void onClick(final GroupMember member) {
        Context context = this;
        //Toast.makeText(context, groupName, Toast.LENGTH_SHORT).show();
        //TODO Launch menu asking if you want to lock this member.
        if (member.isLocked()) {
            Toast.makeText(context, member.getName() + " is already Locked...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // inflate alert dialog xml
            LayoutInflater li2 = LayoutInflater.from(context);
            View dialogView2 = li2.inflate(R.layout.lock_dialog, null);
            AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(context);
            // set title
            alertDialogBuilder2.setTitle("Confirmation");
            alertDialogBuilder2.setView(dialogView2);
            // set dialog message
            alertDialogBuilder2
                    .setCancelable(false)
                    .setPositiveButton("Lock",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                    //Sending a lock user message to the server.
                                    //TODO lock the user!
                                    String type = "lock_request";
                                    ClientToServerMessage msg = new ClientToServerMessage(type,mUsername,"token",mGroup.getmPhotoURL(),
                                            mGroup.getmName(),
                                            mGroup.getmPIN(),
                                            null,
                                            null, member.getName());
                                    mFirebaseDatabaseReference.push().setValue(msg);
                                    //sent a lock request to the server.


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
    }

    /**
     * Created by Regev on 9/8/2017.
     */

    public class MemberAdapter extends RecyclerView.Adapter<com.example.regev.talk2me.GroupScreen.MemberAdapter.MemberAdapterViewHolder> {
        private Vector<GroupMember> mMembers;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
        private final MemberAdapterOnClickHandler mClickHandler;

        /**
         * The interface that receives onClick messages.
         */

        public MemberAdapter(MemberAdapterOnClickHandler clickHandler) {
            mClickHandler = clickHandler;
            mMembers = new Vector<GroupMember>();
        }


        public class MemberAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            //public final TextView mWeatherTextView;
            TextView memberTextView;
            ImageView memberImageView;

            public MemberAdapterViewHolder(View v) {
                super(v);
                memberTextView = (TextView) itemView.findViewById(R.id.memberNameTextView);
                memberImageView = (CircleImageView) itemView.findViewById(R.id.memberImageView);
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int adapterPosition = getAdapterPosition();
                //String groupName = mGroups.get(adapterPosition).getmName();
                //String groupPhoto = mGroups.get(adapterPosition).getmPhotoURL();
                mClickHandler.onClick(mMembers.get(adapterPosition));
            }
        }

        @Override
        public com.example.regev.talk2me.GroupScreen.MemberAdapter.MemberAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            int layoutIdForListItem = R.layout.member_item_view;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
            return new GroupScreen.MemberAdapter.MemberAdapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(com.example.regev.talk2me.GroupScreen.MemberAdapter.MemberAdapterViewHolder MemberAdapterViewHolder, int position) {
            GroupMember member = mMembers.get(position);
            String name = member.getName();
            String splitName = (name.split("@"))[0];
            MemberAdapterViewHolder.memberTextView.setText(splitName);
            //GroupAdapterViewHolder.groupImageView.setImageURI(new URI(group[1]));
            if (member.getPhotoURL() == null || member.getPhotoURL().equals("")) {
                MemberAdapterViewHolder.memberImageView.setImageResource(R.drawable.ic_account_circle_black_36dp);
            } else {
                Glide.with(GroupScreen.this)
                        .load(member.getPhotoURL())
                        .into(MemberAdapterViewHolder.memberImageView);
            }
        }

        @Override
        public int getItemCount() {
            if (null == mMembers) return 0;
            return mMembers.size();
        }

        public void setMemberData(Vector<GroupMember> memberData) {
            mMembers = memberData;
            notifyDataSetChanged();
        }
        public void addMember(GroupMember toAdd)
        {
            for (int i = 0; i < mMembers.size(); i++) {
                GroupMember memberI = mMembers.get(i);
                if (memberI.getName().equals(toAdd.getName()) && memberI.getPhotoURL().equals(toAdd.getPhotoURL()))
                {
                    //Log.i(TAG, "we already have this group...");
                    return; //we already have this group...
                }
            }
            mMembers.add(toAdd);
            //Log.i(TAG, "group added...");
            notifyDataSetChanged();
        }
        public void removeMember(GroupMember toRemove)
        {
            for (int i = 0; i < mMembers.size(); i++) {
                GroupMember memberI = mMembers.get(i);

                if (memberI.getName().equals(toRemove.getName()) && memberI.getPhotoURL().equals(toRemove.getPhotoURL()))
                {
                    //Log.i(TAG, "Found a group to remove");
                    //TODO check if group really removed..
                    mMembers.remove(i);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }


    //END Recycler

    private DataUpdateReceiver mdataUpdateReceiver;
    private Group mGroup;
    private String mUsername;
    private TextView mGroupNameTextview;
    private TextView mGroupPhotoTextview;
    private FloatingActionButton mLeaveGroupButton;
    private FloatingActionButton mBackButton;
    private Intent mResult;
    private DatabaseReference mFirebaseDatabaseReference;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mMembersRecyclerView;
    private GroupScreen.MemberAdapter mMemberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO update to get stuff from the
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Bundle extras = getIntent().getExtras();
        mGroup = (Group) extras.get("group");
        GroupMember user = (GroupMember) extras.get("username");
        mUsername = user.getName();
        mGroupNameTextview = (TextView) findViewById(R.id.groupName);
        mGroupPhotoTextview = (TextView) findViewById(R.id.groupPhoto);
        mGroupNameTextview.setText("Group Name: " + mGroup.getmName());
        mGroupPhotoTextview.setText("PIN: " + mGroup.getmPIN());
        mResult = new Intent();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMembersRecyclerView = (RecyclerView) findViewById(R.id.groupMembersRecyclerView);
        mMemberAdapter = new MemberAdapter(this);
        mMembersRecyclerView.setAdapter(mMemberAdapter);
        mMemberAdapter.setMemberData(mGroup.getmMembers());
        mMembersRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMembersRecyclerView.setVisibility(View.VISIBLE);
        mBackButton = (FloatingActionButton) findViewById(R.id.btn_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back to main screen on click.
                mResult.putExtra("action", "back");
                setResult(Activity.RESULT_OK, mResult);
                finish();
            }
        });

        mLeaveGroupButton = (FloatingActionButton) findViewById(R.id.btn_leave_group);
        mLeaveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send the server a group leaving message.
                String type = "leave_group";
                ClientToServerMessage msg = new ClientToServerMessage(type,mUsername,"token",mGroup.getmPhotoURL(), mGroup.getmName(),
                        mGroup.getmPIN(),
                        null,
                        null, "user_to_lock");
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                mFirebaseDatabaseReference.push().setValue(msg);
                // Go back to main screen.
                mResult.putExtra("group",mGroup);
                mResult.putExtra("action", MainActivity.ACTION_LEAVE);
                setResult(Activity.RESULT_OK, mResult);
                Log.d("A","FCM leaving group " + mGroup.getmName());
                finish();
            }
        });

    }
    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String action = extras.getString("action");
            if(action == null)
            {
                return;
            }
            if(action.equals(ADD_MEMBER))
                {
                    // Add the new member...
                    GroupMember member = (GroupMember) extras.get("member");
                    Log.d("FCM:", "adding member: " + member.getName());
                    mGroup.addMember(member);
                    mMemberAdapter.setMemberData(mGroup.getmMembers());
                }
            else if(action.equals(REMOVE_MEMBER))
            {
                // rempove the new member...
                GroupMember member = (GroupMember) extras.get("member");
                Log.d("FCM:", "removing member: " + member.getName());
                mGroup.removeMember(member);
                mMemberAdapter.setMemberData(mGroup.getmMembers());
            }


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mdataUpdateReceiver == null) mdataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter("refresh");
        registerReceiver(mdataUpdateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        if (mdataUpdateReceiver != null) unregisterReceiver(mdataUpdateReceiver);
        super.onPause();
    }
}
