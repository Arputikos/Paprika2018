package com.paprika.teachme.ui.activities;

import android.app.Application;
import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.common.sdk.model.Visitor;

class Data
{
    public String name;
    public String course;
    public int year;
    public int subjectIDx;//look in Globals.java
    public boolean isActive;
    public boolean navigate;

    private static Data instance = null;
    protected Data() {}
    public static Data getInstance()
    {
        if(instance != null)
            instance = new Data();
        return instance;
    }
}

public class Database
{
    public static String getName() {
        return data.name;
    }

    public static void setName(String imie) {
        data.name = imie;
        SaveToStorage();
        SaveToCloud();
    }

    public static String getCourse() {
        return data.course;
    }

    public static void setCourse(String kierunek) {
        data.course = kierunek;
        SaveToStorage();
        SaveToCloud();
    }

    public static int getYear() {
        return data.year;
    }

    public static void setYear(int rok) {
        data.year = rok;
        SaveToStorage();
        SaveToCloud();
    }

    public static int getSubjectIDx() {
        return data.subjectIDx;
    }

    public static void setSubjectIDx(int w) {
        data.subjectIDx = w;
        SaveToStorage();
        SaveToCloud();
    }

    public static boolean isActive() {
        return data.isActive;
    }

    public static void setActive(boolean active) {
        data.isActive = active;
        SaveToStorage();
        SaveToCloud();
    }

    public static boolean isNavigate() {
        return data.navigate;
    }

    public static void setNavigate(boolean n) {
        data.navigate = n;
        SaveToStorage();
        SaveToCloud();
    }

    private static Data data = Data.getInstance();
    private static Context context;

    static {
        data = new Data();
    }

    public static void SetContext(Context c)
    {
        context = c;
    }

    /*
    The data is stored in device, but synched with visitor,
    so if the user changes ... name, visitor as well as storage data are updated
    Data is loaded to this class at the beginning of application, and saved immediately after change
     */
    public static void LoadFromStorage()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        data.name = preferences.getString("name", "-");
        data.course = preferences.getString("course", "-");
        data.year = preferences.getInt("year", 1);
        data.subjectIDx = preferences.getInt("subjectID", 0);
        data.isActive = preferences.getBoolean("isActive", true);
        data.navigate = preferences.getBoolean("navigate", true);
    }
    public static void SaveToStorage()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putString("name", data.name);
        edit.putString("course", data.course);
        edit.putInt("year", data.year);
        edit.putInt("subjectID", data.subjectIDx);
        edit.putBoolean("isActive", data.isActive);
        edit.putBoolean("navigate", data.navigate);

        edit.commit();
    }

    //saves my settings to global database, so the others can see who i am and what I learn.
    //do it every change
    public static void SaveToCloud()
    {
        Visitor v = IndoorwaySdk.instance().visitor().me();
        v.setMeta(EncodeData());
        Log.e("encode",v.getMeta());
    }

    //loads the data to person class
    public static void LoadFromCloud(Data[] person)
    {
        Visitor v = IndoorwaySdk.instance().visitor().me();
        DecodeData(person, v.getMeta());
    }

    private static String EncodeData()
    {
        return data.name + "," +
                data.course + "," +
                Integer.toString(data.year) + "," +
                Integer.toString(data.subjectIDx) + "," +
                Boolean.toString(data.isActive) + "," +
                Boolean.toString(data.navigate);
    }

    //pass array to modify the value (java is pass-by-value type) we need reference
    private static void DecodeData(Data[] person, String D)
    {
        String[] d = D.split(",");
        if(d.length < 6)
        {
            //error
            System.exit(-1);
        }
        person[0].name = d[0];
        person[0].course = d[1];
        person[0].year = Integer.parseInt(d[2]);
        person[0].subjectIDx = Integer.parseInt(d[3]);
        person[0].isActive = Boolean.parseBoolean(d[4]);
        person[0].navigate = Boolean.parseBoolean(d[5]);
    }
}