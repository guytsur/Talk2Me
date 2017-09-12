package com.example.regev.talk2me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
            // set custom dialog icon
            //alertDialogBuilder.setIcon(R.drawable.ic_launcher);
            // set custom_dialog.xml to alertdialog builder
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

        // COMPLETED (3) Create a final private ForecastAdapterOnClickHandler called mClickHandler
    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
        private final MemberAdapterOnClickHandler mClickHandler;

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
        public MemberAdapter(MemberAdapterOnClickHandler clickHandler) {
            mClickHandler = clickHandler;
            mMembers = new Vector<GroupMember>();
        }

        // COMPLETED (5) Implement OnClickListener in the ForecastAdapterViewHolder class
        /**
         * Cache of the children views for a forecast list item.
         */
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

            // COMPLETED (6) Override onClick, passing the clicked day's data to mClickHandler via its onClick method
            /**
             * This gets called by the child views during a click.
             *
             * @param v The View that was clicked
             */
            @Override
            public void onClick(View v) {
                int adapterPosition = getAdapterPosition();
                //String groupName = mGroups.get(adapterPosition).getmName();
                //String groupPhoto = mGroups.get(adapterPosition).getmPhotoURL();
                mClickHandler.onClick(mMembers.get(adapterPosition));
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
        public com.example.regev.talk2me.GroupScreen.MemberAdapter.MemberAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            int layoutIdForListItem = R.layout.member_item_view;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
            return new GroupScreen.MemberAdapter.MemberAdapterViewHolder(view);
        }

        /**
         * OnBindViewHolder is called by the RecyclerView to display the data at the specified
         * position. In this method, we update the contents of the ViewHolder to display the weather
         * details for this particular position, using the "position" argument that is conveniently
         * passed into us.
         *
         * @param MemberAdapterViewHolder The ViewHolder which should be updated to represent the
         *                                  contents of the item at the given position in the data set.
         * @param position                  The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(com.example.regev.talk2me.GroupScreen.MemberAdapter.MemberAdapterViewHolder MemberAdapterViewHolder, int position) {
            GroupMember member = mMembers.get(position);
            MemberAdapterViewHolder.memberTextView.setText(member.getName());
            //GroupAdapterViewHolder.groupImageView.setImageURI(new URI(group[1]));
            if (member.getPhotoURL() == null || member.getPhotoURL().equals("")) {
                MemberAdapterViewHolder.memberImageView.setImageResource(R.drawable.ic_account_circle_black_36dp);
            } else {
                Glide.with(GroupScreen.this)
                        .load(member.getPhotoURL())
                        .into(MemberAdapterViewHolder.memberImageView);
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
            if (null == mMembers) return 0;
            return mMembers.size();
        }

        /**
         * This method is used to set the weather forecast on a ForecastAdapter if we've already
         * created one. This is handy when we get new data from the web but don't want to create a
         * new ForecastAdapter to display it.
         *
         * @param memberData The new weather data to be displayed.
         */
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


    private Group mGroup;
    private String mUsername;
    private TextView mGroupNameTextview;
    private TextView mGroupPhotoTextview;
    private Button mLeaveGroupButton;
    private Button mInviteButton;
    private Button mBackButton;
    private Intent mResult;
    private DatabaseReference mFirebaseDatabaseReference;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mMembersRecyclerView;
    private GroupScreen.MemberAdapter mMemberAdapter;
    final Context mContext = this;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO update to get stuff from the
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Bundle extras = getIntent().getExtras();
        mGroup = (Group) extras.get("group");
        GroupMember user = (GroupMember) extras.get("username");
        mUsername = user.getName();
        //mGroupName = extras.getString("groupName");
        //mGroupPhoto = extras.getString("groupPhoto");
        mGroupNameTextview = (TextView) findViewById(R.id.groupName);
        mGroupPhotoTextview = (TextView) findViewById(R.id.groupPhoto);
        mGroupNameTextview.setText("Group Name: " + mGroup.getmName());
        mGroupPhotoTextview.setText("PhotoURL: " + mGroup.getmPhotoURL());
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

        mBackButton = (Button) findViewById(R.id.btn_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back to main screen on click.
                mResult.putExtra("action", "back");
                setResult(Activity.RESULT_OK, mResult);
                finish();
            }
        });

        mInviteButton = (Button) findViewById(R.id.btn_invite_member);
        mInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implement invite to someone.. by email?

            }
        });

        mLeaveGroupButton = (Button) findViewById(R.id.btn_leave_group);
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

}
