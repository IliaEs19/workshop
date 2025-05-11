package com.tilldawn.Models;

import java.util.ArrayList;

public class Game {
    private static ArrayList<User> users = new ArrayList<>();

    public static ArrayList<User> getUsers() {
        return users;
    }

    public static void setUsers(ArrayList<User> users) {
        Game.users = users;
    }
}
