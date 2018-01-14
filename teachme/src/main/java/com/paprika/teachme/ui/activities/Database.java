package com.paprika.teachme.ui.activities;

public class Database
{
    public static String getName() {
        return name;
    }

    public static void setName(String imie) {
        name = imie;
    }

    public static String getCourse() {
        return course;
    }

    public static void setCourse(String kierunek) {
        course = kierunek;
    }

    public static int getYear() {
        return year;
    }

    public static void setYear(int rok) {
        year = rok;
    }

    public static int getSubjectIDx() {
        return subjectIDx;
    }

    public static void setSubjectIDx(int w) {
        subjectIDx = w;
    }

    public static boolean isActive() {
        return isActive;
    }

    public static void setActive(boolean active) {
        isActive = active;
    }

    public static boolean isNavigate() {
        return navigate;
    }

    public static void setNavigate(boolean n) {
        navigate = n;
    }

    public static void init()
    {
        //initializer
        name = course = "";
        year = 1;
        subjectIDx = 0;
        isActive = navigate = true;
    }

    private static String name;
    private static String course;
    private static int year;
    private static int subjectIDx;//look in Globals.java
    private static boolean isActive;
    private static boolean navigate;

    public static void SaveToCloud()
    {
        //todo
    }

    public static void LoadFromCloud()
    {
        //todo
    }
}
