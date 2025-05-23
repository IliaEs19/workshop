package com.tilldawn.Models.SaveDatas;

import com.tilldawn.Models.User;

import java.util.List;

public interface UserDataStorage {
    void saveUser(User user);
    User loadUser(String username);
    List<User> loadAllUsers();
    boolean deleteUser(String username);
    boolean userExists(String username);
}
