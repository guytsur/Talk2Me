package com.example.regev.talk2me;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class GroupScreen extends AppCompatActivity {
    private String mGroupName;
    private String mGroupPhoto;
    private TextView mGroupNameTextview;
    private TextView mGroupPhotoTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Bundle extras = getIntent().getExtras();
        mGroupName = extras.getString("groupName");
        mGroupPhoto = extras.getString("groupPhoto");
        mGroupNameTextview = (TextView) findViewById(R.id.groupName);
        mGroupPhotoTextview = (TextView) findViewById(R.id.groupPhoto);
        mGroupNameTextview.setText("Group Name: " + mGroupName);
        mGroupPhotoTextview.setText("PhotoURL: " + mGroupPhoto);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
