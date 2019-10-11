package com.rabbitt.jikook.Preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    //user details
    public static final String ID_KEY = "ID_KEY";
    public static final String USER_PREFS = "USER_DETAILS";
    public static final String USER_PARTNER = "USER_PARTNER";
    public static final String USER_NAME = "USER_KEY";
    public static final String USER_PHONE = "USER_PHONE";
    public static final String USER_NICK = "USER_NICK";
    public static final String USER_GENDER = "USER_GENDER";
    public static final String USER_BIO = "USER_BIO";
    public static final String USER_DOB = "USER_DOB";
    public static final String CHAT_WITH = "CHAT_WITH";

    // Shared preferences file name
    private static final String PREF_NAME = "USER_PREFS";
    private static final String LOGIN = "IsFirstTimeLaunch";
    private SharedPreferences pref, partner_pref;
    private SharedPreferences.Editor editor, user_editor, partner_editor;

    @SuppressLint("CommitPrefEdits")
    public PrefsManager(Context context) {
        // shared pref mode
        int PRIVATE_MODE = 0;

        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        SharedPreferences userpref = context.getSharedPreferences(USER_PREFS, PRIVATE_MODE);
        user_editor = userpref.edit();

        partner_pref = context.getSharedPreferences(USER_PARTNER, PRIVATE_MODE);
        partner_editor = partner_pref.edit();

    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(LOGIN, false);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(LOGIN, isFirstTime);
        editor.commit();
    }

    public void userPreferences(String username, String phonenumber, String dob, String nname, String bio) {
        user_editor.putString(USER_NAME,username);
        user_editor.putString(USER_PHONE,phonenumber);
        user_editor.putString(USER_DOB,dob);
        user_editor.putString(USER_NICK,nname);
        user_editor.putString(USER_BIO,bio);
        user_editor.commit();
    }

    public void chatwith(String chat_soul)
    {
        partner_editor.putString(CHAT_WITH,chat_soul);
        partner_editor.commit();
    }
}
