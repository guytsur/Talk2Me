package com.example.regev.talk2me;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by Regev on 9/8/2017.
 */

public class Group implements Serializable {
    private Vector<GroupMember> mMembers;
    private String mPIN;
    private String mName;
    private String mPhotoURL;

    public Group(String name,String PIN,String photoURL)
    {
        mMembers = new Vector<GroupMember>();
        mName = name;
        mPIN = PIN;
        mPhotoURL = photoURL;
    }

    public Vector<GroupMember> getmMembers() {
        return mMembers;
    }

    public String getmName() {
        return mName;
    }

    public String getmPIN() {
        return mPIN;
    }

    public String getmPhotoURL() {
        return mPhotoURL;
    }

    public void addMember(GroupMember member)
    {
        for (int i = 0; i < mMembers.size(); i++) {
            GroupMember memberI = mMembers.get(i);
            if (memberI.getName().equals(member.getName()) && memberI.getPhotoURL().equals(member.getPhotoURL()))
            {
                return; //member is already in the group...
            }
        }
        mMembers.add(member);
    }
    public void removeMember(GroupMember member)
    {
        for (int i = 0; i < mMembers.size(); i++) {
            GroupMember memberI = mMembers.get(i);
            if (memberI.getName().equals(member.getName()) && memberI.getPhotoURL().equals(member.getPhotoURL()))
            {
                mMembers.remove(i);
                break;
            }
        }
    }

}
