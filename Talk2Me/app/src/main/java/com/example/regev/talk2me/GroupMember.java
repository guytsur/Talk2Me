package com.example.regev.talk2me;

import java.io.Serializable;

/**
 * Created by Regev on 9/8/2017.
 */

public class GroupMember implements Serializable {
    private String mName;
    private String mPhotoURL;
    private boolean mIsLocked;

    public GroupMember(String name,String photoURL,boolean isLocked)
    {
        mName = name;
        mPhotoURL = photoURL;
        mIsLocked = isLocked;
    }

    public String getName()
    {
        return mName;
    }
    public String getPhotoURL()
    {
        return mPhotoURL;
    }
    public boolean isLocked()
    {
        return mIsLocked;
    }
    public void lock()
    {
        mIsLocked = true;
    }
    public void unlock()
    {
        mIsLocked = false;
    }

}
