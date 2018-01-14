package com.paprika.teachme.ui.activities;

import android.app.Application;
import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.common.sdk.model.Visitor;
import com.indoorway.android.common.sdk.model.VisitorData;

class Data
{
    public String name;
    public String course;
    public String uuid;
    public int year;
    public int subjectIDx;//look in Globals.java
    public boolean isActive;
    public boolean navigate;

    public Data() {
        name = "";
        course = "";
        uuid = "";
        year = 1;
        subjectIDx = 0;
        isActive = true;
        navigate = true;
    }
}

public class Database
{
    public static String getName() {
        return name;
    }

    public static void setName(String imie) {
        name = imie;
        SaveToStorage();
        SaveToCloud();
    }

    public static String getCourse() {
        return course;
    }

    public static void setCourse(String kierunek) {
        course = kierunek;
        SaveToStorage();
        SaveToCloud();
    }

    public static int getYear() {
        return year;
    }

    public static void setYear(int rok) {
        year = rok;
        SaveToStorage();
        SaveToCloud();
    }

    public static int getSubjectIDx() {
        return subjectIDx;
    }

    public static void setSubjectIDx(int w) {
        subjectIDx = w;
        SaveToStorage();
        SaveToCloud();
    }

    public static boolean isActive() {
        return isActive;
    }

    public static void setActive(boolean active) {
        isActive = active;
        SaveToStorage();
        SaveToCloud();
    }

    public static boolean isNavigate() {
        return navigate;
    }

    public static void setNavigate(boolean n) {
        navigate = n;
        SaveToStorage();
        SaveToCloud();
    }

    private static String name;
    private static String course;
    private static String uuid;
    private static int year;
    private static int subjectIDx;//look in Globals.java
    private static boolean isActive;
    private static boolean navigate;

    private static Context context;

    public static void SetContext(Context c)
    {
        context = c;
    }

    public static void setUuid(String uu){
        uuid = uu;
        SaveToStorage();
        SaveToCloud();
    }

    public static String getUuid(){
        return uuid;
    }

    /*
    The data is stored in device, but synched with visitor,
    so if the user changes ... name, visitor as well as storage data are updated
    Data is loaded to this class at the beginning of application, and saved immediately after change
     */
    public static void LoadFromStorage()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        name = preferences.getString("name", "-");
        course = preferences.getString("course", "-");
        uuid = preferences.getString("uuid", "-");
        year = preferences.getInt("year", 1);
        subjectIDx = preferences.getInt("subjectID", 0);
        isActive = preferences.getBoolean("isActive", true);
        navigate = preferences.getBoolean("navigate", true);
    }
    public static void SaveToStorage()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putString("name", name);
        edit.putString("course", course);
        edit.putString("uuid", uuid);
        edit.putInt("year", year);
        edit.putInt("subjectID", subjectIDx);
        edit.putBoolean("isActive", isActive);
        edit.putBoolean("navigate", navigate);

        edit.commit();
    }

    //saves my settings to global database, so the others can see who i am and what I learn.
    //do it every change
    public static void SaveToCloud(Visitor v)
    {
        //Visitor v = IndoorwaySdk.instance().visitor().me();
        if(null == v)
        {
            //error
            System.exit(-2);
        }
        v.setMeta(EncodeData());
        Log.e("encode",v.getMeta());

    }

    public static void SaveToCloud()
    {
        Visitor v = IndoorwaySdk.instance().visitor().me();
        if(null == v)
        {
            //error
            System.exit(-7);
        }
        v.setMeta(EncodeData());
        Log.e("encode",v.getMeta());

    }

    //loads the data to person class
    public static void LoadFromCloud(VisitorData v, Data[] person)
    {
        DecodeData(person, v.getMeta());
    }

    private static String EncodeData()
    {
        return name + "," +
                course + "," +
                uuid + "," +
                Integer.toString(year) + "," +
                Integer.toString(subjectIDx) + "," +
                Boolean.toString(isActive) + "," +
                Boolean.toString(navigate);
    }

    //pass array to modify the value (java is pass-by-value type) we need reference
    private static void DecodeData(Data[] person, String D)
    {
        if(person.length <= 0 || D.length() <= 0)
        {
            //error
            System.exit(-1);
        }
        String[] d = D.split(",");
        if(d.length < 7)
        {
            //error
            System.exit(-4);
        }
        person[0].name = d[0];
        person[0].course = d[1];
        person[0].uuid = d[2];
        person[0].year = Integer.parseInt(d[3]);
        person[0].subjectIDx = Integer.parseInt(d[4]);
        person[0].isActive = Boolean.parseBoolean(d[5]);
        person[0].navigate = Boolean.parseBoolean(d[6]);
    }
}