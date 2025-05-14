package com.tilldawn.Controllers;

import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.User;
import com.tilldawn.Views.AvatarSelectionScreen;

public class AvatarSelectionController {
    private AvatarSelectionScreen view;

    public void setView(AvatarSelectionScreen view) {
        this.view = view;
    }

    /**
     * ذخیره آواتار انتخاب شده برای کاربر
     *
     * @param user        کاربر
     * @param avatarIndex شاخص آواتار انتخاب شده
     */
    public void saveUserAvatar(User user, int avatarIndex) {
        user.setAvatarIndex(avatarIndex);
        SaveData.getInstance().saveUserAvatarByIndex(user.getUserName(), avatarIndex);
    }
}
