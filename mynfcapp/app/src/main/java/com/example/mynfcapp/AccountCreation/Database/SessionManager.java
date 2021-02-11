package com.example.mynfcapp.AccountCreation.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.se.omapi.Session;

import java.util.HashMap;

public class SessionManager {

    //Variables
    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;

    //Session Names
    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";

    //User Session Variables
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONENUMNER = "phoneNo";
    public static final String KEY_DATE = "date";
    public static final String KEY_GENDER = "gender";

    //Remember Me variables
    private static final String IS_REMEMBERME = "IsRememberMe";
    public static final String KEY_SESSIONPHONENUMBER = "phoneNo";
    public static final String KEY_SESSIONPASSWORD = "password";

    public SessionManager(Context _context, String sessionName) {
        context = _context;
        userSession = _context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = userSession.edit();
    }

    /*
    USER LOGIN SESSION
     */

    public void createLoginSession(String fullName, String email, String phoneNo, String password, String date, String gender) {

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_FULLNAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONENUMNER, phoneNo);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_DATE, date);
        editor.putString(KEY_GENDER, gender);

        editor.commit();
    }

    public HashMap<String, String> getUserDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_FULLNAME, userSession.getString(KEY_FULLNAME, null));
        userData.put(KEY_EMAIL, userSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PASSWORD, userSession.getString(KEY_PASSWORD, null));
        userData.put(KEY_PHONENUMNER, userSession.getString(KEY_PHONENUMNER, null));
        userData.put(KEY_DATE, userSession.getString(KEY_DATE, null));
        userData.put(KEY_GENDER, userSession.getString(KEY_GENDER, null));

        return userData;
    }

    //Check if user has logged in
    public boolean checkLogin() {
        if (userSession.getBoolean(IS_LOGIN, false)) {
            return true;
        } else {
            return false;
        }
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.commit();
    }


    /*
    REMEMBER ME SESSION
     */

    public void createRememberMeSession(String phoneNo, String password) {

        editor.putBoolean(IS_REMEMBERME, true);

        editor.putString(KEY_SESSIONPHONENUMBER, phoneNo);
        editor.putString(KEY_SESSIONPASSWORD, password);


        editor.commit();
    }

    public HashMap<String, String> getRememberMeDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_SESSIONPASSWORD, userSession.getString(KEY_SESSIONPASSWORD, null));
        userData.put(KEY_SESSIONPHONENUMBER, userSession.getString(KEY_SESSIONPHONENUMBER, null));

        return userData;
    }

    public boolean checkRememberMe() {
        if (userSession.getBoolean(IS_REMEMBERME, false)) {
            return true;
        } else {
            return false;
        }
    }
}
