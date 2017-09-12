package com.example.regev.talk2me;

import android.provider.BaseColumns;

/**
 * Created by Regev on 9/10/2017.
 */

public class Talk2MeContract {
    public static final class MemberEntry implements BaseColumns {
        public static final String TABLE_NAME = "talk2meDB";
        public static final String COLUMN_GROUP_PIN = "groupPIN";
        public static final String COLUMN_GROUP_NAME = "groupName";
        public static final String COLUMN_GROUP_PHOTO = "groupPhotoURL";
        public static final String COLUMN_USER_NAME = "userName";
        public static final String COLUMN_USER_PHOTO = "userPhotoURL";
        public static final String COLUMN_USER_LOCKED = "userIsLocked";
    }
}
