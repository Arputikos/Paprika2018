package com.indoorway.android.example.fixme.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class UsersCollection {
    private static UsersCollection instance = null;
    private List<User> usersList;

    private UsersCollection() {
        usersList = new LinkedList<>();
    }

    public static UsersCollection instance() {
        if (instance == null)
            instance = new UsersCollection();

        return instance;
    }

    public synchronized void add(User user) {
        usersList.add(user);
    }

    public synchronized void remove(User user) {
        usersList.remove(user);
    }

    public Collection<User> getCollection() {
        return usersList;
    }

    public int size() {
        return usersList.size();
    }
}
